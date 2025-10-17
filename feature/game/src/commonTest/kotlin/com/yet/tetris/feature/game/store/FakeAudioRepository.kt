package com.yet.tetris.feature.game.store

import com.yet.tetris.domain.model.audio.AudioSettings
import com.yet.tetris.domain.model.audio.MusicTheme
import com.yet.tetris.domain.model.audio.SoundEffect
import com.yet.tetris.domain.repository.AudioRepository

internal class FakeAudioRepository : AudioRepository {
    var initializeCallCount = 0
        private set

    var playMusicCallCount = 0
        private set

    var playSoundEffectCallCount = 0
        private set

    var stopMusicCallCount = 0
        private set

    var applySettingsCallCount = 0
        private set

    var releaseCallCount = 0
        private set

    var lastMusicTheme: MusicTheme? = null
        private set

    var lastSoundEffect: SoundEffect? = null
        private set

    var lastAppliedSettings: AudioSettings? = null
        private set

    val soundEffectsPlayed = mutableListOf<SoundEffect>()

    override suspend fun initialize() {
        initializeCallCount++
    }

    override suspend fun playMusic(theme: MusicTheme) {
        playMusicCallCount++
        lastMusicTheme = theme
    }

    override fun playSoundEffect(effect: SoundEffect) {
        playSoundEffectCallCount++
        lastSoundEffect = effect
        soundEffectsPlayed.add(effect)
    }

    override fun stopMusic() {
        stopMusicCallCount++
    }

    override fun applySettings(settings: AudioSettings) {
        applySettingsCallCount++
        lastAppliedSettings = settings
    }

    override suspend fun release() {
        releaseCallCount++
    }
}
