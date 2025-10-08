package com.yet.tetris.feature.game.store

import com.arkivanov.mvikotlin.core.store.Store
import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.settings.GameSettings

internal interface GameStore : Store<GameStore.Intent, GameStore.State, GameStore.Label> {

    data class State(
        val gameState: GameState? = null,
        val settings: GameSettings = GameSettings(),
        val isPaused: Boolean = false,
        val elapsedTime: Long = 0,  // milliseconds
        val isLoading: Boolean = false,
        val ghostPieceY: Int? = null
    )

    sealed class Intent {
        data object PauseGame : Intent()
        data object ResumeGame : Intent()
        data object QuitGame : Intent()
        data object MoveLeft : Intent()
        data object MoveRight : Intent()
        data object MoveDown : Intent()
        data object Rotate : Intent()
        data object HardDrop : Intent()
        data class HandleSwipe(
            val deltaX: Float,
            val deltaY: Float,
            val velocityX: Float,
            val velocityY: Float
        ) : Intent()
    }

    sealed interface Action {
        data object GameLoadStarted : Action
    }

    sealed class Msg {
        data class GameInitialized(val gameState: GameState, val settings: GameSettings) : Msg()
        data class GameStateUpdated(val gameState: GameState, val ghostPieceY: Int?) : Msg()
        data class PausedChanged(val isPaused: Boolean) : Msg()
        data class ElapsedTimeUpdated(val elapsedTime: Long) : Msg()
        data class LoadingChanged(val isLoading: Boolean) : Msg()
    }


    sealed class Label {
        data class ShowError(val message: String) : Label()
        data object GameOver : Label()
        data object NavigateBack : Label()
    }
}
