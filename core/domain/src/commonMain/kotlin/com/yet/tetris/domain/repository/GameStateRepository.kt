package com.yet.tetris.domain.repository

import com.yet.tetris.domain.model.game.GameState

/**
 * Repository interface for managing game state persistence.
 * Used for pause/resume functionality and recovering from app restarts.
 */
interface GameStateRepository {
    
    /**
     * Saves the current game state.
     * Allows the game to be resumed later.
     */
    suspend fun saveGameState(state: GameState)
    
    /**
     * Loads the previously saved game state.
     * Returns null if no saved state exists.
     */
    suspend fun loadGameState(): GameState?
    
    /**
     * Clears the saved game state.
     * Should be called when a game ends or a new game starts.
     */
    suspend fun clearGameState()
    
    /**
     * Checks if a saved game state exists.
     */
    suspend fun hasSavedState(): Boolean
}
