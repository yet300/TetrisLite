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
    val piecesPlaced: Long = 0,
    val maxCombo: Int = 0,
    val tetrisesCleared: Long = 0,
    val tSpinClears: Long = 0,
    val perfectClears: Long = 0,
    val hardDrops: Long = 0,
    val hardDropCells: Long = 0,
    val softDropCells: Long = 0,
    val backToBackChain: Int = 0,
    val isTSpinEligible: Boolean = false,
    val isGameOver: Boolean = false,
    val isPaused: Boolean = false,
) {
    companion object {
        const val PREVIEW_PIECES_COUNT = 5
        const val QUEUE_SIZE = PREVIEW_PIECES_COUNT - 1
    }

    val previewPieces: List<Tetromino>
        get() =
            buildList {
                add(nextPiece)
                addAll(nextQueue)
            }
}
