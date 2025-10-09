package com.yet.tetris.feature.game

import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.value.Value
import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.settings.GameSettings

@OptIn(ExperimentalDecomposeApi::class)
interface GameComponent  {
    val model: Value<Model>

    data class Model(
        val isLoading: Boolean = true,
        val gameState: GameState? = null,
        val settings: GameSettings = GameSettings(),
        val isPaused: Boolean = false,
        val elapsedTime: Long = 0,
        val isGameOver: Boolean = false,
        val finalScore: Int = 0,
        val finalLinesCleared: Int = 0,
        val ghostPieceY: Int? = null
    )
    
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
}
