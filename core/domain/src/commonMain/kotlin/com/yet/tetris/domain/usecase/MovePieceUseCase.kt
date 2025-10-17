package com.yet.tetris.domain.usecase

import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.game.Position
import jakarta.inject.Singleton

/**
 * Use case for moving the current tetromino in different directions.
 * Validates moves using collision detection before applying them.
 */
@Singleton
class MovePieceUseCase(
    private val checkCollision: CheckCollisionUseCase,
) {
    enum class Direction {
        LEFT,
        RIGHT,
        DOWN,
    }

    /**
     * Attempts to move the current piece in the specified direction.
     *
     * @param state Current game state
     * @param direction Direction to move (LEFT, RIGHT, or DOWN)
     * @return Updated GameState if move is valid, null if move is blocked
     */
    operator fun invoke(
        state: GameState,
        direction: Direction,
    ): GameState? {
        val piece = state.currentPiece ?: return null
        if (state.isGameOver || state.isPaused) return null

        val offset =
            when (direction) {
                Direction.LEFT -> Position(-1, 0)
                Direction.RIGHT -> Position(1, 0)
                Direction.DOWN -> Position(0, 1)
            }

        val newPosition = state.currentPosition + offset

        return if (!checkCollision(state.board, piece, newPosition)) {
            state.copy(currentPosition = newPosition)
        } else {
            null
        }
    }

    /**
     * Moves the piece left if possible.
     */
    fun moveLeft(state: GameState): GameState? = invoke(state, Direction.LEFT)

    /**
     * Moves the piece right if possible.
     */
    fun moveRight(state: GameState): GameState? = invoke(state, Direction.RIGHT)

    /**
     * Moves the piece down if possible (soft drop).
     */
    fun moveDown(state: GameState): GameState? = invoke(state, Direction.DOWN)
}
