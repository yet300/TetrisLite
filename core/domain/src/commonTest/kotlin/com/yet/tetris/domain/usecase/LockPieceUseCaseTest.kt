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

class LockPieceUseCaseTest {
    private val checkCollision = CheckCollisionUseCase()
    private val calculateScore = CalculateScoreUseCase()
    private val generateTetromino = GenerateTetrominoUseCase()
    private val useCase = LockPieceUseCase(calculateScore, generateTetromino, checkCollision)

    private fun createTestState(
        position: Position = Position(3, 17),
        board: GameBoard = GameBoard(),
        piece: Tetromino = Tetromino.create(TetrominoType.T),
        score: Long = 0,
        linesCleared: Long = 0,
        level: Int = 1,
    ): GameState =
        GameState(
            board = board,
            currentPiece = piece,
            currentPosition = position,
            nextPiece = Tetromino.create(TetrominoType.I),
            score = score,
            linesCleared = linesCleared,
            level = level,
            isGameOver = false,
            isPaused = false,
        )

    @Test
    fun invoke_shouldLockPieceToBoard() {
        // Given
        val state = createTestState()
        val initialCellCount = state.board.cells.size

        // When
        val newState = useCase(state)

        // Then
        assertEquals(initialCellCount + 4, newState.board.cells.size)
    }

    @Test
    fun invoke_shouldSpawnNextPiece() {
        // Given
        val state = createTestState()
        val oldNextPiece = state.nextPiece

        // When
        val newState = useCase(state)

        // Then
        assertNotNull(newState.currentPiece)
        assertEquals(oldNextPiece.type, newState.currentPiece?.type)
        assertNotNull(newState.nextPiece)
    }

    @Test
    fun invoke_withCompleteLine_shouldClearAndScore() {
        // Given - Almost complete line, piece will complete it
        val cells =
            (0 until 6).associate { x ->
                Position(x, 19) to TetrominoType.I
            }
        val board = GameBoard(cells = cells)
        val piece = Tetromino.create(TetrominoType.I, 0) // Horizontal
        val state =
            createTestState(
                position = Position(6, 18),
                board = board,
                piece = piece,
                score = 0,
            )

        // When
        val newState = useCase(state)

        // Then
        assertEquals(1, newState.linesCleared)
        assertTrue(newState.score > 0)
        assertEquals(100, newState.score) // Single line = 100 points
    }

    @Test
    fun invoke_withMultipleLines_shouldClearAllAndScore() {
        // Given - Setup for clearing 2 lines
        val cells = mutableMapOf<Position, TetrominoType>()
        // Almost complete two lines
        for (y in 18..19) {
            for (x in 0 until 6) {
                cells[Position(x, y)] = TetrominoType.I
            }
        }
        val board = GameBoard(cells = cells)
        val piece = Tetromino.create(TetrominoType.I, 0)
        val state =
            createTestState(
                position = Position(6, 17),
                board = board,
                piece = piece,
            )

        // When
        val newState = useCase(state)

        // Then
        assertTrue(newState.linesCleared >= 1)
        assertTrue(newState.score > 0)
    }

    @Test
    fun invoke_spawnCollision_shouldSetGameOver() {
        // Given - Fill top of board so new piece can't spawn
        // Fill rows 0-2 but leave one cell empty in each row to prevent line clearing
        val cells = mutableMapOf<Position, TetrominoType>()
        for (y in 0 until 3) {
            for (x in 0 until 9) { // Only fill 9 out of 10 cells
                cells[Position(x, y)] = TetrominoType.I
            }
        }
        val board = GameBoard(cells = cells)
        val state = createTestState(position = Position(3, 17), board = board)

        // When
        val newState = useCase(state)

        // Then
        assertTrue(newState.isGameOver)
        assertNull(newState.currentPiece)
    }

    @Test
    fun invoke_shouldResetSpawnPosition() {
        // Given
        val state = createTestState(position = Position(7, 15))

        // When
        val newState = useCase(state)

        // Then
        assertEquals(Position(3, 0), newState.currentPosition)
    }

    @Test
    fun invoke_shouldAccumulateScore() {
        // Given
        val cells =
            (0 until 6).associate { x ->
                Position(x, 19) to TetrominoType.I
            }
        val board = GameBoard(cells = cells)
        val piece = Tetromino.create(TetrominoType.I, 0)
        val state =
            createTestState(
                position = Position(6, 18),
                board = board,
                piece = piece,
                score = 500,
            )

        // When
        val newState = useCase(state)

        // Then
        assertEquals(600, newState.score) // 500 + 100
    }

    @Test
    fun invoke_shouldAccumulateLinesCleared() {
        // Given
        val cells =
            (0 until 6).associate { x ->
                Position(x, 19) to TetrominoType.I
            }
        val board = GameBoard(cells = cells)
        val piece = Tetromino.create(TetrominoType.I, 0)
        val state =
            createTestState(
                position = Position(6, 18),
                board = board,
                piece = piece,
                linesCleared = 10,
            )

        // When
        val newState = useCase(state)

        // Then
        assertEquals(11, newState.linesCleared)
    }

    @Test
    fun invoke_shouldIncreaseLevelEveryTenLines() {
        // Given
        val cells =
            (0 until 6).associate { x ->
                Position(x, 19) to TetrominoType.I
            }
        val board = GameBoard(cells = cells)
        val piece = Tetromino.create(TetrominoType.I, 0)
        val state =
            createTestState(
                position = Position(6, 18),
                board = board,
                piece = piece,
                linesCleared = 9,
                level = 1,
            )

        // When
        val newState = useCase(state)

        // Then
        assertEquals(10, newState.linesCleared)
        assertEquals(2, newState.level)
    }

    @Test
    fun shouldLockPiece_canMoveDown_shouldReturnFalse() {
        // Given
        val state = createTestState(position = Position(3, 5))

        // When
        val shouldLock = useCase.shouldLockPiece(state)

        // Then
        assertFalse(shouldLock)
    }

    @Test
    fun shouldLockPiece_atBottom_shouldReturnTrue() {
        // Given
        val state = createTestState(position = Position(3, 18))

        // When
        val shouldLock = useCase.shouldLockPiece(state)

        // Then
        assertTrue(shouldLock)
    }

    @Test
    fun shouldLockPiece_aboveLockedPiece_shouldReturnTrue() {
        // Given - Locked pieces below
        val cells =
            (0 until 10).associate { x ->
                Position(x, 19) to TetrominoType.I
            }
        val board = GameBoard(cells = cells)
        val state = createTestState(position = Position(3, 17), board = board)

        // When
        val shouldLock = useCase.shouldLockPiece(state)

        // Then
        assertTrue(shouldLock)
    }

    @Test
    fun shouldLockPiece_noPiece_shouldReturnFalse() {
        // Given
        val state = createTestState().copy(currentPiece = null)

        // When
        val shouldLock = useCase.shouldLockPiece(state)

        // Then
        assertFalse(shouldLock)
    }
}
