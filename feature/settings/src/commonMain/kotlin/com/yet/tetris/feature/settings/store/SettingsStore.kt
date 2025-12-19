package com.yet.tetris.feature.settings.store

import com.arkivanov.mvikotlin.core.store.Store
import com.yet.tetris.domain.model.audio.MusicTheme
import com.yet.tetris.domain.model.game.Difficulty
import com.yet.tetris.domain.model.settings.GameSettings
import com.yet.tetris.domain.model.settings.KeyboardLayout
import com.yet.tetris.domain.model.settings.SwipeLayout
import com.yet.tetris.domain.model.settings.SwipeSensitivity

internal interface SettingsStore : Store<SettingsStore.Intent, SettingsStore.State, SettingsStore.Label> {
    data class State(
        val settings: GameSettings = GameSettings(),
        val isSaving: Boolean = false,
    )

    sealed class Intent {
        data class ChangeDifficulty(
            val difficulty: Difficulty,
        ) : Intent()

        data class ChangeVisualTheme(
            val theme: com.yet.tetris.domain.model.theme.VisualTheme,
        ) : Intent()

        data class ChangePieceStyle(
            val style: com.yet.tetris.domain.model.theme.PieceStyle,
        ) : Intent()

        data class ChangeKeyboardLayout(
            val layout: KeyboardLayout,
        ) : Intent()

        data class ChangeSwipeLayout(
            val layout: SwipeLayout,
        ) : Intent()

        data class ChangeSwipeSensitivity(
            val sensitivity: SwipeSensitivity,
        ) : Intent()

        data class ToggleMusic(
            val enabled: Boolean,
        ) : Intent()

        data class ToggleSoundEffects(
            val enabled: Boolean,
        ) : Intent()

        data class ChangeMusicVolume(
            val volume: Float,
        ) : Intent()

        data class ChangeSFXVolume(
            val volume: Float,
        ) : Intent()

        data class ChangeMusicTheme(
            val theme: MusicTheme,
        ) : Intent()
    }

    sealed interface Action {
        object SettingsLoadStarted : Action
    }

    sealed class Msg {
        data class SettingsLoaded(
            val settings: GameSettings,
        ) : Msg()

        data class SettingsUpdated(
            val settings: GameSettings,
        ) : Msg()

        data class SavingChanged(
            val isSaving: Boolean,
        ) : Msg()
    }

    sealed class Label {
        data class ShowError(
            val message: String,
        ) : Label()
    }
}
