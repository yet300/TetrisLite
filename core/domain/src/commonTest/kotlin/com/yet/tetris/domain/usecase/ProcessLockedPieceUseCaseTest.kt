package com.yet.tetris.domain.usecase

import com.yet.tetris.domain.model.game.GameBoard
import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.game.Position
import com.yet.tetris.domain.model.game.Tetromino
import com.yet.tetris.domain.model.game.TetrominoType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ProcessLockedPieceUseCaseTest {
    private val checkCollisionUseCase = CheckCollisionUseCase()
    private val movePieceUseCase = MovePieceUseCase(checkCollisionUseCase)
    private val calculateGhostPositionUseCase = CalculateGhostPositionUseCase()
    private val calculateScoreUseCase = CalculateScoreUseCase()
    private val generateTetrominoUseCase = GenerateTetrominoUseCase()
    private val lockPieceUseCase =
        LockPieceUseCase(
            calculateScore = calculateScoreUseCase,
            generateTetromino = generateTetrominoUseCase,
            checkCollision = checkCollisionUseCase,
        )
    private val useCase =
        ProcessLockedPieceUseCase(
            lockPieceUseCase = lockPieceUseCase,
            planVisualFeedbackUseCase = PlanVisualFeedbackUseCase(),
            advanceGameTickUseCase =
                AdvanceGameTickUseCase(
                    movePieceUseCase,
                    calculateGhostPositionUseCase,
                ),
        )

    @Test
    fun resets_combo_and_emits_no_feed_WHEN_no_lines_are_cleared() {
        val state = createNoLineClearLockState()

        val result =
            useCase(
                gameState = state,
                currentComboStreak = 3,
                currentVisualSequence = 10L,
            )

        assertEquals(0, result.linesCleared)
        assertEquals(0, result.nextComboStreak)
        assertNull(result.visualEffectFeed)
        assertFalse(result.levelIncreased)
        assertEquals(state.linesCleared, result.gameState.linesCleared)
    }

    @Test
    fun emits_visual_feed_WHEN_lines_are_cleared() {
        val state = createSingleLineClearState()

        val result =
            useCase(
                gameState = state,
                currentComboStreak = 0,
                currentVisualSequence = 5L,
            )

        assertEquals(1, result.linesCleared)
        assertEquals(1, result.nextComboStreak)
        assertEquals(1, result.gameState.linesCleared)

        val feed = result.visualEffectFeed
        assertNotNull(feed)
        assertEquals(6L, feed.sequence)

        val burst = feed.latest
        assertNotNull(burst)
        assertEquals(6L, burst.id)
        assertEquals(1, burst.linesCleared)
    }

    @Test
    fun marks_level_increase_WHEN_lock_crosses_level_threshold() {
        val state = createSingleLineClearState().copy(linesCleared = 9, level = 1)

        val result =
            useCase(
                gameState = state,
                currentComboStreak = 0,
                currentVisualSequence = 0L,
            )

        assertTrue(result.levelIncreased)
        assertEquals(2, result.gameState.level)
        assertEquals(10, result.gameState.linesCleared)
    }

    private fun createSingleLineClearState(): GameState {
        val filledRow =
            (2 until 10).associate { x ->
                Position(x = x, y = 19) to TetrominoType.I
            }

        return GameState(
            board = GameBoard(width = 10, height = 20, cells = filledRow),
            currentPiece = Tetromino.create(TetrominoType.O),
            currentPosition = Position(x = 0, y = 18),
            nextPiece = Tetromino.create(TetrominoType.T),
            score = 0,
            linesCleared = 0,
            level = 1,
            isGameOver = false,
            isPaused = false,
        )
    }

    private fun createNoLineClearLockState(): GameState =
        GameState(
            board = GameBoard(width = 10, height = 20),
            currentPiece = Tetromino.create(TetrominoType.O),
            currentPosition = Position(x = 0, y = 18),
            nextPiece = Tetromino.create(TetrominoType.T),
            score = 0,
            linesCleared = 0,
            level = 1,
            isGameOver = false,
            isPaused = false,
        )
}
