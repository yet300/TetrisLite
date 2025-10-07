package com.yet.tetris.feature.tab.history.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.yet.tetris.feature.tab.history.DateFilter
import com.yet.tetris.domain.model.history.GameRecord
import com.yet.tetris.domain.repository.GameHistoryRepository
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime

internal class HistoryStoreFactory : KoinComponent {
    
    private val storeFactory: StoreFactory by inject()
    private val gameHistoryRepository: GameHistoryRepository by inject()
    
    fun create(): HistoryStore =
        object : HistoryStore, Store<HistoryStore.Intent, HistoryStore.State, HistoryStore.Label> by storeFactory.create(
            name = "HistoryStore",
            initialState = HistoryStore.State(),
            bootstrapper = SimpleBootstrapper(HistoryStore.Action.HistoryLoadStarted),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    @OptIn(ExperimentalTime::class)
    private inner class ExecutorImpl : CoroutineExecutor<HistoryStore.Intent, HistoryStore.Action, HistoryStore.State, HistoryStore.Msg, HistoryStore.Label>() {

        override fun executeAction(action: HistoryStore.Action) {
            when (action) {
                HistoryStore.Action.HistoryLoadStarted -> loadHistory()
            }
        }
        override fun executeIntent(intent: HistoryStore.Intent) {
            when (intent) {
                is HistoryStore.Intent.Refresh -> loadHistory()
                is HistoryStore.Intent.FilterByDate -> filterByDate(intent.filter,)
                is HistoryStore.Intent.DeleteGame -> deleteGame(intent.id,)
            }
        }
        
        private fun loadHistory() {
            scope.launch {
                try {
                    dispatch(HistoryStore.Msg.LoadingChanged(true))
                    
                    val games = gameHistoryRepository.getAllGames()
                    dispatch(HistoryStore.Msg.GamesLoaded(games))
                    
                    // Apply current filter
                    dispatch(HistoryStore.Msg.LoadingChanged(false))
                } catch (e: Exception) {
                    dispatch(HistoryStore.Msg.LoadingChanged(false))
                    publish(HistoryStore.Label.ShowError(e.message ?: "Failed to load history"))
                }
            }
        }
        
        private fun filterByDate(filter: DateFilter) {
            val state = state()
            val filteredGames = when (filter) {
                DateFilter.ALL -> state.games
                DateFilter.TODAY -> filterToday(state.games)
                DateFilter.THIS_WEEK -> filterThisWeek(state.games)
                DateFilter.THIS_MONTH -> filterThisMonth(state.games)
            }
            dispatch(HistoryStore.Msg.FilterChanged(filter, filteredGames))
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
        

        private fun filterToday(games: List<GameRecord>): List<GameRecord> {
            val now = Clock.System.now()
            val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
            
            return games.filter { game ->
                val gameDate = Instant.fromEpochMilliseconds(game.timestamp)
                    .toLocalDateTime(TimeZone.currentSystemDefault()).date
                gameDate == today
            }
        }
        

        private fun filterThisWeek(games: List<GameRecord>): List<GameRecord> {
            val now = Clock.System.now()
            val weekAgo = now.minus(7.days)
            
            return games.filter { game ->
                game.timestamp >= weekAgo.toEpochMilliseconds()
            }
        }
        

        private fun filterThisMonth(games: List<GameRecord>): List<GameRecord> {
            val now = Clock.System.now()
            val currentMonth =
                now.toLocalDateTime(TimeZone.currentSystemDefault()).month
            val currentYear =
                now.toLocalDateTime(TimeZone.currentSystemDefault()).year
            
            return games.filter { game ->
                val gameDateTime = Instant.fromEpochMilliseconds(game.timestamp)
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                gameDateTime.month == currentMonth && gameDateTime.year == currentYear
            }
        }
    }
    
    private object ReducerImpl : Reducer<HistoryStore.State, HistoryStore.Msg> {
        override fun HistoryStore.State.reduce(msg: HistoryStore.Msg): HistoryStore.State =
            when (msg) {
                is HistoryStore.Msg.GamesLoaded -> {
                    val filtered = when (dateFilter) {
                        DateFilter.ALL -> msg.games
                        else -> filteredGames // Keep current filter
                    }
                    copy(games = msg.games, filteredGames = filtered)
                }
                is HistoryStore.Msg.FilterChanged -> copy(
                    dateFilter = msg.filter,
                    filteredGames = msg.filteredGames
                )
                is HistoryStore.Msg.LoadingChanged -> copy(isLoading = msg.isLoading)
                is HistoryStore.Msg.GameDeleted -> {
                    val updatedGames = games.filter { it.id != msg.id }
                    val updatedFiltered = filteredGames.filter { it.id != msg.id }
                    copy(games = updatedGames, filteredGames = updatedFiltered)
                }
            }
    }
}
