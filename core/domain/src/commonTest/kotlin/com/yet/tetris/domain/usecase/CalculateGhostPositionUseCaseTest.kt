package com.yet.tetris.domain.usecase

import com.yet.tetris.domain.model.game.GameBoard
import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.game.Position
import com.yet.tetris.domain.model.game.Tetromino
import com.yet.tetris.domain.model.game.TetrominoType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CalculateGhostPositionUseCaseTest {
    private val useCase = CalculateGhostPositionUseCase()

    private fun createTestState(
        position: Position = Position(3, 0),
        board: GameBoard = GameBoard(),
        piece: Tetromino = Tetromino.create(TetrominoType.T),
    ): GameState =
        GameState(
            board = board,
            currentPiece = piece,
            currentPosition = position,
            nextPiece = Tetromino.create(TetrominoType.I),
            score = 0,
            linesCleared = 0,
            isGameOver = false,
            isPaused = false,
        )

    @Test
    fun invoke_emptyBoard_shouldReturnBottomPosition() {
        // Given
        val state = createTestState(position = Position(3, 0))
        val piece = state.currentPiece!!

        // When
        val ghostY = useCase(state, piece, state.currentPosition)

        // Then
        assertNotNull(ghostY)
        assertTrue(ghostY!! > 15, "Ghost should be near bottom")
    }

    @Test
    fun invoke_withLockedPieces_shouldStopAboveThem() {
        // Given - Locked pieces at bottom
        val cells =
            (0 until 10).associate { x ->
                Position(x, 19) to TetrominoType.I
            }
        val board = GameBoard(cells = cells)
        val state = createTestState(position = Position(3, 0), board = board)
        val piece = state.currentPiece!!

        // When
        val ghostY = useCase(state, piece, state.currentPosition)

        // Then
        assertNotNull(ghostY)
        assertTrue(ghostY!! < 19, "Ghost should stop above locked pieces")
    }

    @Test
    fun invoke_alreadyAtBottom_shouldReturnCurrentPosition() {
        // Given
        val position = Position(3, 18)
        val state = createTestState(position = position)
        val piece = state.currentPiece!!

        // When
        val ghostY = useCase(state, piece, position)

        // Then
        assertNotNull(ghostY)
        assertEquals(18, ghostY)
    }

    @Test
    fun invoke_partiallyBlockedColumn_shouldStopAtObstacle() {
        // Given - Obstacle in middle of board
        val cells =
            mapOf(
                Position(3, 15) to TetrominoType.I,
                Position(4, 15) to TetrominoType.I,
            )
        val board = GameBoard(cells = cells)
        val state = createTestState(position = Position(3, 5), board = board)
        val piece = state.currentPiece!!

        // When
        val ghostY = useCase(state, piece, state.currentPosition)

        // Then
        assertNotNull(ghostY)
        assertTrue(ghostY!! < 15, "Ghost should stop before obstacle")
        assertTrue(ghostY > 5, "Ghost should be below current position")
    }

    @Test
    fun invoke_iPieceHorizontal_shouldCalculateCorrectly() {
        // Given
        val piece = Tetromino.create(TetrominoType.I, 0) // Horizontal
        val position = Position(3, 0)
        val state = createTestState(position = position, piece = piece)

        // When
        val ghostY = useCase(state, piece, position)

        // Then
        assertNotNull(ghostY)
        assertTrue(ghostY!! > 0)
    }

    @Test
    fun invoke_iPieceVertical_shouldCalculateCorrectly() {
        // Given
        val piece = Tetromino.create(TetrominoType.I, 1) // Vertical
        val position = Position(5, 0)
        val state = createTestState(position = position, piece = piece)

        // When
        val ghostY = useCase(state, piece, position)

        // Then
        assertNotNull(ghostY)
        assertTrue(ghostY!! > 0)
    }

    @Test
    fun invoke_nearLeftEdge_shouldHandleCorrectly() {
        // Given
        val position = Position(0, 5)
        val state = createTestState(position = position)
        val piece = state.currentPiece!!

        // When
        val ghostY = useCase(state, piece, position)

        // Then
        assertNotNull(ghostY)
        assertTrue(ghostY!! >= 5)
    }

    @Test
    fun invoke_nearRightEdge_shouldHandleCorrectly() {
        // Given
        val position = Position(8, 5)
        val state = createTestState(position = position)
        val piece = state.currentPiece!!

        // When
        val ghostY = useCase(state, piece, position)

        // Then
        assertNotNull(ghostY)
        assertTrue(ghostY!! >= 5)
    }

    @Test
    fun invoke_oPiece_shouldCalculateCorrectly() {
        // Given - O piece (square)
        val piece = Tetromino.create(TetrominoType.O)
        val position = Position(4, 0)
        val state = createTestState(position = position, piece = piece)

        // When
        val ghostY = useCase(state, piece, position)

        // Then
        assertNotNull(ghostY)
        assertTrue(ghostY!! > 0)
    }
}
