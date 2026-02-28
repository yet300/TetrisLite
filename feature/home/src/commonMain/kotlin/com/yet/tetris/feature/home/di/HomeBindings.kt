package com.yet.tetris.feature.home.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.yet.tetris.domain.repository.GameSettingsRepository
import com.yet.tetris.domain.repository.GameStateRepository
import com.yet.tetris.feature.history.HistoryComponent
import com.yet.tetris.feature.home.DefaultHomeComponentFactory
import com.yet.tetris.feature.home.HomeComponent
import com.yet.tetris.feature.home.store.HomeStoreFactory
import com.yet.tetris.feature.settings.SettingsComponent
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
@BindingContainer
abstract class HomeBindings {
    @Binds
    internal abstract val DefaultHomeComponentFactory.bindHomeComponentFactory: HomeComponent.Factory

    companion object {
        @Provides
        internal fun provideHomeStoreFactory(
            storeFactory: StoreFactory,
            gameSettingsRepository: GameSettingsRepository,
            gameStateRepository: GameStateRepository,
        ): HomeStoreFactory =
            HomeStoreFactory(
                storeFactory = storeFactory,
                gameSettingsRepository = gameSettingsRepository,
                gameStateRepository = gameStateRepository,
            )

        @Provides
        internal fun provideDefaultHomeComponentFactory(
            homeStoreFactory: HomeStoreFactory,
            settingsComponentFactory: SettingsComponent.Factory,
            historyComponentFactory: HistoryComponent.Factory,
        ): DefaultHomeComponentFactory =
            DefaultHomeComponentFactory(
                homeStoreFactory = homeStoreFactory,
                settingsComponentFactory = settingsComponentFactory,
                historyComponentFactory = historyComponentFactory,
            )
    }
}
