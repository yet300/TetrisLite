package com.yet.tetris.domain.repository

import com.yet.tetris.domain.model.history.GameRecord
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing game history persistence.
 * Stores completed game records with scores and timestamps.
 */
interface GameHistoryRepository {
    /**
     * Saves a completed game record.
     */
    suspend fun saveGame(record: GameRecord)

    /**
     * Retrieves all saved game records.
     * Returns an empty list if no games have been saved.
     */
    suspend fun getAllGames(): List<GameRecord>

    /**
     * Retrieves a specific game record by ID.
     * Returns null if the game is not found.
     */
    suspend fun getGameById(id: String): GameRecord?

    /**
     * Observes changes to the game history.
     * Emits the current list immediately and whenever it changes.
     */
    fun observeGames(): Flow<List<GameRecord>>

    /**
     * Deletes a specific game record by ID.
     */
    suspend fun deleteGame(id: String)

    /**
     * Clears all game history.
     */
    suspend fun clearAllGames()
}
