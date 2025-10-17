package com.yet.tetris.feature.home.store

import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.repository.GameStateRepository

internal class FakeGameStateRepository : GameStateRepository {
    private var savedState: GameState? = null

    var saveGameStateCallCount = 0
        private set

    var loadGameStateCallCount = 0
        private set

    var clearGameStateCallCount = 0
        private set

    var hasSavedStateCallCount = 0
        private set

    var shouldThrowOnSave = false
    var shouldThrowOnLoad = false
    var shouldThrowOnClear = false
    var shouldThrowOnHasSaved = false

    override suspend fun saveGameState(state: GameState) {
        saveGameStateCallCount++
        if (shouldThrowOnSave) {
            throw Exception("Failed to save game state")
        }
        savedState = state
    }

    override suspend fun loadGameState(): GameState? {
        loadGameStateCallCount++
        if (shouldThrowOnLoad) {
            throw Exception("Failed to load game state")
        }
        return savedState
    }

    override suspend fun clearGameState() {
        clearGameStateCallCount++
        if (shouldThrowOnClear) {
            throw Exception("Failed to clear game state")
        }
        savedState = null
    }

    override suspend fun hasSavedState(): Boolean {
        hasSavedStateCallCount++
        if (shouldThrowOnHasSaved) {
            throw Exception("Failed to check saved state")
        }
        return savedState != null
    }

    fun setSavedState(state: GameState?) {
        savedState = state
    }
}
