package com.yet.tetris.feature.home.store

import com.arkivanov.mvikotlin.core.store.Store
import com.yet.tetris.domain.model.game.Difficulty
import com.yet.tetris.domain.model.settings.GameSettings

internal interface HomeStore : Store<HomeStore.Intent, HomeStore.State, HomeStore.Label> {

    data class State(
        val settings: GameSettings = GameSettings(),
        val hasSavedGame: Boolean = false,
        val isLoading: Boolean = false
    )

    sealed class Intent {
        data object StartNewGame : Intent()
        data object ResumeGame : Intent()
        data class ChangeDifficulty(val difficulty: Difficulty) : Intent()
    }

    sealed class Msg {
        data class SettingsLoaded(val settings: GameSettings) : Msg()
        data class SavedGameStateChanged(val hasSavedGame: Boolean) : Msg()
        data class DifficultyChanged(val difficulty: Difficulty) : Msg()
        data class LoadingChanged(val isLoading: Boolean) : Msg()
    }

    sealed class Action {
        data object HomeLoadStarted : Action()
    }


    sealed class Label {
        data object NavigateToGame : Label()
        data class ShowError(val message: String) : Label()
    }
}
