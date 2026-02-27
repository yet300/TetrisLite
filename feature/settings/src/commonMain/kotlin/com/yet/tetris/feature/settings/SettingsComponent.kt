package com.yet.tetris.feature.settings

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.yet.tetris.domain.model.audio.MusicTheme
import com.yet.tetris.domain.model.game.Difficulty
import com.yet.tetris.domain.model.settings.GameSettings
import com.yet.tetris.domain.model.theme.PieceStyle
import com.yet.tetris.domain.model.theme.VisualTheme

interface SettingsComponent {
    val model: Value<Model>

    data class Model(
        val settings: GameSettings,
        val isSaving: Boolean,
    )

    fun onDifficultyChanged(difficulty: Difficulty)

    fun onVisualThemeChanged(theme: VisualTheme)

    fun onPieceStyleChanged(style: PieceStyle)

    fun onMusicToggled(enabled: Boolean)

    fun onSoundEffectsToggled(enabled: Boolean)

    fun onMusicVolumeChanged(volume: Float)

    fun onSFXVolumeChanged(volume: Float)

    fun onMusicThemeChanged(theme: MusicTheme)

    fun onClose()

    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onCloseRequest: () -> Unit,
        ): SettingsComponent
    }
}
