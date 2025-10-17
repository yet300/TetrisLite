package com.yet.tetris.data.repository

import com.yet.tetris.data.RobolectricTestRunner
import com.yet.tetris.data.createTestDatabaseDriverFactory
import com.yet.tetris.database.dao.GameStateDao
import com.yet.tetris.database.db.DatabaseManager
import com.yet.tetris.domain.model.game.GameBoard
import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.game.Position
import com.yet.tetris.domain.model.game.Tetromino
import com.yet.tetris.domain.model.game.TetrominoType
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class GameStateRepositoryImplTest : RobolectricTestRunner() {
    private lateinit var repository: GameStateRepositoryImpl
    private lateinit var dao: GameStateDao

    @BeforeTest
    fun setup() {
        val databaseManager = DatabaseManager(createTestDatabaseDriverFactory())
        dao = GameStateDao(databaseManager)
        repository = GameStateRepositoryImpl(dao)
    }

    @AfterTest
    fun tearDown() =
        runTest {
            dao.clearGameState()
        }

    @Test
    fun saveGameState_shouldSaveCompleteState() =
        runTest {
            // Given
            val state = createTestGameState(score = 1000, linesCleared = 10)

            // When
            repository.saveGameState(state)

            // Then
            val loaded = repository.loadGameState()
            assertNotNull(loaded)
            assertEquals(1000, loaded.score)
            assertEquals(10, loaded.linesCleared)
        }

    @Test
    fun saveGameState_shouldSaveWithCurrentPiece() =
        runTest {
            // Given
            val state =
                createTestGameState(
                    currentPiece = Tetromino.create(TetrominoType.T, 1),
                )

            // When
            repository.saveGameState(state)

            // Then
            val loaded = repository.loadGameState()
            assertNotNull(loaded)
            assertNotNull(loaded.currentPiece)
            assertEquals(TetrominoType.T, loaded.currentPiece?.type)
        }

    @Test
    fun saveGameState_shouldSaveWithoutCurrentPiece() =
        runTest {
            // Given
            val state = createTestGameState(currentPiece = null)

            // When
            repository.saveGameState(state)

            // Then
            val loaded = repository.loadGameState()
            assertNotNull(loaded)
            assertNull(loaded.currentPiece)
        }

    @Test
    fun loadGameState_shouldReturnNullWhenNoState() =
        runTest {
            // When
            val loaded = repository.loadGameState()

            // Then
            assertNull(loaded)
        }

    @Test
    fun clearGameState_shouldRemoveState() =
        runTest {
            // Given
            val state = createTestGameState()
            repository.saveGameState(state)

            // When
            repository.clearGameState()

            // Then
            val loaded = repository.loadGameState()
            assertNull(loaded)
        }

    @Test
    fun hasSavedState_shouldReturnTrueWhenStateExists() =
        runTest {
            // Given
            val state = createTestGameState()
            repository.saveGameState(state)

            // When
            val hasSaved = repository.hasSavedState()

            // Then
            assertTrue(hasSaved)
        }

    @Test
    fun hasSavedState_shouldReturnFalseWhenNoState() =
        runTest {
            // When
            val hasSaved = repository.hasSavedState()

            // Then
            assertFalse(hasSaved)
        }

    @Test
    fun saveGameState_shouldReplaceExistingState() =
        runTest {
            // Given
            repository.saveGameState(createTestGameState(score = 1000))

            // When
            repository.saveGameState(createTestGameState(score = 2000))

            // Then
            val loaded = repository.loadGameState()
            assertNotNull(loaded)
            assertEquals(2000, loaded.score)
        }

    private fun createTestGameState(
        score: Long = 0,
        linesCleared: Long = 0,
        currentPiece: Tetromino? = Tetromino.create(TetrominoType.I, 0),
        isGameOver: Boolean = false,
        isPaused: Boolean = false,
    ): GameState {
        val board =
            GameBoard(
                width = 10,
                height = 20,
                cells =
                    mapOf(
                        Position(0, 0) to TetrominoType.I,
                        Position(1, 0) to TetrominoType.O,
                    ),
            )

        return GameState(
            board = board,
            currentPiece = currentPiece,
            currentPosition = Position(5, 3),
            nextPiece = Tetromino.create(TetrominoType.L, 0),
            score = score,
            linesCleared = linesCleared,
            isGameOver = isGameOver,
            isPaused = isPaused,
        )
    }
}
