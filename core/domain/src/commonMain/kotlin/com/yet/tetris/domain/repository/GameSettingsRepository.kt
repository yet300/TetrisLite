package com.yet.tetris.domain.repository

import com.yet.tetris.domain.model.settings.GameSettings
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing game settings persistence.
 * Implementations should use platform-specific storage mechanisms.
 */
interface GameSettingsRepository {
    
    /**
     * Retrieves the current game settings.
     * Returns default settings if none are saved.
     */
    suspend fun getSettings(): GameSettings
    
    /**
     * Saves the provided game settings.
     */
    suspend fun saveSettings(settings: GameSettings)
    
    /**
     * Observes changes to game settings.
     * Emits the current settings immediately and whenever they change.
     */
    fun observeSettings(): Flow<GameSettings>
}
