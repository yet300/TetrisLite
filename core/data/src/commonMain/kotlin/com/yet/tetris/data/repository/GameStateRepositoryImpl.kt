package com.yet.tetris.data.repository

import com.russhwolf.settings.Settings
import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.repository.GameStateRepository
import jakarta.inject.Singleton
import kotlinx.serialization.json.Json



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
            val stateJson = json.encodeToString(state)
            settings.putString(KEY_GAME_STATE, stateJson)
        } catch (e: Exception) {
            throw e
        }
    }
    
    override suspend fun loadGameState(): GameState? {
        return try {
            val stateJson = settings.getStringOrNull(KEY_GAME_STATE)
            if (stateJson != null) {
                json.decodeFromString<GameState>(stateJson)
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
