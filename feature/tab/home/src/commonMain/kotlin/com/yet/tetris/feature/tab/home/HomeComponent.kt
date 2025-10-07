package com.yet.tetris.feature.tab.home

import com.arkivanov.decompose.value.Value
import com.yet.tetris.domain.model.game.Difficulty
import com.yet.tetris.domain.model.settings.GameSettings

interface HomeComponent {
    val model: Value<Model>
    
    fun onStartNewGame()
    fun onResumeGame()
    fun onDifficultyChanged(difficulty: Difficulty)
    fun onOpenSettings()
    
    sealed interface Model {
        data object Loading : Model
        data class Content(
            val settings: GameSettings,
            val hasSavedGame: Boolean
        ) : Model
    }
}