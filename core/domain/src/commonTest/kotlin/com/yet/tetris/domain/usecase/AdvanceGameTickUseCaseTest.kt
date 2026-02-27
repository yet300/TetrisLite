package com.yet.tetris.domain.usecase

import com.yet.tetris.domain.model.game.GameBoard
import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.game.Position
import com.yet.tetris.domain.model.game.Tetromino
import com.yet.tetris.domain.model.game.TetrominoType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AdvanceGameTickUseCaseTest {
    private val checkCollisionUseCase = CheckCollisionUseCase()
    private val movePieceUseCase = MovePieceUseCase(checkCollisionUseCase)
    private val calculateGhostPositionUseCase = CalculateGhostPositionUseCase()
    private val useCase = AdvanceGameTickUseCase(movePieceUseCase, calculateGhostPositionUseCase)

    @Test
    fun returns_moved_result_WHEN_piece_can_move_down() {
        val state = createGameState(position = Position(4, 0))

        val result = useCase(state)

        assertIs<AdvanceGameTickUseCase.Result.Moved>(result)
        assertEquals(1, result.gameState.currentPosition.y)
        assertNotNull(result.ghostPieceY)
    }

    @Test
    fun returns_requires_lock_WHEN_piece_cannot_move_down() {
        val state =
            createGameState(
                piece = Tetromino.create(TetrominoType.O),
                position = Position(4, 18),
            )

        val result = useCase(state)

        assertIs<AdvanceGameTickUseCase.Result.RequiresLock>(result)
        assertEquals(state, result.gameState)
    }

    @Test
    fun calculateGhostY_returns_null_WHEN_current_piece_is_null() {
        val state = createGameState(position = Position(4, 0)).copy(currentPiece = null)

        val ghostY = useCase.calculateGhostY(state)

        assertNull(ghostY)
    }

    private fun createGameState(
        piece: Tetromino = Tetromino.create(TetrominoType.T),
        position: Position,
    ): GameState =
        GameState(
            board = GameBoard(width = 10, height = 20),
            currentPiece = piece,
            currentPosition = position,
            nextPiece = Tetromino.create(TetrominoType.I),
            score = 0,
            linesCleared = 0,
            level = 1,
            isGameOver = false,
            isPaused = false,
        )
}
