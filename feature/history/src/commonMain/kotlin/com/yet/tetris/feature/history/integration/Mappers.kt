package com.yet.tetris.feature.history.integration

import com.yet.tetris.feature.history.HistoryComponent
import com.yet.tetris.feature.history.store.HistoryStore

internal val stateToModel: (HistoryStore.State) -> HistoryComponent.Model =
    { state ->
        if (state.isLoading && state.games.isEmpty()) {
            HistoryComponent.Model.Loading
        } else {
            HistoryComponent.Model.Content(
                games = state.filteredGames,
                currentFilter = state.dateFilter,
            )
        }
    }
