package com.yet.tetris.feature.settings.integration

import com.yet.tetris.feature.settings.SettingsComponent
import com.yet.tetris.feature.settings.store.SettingsStore

internal val stateToModel: (SettingsStore.State) -> SettingsComponent.Model =
    { state ->
        SettingsComponent.Model(
            settings = state.settings,
            isSaving = state.isSaving,
        )
    }
