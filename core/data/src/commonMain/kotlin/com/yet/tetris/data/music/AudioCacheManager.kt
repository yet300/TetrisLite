package com.yet.tetris.data.music

import com.yet.tetris.data.mapper.getParamsForEffect
import com.yet.tetris.data.mapper.getSequenceForTheme
import com.yet.tetris.data.mapper.getWaveformForTheme
import com.yet.tetris.domain.model.audio.MusicTheme
import com.yet.tetris.domain.model.audio.SoundEffect
import com.app.common.AppDispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class AudioCacheManager(
    private val dispatchers: AppDispatchers,
) {
    private val sfxPcmCache = mutableMapOf<SoundEffect, FloatArray>()
    private val musicPcmCache = mutableMapOf<MusicTheme, FloatArray>()
    private val cacheMutex = Mutex()

    /**
     * Pre-synthesizes and caches all sound effects.
     * Should be called once on startup.
     */
    suspend fun preheatSfxCache() = withContext(dispatchers.default) {
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
    suspend fun getOrSynthesizeMusicPcm(theme: MusicTheme): FloatArray? = withContext(dispatchers.default) {
        cacheMutex.withLock {
            musicPcmCache[theme]?.let { return@withContext it }
        }

        val sequence = getSequenceForTheme(theme) ?: return@withContext FloatArray(0)
        val waveform = getWaveformForTheme(theme)
        val synthesized = AudioSynthesizer.synthesizeMusicSequence(sequence, waveform)

        cacheMutex.withLock {
            musicPcmCache.getOrPut(theme) { synthesized }
        }
    }

    suspend fun clear() {
        cacheMutex.withLock {
            sfxPcmCache.clear()
            musicPcmCache.clear()
        }
    }
}
