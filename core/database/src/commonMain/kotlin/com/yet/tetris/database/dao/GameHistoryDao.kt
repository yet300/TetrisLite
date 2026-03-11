package com.yet.tetris.database.dao

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.app.common.AppDispatchers
import com.yet.tetris.database.GameHistory
import com.yet.tetris.database.db.DatabaseManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class GameHistoryDao(
    private val databaseManager: DatabaseManager,
    private val dispatchers: AppDispatchers,
) {
    suspend fun insertGame(gameHistory: GameHistory) =
        withContext(dispatchers.io) {
            databaseManager.getDb().gameHistoryQueries.insertGame(
                id = gameHistory.id,
                score = gameHistory.score,
                linesCleared = gameHistory.linesCleared,
                level = gameHistory.level,
                difficulty = gameHistory.difficulty,
                timestamp = gameHistory.timestamp,
                durationMs = gameHistory.durationMs,
                piecesPlaced = gameHistory.piecesPlaced,
                maxCombo = gameHistory.maxCombo,
                tetrisesCleared = gameHistory.tetrisesCleared,
                tSpinClears = gameHistory.tSpinClears,
                perfectClears = gameHistory.perfectClears,
                hardDrops = gameHistory.hardDrops,
                hardDropCells = gameHistory.hardDropCells,
                softDropCells = gameHistory.softDropCells,
            )
        }

    suspend fun getAllGames(): List<GameHistory> =
        withContext(dispatchers.io) {
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
                    .mapToList(dispatchers.io),
            )
        }

    suspend fun getGameById(id: String): GameHistory? =
        withContext(dispatchers.io) {
            databaseManager
                .getDb()
                .gameHistoryQueries
                .getGameById(id)
                .awaitAsOneOrNull()
        }

    suspend fun deleteGame(id: String) =
        withContext(dispatchers.io) {
            databaseManager.getDb().gameHistoryQueries.deleteGame(id)
        }

    suspend fun clearAllGames() =
        withContext(dispatchers.io) {
            databaseManager.getDb().gameHistoryQueries.clearAllGames()
        }

    suspend fun getGamesCount(): Long =
        withContext(dispatchers.io) {
            databaseManager
                .getDb()
                .gameHistoryQueries
                .getGamesCount()
                .awaitAsOne()
        }

    suspend fun deleteOldestGames(count: Long) =
        withContext(dispatchers.io) {
            databaseManager.getDb().gameHistoryQueries.deleteOldestGames(count)
        }
}
