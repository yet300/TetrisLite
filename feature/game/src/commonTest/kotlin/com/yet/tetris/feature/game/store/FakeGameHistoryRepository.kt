package com.yet.tetris.feature.game.store

import com.yet.tetris.domain.model.history.GameRecord
import com.yet.tetris.domain.repository.GameHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class FakeGameHistoryRepository : GameHistoryRepository {
    private val games = MutableStateFlow<List<GameRecord>>(emptyList())
    private val gamesFlow = games.asStateFlow()

    var saveGameCallCount = 0
        private set

    var getAllGamesCallCount = 0
        private set

    var getGameByIdCallCount = 0
        private set

    var deleteGameCallCount = 0
        private set

    var clearAllGamesCallCount = 0
        private set

    var shouldThrowOnSave = false
    var shouldThrowOnGetAll = false
    var shouldThrowOnGetById = false
    var shouldThrowOnDelete = false
    var shouldThrowOnClear = false

    override suspend fun saveGame(record: GameRecord) {
        saveGameCallCount++
        if (shouldThrowOnSave) {
            throw Exception("Failed to save game")
        }
        games.value = games.value + record
    }

    override suspend fun getAllGames(): List<GameRecord> {
        getAllGamesCallCount++
        if (shouldThrowOnGetAll) {
            throw Exception("Failed to get all games")
        }
        return games.value
    }

    override suspend fun getGameById(id: String): GameRecord? {
        getGameByIdCallCount++
        if (shouldThrowOnGetById) {
            throw Exception("Failed to get game by id")
        }
        return games.value.find { it.id == id }
    }

    override fun observeGames(): Flow<List<GameRecord>> = gamesFlow

    override suspend fun deleteGame(id: String) {
        deleteGameCallCount++
        if (shouldThrowOnDelete) {
            throw Exception("Failed to delete game")
        }
        games.value = games.value.filter { it.id != id }
    }

    override suspend fun clearAllGames() {
        clearAllGamesCallCount++
        if (shouldThrowOnClear) {
            throw Exception("Failed to clear all games")
        }
        games.value = emptyList()
    }

    fun setGames(games: List<GameRecord>) {
        this.games.value = games
    }
}
