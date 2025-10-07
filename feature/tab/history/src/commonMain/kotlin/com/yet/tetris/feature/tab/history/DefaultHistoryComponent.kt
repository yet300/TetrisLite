package com.yet.tetris.feature.tab.history

import com.app.common.decompose.asValue
import com.app.common.decompose.coroutineScope
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.yet.tetris.feature.tab.history.store.HistoryStore
import com.yet.tetris.feature.tab.history.store.HistoryStoreFactory
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DefaultHistoryComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext, HistoryComponent, KoinComponent {

    private val historyStoreFactory: HistoryStoreFactory by inject()
    private val store = instanceKeeper.getStore { historyStoreFactory.create() }

    init {
        coroutineScope().launch {
            // Handle labels
            store.labels.collect {
                when (it) {
                    is HistoryStore.Label.ShowError -> {
                        // Handle error
                    }

                    is HistoryStore.Label.GameDeleted -> {
                        // Could show confirmation
                    }
                }
            }
        }

    }

    override val model: Value<HistoryComponent.Model> = store.asValue().map { state ->
        if (state.isLoading && state.games.isEmpty()) {
            HistoryComponent.Model.Loading
        } else {
            HistoryComponent.Model.Content(
                games = state.filteredGames,
                currentFilter = state.dateFilter
            )
        }
    }

    override fun onRefresh() {
        store.accept(HistoryStore.Intent.Refresh)
    }

    override fun onFilterChanged(filter: DateFilter) {
        store.accept(HistoryStore.Intent.FilterByDate(filter))
    }

    override fun onDeleteGame(id: String) {
        store.accept(HistoryStore.Intent.DeleteGame(id))
    }
}
