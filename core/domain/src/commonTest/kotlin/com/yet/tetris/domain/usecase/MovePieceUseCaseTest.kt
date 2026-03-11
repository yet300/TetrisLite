package com.yet.tetris.domain.usecase

import com.yet.tetris.domain.model.game.GameBoard
import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.game.Position
import com.yet.tetris.domain.model.game.Tetromino
import com.yet.tetris.domain.model.game.TetrominoType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class MovePieceUseCaseTest {
    private val checkCollision = CheckCollisionUseCase()
    private val useCase = MovePieceUseCase(checkCollision)

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
    fun moveLeft_validMove_shouldUpdatePosition() {
        // Given
        val state = createTestState(position = Position(5, 10))

        // When
        val result = useCase.moveLeft(state)
        val newState = assertApplied(result)

        // Then
        assertEquals(Position(4, 10), newState.currentPosition)
    }

    @Test
    fun moveLeft_atLeftBoundary_shouldReturnBlocked() {
        // Given
        val state = createTestState(position = Position(0, 10))

        // When
        val result = useCase.moveLeft(state)
        val blocked = assertIs<MovePieceUseCase.Result.Blocked>(result)

        // Then
        assertEquals(MovePieceUseCase.BlockedReason.COLLISION, blocked.reason)
    }

    @Test
    fun moveRight_validMove_shouldUpdatePosition() {
        // Given
        val state = createTestState(position = Position(3, 10))

        // When
        val result = useCase.moveRight(state)
        val newState = assertApplied(result)

        // Then
        assertEquals(Position(4, 10), newState.currentPosition)
    }

    @Test
    fun moveRight_atRightBoundary_shouldReturnBlocked() {
        // Given
        val state = createTestState(position = Position(9, 10))

        // When
        val result = useCase.moveRight(state)
        val blocked = assertIs<MovePieceUseCase.Result.Blocked>(result)

        // Then
        assertEquals(MovePieceUseCase.BlockedReason.COLLISION, blocked.reason)
    }

    @Test
    fun moveDown_validMove_shouldUpdatePosition() {
        // Given
        val state = createTestState(position = Position(3, 10))

        // When
        val result = useCase.moveDown(state)
        val newState = assertApplied(result)

        // Then
        assertEquals(Position(3, 11), newState.currentPosition)
    }

    @Test
    fun moveDown_atBottom_shouldReturnBlocked() {
        // Given
        val state = createTestState(position = Position(3, 18))

        // When
        val result = useCase.moveDown(state)
        val blocked = assertIs<MovePieceUseCase.Result.Blocked>(result)

        // Then
        assertEquals(MovePieceUseCase.BlockedReason.COLLISION, blocked.reason)
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
        assertEquals(Position(4, 10), assertApplied(movedLeft).currentPosition)

        assertEquals(Position(6, 10), assertApplied(movedRight).currentPosition)

        assertEquals(Position(5, 11), assertApplied(movedDown).currentPosition)
    }

    @Test
    fun move_blockedByLockedPiece_shouldReturnBlocked() {
        // Given - Create a wall of blocks to the left
        val cells = mutableMapOf<Position, TetrominoType>()
        for (y in 9..11) {
            cells[Position(2, y)] = TetrominoType.I
        }
        val board = GameBoard(cells = cells)
        val state = createTestState(position = Position(3, 10), board = board)

        // When
        val result = useCase.moveLeft(state)
        val blocked = assertIs<MovePieceUseCase.Result.Blocked>(result)

        // Then
        assertEquals(MovePieceUseCase.BlockedReason.COLLISION, blocked.reason)
    }

    @Test
    fun move_whenGameOver_shouldReturnBlocked() {
        // Given
        val state = createTestState().copy(isGameOver = true)

        // When
        val movedLeft = useCase.moveLeft(state)
        val movedRight = useCase.moveRight(state)
        val movedDown = useCase.moveDown(state)

        // Then
        assertEquals(MovePieceUseCase.BlockedReason.GAME_OVER, assertBlocked(movedLeft).reason)
        assertEquals(MovePieceUseCase.BlockedReason.GAME_OVER, assertBlocked(movedRight).reason)
        assertEquals(MovePieceUseCase.BlockedReason.GAME_OVER, assertBlocked(movedDown).reason)
    }

    @Test
    fun move_whenPaused_shouldReturnBlocked() {
        // Given
        val state = createTestState().copy(isPaused = true)

        // When
        val movedLeft = useCase.moveLeft(state)
        val movedRight = useCase.moveRight(state)
        val movedDown = useCase.moveDown(state)

        // Then
        assertEquals(MovePieceUseCase.BlockedReason.PAUSED, assertBlocked(movedLeft).reason)
        assertEquals(MovePieceUseCase.BlockedReason.PAUSED, assertBlocked(movedRight).reason)
        assertEquals(MovePieceUseCase.BlockedReason.PAUSED, assertBlocked(movedDown).reason)
    }

    @Test
    fun move_withNoPiece_shouldReturnBlocked() {
        // Given
        val state = createTestState().copy(currentPiece = null)

        // When
        val movedLeft = useCase.moveLeft(state)

        // Then
        assertEquals(MovePieceUseCase.BlockedReason.NO_CURRENT_PIECE, assertBlocked(movedLeft).reason)
    }

    @Test
    fun move_multipleMoves_shouldAccumulate() {
        // Given
        var state = createTestState(position = Position(5, 10))

        // When - Move left twice
        state = assertApplied(useCase.moveLeft(state))
        state = assertApplied(useCase.moveLeft(state))

        // Then
        assertEquals(Position(3, 10), state.currentPosition)
    }

    private fun assertApplied(result: MovePieceUseCase.Result): GameState = assertIs<MovePieceUseCase.Result.Applied>(result).gameState

    private fun assertBlocked(result: MovePieceUseCase.Result): MovePieceUseCase.Result.Blocked =
        assertIs<MovePieceUseCase.Result.Blocked>(result)
}
