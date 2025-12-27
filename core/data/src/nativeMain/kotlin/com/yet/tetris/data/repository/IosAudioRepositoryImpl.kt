package com.yet.tetris.data.repository

import com.yet.tetris.data.music.AudioCacheManager
import com.yet.tetris.data.music.AudioSynthesizer
import com.yet.tetris.domain.model.audio.AudioSettings
import com.yet.tetris.domain.model.audio.MusicTheme
import com.yet.tetris.domain.model.audio.SoundEffect
import com.yet.tetris.domain.repository.AudioRepository
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import platform.AVFAudio.AVAudioEngine
import platform.AVFAudio.AVAudioFormat
import platform.AVFAudio.AVAudioPCMBuffer
import platform.AVFAudio.AVAudioPCMFormatFloat32
import platform.AVFAudio.AVAudioPlayerNode
import platform.Foundation.NSError
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class, UnsafeNumber::class)
class IosAudioRepositoryImpl(
    private val cacheManager: AudioCacheManager,
) : AudioRepository {
    /**
     * An isolated, thread-local object that holds all AVFoundation state.
     * This ensures that these native objects are only ever created and accessed from the main thread,
     * which is a strict requirement for AVFoundation and avoids Kotlin/Native's freezing mechanism.
     */
    @kotlin.native.concurrent.ThreadLocal
    private object AudioEngine {
        val engine = AVAudioEngine()
        val sfxPlayerNode = AVAudioPlayerNode()
        val musicPlayerNode = AVAudioPlayerNode()
        val audioFormat =
            AVAudioFormat(
                commonFormat = AVAudioPCMFormatFloat32,
                sampleRate = AudioSynthesizer.SAMPLE_RATE.toDouble(),
                channels = 1.toUInt(),
                interleaved = false,
            )
        val sfxCache = mutableMapOf<SoundEffect, AVAudioPCMBuffer>()
        var currentSettings = AudioSettings()

        init {
            engine.attachNode(sfxPlayerNode)
            engine.attachNode(musicPlayerNode)
            engine.connect(sfxPlayerNode, to = engine.mainMixerNode, format = audioFormat)
            engine.connect(musicPlayerNode, to = engine.mainMixerNode, format = audioFormat)
            engine.prepare()
            memScoped {
                val error = alloc<ObjCObjectVar<NSError?>>()
                engine.startAndReturnError(error.ptr)
                error.value?.let { println("Error starting AVAudioEngine: ${it.localizedDescription}") }
            }
            sfxPlayerNode.play()
            musicPlayerNode.play()
        }
    }

    // All AVFoundation operations are dispatched to this scope, which runs on the main thread.
    private val mainScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    /**
     * Pre-heats the PCM cache on a background thread, then converts the data to
     * platform-specific AVAudioPCMBuffers on the main thread.
     */
    override suspend fun initialize() {
        val synthesizedCache = mutableMapOf<SoundEffect, AVAudioPCMBuffer>()
        // Perform CPU-heavy synthesis on a background thread.
        withContext(Dispatchers.Default) {
            cacheManager.preheatSfxCache()
            SoundEffect.entries.forEach { effect ->
                cacheManager.getSfxPcm(effect)?.let { pcmData ->
                    val buffer = pcmDataToPcmBuffer(AudioEngine.audioFormat, pcmData)
                    synthesizedCache[effect] = buffer
                }
            }
        }
        // Safely update the main-thread cache.
        mainScope.launch {
            AudioEngine.sfxCache.putAll(synthesizedCache)
        }
    }

    /**
     * Applies audio settings on the main thread.
     */
    override fun applySettings(settings: AudioSettings) {
        mainScope.launch {
            AudioEngine.currentSettings = settings
            AudioEngine.sfxPlayerNode.volume = if (settings.soundEffectsEnabled) settings.sfxVolume else 0.0f
            AudioEngine.musicPlayerNode.volume = if (settings.musicEnabled) settings.musicVolume else 0.0f
        }
    }

    /**
     * Plays a sound effect by scheduling a pre-cached buffer on the main thread.
     * This is a "fire-and-forget" operation.
     */
    override fun playSoundEffect(effect: SoundEffect) {
        mainScope.launch {
            if (!AudioEngine.currentSettings.soundEffectsEnabled) return@launch

            // Ленивое кэширование: если звука нет в кэше, синтезируем его.
            if (AudioEngine.sfxCache[effect] == null) {
                val pcmData =
                    withContext(Dispatchers.Default) {
                        cacheManager.getSfxPcm(effect)
                    }
                if (pcmData != null) {
                    val buffer = pcmDataToPcmBuffer(AudioEngine.audioFormat, pcmData)
                    AudioEngine.sfxCache[effect] = buffer
                }
            }

            // Теперь звук точно должен быть в кэше.
            AudioEngine.sfxCache[effect]?.let { buffer ->
                AudioEngine.sfxPlayerNode.scheduleBuffer(buffer, null)
            }
        }
    }

    /**
     * Retrieves or synthesizes music data and starts looped playback on the main thread.
     */
    override suspend fun playMusic(theme: MusicTheme) {
        // We need to be on the main thread for AVFoundation calls, but synthesis can be backgrounded.
        mainScope.launch {
            AudioEngine.musicPlayerNode.stop()
            if (!AudioEngine.currentSettings.musicEnabled || theme == MusicTheme.NONE) return@launch

            // getOrSynthesizeMusicPcm is a suspend function that handles its own threading.
            val pcmData = cacheManager.getOrSynthesizeMusicPcm(theme) ?: return@launch
            if (pcmData.isEmpty()) return@launch

            val musicBuffer = pcmDataToPcmBuffer(AudioEngine.audioFormat, pcmData)

            AudioEngine.musicPlayerNode.play()

            fun playLoop() {
                // The completion handler might be called on a different thread,
                // so we re-dispatch to the mainScope to ensure thread safety.
                mainScope.launch {
                    if (AudioEngine.musicPlayerNode.isPlaying()) {
                        AudioEngine.musicPlayerNode.scheduleBuffer(musicBuffer, completionHandler = { playLoop() })
                    }
                }
            }
            playLoop()
        }
    }

    /**
     * Asynchronously stops music playback on the main thread.
     */
    override fun stopMusic() {
        mainScope.launch {
            AudioEngine.musicPlayerNode.stop()
        }
    }

    /**
     * Asynchronously stops all sounds, clears caches, and cancels all coroutines.
     */
    override suspend fun release() {
        mainScope
            .launch {
                AudioEngine.musicPlayerNode.stop()
                AudioEngine.engine.stop()
                AudioEngine.sfxCache.clear()
            }.join() // Wait for the cleanup to finish
        cacheManager.clear()
        mainScope.cancel()
    }

    /**
     * Helper function to fill an AVAudioPCMBuffer with data from a FloatArray.
     * This function performs memory operations and should be used carefully.
     */
    private fun fillPcmBuffer(
        buffer: AVAudioPCMBuffer,
        pcmData: FloatArray,
    ) {
        buffer.frameLength = pcmData.size.toUInt()
        val channelData = buffer.floatChannelData?.get(0)
        if (channelData != null) {
            pcmData.usePinned { pinned ->
                memcpy(
                    channelData,
                    pinned.addressOf(0),
                    (pcmData.size * Float.SIZE_BYTES).convert()
                )
            }
        }
    }

    /**
     * Helper function to convert a Kotlin FloatArray into a new AVAudioPCMBuffer.
     */
    private fun pcmDataToPcmBuffer(
        format: AVAudioFormat,
        pcmData: FloatArray,
    ): AVAudioPCMBuffer {
        val buffer = AVAudioPCMBuffer(pCMFormat = format, frameCapacity = pcmData.size.toUInt())
        fillPcmBuffer(buffer, pcmData)
        return buffer
    }
}
