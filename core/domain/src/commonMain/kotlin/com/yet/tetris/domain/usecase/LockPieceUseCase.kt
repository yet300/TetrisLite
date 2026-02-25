package com.yet.tetris.domain.usecase

import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.game.LevelProgression
import com.yet.tetris.domain.model.game.Position
import jakarta.inject.Singleton

/**
 * Use case for locking the current piece to the board.
 * This triggers line clearing, score calculation, and spawning the next piece.
 * This is a critical use case that orchestrates multiple game mechanics.
 */
@Singleton
class LockPieceUseCase(
    private val calculateScore: CalculateScoreUseCase,
    private val generateTetromino: GenerateTetrominoUseCase,
    private val checkCollision: CheckCollisionUseCase,
) {
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
    operator fun invoke(state: GameState): GameState {
        val piece = state.currentPiece ?: return state

        // 1. Lock the piece to the board
        val boardWithPiece = state.board.lockPiece(piece, state.currentPosition)

        // 2. Clear completed lines
        val (clearedBoard, linesCleared) = boardWithPiece.clearLines()

        // 3. Calculate score increment
        val scoreIncrement = calculateScore(linesCleared)
        val newScore = state.score + scoreIncrement
        val newLinesCleared = state.linesCleared + linesCleared
        val newLevel = LevelProgression.levelForLines(newLinesCleared)

        // 4. Spawn next piece
        val newCurrentPiece = state.nextPiece
        val newNextPiece = generateTetromino()
        val spawnPosition = Position(SPAWN_X, SPAWN_Y)

        // 5. Check for game over (new piece collides immediately)
        val isGameOver = checkCollision(clearedBoard, newCurrentPiece, spawnPosition)

        return state.copy(
            board = clearedBoard,
            currentPiece = if (isGameOver) null else newCurrentPiece,
            currentPosition = spawnPosition,
            nextPiece = newNextPiece,
            score = newScore,
            linesCleared = newLinesCleared,
            level = newLevel,
            isGameOver = isGameOver,
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
}
