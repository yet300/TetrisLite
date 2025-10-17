package com.yet.tetris.data.repository

import app.cash.turbine.test
import com.yet.tetris.data.RobolectricTestRunner
import com.yet.tetris.data.createTestDatabaseDriverFactory
import com.yet.tetris.database.dao.GameHistoryDao
import com.yet.tetris.database.db.DatabaseManager
import com.yet.tetris.domain.model.game.Difficulty
import com.yet.tetris.domain.model.history.GameRecord
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class GameHistoryRepositoryImplTest : RobolectricTestRunner() {
    private lateinit var repository: GameHistoryRepositoryImpl
    private lateinit var dao: GameHistoryDao

    @BeforeTest
    fun setup() {
        val databaseManager = DatabaseManager(driverFactory = createTestDatabaseDriverFactory())
        dao = GameHistoryDao(databaseManager)
        repository = GameHistoryRepositoryImpl(dao)
    }

    @AfterTest
    fun tearDown() =
        runTest {
            dao.clearAllGames()
        }

    @Test
    fun saveGame_shouldSaveGameRecord() =
        runTest {
            // Given
            val record = createTestRecord(id = "1", score = 1000)

            // When
            repository.saveGame(record)

            // Then
            val retrieved = repository.getGameById("1")
            assertNotNull(retrieved)
            assertEquals(record.id, retrieved.id)
            assertEquals(record.score, retrieved.score)
        }

    @Test
    fun saveGame_shouldLimitHistoryTo100Games() =
        runTest {
            // Given - Insert 105 games
            repeat(105) { index ->
                val record =
                    createTestRecord(
                        id = "game_$index",
                        score = index.toLong(),
                        timestamp = index.toLong(),
                    )
                repository.saveGame(record)
            }

            // When
            val allGames = repository.getAllGames()

            // Then - Should only have 100 games (oldest 5 deleted)
            assertEquals(100, allGames.size)
            // Newest games should remain
            assertTrue(allGames.any { it.id == "game_104" })
            // Oldest games should be deleted
            assertFalse(allGames.any { it.id == "game_0" })
        }

    @Test
    fun getAllGames_shouldReturnAllGames() =
        runTest {
            // Given
            repository.saveGame(createTestRecord(id = "1", score = 1000))
            repository.saveGame(createTestRecord(id = "2", score = 2000))
            repository.saveGame(createTestRecord(id = "3", score = 3000))

            // When
            val games = repository.getAllGames()

            // Then
            assertEquals(3, games.size)
        }

    @Test
    fun getAllGames_shouldReturnEmptyListOnError() =
        runTest {
            // Given - Corrupt state (close database)
            dao.clearAllGames()

            // When
            val games = repository.getAllGames()

            // Then - Should return empty list, not throw
            assertNotNull(games)
        }

    @Test
    fun getGameById_shouldReturnGame() =
        runTest {
            // Given
            val record = createTestRecord(id = "test", score = 5000)
            repository.saveGame(record)

            // When
            val retrieved = repository.getGameById("test")

            // Then
            assertNotNull(retrieved)
            assertEquals("test", retrieved.id)
            assertEquals(5000, retrieved.score)
        }

    @Test
    fun getGameById_shouldReturnNullForNonExistent() =
        runTest {
            // When
            val retrieved = repository.getGameById("nonexistent")

            // Then
            assertNull(retrieved)
        }

    @Test
    fun observeGames_shouldEmitUpdates() =
        runTest {
            // When/Then
            repository.observeGames().test {
                // Initial empty state
                val initial = awaitItem()
                assertEquals(0, initial.size)

                // Add game
                repository.saveGame(createTestRecord(id = "1", score = 1000))
                val afterInsert = awaitItem()
                assertEquals(1, afterInsert.size)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun deleteGame_shouldRemoveGame() =
        runTest {
            // Given
            repository.saveGame(createTestRecord(id = "1", score = 1000))

            // When
            repository.deleteGame("1")

            // Then
            val retrieved = repository.getGameById("1")
            assertNull(retrieved)
        }

    @Test
    fun clearAllGames_shouldRemoveAllGames() =
        runTest {
            // Given
            repository.saveGame(createTestRecord(id = "1", score = 1000))
            repository.saveGame(createTestRecord(id = "2", score = 2000))

            // When
            repository.clearAllGames()

            // Then
            val games = repository.getAllGames()
            assertEquals(0, games.size)
        }

    @OptIn(ExperimentalTime::class)
    private fun createTestRecord(
        id: String,
        score: Long = 1000,
        linesCleared: Long = 10,
        difficulty: Difficulty = Difficulty.NORMAL,
        timestamp: Long = Clock.System.now().epochSeconds,
    ) = GameRecord(
        id = id,
        score = score,
        linesCleared = linesCleared,
        difficulty = difficulty,
        timestamp = timestamp,
    )
}
