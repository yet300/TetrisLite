package com.yet.tetris.domain.repository

import com.yet.tetris.domain.model.audio.AudioSettings
import com.yet.tetris.domain.model.audio.MusicTheme
import com.yet.tetris.domain.model.audio.SoundEffect

interface AudioRepository {

    fun initialize()

    fun playMusic(theme: MusicTheme)

    fun playSoundEffect(effect: SoundEffect)

    fun stopMusic()

    fun applySettings(settings: AudioSettings)

    fun release()
}