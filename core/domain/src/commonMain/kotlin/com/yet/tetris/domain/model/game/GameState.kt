package com.yet.tetris.domain.model.game

data class GameState(
    val board: GameBoard,
    val currentPiece: Tetromino?,
    val currentPosition: Position,
    val nextPiece: Tetromino,
    val nextQueue: List<Tetromino> = emptyList(),
    val holdPiece: Tetromino? = null,
    val canHold: Boolean = true,
    val score: Long = 0L,
    val linesCleared: Long = 0,
    val level: Int = 1,
    val isGameOver: Boolean = false,
    val isPaused: Boolean = false,
) {
    companion object {
        const val PREVIEW_PIECES_COUNT = 5
        const val QUEUE_SIZE = PREVIEW_PIECES_COUNT - 1
    }

    val previewPieces: List<Tetromino>
        get() = buildList {
            add(nextPiece)
            addAll(nextQueue)
        }
}
