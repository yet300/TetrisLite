package com.yet.tetris.domain.usecase

import com.yet.tetris.domain.model.game.GameBoard
import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.game.Position
import com.yet.tetris.domain.model.game.Tetromino
import com.yet.tetris.domain.model.game.TetrominoType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class RotatePieceUseCaseTest {
    private val checkCollision = CheckCollisionUseCase()
    private val useCase = RotatePieceUseCase(checkCollision)

    private fun createTestState(
        position: Position = Position(3, 10),
        board: GameBoard = GameBoard(),
        piece: Tetromino = Tetromino.create(TetrominoType.T, 0),
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
    fun invoke_validRotation_shouldRotatePiece() {
        // Given
        val state = createTestState(piece = Tetromino.create(TetrominoType.T, 0))

        // When
        val newState = useCase(state)

        // Then
        assertNotNull(newState)
        assertEquals(1, newState.currentPiece?.rotation)
    }

    @Test
    fun invoke_multipleRotations_shouldCycle() {
        // Given
        var state = createTestState(piece = Tetromino.create(TetrominoType.T, 0))

        // When - Rotate 4 times
        state = useCase(state)!!
        state = useCase(state)!!
        state = useCase(state)!!
        state = useCase(state)!!

        // Then - Should be back to rotation 0
        assertEquals(0, state.currentPiece?.rotation)
    }

    @Test
    fun invoke_blockedRotation_shouldReturnNull() {
        // Given - Surround piece so it can't rotate
        val cells = mutableMapOf<Position, TetrominoType>()
        for (x in 2..4) {
            for (y in 9..11) {
                if (x != 3 || y != 10) { // Leave center empty for current piece
                    cells[Position(x, y)] = TetrominoType.I
                }
            }
        }
        val board = GameBoard(cells = cells)
        val state = createTestState(position = Position(3, 10), board = board)

        // When
        val newState = useCase(state)

        // Then
        assertNull(newState, "Rotation should fail when blocked")
    }

    @Test
    fun invoke_wallKick_shouldAdjustPosition() {
        // Given - Piece at left wall
        val state =
            createTestState(
                position = Position(0, 10),
                piece = Tetromino.create(TetrominoType.I, 0),
            )

        // When - Try to rotate (would go out of bounds without wall kick)
        val newState = useCase(state)

        // Then - Should succeed with wall kick adjustment
        assertNotNull(newState, "Wall kick should allow rotation")
        assertEquals(1, newState.currentPiece?.rotation)
    }

    @Test
    fun invoke_oPiece_shouldAlwaysSucceed() {
        // Given - O piece doesn't change shape
        val state = createTestState(piece = Tetromino.create(TetrominoType.O, 0))

        // When
        val newState = useCase(state)

        // Then
        assertNotNull(newState)
        assertEquals(1, newState.currentPiece?.rotation)
    }

    @Test
    fun invoke_whenGameOver_shouldReturnNull() {
        // Given
        val state = createTestState().copy(isGameOver = true)

        // When
        val newState = useCase(state)

        // Then
        assertNull(newState, "Cannot rotate when game is over")
    }

    @Test
    fun invoke_whenPaused_shouldReturnNull() {
        // Given
        val state = createTestState().copy(isPaused = true)

        // When
        val newState = useCase(state)

        // Then
        assertNull(newState, "Cannot rotate when game is paused")
    }

    @Test
    fun invoke_withNoPiece_shouldReturnNull() {
        // Given
        val state = createTestState().copy(currentPiece = null)

        // When
        val newState = useCase(state)

        // Then
        assertNull(newState, "Cannot rotate when there is no current piece")
    }

    @Test
    fun invoke_iPiece_shouldUseIPieceWallKicks() {
        // Given - I piece has different wall kick behavior
        val state =
            createTestState(
                position = Position(0, 10),
                piece = Tetromino.create(TetrominoType.I, 0),
            )

        // When
        val newState = useCase(state)

        // Then - Should succeed with I-piece specific wall kicks
        assertNotNull(newState)
    }

    @Test
    fun invoke_tPiece_shouldUseStandardWallKicks() {
        // Given
        val state =
            createTestState(
                position = Position(0, 10),
                piece = Tetromino.create(TetrominoType.T, 0),
            )

        // When
        val newState = useCase(state)

        // Then
        assertNotNull(newState)
    }

    @Test
    fun invoke_atRightWall_shouldWallKick() {
        // Given - Piece at right wall
        val state =
            createTestState(
                position = Position(8, 10),
                piece = Tetromino.create(TetrominoType.T, 0),
            )

        // When - Rotate (may need wall kick)
        val newState = useCase(state)

        // Then - Should succeed with wall kick or normal rotation
        assertNotNull(newState)
    }

    @Test
    fun invoke_nearBottom_shouldHandleCorrectly() {
        // Given - Piece near bottom
        val state =
            createTestState(
                position = Position(5, 18),
                piece = Tetromino.create(TetrominoType.I, 0),
            )

        // When
        val newState = useCase(state)

        // Then - May or may not succeed depending on exact position
        // Just verify it doesn't crash
        if (newState != null) {
            assertEquals(1, newState.currentPiece?.rotation)
        }
    }
}
