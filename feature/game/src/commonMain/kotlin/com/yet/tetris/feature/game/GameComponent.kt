package com.yet.tetris.feature.game

import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.settings.GameSettings

@OptIn(ExperimentalDecomposeApi::class)
interface GameComponent : BackHandlerOwner {
    val model: Value<Model>

    val childSlot: Value<ChildSlot<*, DialogChild>>

    data class Model(
        val isLoading: Boolean = true,
        val gameState: GameState? = null,
        val settings: GameSettings = GameSettings(),
        val elapsedTime: Long = 0,
        val isGameOver: Boolean = false,
        val finalScore: Long = 0,
        val finalLinesCleared: Long = 0,
        val ghostPieceY: Int? = null
    )

    fun onDismissSheet()
    fun onBackClick()
    fun onRetry()

    fun onPause()
    fun onResume()
    fun onQuit()
    fun onMoveLeft()
    fun onMoveRight()
    fun onMoveDown()
    fun onRotate()
    fun onHardDrop()
    fun onSwipe(deltaX: Float, deltaY: Float, velocityX: Float, velocityY: Float)

    fun onBoardSizeChanged(height: Float)
    fun onDragStarted()
    fun onDragged(deltaX: Float, deltaY: Float)
    fun onDragEnded()

    sealed interface DialogChild {
        class Pause : DialogChild
        class GameOver : DialogChild

        class Error(val message: String) : DialogChild
    }

}
