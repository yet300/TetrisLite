package com.yet.tetris.domain.usecase

import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.game.Position

/**
 * Use case for moving the current tetromino in different directions.
 * Validates moves using collision detection before applying them.
 */
class MovePieceUseCase(
    private val checkCollision: CheckCollisionUseCase,
) {
    sealed interface Result {
        data class Applied(
            val gameState: GameState,
        ) : Result

        data class Blocked(
            val reason: BlockedReason,
        ) : Result
    }

    enum class BlockedReason {
        NO_CURRENT_PIECE,
        GAME_OVER,
        PAUSED,
        COLLISION,
    }

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
     * @return Explicit move result with updated state or blocking reason
     */
    operator fun invoke(
        state: GameState,
        direction: Direction,
    ): Result {
        val piece =
            state.currentPiece
                ?: return Result.Blocked(BlockedReason.NO_CURRENT_PIECE)
        if (state.isGameOver) return Result.Blocked(BlockedReason.GAME_OVER)
        if (state.isPaused) return Result.Blocked(BlockedReason.PAUSED)

        val offset =
            when (direction) {
                Direction.LEFT -> Position(-1, 0)
                Direction.RIGHT -> Position(1, 0)
                Direction.DOWN -> Position(0, 1)
            }

        val newPosition = state.currentPosition + offset

        return if (!checkCollision(state.board, piece, newPosition)) {
            Result.Applied(
                state.copy(currentPosition = newPosition),
            )
        } else {
            Result.Blocked(BlockedReason.COLLISION)
        }
    }

    /**
     * Moves the piece left if possible.
     */
    fun moveLeft(state: GameState): Result = invoke(state, Direction.LEFT)

    /**
     * Moves the piece right if possible.
     */
    fun moveRight(state: GameState): Result = invoke(state, Direction.RIGHT)

    /**
     * Moves the piece down if possible (soft drop).
     */
    fun moveDown(state: GameState): Result = invoke(state, Direction.DOWN)
}
