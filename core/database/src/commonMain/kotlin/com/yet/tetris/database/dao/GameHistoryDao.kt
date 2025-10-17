package com.yet.tetris.database.dao

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.yet.tetris.database.GameHistory
import com.yet.tetris.database.db.DatabaseManager
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

@Singleton
class GameHistoryDao(
    private val databaseManager: DatabaseManager,
) {
    suspend fun insertGame(gameHistory: GameHistory) =
        withContext(Dispatchers.Default) {
            databaseManager.getDb().gameHistoryQueries.insertGame(
                id = gameHistory.id,
                score = gameHistory.score,
                linesCleared = gameHistory.linesCleared,
                difficulty = gameHistory.difficulty,
                timestamp = gameHistory.timestamp,
            )
        }

    suspend fun getAllGames(): List<GameHistory> =
        withContext(Dispatchers.Default) {
            databaseManager
                .getDb()
                .gameHistoryQueries
                .getAllGames()
                .awaitAsList()
        }

    fun observeAllGames(): Flow<List<GameHistory>> =
        flow {
            val db = databaseManager.getDb()
            emitAll(
                db.gameHistoryQueries
                    .getAllGames()
                    .asFlow()
                    .mapToList(Dispatchers.Default),
            )
        }

    suspend fun getGameById(id: String): GameHistory? =
        withContext(Dispatchers.Default) {
            databaseManager
                .getDb()
                .gameHistoryQueries
                .getGameById(id)
                .awaitAsOneOrNull()
        }

    suspend fun deleteGame(id: String) =
        withContext(Dispatchers.Default) {
            databaseManager.getDb().gameHistoryQueries.deleteGame(id)
        }

    suspend fun clearAllGames() =
        withContext(Dispatchers.Default) {
            databaseManager.getDb().gameHistoryQueries.clearAllGames()
        }

    suspend fun getGamesCount(): Long =
        withContext(Dispatchers.Default) {
            databaseManager
                .getDb()
                .gameHistoryQueries
                .getGamesCount()
                .awaitAsOne()
        }

    suspend fun deleteOldestGames(count: Long) =
        withContext(Dispatchers.Default) {
            databaseManager.getDb().gameHistoryQueries.deleteOldestGames(count)
        }
}
