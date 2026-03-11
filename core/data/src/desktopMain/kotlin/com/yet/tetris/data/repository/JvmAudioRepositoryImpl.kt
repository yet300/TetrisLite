package com.yet.tetris.data.repository

import com.app.common.AppDispatchers
import com.yet.tetris.data.music.AudioCacheManager
import com.yet.tetris.data.music.AudioSynthesizer
import com.yet.tetris.domain.model.audio.AudioSettings
import com.yet.tetris.domain.model.audio.MusicTheme
import com.yet.tetris.domain.model.audio.SoundEffect
import com.yet.tetris.domain.repository.AudioRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.FloatControl
import javax.sound.sampled.SourceDataLine
import kotlin.math.log10
import kotlin.math.min

/**
 * JVM-specific implementation of the AudioRepository using the Java Sound API (javax.sound.sampled).
 * This class is responsible for playing procedurally generated audio on desktop platforms.
 */
class JvmAudioRepositoryImpl(
    private val cacheManager: AudioCacheManager,
    private val dispatchers: AppDispatchers,
) : AudioRepository {
    private val audioScope = CoroutineScope(dispatchers.io + SupervisorJob())
    private var musicLine: SourceDataLine? = null
    private var musicJob: Job? = null
    private var musicPlaybackId = 0L
    private var currentSettings = AudioSettings()

    // Platform-specific audio format.
    private val audioFormat =
        AudioFormat(
            AudioSynthesizer.SAMPLE_RATE.toFloat(),
            16, // 16-bit audio depth
            1, // mono channel
            true, // signed
            false, // little-endian
        )

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
        musicLine?.let { setLineVolume(it, settings.musicVolume) }
    }

    /**
     * Asynchronously retrieves PCM data and plays it as a "fire-and-forget" sound.
     * Playback runs on the audio scope so draining does not block the caller.
     */
    override fun playSoundEffect(effect: SoundEffect) {
        if (!currentSettings.soundEffectsEnabled) return

        audioScope.launch {
            cacheManager.getSfxPcm(effect)?.let { pcmData ->
                val byteData = floatPcmTo16Bit(pcmData)
                val sfxLine = AudioSystem.getSourceDataLine(audioFormat)
                sfxLine.open(audioFormat, byteData.size)

                setLineVolume(sfxLine, currentSettings.sfxVolume)
                sfxLine.start()
                sfxLine.write(byteData, 0, byteData.size)
                sfxLine.drain()
                sfxLine.stop()
                sfxLine.close()
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

        val playbackId = ++musicPlaybackId
        val line = AudioSystem.getSourceDataLine(audioFormat)
        line.open(audioFormat)
        musicLine = line
        musicJob =
            audioScope.launch {
                try {
                    if (musicLine !== line) {
                        return@launch
                    }

                    setLineVolume(line, currentSettings.musicVolume)
                    line.start()

                    var readPosition = 0
                    val bufferSizeInSamples = 4096
                    while (isActive && playbackId == musicPlaybackId) {
                        val remainingSamples = pcmData.size - readPosition
                        val samplesToWrite = min(bufferSizeInSamples, remainingSamples)
                        if (samplesToWrite > 0) {
                            val chunk = pcmData.copyOfRange(readPosition, readPosition + samplesToWrite)
                            val byteChunk = floatPcmTo16Bit(chunk)
                            line.write(byteChunk, 0, byteChunk.size)
                            readPosition += samplesToWrite
                        }
                        if (readPosition >= pcmData.size) {
                            readPosition = 0
                        }
                    }
                } finally {
                    if (musicLine === line) {
                        musicLine = null
                    }
                    runCatching { line.stop() }
                    runCatching { line.flush() }
                    runCatching { line.close() }
                }
            }
    }

    /**
     * Stops the currently playing music by breaking the streaming loop.
     */
    override fun stopMusic() {
        musicPlaybackId++
        musicJob?.cancel()
        musicJob = null
        musicLine?.let { line ->
            runCatching { line.stop() }
            runCatching { line.flush() }
        }
    }

    /**
     * Stops all sounds, clears caches via the manager, and cancels all coroutines.
     */
    override suspend fun release() {
        stopMusic()
        cacheManager.clear()
        audioScope.cancel()
    }

    /**
     * Converts a FloatArray of PCM data (-1.0 to 1.0) to a ByteArray in 16-bit little-endian format.
     */
    private fun floatPcmTo16Bit(data: FloatArray): ByteArray {
        val byteData = ByteArray(data.size * 2)
        for (i in data.indices) {
            val shortSample = (data[i] * Short.MAX_VALUE).toInt().toShort()
            byteData[i * 2] = shortSample.toByte()
            byteData[i * 2 + 1] = (shortSample.toInt() shr 8).toByte()
        }
        return byteData
    }

    /**
     * Sets the volume of a SourceDataLine by converting a linear scale (0.0-1.0) to decibels.
     */
    private fun setLineVolume(
        line: SourceDataLine,
        volume: Float,
    ) {
        if (line.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            val gainControl = line.getControl(FloatControl.Type.MASTER_GAIN) as FloatControl
            if (volume > 0.0001f) {
                val dB = 20.0f * log10(volume)
                gainControl.value = dB
            } else {
                gainControl.value = gainControl.minimum
            }
        }
    }
}
