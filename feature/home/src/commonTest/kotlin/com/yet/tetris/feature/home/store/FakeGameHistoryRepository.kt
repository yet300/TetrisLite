package com.yet.tetris.feature.home.store

import com.yet.tetris.domain.model.history.GameRecord
import com.yet.tetris.domain.repository.GameHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class FakeGameHistoryRepository : GameHistoryRepository {
    private val games = MutableStateFlow<List<GameRecord>>(emptyList())

    override suspend fun saveGame(record: GameRecord) {
        games.value = games.value + record
    }

    override suspend fun getAllGames(): List<GameRecord> = games.value

    override suspend fun getGameById(id: String): GameRecord? = games.value.find { it.id == id }

    override fun observeGames(): Flow<List<GameRecord>> = games.asStateFlow()

    override suspend fun deleteGame(id: String) {
        games.value = games.value.filter { it.id != id }
    }

    override suspend fun clearAllGames() {
        games.value = emptyList()
    }

    fun setGames(records: List<GameRecord>) {
        games.value = records
    }
}
