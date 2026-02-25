package com.yet.tetris.database.mapper

import com.yet.tetris.database.BoardCells
import com.yet.tetris.database.CurrentGameState
import com.yet.tetris.domain.model.game.GameBoard
import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.game.Position
import com.yet.tetris.domain.model.game.Tetromino
import com.yet.tetris.domain.model.game.TetrominoType

// Database entity to Domain
fun CurrentGameState.toDomain(boardCells: List<BoardCells>): GameState {
    // Reconstruct board
    val cells =
        boardCells.associate { cell ->
            Position(cell.positionX.toInt(), cell.positionY.toInt()) to cell.pieceType
        }
    val board =
        GameBoard(
            width = boardWidth.toInt(),
            height = boardHeight.toInt(),
            cells = cells,
        )

    // Reconstruct current piece (if exists)
    val currentPiece =
        currentPieceType?.let { type ->
            Tetromino.create(type, currentPieceRotation.toInt())
        }

    // Reconstruct next piece
    val nextPiece = Tetromino.create(nextPieceType, nextPieceRotation.toInt())

    return GameState(
        board = board,
        currentPiece = currentPiece,
        currentPosition = Position(currentPositionX.toInt(), currentPositionY.toInt()),
        nextPiece = nextPiece,
        score = score,
        linesCleared = linesCleared,
        level = level.toInt(),
        isGameOver = isGameOver,
        isPaused = isPaused,
    )
}

// Domain to Database entities
data class GameStateEntities(
    val gameState: CurrentGameStateData,
    val boardCells: List<BoardCells>,
)

data class CurrentGameStateData(
    val score: Long,
    val linesCleared: Long,
    val level: Long,
    val currentPieceType: TetrominoType?,
    val currentPieceRotation: Long,
    val currentPositionX: Long,
    val currentPositionY: Long,
    val nextPieceType: TetrominoType,
    val nextPieceRotation: Long,
    val isGameOver: Boolean,
    val isPaused: Boolean,
    val boardWidth: Long,
    val boardHeight: Long,
)

fun GameState.toEntities(): GameStateEntities {
    val boardCells =
        board.cells.map { (position, type) ->
            BoardCells(
                positionX = position.x.toLong(),
                positionY = position.y.toLong(),
                pieceType = type,
            )
        }

    val gameStateData =
        CurrentGameStateData(
            score = score,
            linesCleared = linesCleared,
            level = level.toLong(),
            currentPieceType = currentPiece?.type,
            currentPieceRotation = (currentPiece?.rotation ?: 0).toLong(),
            currentPositionX = currentPosition.x.toLong(),
            currentPositionY = currentPosition.y.toLong(),
            nextPieceType = nextPiece.type,
            nextPieceRotation = nextPiece.rotation.toLong(),
            isGameOver = isGameOver,
            isPaused = isPaused,
            boardWidth = board.width.toLong(),
            boardHeight = board.height.toLong(),
        )

    return GameStateEntities(gameStateData, boardCells)
}
