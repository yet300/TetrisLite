package com.yet.tetris.feature.settings

import com.arkivanov.decompose.value.Value
import com.yet.tetris.domain.model.audio.MusicTheme
import com.yet.tetris.domain.model.game.Difficulty
import com.yet.tetris.domain.model.game.TetrominoType
import com.yet.tetris.domain.model.settings.GameSettings
import com.yet.tetris.domain.model.settings.KeyboardLayout
import com.yet.tetris.domain.model.settings.SwipeLayout
import com.yet.tetris.domain.model.settings.SwipeSensitivity

interface SettingsComponent {
    val model: Value<Model>

    data class Model(
        val settings: GameSettings,
        val isSaving: Boolean,
        val hasUnsavedChanges: Boolean
    )

    fun onDifficultyChanged(difficulty: Difficulty)
    fun onTetrominoColorChanged(type: TetrominoType, color: String)
    fun onBackgroundColorChanged(color: String)
    fun onKeyboardLayoutChanged(layout: KeyboardLayout)
    fun onSwipeLayoutChanged(layout: SwipeLayout)
    fun onSwipeSensitivityChanged(sensitivity: SwipeSensitivity)
    fun onMusicToggled(enabled: Boolean)
    fun onSoundEffectsToggled(enabled: Boolean)
    fun onMusicVolumeChanged(volume: Float)
    fun onSFXVolumeChanged(volume: Float)
    fun onMusicThemeChanged(theme: MusicTheme)
    fun onSave()
    fun onDiscard()

}

