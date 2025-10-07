package com.yet.tetris.feature.history

import com.arkivanov.decompose.value.Value
import com.yet.tetris.domain.model.history.GameRecord

interface HistoryComponent {
    val model: Value<Model>
    
    fun onRefresh()
    fun onFilterChanged(filter: DateFilter)
    fun onDeleteGame(id: String)
    
    sealed interface Model {
        data object Loading : Model
        data class Content(
            val games: List<GameRecord>,
            val currentFilter: DateFilter
        ) : Model
    }

}