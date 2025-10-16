package com.yet.tetris.domain.usecase

import com.yet.tetris.domain.model.game.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class MovePieceUseCaseTest {

    private val checkCollision = CheckCollisionUseCase()
    private val useCase = MovePieceUseCase(checkCollision)

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
    fun moveLeft_validMove_shouldUpdatePosition() {
        // Given
        val state = createTestState(position = Position(5, 10))

        // When
        val newState = useCase.moveLeft(state)

        // Then
        assertNotNull(newState)
        assertEquals(Position(4, 10), newState.currentPosition)
    }

    @Test
    fun moveLeft_atLeftBoundary_shouldReturnNull() {
        // Given
        val state = createTestState(position = Position(0, 10))

        // When
        val newState = useCase.moveLeft(state)

        // Then
        assertNull(newState, "Cannot move left from left boundary")
    }

    @Test
    fun moveRight_validMove_shouldUpdatePosition() {
        // Given
        val state = createTestState(position = Position(3, 10))

        // When
        val newState = useCase.moveRight(state)

        // Then
        assertNotNull(newState)
        assertEquals(Position(4, 10), newState.currentPosition)
    }

    @Test
    fun moveRight_atRightBoundary_shouldReturnNull() {
        // Given
        val state = createTestState(position = Position(9, 10))

        // When
        val newState = useCase.moveRight(state)

        // Then
        assertNull(newState, "Cannot move right from right boundary")
    }

    @Test
    fun moveDown_validMove_shouldUpdatePosition() {
        // Given
        val state = createTestState(position = Position(3, 10))

        // When
        val newState = useCase.moveDown(state)

        // Then
        assertNotNull(newState)
        assertEquals(Position(3, 11), newState.currentPosition)
    }

    @Test
    fun moveDown_atBottom_shouldReturnNull() {
        // Given
        val state = createTestState(position = Position(3, 18))

        // When
        val newState = useCase.moveDown(state)

        // Then
        assertNull(newState, "Cannot move down when at bottom")
    }

    @Test
    fun invoke_withDirection_shouldMoveCorrectly() {
        // Given
        val state = createTestState(position = Position(5, 10))

        // When
        val movedLeft = useCase(state, MovePieceUseCase.Direction.LEFT)
        val movedRight = useCase(state, MovePieceUseCase.Direction.RIGHT)
        val movedDown = useCase(state, MovePieceUseCase.Direction.DOWN)

        // Then
        assertNotNull(movedLeft)
        assertEquals(Position(4, 10), movedLeft.currentPosition)
        
        assertNotNull(movedRight)
        assertEquals(Position(6, 10), movedRight.currentPosition)
        
        assertNotNull(movedDown)
        assertEquals(Position(5, 11), movedDown.currentPosition)
    }

    @Test
    fun move_blockedByLockedPiece_shouldReturnNull() {
        // Given - Create a wall of blocks to the left
        val cells = mutableMapOf<Position, TetrominoType>()
        for (y in 9..11) {
            cells[Position(2, y)] = TetrominoType.I
        }
        val board = GameBoard(cells = cells)
        val state = createTestState(position = Position(3, 10), board = board)

        // When
        val newState = useCase.moveLeft(state)

        // Then
        assertNull(newState, "Cannot move into locked piece")
    }

    @Test
    fun move_whenGameOver_shouldReturnNull() {
        // Given
        val state = createTestState().copy(isGameOver = true)

        // When
        val movedLeft = useCase.moveLeft(state)
        val movedRight = useCase.moveRight(state)
        val movedDown = useCase.moveDown(state)

        // Then
        assertNull(movedLeft, "Cannot move when game is over")
        assertNull(movedRight, "Cannot move when game is over")
        assertNull(movedDown, "Cannot move when game is over")
    }

    @Test
    fun move_whenPaused_shouldReturnNull() {
        // Given
        val state = createTestState().copy(isPaused = true)

        // When
        val movedLeft = useCase.moveLeft(state)
        val movedRight = useCase.moveRight(state)
        val movedDown = useCase.moveDown(state)

        // Then
        assertNull(movedLeft, "Cannot move when game is paused")
        assertNull(movedRight, "Cannot move when game is paused")
        assertNull(movedDown, "Cannot move when game is paused")
    }

    @Test
    fun move_withNoPiece_shouldReturnNull() {
        // Given
        val state = createTestState().copy(currentPiece = null)

        // When
        val movedLeft = useCase.moveLeft(state)

        // Then
        assertNull(movedLeft, "Cannot move when there is no current piece")
    }

    @Test
    fun move_multipleMoves_shouldAccumulate() {
        // Given
        var state = createTestState(position = Position(5, 10))

        // When - Move left twice
        state = useCase.moveLeft(state)!!
        state = useCase.moveLeft(state)!!

        // Then
        assertEquals(Position(3, 10), state.currentPosition)
    }
}
