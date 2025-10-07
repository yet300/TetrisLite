package com.yet.tetris.feature.history.store

import com.arkivanov.mvikotlin.core.store.Store
import com.yet.tetris.feature.history.DateFilter
import com.yet.tetris.domain.model.history.GameRecord

internal interface HistoryStore :
    Store<HistoryStore.Intent, HistoryStore.State, HistoryStore.Label> {

    data class State(
        val games: List<GameRecord> = emptyList(),
        val filteredGames: List<GameRecord> = emptyList(),
        val dateFilter: DateFilter = DateFilter.ALL,
        val isLoading: Boolean = true
    )

    sealed class Intent {
        data object Refresh : Intent()
        data class FilterByDate(val filter: DateFilter) : Intent()
        data class DeleteGame(val id: String) : Intent()
    }

    sealed class Msg {
        data class GamesLoaded(val games: List<GameRecord>) : Msg()
        data class FilterChanged(val filter: DateFilter, val filteredGames: List<GameRecord>) :
            Msg()

        data class LoadingChanged(val isLoading: Boolean) : Msg()
        data class GameDeleted(val id: String) : Msg()
    }

    sealed class Action {
        data object HistoryLoadStarted : Action()
    }


    sealed class Label {
        data class ShowError(val message: String) : Label()
        data class GameDeleted(val id: String) : Label()
    }

}
