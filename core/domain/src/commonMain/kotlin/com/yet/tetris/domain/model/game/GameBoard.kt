package com.yet.tetris.domain.model.game

import kotlinx.serialization.Serializable

@Serializable
data class GameBoard(
    val width: Int = 10,
    val height: Int = 20,
    val cells: Map<Position, TetrominoType> = emptyMap()
) {
    fun isPositionOccupied(position: Position): Boolean {
        return cells.containsKey(position)
    }

    fun isPositionValid(position: Position): Boolean {
        return position.x in 0 until width && position.y >= 0 && position.y < height
    }

    fun lockPiece(piece: Tetromino, offset: Position): GameBoard {
        val newCells = cells.toMutableMap()
        piece.getAbsolutePositions(offset).forEach { pos ->
            newCells[pos] = piece.type
        }
        return copy(cells = newCells)
    }

    fun clearLines(): Pair<GameBoard, Int> {
        val completedLines = (0 until height).filter { y ->
            (0 until width).all { x -> cells.containsKey(Position(x, y)) }
        }

        if (completedLines.isEmpty()) {
            return this to 0
        }

        val newCells = mutableMapOf<Position, TetrominoType>()
        var targetY = height - 1

        for (y in (height - 1) downTo 0) {
            if (y !in completedLines) {
                for (x in 0 until width) {
                    val pos = Position(x, y)
                    cells[pos]?.let { type ->
                        newCells[Position(x, targetY)] = type
                    }
                }
                targetY--
            }
        }

        return copy(cells = newCells) to completedLines.size
    }
}
