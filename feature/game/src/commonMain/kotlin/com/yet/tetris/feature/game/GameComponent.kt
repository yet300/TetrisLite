package com.yet.tetris.feature.game

import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.value.Value
import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.settings.GameSettings

@OptIn(ExperimentalDecomposeApi::class)
interface GameComponent  {
    val model: Value<Model>

    sealed interface Model {
        data object Loading : Model
        data class Playing(
            val gameState: GameState,
            val settings: GameSettings,
            val isPaused: Boolean,
            val elapsedTime: Long
        ) : Model
        data class GameOver(
            val finalScore: Int,
            val linesCleared: Int
        ) : Model
    }
    fun onPause()
    fun onResume()
    fun onQuit()
    fun onMoveLeft()
    fun onMoveRight()
    fun onMoveDown()
    fun onRotate()
    fun onHardDrop()
    fun onSwipe(deltaX: Float, deltaY: Float, velocityX: Float, velocityY: Float)

}
