package com.yet.tetris.feature.home.integration

import com.yet.tetris.feature.home.HomeComponent
import com.yet.tetris.feature.home.store.HomeStore

internal val stateToModel: (HomeStore.State) -> HomeComponent.Model =
    { state ->
        if (state.isLoading) {
            HomeComponent.Model.Loading
        } else {
            HomeComponent.Model.Content(
                settings = state.settings,
                hasSavedGame = state.hasSavedGame,
            )
        }
    }
