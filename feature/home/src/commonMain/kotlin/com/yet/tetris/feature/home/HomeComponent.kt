package com.yet.tetris.feature.home

import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.Value
import com.yet.tetris.domain.model.game.Difficulty
import com.yet.tetris.domain.model.settings.GameSettings
import com.yet.tetris.feature.history.HistoryComponent
import com.yet.tetris.feature.settings.SettingsComponent

interface HomeComponent {
    val model: Value<Model>

    val childBottomSheetNavigation: Value<ChildSlot<*, BottomSheetChild>>

    fun onDismissBottomSheet()
    fun onStartNewGame()
    fun onResumeGame()
    fun onDifficultyChanged(difficulty: Difficulty)
    fun onOpenSettings()
    fun onOpenHistory()
    
    sealed interface Model {
        data object Loading : Model
        data class Content(
            val settings: GameSettings,
            val hasSavedGame: Boolean
        ) : Model
    }

    sealed interface BottomSheetChild {
        class HistoryChild(val component: HistoryComponent) : BottomSheetChild
        class SettingsChild(val component: SettingsComponent) : BottomSheetChild
    }
}