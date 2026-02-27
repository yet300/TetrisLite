package com.yet.tetris.domain.usecase

import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.game.Position

/**
 * Use case for performing a hard drop - instantly dropping the piece to the lowest valid position.
 * This is a key Tetris mechanic that allows players to quickly place pieces.
 */
class HardDropUseCase(
    private val checkCollision: CheckCollisionUseCase,
) {
    /**
     * Calculates the lowest valid position for the current piece and returns
     * the updated state with the piece at that position.
     *
     * Note: This use case only moves the piece to the drop position.
     * The actual locking of the piece should be handled by LockPieceUseCase.
     *
     * @param state Current game state
     * @return Updated GameState with piece at lowest position, or null if no piece exists
     */
    operator fun invoke(state: GameState): GameState? {
        val piece = state.currentPiece ?: return null
        if (state.isGameOver || state.isPaused) return null

        val dropPosition = calculateDropPosition(state)

        return state.copy(currentPosition = dropPosition)
    }

    /**
     * Calculates the lowest valid Y position for the current piece.
     * Moves down one row at a time until a collision is detected.
     */
    private fun calculateDropPosition(state: GameState): Position {
        val piece = state.currentPiece ?: return state.currentPosition
        var testPosition = state.currentPosition

        // Keep moving down until we hit something
        while (true) {
            val nextPosition = testPosition + Position(0, 1)
            if (checkCollision(state.board, piece, nextPosition)) {
                // Can't move further down, return last valid position
                return testPosition
            }
            testPosition = nextPosition
        }
    }

    /**
     * Calculates the distance the piece will drop (useful for scoring or ghost piece).
     */
    fun calculateDropDistance(state: GameState): Int {
        val dropPosition = calculateDropPosition(state)
        return dropPosition.y - state.currentPosition.y
    }
}
