package com.yet.tetris.data.music

import com.yet.tetris.data.mapper.getParamsForEffect
import com.yet.tetris.data.mapper.getSequenceForTheme
import com.yet.tetris.domain.model.audio.MusicTheme
import com.yet.tetris.domain.model.audio.SoundEffect
import com.yet.tetris.domain.model.audio.WaveformType
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class AudioCacheManager {
    private val sfxPcmCache = mutableMapOf<SoundEffect, FloatArray>()
    private val musicPcmCache = mutableMapOf<MusicTheme, FloatArray>()
    private val cacheMutex = Mutex()

    /**
     * Pre-synthesizes and caches all sound effects.
     * Should be called once on startup.
     */
    suspend fun preheatSfxCache() {
        val synthesizedSfx = mutableMapOf<SoundEffect, FloatArray>()
        SoundEffect.entries.forEach { effect ->
            getParamsForEffect(effect)?.let { params ->
                synthesizedSfx[effect] = AudioSynthesizer.synthesizeSoundEffect(params)
            }
        }
        cacheMutex.withLock {
            sfxPcmCache.putAll(synthesizedSfx)
        }
    }

    /**
     * Retrieves a sound effect from the cache.
     */
    suspend fun getSfxPcm(effect: SoundEffect): FloatArray? = cacheMutex.withLock { sfxPcmCache[effect] }

    /**
     * Retrieves a music track from the cache. If not present,
     * synthesizes and caches it before returning.
     */
    suspend fun getOrSynthesizeMusicPcm(theme: MusicTheme): FloatArray? {
        musicPcmCache[theme]?.let { return it }

        return cacheMutex.withLock {
            musicPcmCache.getOrPut(theme) {
                val sequence = getSequenceForTheme(theme) ?: return@getOrPut FloatArray(0)
                val waveform = if (theme == MusicTheme.CLASSIC) WaveformType.SQUARE else WaveformType.TRIANGLE
                AudioSynthesizer.synthesizeMusicSequence(sequence, waveform)
            }
        }
    }

    suspend fun clear() {
        cacheMutex.withLock {
            sfxPcmCache.clear()
            musicPcmCache.clear()
        }
    }
}
