package com.yet.tetris.domain.usecase

import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.game.Position
import com.yet.tetris.domain.model.game.Tetromino

/**
 * Handles hold-piece mechanics:
 * - A piece can be held once until the next lock.
 * - Holding with empty slot pulls the next preview piece into play.
 * - Holding with occupied slot swaps the current and held pieces.
 */
class HoldPieceUseCase(
    private val checkCollisionUseCase: CheckCollisionUseCase,
    private val previewQueueEngine: PreviewQueueEngine,
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
        HOLD_ALREADY_USED,
    }

    companion object {
        private const val SPAWN_X = 3
        private const val SPAWN_Y = 0
    }

    operator fun invoke(state: GameState): Result {
        val currentPiece =
            state.currentPiece
                ?: return Result.Blocked(BlockedReason.NO_CURRENT_PIECE)
        if (state.isGameOver) return Result.Blocked(BlockedReason.GAME_OVER)
        if (state.isPaused) return Result.Blocked(BlockedReason.PAUSED)
        if (!state.canHold) return Result.Blocked(BlockedReason.HOLD_ALREADY_USED)

        val normalizedCurrent = currentPiece.resetRotation()
        val spawnPosition = Position(SPAWN_X, SPAWN_Y)

        val (nextCurrent, nextPiece, nextQueue) =
            if (state.holdPiece == null) {
                val preview = previewQueueEngine.advance(state.nextQueue)
                Triple(state.nextPiece.resetRotation(), preview.nextPiece, preview.nextQueue)
            } else {
                val preview = previewQueueEngine.normalize(state.nextPiece, state.nextQueue)
                Triple(state.holdPiece.resetRotation(), preview.nextPiece, preview.nextQueue)
            }

        val spawnCollision = checkCollisionUseCase(state.board, nextCurrent, spawnPosition)

        return Result.Applied(
            state.copy(
                currentPiece = if (spawnCollision) null else nextCurrent,
                currentPosition = spawnPosition,
                nextPiece = nextPiece,
                nextQueue = nextQueue,
                holdPiece = normalizedCurrent,
                canHold = false,
                isGameOver = spawnCollision || state.isGameOver,
            ),
        )
    }

    private fun Tetromino.resetRotation(): Tetromino = Tetromino.create(type = type, rotation = 0)
}
