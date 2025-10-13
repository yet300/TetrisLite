package com.yet.tetris.data.repository

import com.yet.tetris.data.music.AudioCacheManager
import com.yet.tetris.data.music.AudioSynthesizer
import com.yet.tetris.domain.model.audio.AudioSettings
import com.yet.tetris.domain.model.audio.MusicTheme
import com.yet.tetris.domain.model.audio.SoundEffect
import com.yet.tetris.domain.repository.AudioRepository
import js.buffer.ArrayBuffer
import js.typedarrays.Float32Array
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import web.audio.AudioBuffer
import web.audio.AudioBufferSourceNode
import web.audio.AudioContext
import web.audio.AudioContextState
import web.audio.GainNode
import web.audio.close
import web.audio.resume
import web.audio.suspended


/**
 * JavaScript-specific implementation of the AudioRepository using the Web Audio API.
 * This implementation is fully asynchronous and runs in the browser.
 */
class JsAudioRepositoryImpl(
    private val cacheManager: AudioCacheManager
) : AudioRepository {

    // A lazy-initialized AudioContext. Browsers require a user interaction to start it.
    private val audioContext: AudioContext by lazy { AudioContext() }

    // Master gain nodes to control SFX and Music volume independently.
    private val sfxGain: GainNode by lazy { audioContext.createGain().also { it.connect(audioContext.destination) } }
    private val musicGain: GainNode by lazy { audioContext.createGain().also { it.connect(audioContext.destination) } }

    private val audioScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    // Platform-specific cache for ready-to-play AudioBuffers.
    private val sfxBufferCache = mutableMapOf<SoundEffect, AudioBuffer>()

    private var currentMusicSource: AudioBufferSourceNode? = null
    private var currentSettings = AudioSettings()

    /**
     * Pre-heats the PCM cache via the manager, then converts the data into
     * platform-specific AudioBuffers for efficient playback.
     */
    override suspend fun initialize() {
        cacheManager.preheatSfxCache()
        // Convert all pre-synthesized PCM data into AudioBuffers
        SoundEffect.entries.forEach { effect ->
            cacheManager.getSfxPcm(effect)?.let { pcmData ->
                sfxBufferCache[effect] = pcmDataToAudioBuffer(pcmData)
            }
        }
    }

    /**
     * Applies the given audio settings by updating the gain values on the central GainNodes.
     */
    override fun applySettings(settings: AudioSettings) {
        this.currentSettings = settings
        sfxGain.gain.value = if (settings.soundEffectsEnabled) settings.sfxVolume else 0.0f
        musicGain.gain.value = if (settings.musicEnabled) settings.musicVolume else 0.0f
    }

    /**
     * Plays a sound effect from the pre-converted AudioBuffer cache.
     * This is a "fire-and-forget" operation.
     */
    override fun playSoundEffect(effect: SoundEffect) {
        if (!currentSettings.soundEffectsEnabled) return

        audioScope.launch {
            if (sfxBufferCache[effect] == null) {
                cacheManager.getSfxPcm(effect)?.let { pcmData ->
                    sfxBufferCache[effect] = pcmDataToAudioBuffer(pcmData)
                }
            }

            val audioBuffer = sfxBufferCache[effect] ?: return@launch

            if (audioContext.state == AudioContextState.suspended) {
                audioContext.resume()
            }
            val source = audioContext.createBufferSource()
            source.buffer = audioBuffer
            source.connect(sfxGain)
            source.start()
        }
    }

    /**
     * Retrieves (or synthesizes) the music track PCM data, converts it to an
     * AudioBuffer, and starts looped playback.
     */
    override suspend fun playMusic(theme: MusicTheme) {
        stopMusic()
        if (!currentSettings.musicEnabled || theme == MusicTheme.NONE) return

        val pcmData = cacheManager.getOrSynthesizeMusicPcm(theme) ?: return
        if (pcmData.isEmpty()) return

        val audioBuffer = pcmDataToAudioBuffer(pcmData)

        if (audioContext.state == AudioContextState.suspended) {
            audioContext.resume()
        }
        val source = audioContext.createBufferSource()
        source.buffer = audioBuffer
        source.loop = true
        source.connect(musicGain)
        source.start()
        currentMusicSource = source
    }

    /**
     * Stops the currently playing music source node.
     */
    override fun stopMusic() {
        currentMusicSource?.stop()
        currentMusicSource?.disconnect()
        currentMusicSource = null
    }

    /**
     * Stops all sounds, clears caches, and releases the AudioContext.
     */
    override suspend fun release() {
        stopMusic()
        cacheManager.clear()
        sfxBufferCache.clear()
        audioContext.close()
        audioScope.cancel()
    }

    /**
     * Helper function to convert a Kotlin FloatArray into a Web Audio API AudioBuffer.
     * This is the bridge between the common KMP world and the JS-specific audio engine.
     */
    private fun pcmDataToAudioBuffer(pcmData: FloatArray): AudioBuffer {
        val buffer = audioContext.createBuffer(
            1, // number of channels (mono)
            pcmData.size, // number of frames
            AudioSynthesizer.SAMPLE_RATE.toFloat() // sample rate
        )
        // This cast is the most direct and performant way to pass the data.
        buffer.copyToChannel(pcmData.unsafeCast<Float32Array<ArrayBuffer>>(), 0)
        return buffer
    }
}