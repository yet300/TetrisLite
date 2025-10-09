package com.yet.tetris.data.repository

import com.russhwolf.settings.Settings
import com.yet.tetris.data.mapper.toDomain
import com.yet.tetris.data.mapper.toDto
import com.yet.tetris.data.model.GameStateDto
import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.repository.GameStateRepository
import jakarta.inject.Singleton
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

/**
 * Implementation of GameStateRepository using multiplatform-settings.
 * Used for pause/resume functionality.
 */
@Singleton
class GameStateRepositoryImpl(
    private val settings: Settings,
    private val json: Json
) : GameStateRepository {

    companion object {
        private const val KEY_GAME_STATE = "current_game_state"
    }
    
    override suspend fun saveGameState(state: GameState) {
        try {
            val dto = state.toDto()
            val stateJson = json.encodeToString(serializer<GameStateDto>(), dto)
            settings.putString(KEY_GAME_STATE, stateJson)
        } catch (e: Exception) {
            throw e
        }
    }
    
    override suspend fun loadGameState(): GameState? {
        return try {
            val stateJson = settings.getStringOrNull(KEY_GAME_STATE)
            if (stateJson != null) {
                val dto = json.decodeFromString<GameStateDto>(stateJson)
                dto.toDomain()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun clearGameState() {
        settings.remove(KEY_GAME_STATE)
    }
    
    override suspend fun hasSavedState(): Boolean {
        return settings.hasKey(KEY_GAME_STATE)
    }
}
