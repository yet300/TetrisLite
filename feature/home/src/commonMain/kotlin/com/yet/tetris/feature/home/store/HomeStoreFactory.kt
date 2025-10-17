package com.yet.tetris.feature.home.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.yet.tetris.domain.model.game.Difficulty
import com.yet.tetris.domain.repository.GameSettingsRepository
import com.yet.tetris.domain.repository.GameStateRepository
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class HomeStoreFactory : KoinComponent {
    private val storeFactory: StoreFactory by inject()
    private val gameSettingsRepository: GameSettingsRepository by inject()
    private val gameStateRepository: GameStateRepository by inject()

    fun create(): HomeStore =
        object :
            HomeStore,
            Store<HomeStore.Intent, HomeStore.State, HomeStore.Label> by storeFactory.create(
                name = "HomeStore",
                initialState = HomeStore.State(),
                bootstrapper = SimpleBootstrapper(HomeStore.Action.HomeLoadStarted),
                executorFactory = ::ExecutorImpl,
                reducer = ReducerImpl,
            ) {}

    private object ReducerImpl : Reducer<HomeStore.State, HomeStore.Msg> {
        override fun HomeStore.State.reduce(msg: HomeStore.Msg): HomeStore.State =
            when (msg) {
                is HomeStore.Msg.SettingsLoaded -> copy(settings = msg.settings)
                is HomeStore.Msg.SavedGameStateChanged -> copy(hasSavedGame = msg.hasSavedGame)
                is HomeStore.Msg.DifficultyChanged -> copy(settings = settings.copy(difficulty = msg.difficulty))
                is HomeStore.Msg.LoadingChanged -> copy(isLoading = msg.isLoading)
            }
    }

    private inner class ExecutorImpl :
        CoroutineExecutor<HomeStore.Intent, HomeStore.Action, HomeStore.State, HomeStore.Msg, HomeStore.Label>() {
        override fun executeAction(action: HomeStore.Action) {
            when (action) {
                HomeStore.Action.HomeLoadStarted -> loadInitialData()
            }
        }

        override fun executeIntent(intent: HomeStore.Intent) {
            when (intent) {
                is HomeStore.Intent.StartNewGame -> startNewGame()
                is HomeStore.Intent.ResumeGame -> resumeGame()
                is HomeStore.Intent.ChangeDifficulty -> changeDifficulty(intent.difficulty)
            }
        }

        private fun loadInitialData() {
            scope.launch {
                try {
                    dispatch(HomeStore.Msg.LoadingChanged(true))

                    // Load settings
                    val settings = gameSettingsRepository.getSettings()
                    dispatch(HomeStore.Msg.SettingsLoaded(settings))

                    // Check if there's a saved game
                    val hasSavedGame = gameStateRepository.hasSavedState()
                    dispatch(HomeStore.Msg.SavedGameStateChanged(hasSavedGame))

                    dispatch(HomeStore.Msg.LoadingChanged(false))
                } catch (e: Exception) {
                    dispatch(HomeStore.Msg.LoadingChanged(false))
                    publish(HomeStore.Label.ShowError(e.message ?: "Failed to load data"))
                }
            }
        }

        private fun startNewGame() {
            scope.launch {
                try {
                    // Clear any saved game state
                    gameStateRepository.clearGameState()
                    dispatch(HomeStore.Msg.SavedGameStateChanged(false))

                    // Navigate to game
                    publish(HomeStore.Label.NavigateToGame)
                } catch (e: Exception) {
                    publish(HomeStore.Label.ShowError(e.message ?: "Failed to start game"))
                }
            }
        }

        private fun resumeGame() {
            // Just navigate to game, it will load the saved state
            publish(HomeStore.Label.NavigateToGame)
        }

        private fun changeDifficulty(difficulty: Difficulty) {
            scope.launch {
                try {
                    val updatedSettings = state().settings.copy(difficulty = difficulty)
                    gameSettingsRepository.saveSettings(updatedSettings)
                    dispatch(HomeStore.Msg.DifficultyChanged(difficulty))
                } catch (e: Exception) {
                    publish(HomeStore.Label.ShowError(e.message ?: "Failed to change difficulty"))
                }
            }
        }
    }
}
