package com.yet.tetris.database.db

import app.cash.sqldelight.db.SqlDriver
import com.yet.tetris.database.TetrisLiteDatabase
import com.yet.tetris.database.utils.enumAdapter
import jakarta.inject.Singleton
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


/**
 * Manages the singleton instance of the database, handling its asynchronous initialization.
 * This class ensures that the database driver is created only once in a thread-safe manner.
 */
@Singleton
class DatabaseManager(private val driverFactory: DatabaseDriverFactory) {

    private var driver: SqlDriver? = null

    // A deferred value that will hold the fully initialized database instance.
    private val deferredDb =
        CompletableDeferred<TetrisLiteDatabase>()

    // A mutex to ensure that the initialization block is executed by only one coroutine at a time.
    private val mutex = Mutex()

    /**
     * Gets the database instance. If it's not initialized yet, this function
     * will suspend until the initialization is complete.
     */
    suspend fun getDb(): TetrisLiteDatabase {
        // If the db is already initialized, return it immediately.
        if (deferredDb.isCompleted) {
            return deferredDb.await()
        }

        // Use a mutex to prevent multiple coroutines from trying to initialize at the same time.
        mutex.withLock {
            // Double-check if another coroutine initialized it while we were waiting for the lock.
            if (!deferredDb.isCompleted) {
                // This is the first coroutine, so it performs the initialization.
                try {
                    val driver = driverFactory.provideDbDriver(TetrisLiteDatabase.Schema)
                    this.driver = driver
                    val database = TetrisLiteDatabase(
                        driver = driver,
                        GameHistoryAdapter = com.yet.tetris.database.GameHistory.Adapter(
                            difficultyAdapter = enumAdapter()
                        ),
                        CurrentGameStateAdapter = com.yet.tetris.database.CurrentGameState.Adapter(
                            currentPieceTypeAdapter = enumAdapter(),
                            nextPieceTypeAdapter = enumAdapter(),
                        ),
                        BoardCellsAdapter = com.yet.tetris.database.BoardCells.Adapter(
                            pieceTypeAdapter = enumAdapter()
                        )
                    )
                    // Once initialized, complete the deferred object with the result.
                    deferredDb.complete(database)
                } catch (e: Exception) {
                    // If initialization fails, complete the deferred object with an exception.
                    deferredDb.completeExceptionally(e)
                }
            }
        }

        // Wait for the initialization to complete (either by us or another coroutine) and return the result.
        return deferredDb.await()
    }

    fun close() {
        driver?.close()
    }
}