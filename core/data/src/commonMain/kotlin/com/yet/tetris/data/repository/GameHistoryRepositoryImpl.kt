package com.yet.tetris.data.repository

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import com.yet.tetris.data.mapper.toDomain
import com.yet.tetris.data.mapper.toDto
import com.yet.tetris.data.model.GameRecordDto
import com.yet.tetris.domain.model.history.GameRecord
import com.yet.tetris.domain.repository.GameHistoryRepository
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

/**
 * Implementation of GameHistoryRepository using multiplatform-settings.
 * Limits history to 100 games to prevent unbounded growth.
 */
@OptIn(ExperimentalSettingsApi::class)
@Singleton
class GameHistoryRepositoryImpl(
    private val flowSettings: FlowSettings,
    private val json: Json
) : GameHistoryRepository {

    
    companion object {
        private const val KEY_HISTORY = "game_history"
        private const val MAX_HISTORY_SIZE = 100
    }
    
    override suspend fun saveGame(record: GameRecord) {
        try {
            val currentGames = getAllGames().toMutableList()
            
            // Add new record at the beginning
            currentGames.add(0, record)
            
            // Limit to MAX_HISTORY_SIZE
            val limitedGames = currentGames.take(MAX_HISTORY_SIZE)
            
            val dtos = limitedGames.map { it.toDto() }
            val historyJson = json.encodeToString(ListSerializer(serializer<GameRecordDto>()), dtos)
            flowSettings.putString(KEY_HISTORY, historyJson)
        } catch (e: Exception) {
            throw e
        }
    }
    
    override suspend fun getAllGames(): List<GameRecord> {
        return try {
            val historyJson = flowSettings.getStringOrNull(KEY_HISTORY)
            if (historyJson != null) {
                val dtos = json.decodeFromString<List<GameRecordDto>>(historyJson)
                dtos.map { it.toDomain() }.sortedByDescending { it.timestamp }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    override suspend fun getGameById(id: String): GameRecord? {
        return getAllGames().find { it.id == id }
    }
    
    override fun observeGames(): Flow<List<GameRecord>> {
        return flowSettings.getStringOrNullFlow(KEY_HISTORY)
            .map { historyJson ->
                if (historyJson != null) {
                    try {
                        val dtos = json.decodeFromString<List<GameRecordDto>>(historyJson)
                        dtos.map { it.toDomain() }.sortedByDescending { it.timestamp }
                    } catch (e: Exception) {
                        emptyList()
                    }
                } else {
                    emptyList()
                }
            }
    }
    
    override suspend fun deleteGame(id: String) {
        try {
            val currentGames = getAllGames().toMutableList()
            currentGames.removeAll { it.id == id }
            
            val dtos = currentGames.map { it.toDto() }
            val historyJson = json.encodeToString(ListSerializer(serializer<GameRecordDto>()), dtos)
            flowSettings.putString(KEY_HISTORY, historyJson)
        } catch (e: Exception) {
            throw e
        }
    }
    
    override suspend fun clearAllGames() {
        flowSettings.remove(KEY_HISTORY)
    }
}
