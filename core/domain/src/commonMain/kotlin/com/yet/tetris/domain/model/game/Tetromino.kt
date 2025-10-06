package com.yet.tetris.domain.model.game

import kotlinx.serialization.Serializable

@Serializable
data class Tetromino(
    val type: TetrominoType,
    val blocks: List<Position>,
    val rotation: Int = 0
) {
    companion object {
        // Tetromino shape definitions for each rotation state (0-3)
        private val shapes = mapOf(
            TetrominoType.I to listOf(
                listOf(Position(0, 1), Position(1, 1), Position(2, 1), Position(3, 1)), // Rotation 0
                listOf(Position(2, 0), Position(2, 1), Position(2, 2), Position(2, 3)), // Rotation 1
                listOf(Position(0, 2), Position(1, 2), Position(2, 2), Position(3, 2)), // Rotation 2
                listOf(Position(1, 0), Position(1, 1), Position(1, 2), Position(1, 3))  // Rotation 3
            ),
            TetrominoType.O to listOf(
                listOf(Position(0, 0), Position(1, 0), Position(0, 1), Position(1, 1)), // No rotation
                listOf(Position(0, 0), Position(1, 0), Position(0, 1), Position(1, 1)),
                listOf(Position(0, 0), Position(1, 0), Position(0, 1), Position(1, 1)),
                listOf(Position(0, 0), Position(1, 0), Position(0, 1), Position(1, 1))
            ),
            TetrominoType.T to listOf(
                listOf(Position(1, 0), Position(0, 1), Position(1, 1), Position(2, 1)), // Rotation 0
                listOf(Position(1, 0), Position(1, 1), Position(2, 1), Position(1, 2)), // Rotation 1
                listOf(Position(0, 1), Position(1, 1), Position(2, 1), Position(1, 2)), // Rotation 2
                listOf(Position(1, 0), Position(0, 1), Position(1, 1), Position(1, 2))  // Rotation 3
            ),
            TetrominoType.S to listOf(
                listOf(Position(1, 0), Position(2, 0), Position(0, 1), Position(1, 1)), // Rotation 0
                listOf(Position(1, 0), Position(1, 1), Position(2, 1), Position(2, 2)), // Rotation 1
                listOf(Position(1, 1), Position(2, 1), Position(0, 2), Position(1, 2)), // Rotation 2
                listOf(Position(0, 0), Position(0, 1), Position(1, 1), Position(1, 2))  // Rotation 3
            ),
            TetrominoType.Z to listOf(
                listOf(Position(0, 0), Position(1, 0), Position(1, 1), Position(2, 1)), // Rotation 0
                listOf(Position(2, 0), Position(1, 1), Position(2, 1), Position(1, 2)), // Rotation 1
                listOf(Position(0, 1), Position(1, 1), Position(1, 2), Position(2, 2)), // Rotation 2
                listOf(Position(1, 0), Position(0, 1), Position(1, 1), Position(0, 2))  // Rotation 3
            ),
            TetrominoType.J to listOf(
                listOf(Position(0, 0), Position(0, 1), Position(1, 1), Position(2, 1)), // Rotation 0
                listOf(Position(1, 0), Position(2, 0), Position(1, 1), Position(1, 2)), // Rotation 1
                listOf(Position(0, 1), Position(1, 1), Position(2, 1), Position(2, 2)), // Rotation 2
                listOf(Position(1, 0), Position(1, 1), Position(0, 2), Position(1, 2))  // Rotation 3
            ),
            TetrominoType.L to listOf(
                listOf(Position(2, 0), Position(0, 1), Position(1, 1), Position(2, 1)), // Rotation 0
                listOf(Position(1, 0), Position(1, 1), Position(1, 2), Position(2, 2)), // Rotation 1
                listOf(Position(0, 1), Position(1, 1), Position(2, 1), Position(0, 2)), // Rotation 2
                listOf(Position(0, 0), Position(1, 0), Position(1, 1), Position(1, 2))  // Rotation 3
            )
        )

        fun create(type: TetrominoType, rotation: Int = 0): Tetromino {
            val blocks = shapes[type]?.get(rotation % 4) ?: emptyList()
            return Tetromino(type, blocks, rotation % 4)
        }
    }

    fun rotate(): Tetromino {
        val newRotation = (rotation + 1) % 4
        return create(type, newRotation)
    }

    fun getAbsolutePositions(offset: Position): List<Position> {
        return blocks.map { it + offset }
    }
}
