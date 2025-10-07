package com.yet.tetris.feature.game


import com.app.common.decompose.asValue
import com.app.common.decompose.coroutineScope
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.yet.tetris.feature.game.store.GameStore
import com.yet.tetris.feature.game.store.GameStoreFactory
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent


class DefaultGameComponent(
    componentContext: ComponentContext,
    private val navigateBack: () -> Unit,
) : ComponentContext by componentContext, GameComponent, KoinComponent {

    private val store = instanceKeeper.getStore { GameStoreFactory().create() }


    init {
        coroutineScope().launch {
            // Handle labels
            store.labels.collect {
                when (it) {
                    is GameStore.Label.GameOver -> {
                        // Game over handled in model mapping
                    }
                    is GameStore.Label.NavigateBack -> navigateBack()
                    is GameStore.Label.ShowError -> {
                        // Handle error
                    }
                }
            }
        }
    }

    override val model: Value<GameComponent.Model> = store.asValue().map { state ->
        when {
            state.isLoading || state.gameState == null -> GameComponent.Model.Loading
            state.gameState.isGameOver -> GameComponent.Model.GameOver(
                finalScore = state.gameState.score,
                linesCleared = state.gameState.linesCleared
            )
            else -> GameComponent.Model.Playing(
                gameState = state.gameState,
                settings = state.settings,
                isPaused = state.isPaused,
                elapsedTime = state.elapsedTime
            )
        }
    }

    override fun onPause() {
        store.accept(GameStore.Intent.PauseGame)
    }

    override fun onResume() {
        store.accept(GameStore.Intent.ResumeGame)
    }

    override fun onQuit() {
        store.accept(GameStore.Intent.QuitGame)
    }

    override fun onMoveLeft() {
        store.accept(GameStore.Intent.MoveLeft)
    }

    override fun onMoveRight() {
        store.accept(GameStore.Intent.MoveRight)
    }

    override fun onMoveDown() {
        store.accept(GameStore.Intent.MoveDown)
    }

    override fun onRotate() {
        store.accept(GameStore.Intent.Rotate)
    }

    override fun onHardDrop() {
        store.accept(GameStore.Intent.HardDrop)
    }

    override fun onSwipe(deltaX: Float, deltaY: Float, velocityX: Float, velocityY: Float) {
        store.accept(GameStore.Intent.HandleSwipe(deltaX, deltaY, velocityX, velocityY))
    }
}