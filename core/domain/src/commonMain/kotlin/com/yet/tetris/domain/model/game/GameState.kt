package com.yet.tetris.domain.model.game

data class GameState(
    val board: GameBoard,
    val currentPiece: Tetromino?,
    val currentPosition: Position,
    val nextPiece: Tetromino,
    val score: Long = 0L,
    val linesCleared: Long = 0,
    val isGameOver: Boolean = false,
    val isPaused: Boolean = false,
)
