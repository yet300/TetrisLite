package com.yet.tetris.database.db


import com.yet.tetris.database.RobolectricTestRunner
import com.yet.tetris.database.createTestDatabaseDriverFactory
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertSame

class DatabaseManagerTest: RobolectricTestRunner() {

    private val driverFactory = createTestDatabaseDriverFactory()

    private fun createManager() = DatabaseManager(driverFactory)


    @Test
    fun getDb_shouldReturnDatabaseInstance() = runTest {
        // Given
        val manager = createManager()
        
        // When
        val db = manager.getDb()

        // Then
        assertNotNull(db)
    }

    @Test
    fun getDb_shouldReturnSameInstanceOnMultipleCalls() = runTest {
        // Given
        val manager = createManager()
        
        // When
        val db1 = manager.getDb()
        val db2 = manager.getDb()

        // Then
        assertSame(db1, db2)
    }

    @Test
    fun getDb_shouldHandleConcurrentInitialization() = runTest {
        // Given
        val manager = createManager()
        
        // When - Multiple coroutines try to get the database simultaneously
        val deferredDbs = List(10) {
            async { manager.getDb() }
        }
        val databases = deferredDbs.awaitAll()

        // Then - All should get the same instance
        val firstDb = databases.first()
        databases.forEach { db ->
            assertSame(firstDb, db)
        }
    }

    @Test
    fun getDb_shouldInitializeDatabaseSchema() = runTest {
        // Given
        val manager = createManager()
        
        // When
        val db = manager.getDb()

        // Then - Verify tables exist by attempting queries
        val gameHistoryCount = db.gameHistoryQueries.getGamesCount().executeAsOne()
        assertEquals(0, gameHistoryCount)

        val hasSavedState = db.currentGameStateQueries.hasSavedState().executeAsOne()
        assertEquals(false, hasSavedState)
    }
}
