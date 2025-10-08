package com.yet.tetris.feature.settings

import com.app.common.decompose.PreviewComponentContext
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.webhistory.WebNavigationOwner
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.yet.tetris.domain.model.audio.MusicTheme
import com.yet.tetris.domain.model.game.Difficulty
import com.yet.tetris.domain.model.game.TetrominoType
import com.yet.tetris.domain.model.settings.GameSettings
import com.yet.tetris.domain.model.settings.KeyboardLayout
import com.yet.tetris.domain.model.settings.SwipeLayout
import com.yet.tetris.domain.model.settings.SwipeSensitivity
import com.yet.tetris.domain.model.theme.PieceStyle
import com.yet.tetris.domain.model.theme.VisualTheme

@OptIn(ExperimentalDecomposeApi::class)
class PreviewSettingsComponent :
    SettingsComponent,
    ComponentContext by PreviewComponentContext, WebNavigationOwner.NoOp {
    override val model: Value<SettingsComponent.Model> =
        MutableValue(
            SettingsComponent.Model(
                settings = GameSettings(),
                isSaving = false,
                hasUnsavedChanges = false
            )
        )


    override fun onDifficultyChanged(difficulty: Difficulty) {
        TODO("Not yet implemented")
    }

    override fun onVisualThemeChanged(theme: VisualTheme) {
        TODO("Not yet implemented")
    }

    override fun onPieceStyleChanged(style: PieceStyle) {
        TODO("Not yet implemented")
    }

    override fun onKeyboardLayoutChanged(layout: KeyboardLayout) {
        TODO("Not yet implemented")
    }

    override fun onSwipeLayoutChanged(layout: SwipeLayout) {
        TODO("Not yet implemented")
    }

    override fun onSwipeSensitivityChanged(sensitivity: SwipeSensitivity) {
        TODO("Not yet implemented")
    }

    override fun onMusicToggled(enabled: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onSoundEffectsToggled(enabled: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onMusicVolumeChanged(volume: Float) {
        TODO("Not yet implemented")
    }

    override fun onSFXVolumeChanged(volume: Float) {
        TODO("Not yet implemented")
    }

    override fun onMusicThemeChanged(theme: MusicTheme) {
        TODO("Not yet implemented")
    }

    override fun onSave() {
        TODO("Not yet implemented")
    }

    override fun onDiscard() {
        TODO("Not yet implemented")
    }


}