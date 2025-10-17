package com.yet.tetris.database.dao

import app.cash.turbine.test
import com.yet.tetris.database.GameHistory
import com.yet.tetris.database.RobolectricTestRunner
import com.yet.tetris.database.createTestDatabaseDriverFactory
import com.yet.tetris.database.db.DatabaseManager
import com.yet.tetris.domain.model.game.Difficulty
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class GameHistoryDaoTest : RobolectricTestRunner() {
    private lateinit var dao: GameHistoryDao

    @BeforeTest
    fun setup() {
        val driverFactory = createTestDatabaseDriverFactory()
        val databaseManager = DatabaseManager(driverFactory)
        dao = GameHistoryDao(databaseManager)
    }

    @AfterTest
    fun tearDown() =
        runTest {
            dao.clearAllGames()
        }

    @Test
    fun insertGame_shouldSaveGameToDatabase() =
        runTest {
            // Given
            val game = createTestGame(id = "1", score = 1000)

            // When
            dao.insertGame(game)

            // Then
            val retrieved = dao.getGameById("1")
            assertNotNull(retrieved)
            assertEquals(game.id, retrieved.id)
            assertEquals(game.score, retrieved.score)
            assertEquals(game.linesCleared, retrieved.linesCleared)
            assertEquals(game.difficulty, retrieved.difficulty)
        }

    @Test
    fun getAllGames_shouldReturnAllGamesOrderedByTimestamp() =
        runTest {
            // Given
            val game1 = createTestGame(id = "1", score = 1000, timestamp = 100)
            val game2 = createTestGame(id = "2", score = 2000, timestamp = 200)
            val game3 = createTestGame(id = "3", score = 3000, timestamp = 150)

            dao.insertGame(game1)
            dao.insertGame(game2)
            dao.insertGame(game3)

            // When
            val games = dao.getAllGames()

            // Then
            assertEquals(3, games.size)
            // Should be ordered by timestamp DESC
            assertEquals("2", games[0].id)
            assertEquals("3", games[1].id)
            assertEquals("1", games[2].id)
        }

    @Test
    fun observeAllGames_shouldEmitUpdates() =
        runTest {
            // Given
            val game1 = createTestGame(id = "1", score = 1000)

            // When/Then
            dao.observeAllGames().test {
                // Initial empty state
                val initial = awaitItem()
                assertEquals(0, initial.size)

                // Insert game
                dao.insertGame(game1)
                val afterInsert = awaitItem()
                assertEquals(1, afterInsert.size)
                assertEquals("1", afterInsert[0].id)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun getGameById_shouldReturnNullForNonExistentGame() =
        runTest {
            // When
            val game = dao.getGameById("nonexistent")

            // Then
            assertNull(game)
        }

    @Test
    fun deleteGame_shouldRemoveGameFromDatabase() =
        runTest {
            // Given
            val game = createTestGame(id = "1", score = 1000)
            dao.insertGame(game)

            // When
            dao.deleteGame("1")

            // Then
            val retrieved = dao.getGameById("1")
            assertNull(retrieved)
        }

    @Test
    fun clearAllGames_shouldRemoveAllGames() =
        runTest {
            // Given
            dao.insertGame(createTestGame(id = "1", score = 1000))
            dao.insertGame(createTestGame(id = "2", score = 2000))

            // When
            dao.clearAllGames()

            // Then
            val games = dao.getAllGames()
            assertEquals(0, games.size)
        }

    @Test
    fun getGamesCount_shouldReturnCorrectCount() =
        runTest {
            // Given
            dao.insertGame(createTestGame(id = "1", score = 1000))
            dao.insertGame(createTestGame(id = "2", score = 2000))
            dao.insertGame(createTestGame(id = "3", score = 3000))

            // When
            val count = dao.getGamesCount()

            // Then
            assertEquals(3, count)
        }

    @Test
    fun deleteOldestGames_shouldRemoveOldestGamesByTimestamp() =
        runTest {
            // Given
            dao.insertGame(createTestGame(id = "1", score = 1000, timestamp = 100))
            dao.insertGame(createTestGame(id = "2", score = 2000, timestamp = 200))
            dao.insertGame(createTestGame(id = "3", score = 3000, timestamp = 300))

            // When
            dao.deleteOldestGames(2)

            // Then
            val remaining = dao.getAllGames()
            assertEquals(1, remaining.size)
            assertEquals("3", remaining[0].id) // Only the newest should remain
        }

    @OptIn(ExperimentalTime::class)
    private fun createTestGame(
        id: String,
        score: Long = 1000,
        linesCleared: Long = 10,
        difficulty: Difficulty = Difficulty.NORMAL,
        timestamp: Long = Clock.System.now().epochSeconds,
    ) = GameHistory(
        id = id,
        score = score,
        linesCleared = linesCleared,
        difficulty = difficulty,
        timestamp = timestamp,
    )
}
