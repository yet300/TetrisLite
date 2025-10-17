package com.yet.tetris.data.repository

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.yet.tetris.data.music.AudioCacheManager
import com.yet.tetris.data.music.AudioSynthesizer
import com.yet.tetris.domain.model.audio.AudioSettings
import com.yet.tetris.domain.model.audio.MusicTheme
import com.yet.tetris.domain.model.audio.SoundEffect
import com.yet.tetris.domain.repository.AudioRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.min

/**
 * Android-specific implementation of the AudioRepository using the low-level AudioTrack API.
 * This is suitable for playing procedurally generated PCM data with low latency.
 */
class AndroidAudioRepositoryImpl(
    private val cacheManager: AudioCacheManager,
) : AudioRepository {
    private val audioScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var musicTrack: AudioTrack? = null
    private var isMusicPlaying = false
    private var currentSettings = AudioSettings()

    // Platform-specific audio configuration objects.
    private val audioFormat =
        AudioFormat
            .Builder()
            .setEncoding(AudioFormat.ENCODING_PCM_FLOAT)
            .setSampleRate(AudioSynthesizer.SAMPLE_RATE)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()
    private val audioAttributes =
        AudioAttributes
            .Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

    /**
     * Delegates the pre-caching of sound effects to the cache manager.
     */
    override suspend fun initialize() {
        cacheManager.preheatSfxCache()
    }

    /**
     * Applies the given audio settings and updates the volume of any active music.
     */
    override fun applySettings(settings: AudioSettings) {
        this.currentSettings = settings
        // If music is already playing, update its volume in real-time.
        musicTrack?.setVolume(settings.musicVolume)
    }

    /**
     * Asynchronously retrieves PCM data from the cache and plays it as a "fire-and-forget" sound.
     */
    override fun playSoundEffect(effect: SoundEffect) {
        if (!currentSettings.soundEffectsEnabled) return

        audioScope.launch {
            cacheManager.getSfxPcm(effect)?.let { pcmData ->
                val bufferSize = pcmData.size * Float.SIZE_BYTES
                val sfxTrack = AudioTrack(audioAttributes, audioFormat, bufferSize, AudioTrack.MODE_STATIC, 0)

                sfxTrack.setVolume(currentSettings.sfxVolume)

                sfxTrack.write(pcmData, 0, pcmData.size, AudioTrack.WRITE_BLOCKING)
                sfxTrack.play()

                val durationMs = (pcmData.size.toFloat() / AudioSynthesizer.SAMPLE_RATE * 1000).toLong()
                delay(durationMs)

                // Освобождаем ресурсы
                sfxTrack.stop()
                sfxTrack.release()
            }
        }
    }

    /**
     * Retrieves (or synthesizes) the music track PCM data and starts streaming playback.
     */
    override suspend fun playMusic(theme: MusicTheme) {
        stopMusic()
        if (!currentSettings.musicEnabled || theme == MusicTheme.NONE) return

        val pcmData = cacheManager.getOrSynthesizeMusicPcm(theme) ?: return
        if (pcmData.isEmpty()) return

        val bufferSize = AudioTrack.getMinBufferSize(audioFormat.sampleRate, audioFormat.channelMask, audioFormat.encoding)
        musicTrack = AudioTrack(audioAttributes, audioFormat, bufferSize, AudioTrack.MODE_STREAM, 0)
        musicTrack?.setVolume(currentSettings.musicVolume)

        isMusicPlaying = true
        musicTrack?.play()

        // Start a coroutine to stream the music data in a loop.
        audioScope.launch {
            var writePosition = 0
            while (isMusicPlaying) {
                val chunkSize = min(bufferSize / Float.SIZE_BYTES, pcmData.size - writePosition)
                if (chunkSize > 0) {
                    musicTrack?.write(pcmData, writePosition, chunkSize, AudioTrack.WRITE_BLOCKING)
                    writePosition += chunkSize
                }

                // Loop the track when it reaches the end.
                if (writePosition >= pcmData.size) {
                    writePosition = 0
                }
            }
        }
    }

    /**
     * Stops the currently playing music track and releases its resources.
     */
    override fun stopMusic() {
        if (!isMusicPlaying) return
        isMusicPlaying = false
        musicTrack?.pause()
        musicTrack?.flush()
        musicTrack?.release()
        musicTrack = null
    }

    /**
     * Stops all sounds, clears caches via the manager, and cancels all coroutines.
     */
    override suspend fun release() {
        stopMusic()
        cacheManager.clear()
        audioScope.cancel()
    }
}
