package com.yet.tetris.data.repository

import com.yet.tetris.database.dao.GameHistoryDao
import com.yet.tetris.database.mapper.toDomain
import com.yet.tetris.database.mapper.toEntity
import com.yet.tetris.domain.model.history.GameRecord
import com.yet.tetris.domain.repository.GameHistoryRepository
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementation of GameHistoryRepository using SQLDelight.
 * Limits history to 100 games to prevent unbounded growth.
 */
@Singleton
class GameHistoryRepositoryImpl(
    private val gameHistoryDao: GameHistoryDao,
) : GameHistoryRepository {
    companion object {
        private const val MAX_HISTORY_SIZE = 100L
    }

    override suspend fun saveGame(record: GameRecord) {
        try {
            // Insert the new game
            gameHistoryDao.insertGame(record.toEntity())

            // Check if we need to delete old games
            val count = gameHistoryDao.getGamesCount()
            if (count > MAX_HISTORY_SIZE) {
                val toDelete = count - MAX_HISTORY_SIZE
                gameHistoryDao.deleteOldestGames(toDelete)
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getAllGames(): List<GameRecord> =
        try {
            gameHistoryDao.getAllGames().map { it.toDomain() }
        } catch (e: Exception) {
            emptyList()
        }

    override suspend fun getGameById(id: String): GameRecord? =
        try {
            gameHistoryDao.getGameById(id)?.toDomain()
        } catch (e: Exception) {
            null
        }

    override fun observeGames(): Flow<List<GameRecord>> =
        gameHistoryDao
            .observeAllGames()
            .map { games -> games.map { it.toDomain() } }

    override suspend fun deleteGame(id: String) {
        try {
            gameHistoryDao.deleteGame(id)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun clearAllGames() {
        try {
            gameHistoryDao.clearAllGames()
        } catch (e: Exception) {
            throw e
        }
    }
}
