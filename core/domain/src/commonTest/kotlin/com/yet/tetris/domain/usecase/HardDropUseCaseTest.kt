package com.yet.tetris.domain.usecase

import com.yet.tetris.domain.model.game.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class HardDropUseCaseTest {

    private val checkCollision = CheckCollisionUseCase()
    private val useCase = HardDropUseCase(checkCollision)

    private fun createTestState(
        position: Position = Position(3, 0),
        board: GameBoard = GameBoard(),
        piece: Tetromino = Tetromino.create(TetrominoType.T)
    ): GameState {
        return GameState(
            board = board,
            currentPiece = piece,
            currentPosition = position,
            nextPiece = Tetromino.create(TetrominoType.I),
            score = 0,
            linesCleared = 0,
            isGameOver = false,
            isPaused = false
        )
    }

    @Test
    fun invoke_emptyBoard_shouldDropToBottom() {
        // Given
        val state = createTestState(position = Position(3, 0))

        // When
        val newState = useCase(state)

        // Then
        assertNotNull(newState)
        assertTrue(newState.currentPosition.y > state.currentPosition.y)
        assertTrue(newState.currentPosition.y >= 17) // Near bottom
    }

    @Test
    fun invoke_withLockedPieces_shouldStopAboveThem() {
        // Given - Locked pieces at bottom
        val cells = (0 until 10).associate { x ->
            Position(x, 19) to TetrominoType.I
        }
        val board = GameBoard(cells = cells)
        val state = createTestState(position = Position(3, 0), board = board)

        // When
        val newState = useCase(state)

        // Then
        assertNotNull(newState)
        assertTrue(newState.currentPosition.y < 19)
        assertTrue(newState.currentPosition.y > state.currentPosition.y)
    }

    @Test
    fun invoke_alreadyAtBottom_shouldNotMove() {
        // Given - Piece already at lowest position
        val state = createTestState(position = Position(3, 18))

        // When
        val newState = useCase(state)

        // Then
        assertNotNull(newState)
        assertEquals(18, newState.currentPosition.y)
    }

    @Test
    fun invoke_whenGameOver_shouldReturnNull() {
        // Given
        val state = createTestState().copy(isGameOver = true)

        // When
        val newState = useCase(state)

        // Then
        assertNull(newState)
    }

    @Test
    fun invoke_whenPaused_shouldReturnNull() {
        // Given
        val state = createTestState().copy(isPaused = true)

        // When
        val newState = useCase(state)

        // Then
        assertNull(newState)
    }

    @Test
    fun invoke_withNoPiece_shouldReturnNull() {
        // Given
        val state = createTestState().copy(currentPiece = null)

        // When
        val newState = useCase(state)

        // Then
        assertNull(newState)
    }

    @Test
    fun calculateDropDistance_emptyBoard_shouldReturnLargeDistance() {
        // Given
        val state = createTestState(position = Position(3, 0))

        // When
        val distance = useCase.calculateDropDistance(state)

        // Then
        assertTrue(distance > 15, "Should drop significant distance")
    }

    @Test
    fun calculateDropDistance_alreadyAtBottom_shouldReturn0() {
        // Given
        val state = createTestState(position = Position(3, 18))

        // When
        val distance = useCase.calculateDropDistance(state)

        // Then
        assertEquals(0, distance)
    }

    @Test
    fun calculateDropDistance_withObstacle_shouldReturnCorrectDistance() {
        // Given - Obstacle at y=15
        val cells = (0 until 10).associate { x ->
            Position(x, 15) to TetrominoType.I
        }
        val board = GameBoard(cells = cells)
        val state = createTestState(position = Position(3, 5), board = board)

        // When
        val distance = useCase.calculateDropDistance(state)

        // Then
        assertTrue(distance < 10, "Should stop before obstacle")
        assertTrue(distance > 0, "Should move some distance")
    }

    @Test
    fun invoke_iPieceVertical_shouldDropCorrectly() {
        // Given - Vertical I-piece
        val piece = Tetromino.create(TetrominoType.I, 1)
        val state = createTestState(position = Position(5, 0), piece = piece)

        // When
        val newState = useCase(state)

        // Then
        assertNotNull(newState)
        assertTrue(newState.currentPosition.y > 0)
    }

    @Test
    fun invoke_shouldNotChangeXPosition() {
        // Given
        val state = createTestState(position = Position(7, 5))

        // When
        val newState = useCase(state)

        // Then
        assertNotNull(newState)
        assertEquals(7, newState.currentPosition.x)
    }
}
