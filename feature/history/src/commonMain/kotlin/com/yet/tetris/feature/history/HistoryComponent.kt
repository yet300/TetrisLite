package com.yet.tetris.feature.history

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.yet.tetris.domain.model.history.GameRecord
import com.yet.tetris.domain.model.progression.ProgressionSummary

interface HistoryComponent {
    val model: Value<Model>

    fun onDismiss()

    fun onRefresh()

    fun onFilterChanged(filter: DateFilter)

    fun onDeleteGame(id: String)

    sealed interface Model {
        data object Loading : Model

        data class Content(
            val games: List<GameRecord>,
            val currentFilter: DateFilter,
            val totalGamesCount: Int,
            val progression: ProgressionSummary,
        ) : Model
    }

    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            dismiss: () -> Unit,
        ): HistoryComponent
    }
}
