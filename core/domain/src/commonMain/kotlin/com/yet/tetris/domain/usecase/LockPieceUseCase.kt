package com.yet.tetris.domain.usecase

import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.game.LevelProgression
import com.yet.tetris.domain.model.game.Position
import com.yet.tetris.domain.model.game.Tetromino

/**
 * Use case for locking the current piece to the board.
 * This triggers line clearing, score calculation, and spawning the next piece.
 * This is a critical use case that orchestrates multiple game mechanics.
 */
class LockPieceUseCase(
    private val calculateScore: CalculateScoreUseCase,
    private val checkCollision: CheckCollisionUseCase,
    private val previewQueueEngine: PreviewQueueEngine,
) {
    data class Result(
        val gameState: GameState,
        val clearedRows: List<Int>,
        val lockCells: List<Position>,
    )

    companion object {
        // Standard spawn position for new pieces (top-center of board)
        private const val SPAWN_X = 3
        private const val SPAWN_Y = 0
    }

    /**
     * Locks the current piece to the board and processes the consequences:
     * 1. Lock piece to board
     * 2. Clear any completed lines
     * 3. Calculate and add score
     * 4. Spawn next piece
     * 5. Check for game over
     *
     * @param state Current game state
     * @return Updated GameState with locked piece and new current piece
     */
    operator fun invoke(state: GameState): GameState = invokeDetailed(state).gameState

    fun invokeDetailed(state: GameState): Result {
        val piece = state.currentPiece ?: return state
            .let { Result(gameState = it, clearedRows = emptyList(), lockCells = emptyList()) }

        val lockCells = piece.getAbsolutePositions(state.currentPosition)

        // 1. Lock the piece to the board
        val boardWithPiece = state.board.lockPiece(piece, state.currentPosition)

        // 2. Clear completed lines
        val clearResult = boardWithPiece.clearLinesDetailed()
        val clearedBoard = clearResult.board
        val linesCleared = clearResult.linesCleared

        // 3. Calculate score increment
        val scoreIncrement = calculateScore(linesCleared)
        val newScore = state.score + scoreIncrement
        val newLinesCleared = state.linesCleared + linesCleared
        val newLevel = LevelProgression.levelForLines(newLinesCleared)

        // 4. Spawn next piece and advance queue
        val newCurrentPiece = state.nextPiece.resetRotation()
        val preview = previewQueueEngine.advance(state.nextQueue)
        val spawnPosition = Position(SPAWN_X, SPAWN_Y)

        // 5. Check for game over (new piece collides immediately)
        val isGameOver = checkCollision(clearedBoard, newCurrentPiece, spawnPosition)

        return Result(
            gameState =
                state.copy(
                    board = clearedBoard,
                    currentPiece = if (isGameOver) null else newCurrentPiece,
                    currentPosition = spawnPosition,
                    nextPiece = preview.nextPiece,
                    nextQueue = preview.nextQueue,
                    canHold = true,
                    score = newScore,
                    linesCleared = newLinesCleared,
                    level = newLevel,
                    isGameOver = isGameOver,
                ),
            clearedRows = clearResult.clearedRows,
            lockCells = lockCells,
        )
    }

    /**
     * Checks if the current piece should be locked (can't move down anymore).
     */
    fun shouldLockPiece(state: GameState): Boolean {
        val piece = state.currentPiece ?: return false
        val downPosition = state.currentPosition + Position(0, 1)
        return checkCollision(state.board, piece, downPosition)
    }

    private fun Tetromino.resetRotation(): Tetromino = Tetromino.create(type = type, rotation = 0)
}
