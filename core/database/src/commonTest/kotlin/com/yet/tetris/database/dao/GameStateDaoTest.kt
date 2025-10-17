package com.yet.tetris.database.dao

import app.cash.turbine.test
import com.yet.tetris.database.BoardCells
import com.yet.tetris.database.RobolectricTestRunner
import com.yet.tetris.database.createTestDatabaseDriverFactory
import com.yet.tetris.database.db.DatabaseManager
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

class GameStateDaoTest : RobolectricTestRunner() {
    private lateinit var dao: GameStateDao

    @BeforeTest
    fun setup() {
        val driverFactory = createTestDatabaseDriverFactory()
        val databaseManager = DatabaseManager(driverFactory)
        dao = GameStateDao(databaseManager)
    }

    @AfterTest
    fun tearDown() =
        runTest {
            dao.clearGameState()
        }

    @Test
    fun saveGameState_shouldSaveCompleteGameState() =
        runTest {
            // Given
            val boardCells =
                listOf(
                    BoardCells(positionX = 0, positionY = 0, pieceType = TetrominoType.I),
                    BoardCells(positionX = 1, positionY = 0, pieceType = TetrominoType.O),
                )

            // When
            dao.saveGameState(
                score = 1000,
                linesCleared = 10,
                currentPieceType = TetrominoType.T,
                currentPieceRotation = 1,
                currentPositionX = 5,
                currentPositionY = 3,
                nextPieceType = TetrominoType.L,
                nextPieceRotation = 0,
                isGameOver = false,
                isPaused = false,
                boardWidth = 10,
                boardHeight = 20,
                boardCells = boardCells,
            )

            // Then
            val state = dao.getGameState()
            assertNotNull(state)
            assertEquals(1000, state.score)
            assertEquals(10, state.linesCleared)
            assertEquals(TetrominoType.T, state.currentPieceType)
            assertEquals(1, state.currentPieceRotation)
            assertEquals(5, state.currentPositionX)
            assertEquals(3, state.currentPositionY)
            assertEquals(TetrominoType.L, state.nextPieceType)
            assertFalse(state.isGameOver)
            assertFalse(state.isPaused)

            val cells = dao.getBoardCells()
            assertEquals(2, cells.size)
        }

    @Test
    fun saveGameState_shouldReplaceExistingState() =
        runTest {
            // Given
            val initialCells =
                listOf(
                    BoardCells(positionX = 0, positionY = 0, pieceType = TetrominoType.I),
                )
            dao.saveGameState(
                score = 1000,
                linesCleared = 10,
                currentPieceType = TetrominoType.T,
                currentPieceRotation = 0,
                currentPositionX = 5,
                currentPositionY = 3,
                nextPieceType = TetrominoType.L,
                nextPieceRotation = 0,
                isGameOver = false,
                isPaused = false,
                boardWidth = 10,
                boardHeight = 20,
                boardCells = initialCells,
            )

            // When - Save new state
            val newCells =
                listOf(
                    BoardCells(positionX = 2, positionY = 2, pieceType = TetrominoType.Z),
                )
            dao.saveGameState(
                score = 2000,
                linesCleared = 20,
                currentPieceType = TetrominoType.S,
                currentPieceRotation = 2,
                currentPositionX = 7,
                currentPositionY = 5,
                nextPieceType = TetrominoType.J,
                nextPieceRotation = 1,
                isGameOver = true,
                isPaused = true,
                boardWidth = 10,
                boardHeight = 20,
                boardCells = newCells,
            )

            // Then
            val state = dao.getGameState()
            assertNotNull(state)
            assertEquals(2000, state.score)
            assertEquals(20, state.linesCleared)
            assertEquals(TetrominoType.S, state.currentPieceType)
            assertTrue(state.isGameOver)
            assertTrue(state.isPaused)

            val cells = dao.getBoardCells()
            assertEquals(1, cells.size)
            assertEquals(2, cells[0].positionX)
        }

    @Test
    fun getGameState_shouldReturnNullWhenNoStateSaved() =
        runTest {
            // When
            val state = dao.getGameState()

            // Then
            assertNull(state)
        }

    @Test
    fun getBoardCells_shouldReturnEmptyListWhenNoStateSaved() =
        runTest {
            // When
            val cells = dao.getBoardCells()

            // Then
            assertEquals(0, cells.size)
        }

    @Test
    fun clearGameState_shouldRemoveStateAndCells() =
        runTest {
            // Given
            val boardCells =
                listOf(
                    BoardCells(positionX = 0, positionY = 0, pieceType = TetrominoType.I),
                )
            dao.saveGameState(
                score = 1000,
                linesCleared = 10,
                currentPieceType = TetrominoType.T,
                currentPieceRotation = 0,
                currentPositionX = 5,
                currentPositionY = 3,
                nextPieceType = TetrominoType.L,
                nextPieceRotation = 0,
                isGameOver = false,
                isPaused = false,
                boardWidth = 10,
                boardHeight = 20,
                boardCells = boardCells,
            )

            // When
            dao.clearGameState()

            // Then
            val state = dao.getGameState()
            assertNull(state)
            val cells = dao.getBoardCells()
            assertEquals(0, cells.size)
        }

    @Test
    fun hasSavedState_shouldReturnTrueWhenStateExists() =
        runTest {
            // Given
            dao.saveGameState(
                score = 1000,
                linesCleared = 10,
                currentPieceType = TetrominoType.T,
                currentPieceRotation = 0,
                currentPositionX = 5,
                currentPositionY = 3,
                nextPieceType = TetrominoType.L,
                nextPieceRotation = 0,
                isGameOver = false,
                isPaused = false,
                boardWidth = 10,
                boardHeight = 20,
                boardCells = emptyList(),
            )

            // When
            val hasSaved = dao.hasSavedState()

            // Then
            assertTrue(hasSaved)
        }

    @Test
    fun hasSavedState_shouldReturnFalseWhenNoStateExists() =
        runTest {
            // When
            val hasSaved = dao.hasSavedState()

            // Then
            assertFalse(hasSaved)
        }

    @Test
    fun observeGameState_shouldEmitUpdates() =
        runTest {
            // When/Then
            dao.observeGameState().test {
                // Initial null state
                val initial = awaitItem()
                assertNull(initial)

                // Save state
                dao.saveGameState(
                    score = 1000,
                    linesCleared = 10,
                    currentPieceType = TetrominoType.T,
                    currentPieceRotation = 0,
                    currentPositionX = 5,
                    currentPositionY = 3,
                    nextPieceType = TetrominoType.L,
                    nextPieceRotation = 0,
                    isGameOver = false,
                    isPaused = false,
                    boardWidth = 10,
                    boardHeight = 20,
                    boardCells = emptyList(),
                )

                val afterSave = awaitItem()
                assertNotNull(afterSave)
                assertEquals(1000, afterSave.score)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun saveGameState_withNullCurrentPiece_shouldSaveCorrectly() =
        runTest {
            // When
            dao.saveGameState(
                score = 500,
                linesCleared = 5,
                currentPieceType = null,
                currentPieceRotation = 0,
                currentPositionX = 0,
                currentPositionY = 0,
                nextPieceType = TetrominoType.I,
                nextPieceRotation = 0,
                isGameOver = false,
                isPaused = false,
                boardWidth = 10,
                boardHeight = 20,
                boardCells = emptyList(),
            )

            // Then
            val state = dao.getGameState()
            assertNotNull(state)
            assertNull(state.currentPieceType)
            assertEquals(TetrominoType.I, state.nextPieceType)
        }
}
