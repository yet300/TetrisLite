package com.yet.tetris.feature.tab.home

import com.app.common.decompose.asValue
import com.app.common.decompose.coroutineScope
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.yet.tetris.domain.model.game.Difficulty
import com.yet.tetris.feature.tab.home.store.HomeStore
import com.yet.tetris.feature.tab.home.store.HomeStoreFactory
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class DefaultHomeComponent(
    componentContext: ComponentContext,
    private val navigateToGame: () -> Unit,
    private val openSettings: () -> Unit
) : ComponentContext by componentContext, HomeComponent, KoinComponent {
    private val store = instanceKeeper.getStore { HomeStoreFactory().create() }


    init {
        coroutineScope().launch {
            // Handle labels
            store.labels.collect {
                when (it) {
                    is HomeStore.Label.NavigateToGame -> navigateToGame()
                    is HomeStore.Label.OpenSettingsSheet -> openSettings()
                    is HomeStore.Label.ShowError -> {
                        // Handle error (could emit to UI or log)
                    }
                }
            }
        }

    }

    override val model: Value<HomeComponent.Model> = store.asValue().map { state ->
        if (state.isLoading) {
            HomeComponent.Model.Loading
        } else {
            HomeComponent.Model.Content(
                settings = state.settings,
                hasSavedGame = state.hasSavedGame
            )
        }
    }

    override fun onStartNewGame() {
        store.accept(HomeStore.Intent.StartNewGame)
    }

    override fun onResumeGame() {
        store.accept(HomeStore.Intent.ResumeGame)
    }

    override fun onDifficultyChanged(difficulty: Difficulty) {
        store.accept(HomeStore.Intent.ChangeDifficulty(difficulty))
    }

    override fun onOpenSettings() {
        store.accept(HomeStore.Intent.OpenSettings)
    }
}
