package com.yet.tetris.domain.usecase

import com.yet.tetris.domain.model.game.GameBoard
import com.yet.tetris.domain.model.game.Position
import com.yet.tetris.domain.model.game.Tetromino
import jakarta.inject.Singleton

/**
 * Use case for checking if a tetromino collides with the board boundaries or locked blocks.
 * This is a core piece of game logic used by movement, rotation, and spawning operations.
 */
@Singleton
class CheckCollisionUseCase {
    /**
     * Checks if placing a tetromino at the given position would result in a collision.
     *
     * @param board The current game board state
     * @param piece The tetromino to check
     * @param position The position where the piece would be placed
     * @return true if there is a collision, false if the position is valid
     */
    operator fun invoke(
        board: GameBoard,
        piece: Tetromino,
        position: Position,
    ): Boolean {
        val absolutePositions = piece.getAbsolutePositions(position)

        return absolutePositions.any { pos ->
            // Check boundary collisions
            !isWithinBounds(board, pos) ||
                // Check collisions with locked blocks
                board.isPositionOccupied(pos)
        }
    }

    /**
     * Checks if a position is within the board boundaries.
     */
    private fun isWithinBounds(
        board: GameBoard,
        position: Position,
    ): Boolean =
        position.x >= 0 &&
            position.x < board.width &&
            position.y >= 0 &&
            position.y < board.height

    /**
     * Checks if a specific position is valid (within bounds and not occupied).
     */
    fun isPositionValid(
        board: GameBoard,
        position: Position,
    ): Boolean = isWithinBounds(board, position) && !board.isPositionOccupied(position)

    /**
     * Checks if a tetromino can be placed at the given position.
     * This is the inverse of the collision check.
     */
    fun canPlacePiece(
        board: GameBoard,
        piece: Tetromino,
        position: Position,
    ): Boolean = !invoke(board, piece, position)
}
