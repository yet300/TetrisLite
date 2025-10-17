package com.yet.tetris.feature.game.store

import com.yet.tetris.domain.model.settings.GameSettings
import com.yet.tetris.domain.repository.GameSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class FakeGameSettingsRepository : GameSettingsRepository {
    private val _settings = MutableStateFlow(GameSettings())
    private val settingsFlow = _settings.asStateFlow()

    var getSettingsCallCount = 0
        private set

    var saveSettingsCallCount = 0
        private set

    var shouldThrowOnGet = false
    var shouldThrowOnSave = false

    override suspend fun getSettings(): GameSettings {
        getSettingsCallCount++
        if (shouldThrowOnGet) {
            throw Exception("Failed to get settings")
        }
        return _settings.value
    }

    override suspend fun saveSettings(settings: GameSettings) {
        saveSettingsCallCount++
        if (shouldThrowOnSave) {
            throw Exception("Failed to save settings")
        }
        _settings.value = settings
    }

    override fun observeSettings(): Flow<GameSettings> = settingsFlow

    fun setInitialSettings(settings: GameSettings) {
        _settings.value = settings
    }
}
