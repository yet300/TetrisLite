package com.yet.tetris.domain.model.game

import kotlinx.serialization.Serializable

@Serializable
data class GameState(
    val board: GameBoard,
    val currentPiece: Tetromino?,
    val currentPosition: Position,
    val nextPiece: Tetromino,
    val score: Int = 0,
    val linesCleared: Int = 0,
    val isGameOver: Boolean = false,
    val isPaused: Boolean = false
)
