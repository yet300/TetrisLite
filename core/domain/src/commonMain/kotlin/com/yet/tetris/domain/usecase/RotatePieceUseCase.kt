package com.yet.tetris.domain.usecase

import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.game.Position
import com.yet.tetris.domain.model.game.Tetromino

/**
 * Use case for rotating tetrominoes with SRS (Super Rotation System) wall kicks.
 * Wall kicks allow pieces to rotate near walls and other blocks by trying offset positions.
 */
class RotatePieceUseCase(
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

    /**
     * Attempts to rotate the current piece clockwise.
     * Uses SRS wall kick system to find a valid rotation position.
     *
     * @return Explicit rotation result with updated state or blocking reason
     */
    operator fun invoke(state: GameState): Result {
        val piece =
            state.currentPiece
                ?: return Result.Blocked(BlockedReason.NO_CURRENT_PIECE)
        if (state.isGameOver) return Result.Blocked(BlockedReason.GAME_OVER)
        if (state.isPaused) return Result.Blocked(BlockedReason.PAUSED)

        val rotatedPiece = piece.rotate()

        // Try rotation at current position first
        if (!checkCollision(state.board, rotatedPiece, state.currentPosition)) {
            return Result.Applied(
                state.copy(currentPiece = rotatedPiece),
            )
        }

        // Try wall kick offsets
        val kickOffsets = getWallKickOffsets(piece, rotatedPiece)
        for (offset in kickOffsets) {
            val newPosition = state.currentPosition + offset
            if (!checkCollision(state.board, rotatedPiece, newPosition)) {
                return Result.Applied(
                    state.copy(
                        currentPiece = rotatedPiece,
                        currentPosition = newPosition,
                    ),
                )
            }
        }

        // Rotation not possible
        return Result.Blocked(BlockedReason.COLLISION)
    }

    /**
     * Gets the SRS wall kick offset table for the rotation.
     * Different offsets are used for I-piece vs other pieces.
     */
    private fun getWallKickOffsets(
        from: Tetromino,
        to: Tetromino,
    ): List<Position> =
        if (from.type == com.yet.tetris.domain.model.game.TetrominoType.I) {
            getIKickOffsets(from.rotation, to.rotation)
        } else {
            getStandardKickOffsets(from.rotation, to.rotation)
        }

    /**
     * SRS wall kick offsets for standard pieces (J, L, S, T, Z).
     * O-piece doesn't rotate, so these won't be used for it.
     */
    private fun getStandardKickOffsets(
        fromRotation: Int,
        toRotation: Int,
    ): List<Position> =
        when (fromRotation to toRotation) {
            0 to 1 -> listOf(Position(-1, 0), Position(-1, 1), Position(0, -2), Position(-1, -2))
            1 to 2 -> listOf(Position(1, 0), Position(1, -1), Position(0, 2), Position(1, 2))
            2 to 3 -> listOf(Position(1, 0), Position(1, 1), Position(0, -2), Position(1, -2))
            3 to 0 -> listOf(Position(-1, 0), Position(-1, -1), Position(0, 2), Position(-1, 2))
            else -> emptyList()
        }

    /**
     * SRS wall kick offsets for I-piece (has different kick behavior).
     */
    private fun getIKickOffsets(
        fromRotation: Int,
        toRotation: Int,
    ): List<Position> =
        when (fromRotation to toRotation) {
            0 to 1 -> listOf(Position(-2, 0), Position(1, 0), Position(-2, -1), Position(1, 2))
            1 to 2 -> listOf(Position(-1, 0), Position(2, 0), Position(-1, 2), Position(2, -1))
            2 to 3 -> listOf(Position(2, 0), Position(-1, 0), Position(2, 1), Position(-1, -2))
            3 to 0 -> listOf(Position(1, 0), Position(-2, 0), Position(1, -2), Position(-2, 1))
            else -> emptyList()
        }
}
