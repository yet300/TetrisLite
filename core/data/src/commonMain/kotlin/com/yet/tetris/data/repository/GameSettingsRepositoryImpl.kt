package com.yet.tetris.data.repository

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import com.yet.tetris.domain.model.settings.GameSettings
import com.yet.tetris.domain.repository.GameSettingsRepository
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

/**
 * Implementation of GameSettingsRepository using multiplatform-settings.
 */
@OptIn(ExperimentalSettingsApi::class)
@Singleton
class GameSettingsRepositoryImpl(
    private val flowSettings: FlowSettings,
    private val json: Json
) : GameSettingsRepository {
    
    companion object {
        private const val KEY_SETTINGS = "game_settings"
    }
    
    override suspend fun getSettings(): GameSettings {
        return try {
            val settingsJson = flowSettings.getStringOrNull(KEY_SETTINGS)
            if (settingsJson != null) {
                json.decodeFromString<GameSettings>(settingsJson)
            } else {
                GameSettings() // Return default settings
            }
        } catch (e: Exception) {
            // If deserialization fails, return default settings
            GameSettings()
        }
    }
    
    override suspend fun saveSettings(settings: GameSettings) {
        try {
            val settingsJson = json.encodeToString(settings)
            flowSettings.putString(KEY_SETTINGS, settingsJson)
        } catch (e: Exception) {
            // Log error in production
            throw e
        }
    }
    
    override fun observeSettings(): Flow<GameSettings> {
        return flowSettings.getStringOrNullFlow(KEY_SETTINGS)
            .map { settingsJson ->
                if (settingsJson != null) {
                    try {
                        json.decodeFromString<GameSettings>(settingsJson)
                    } catch (e: Exception) {
                        GameSettings()
                    }
                } else {
                    GameSettings()
                }
            }
    }
}
