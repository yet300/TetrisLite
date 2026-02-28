package com.yet.tetris.feature.history.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.yet.tetris.domain.repository.GameHistoryRepository
import com.yet.tetris.feature.history.DateFilter
import kotlinx.coroutines.launch

internal class HistoryStoreFactory
    constructor(
        private val storeFactory: StoreFactory,
        private val gameHistoryRepository: GameHistoryRepository,
    ) {
        fun create(): HistoryStore =
            object :
                HistoryStore,
                Store<HistoryStore.Intent, HistoryStore.State, HistoryStore.Label> by storeFactory.create(
                    name = "HistoryStore",
                    initialState = HistoryStore.State(),
                    bootstrapper = SimpleBootstrapper(HistoryStore.Action.HistoryLoadStarted),
                    executorFactory = ::ExecutorImpl,
                    reducer = ReducerImpl,
                ) {}

        private inner class ExecutorImpl :
            CoroutineExecutor<HistoryStore.Intent, HistoryStore.Action, HistoryStore.State, HistoryStore.Msg, HistoryStore.Label>() {
            override fun executeAction(action: HistoryStore.Action) {
                when (action) {
                    HistoryStore.Action.HistoryLoadStarted -> loadHistory()
                }
            }

            override fun executeIntent(intent: HistoryStore.Intent) {
                when (intent) {
                    is HistoryStore.Intent.Refresh -> loadHistory()
                    is HistoryStore.Intent.FilterByDate -> filterByDate(intent.filter)
                    is HistoryStore.Intent.DeleteGame -> deleteGame(intent.id)
                }
            }

            private fun loadHistory() {
                scope.launch {
                    try {
                        dispatch(HistoryStore.Msg.LoadingChanged(true))

                        val games = gameHistoryRepository.getAllGames()
                        dispatch(HistoryStore.Msg.GamesLoaded(games))

                        dispatch(HistoryStore.Msg.LoadingChanged(false))
                    } catch (e: Exception) {
                        dispatch(HistoryStore.Msg.LoadingChanged(false))
                        publish(HistoryStore.Label.ShowError(e.message ?: "Failed to load history"))
                    }
                }
            }

            private fun filterByDate(filter: DateFilter) {
                dispatch(HistoryStore.Msg.FilterChanged(filter))
            }

            private fun deleteGame(id: String) {
                scope.launch {
                    try {
                        gameHistoryRepository.deleteGame(id)
                        dispatch(HistoryStore.Msg.GameDeleted(id))
                        publish(HistoryStore.Label.GameDeleted(id))

                        // Reload to get updated list
                        loadHistory()
                    } catch (e: Exception) {
                        publish(HistoryStore.Label.ShowError(e.message ?: "Failed to delete game"))
                    }
                }
            }
        }

        private object ReducerImpl : Reducer<HistoryStore.State, HistoryStore.Msg> {
            override fun HistoryStore.State.reduce(msg: HistoryStore.Msg): HistoryStore.State =
                when (msg) {
                    is HistoryStore.Msg.GamesLoaded -> copy(games = msg.games)
                    is HistoryStore.Msg.FilterChanged ->
                        copy(
                            dateFilter = msg.filter,
                        )

                    is HistoryStore.Msg.LoadingChanged -> copy(isLoading = msg.isLoading)
                    is HistoryStore.Msg.GameDeleted -> {
                        val updatedGames = games.filter { it.id != msg.id }
                        copy(games = updatedGames)
                    }
                }
        }
    }
