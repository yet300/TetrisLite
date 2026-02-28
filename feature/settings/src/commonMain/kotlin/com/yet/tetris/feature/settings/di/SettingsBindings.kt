package com.yet.tetris.feature.settings.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.yet.tetris.domain.repository.GameSettingsRepository
import com.yet.tetris.feature.settings.DefaultSettingsComponentFactory
import com.yet.tetris.feature.settings.SettingsComponent
import com.yet.tetris.feature.settings.store.SettingsStoreFactory
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
@BindingContainer
abstract class SettingsBindings {
    @Binds
    internal abstract val DefaultSettingsComponentFactory.bindSettingsComponentFactory: SettingsComponent.Factory

    companion object {
        @Provides
        internal fun provideSettingsStoreFactory(
            storeFactory: StoreFactory,
            gameSettingsRepository: GameSettingsRepository,
        ): SettingsStoreFactory =
            SettingsStoreFactory(
                storeFactory = storeFactory,
                gameSettingsRepository = gameSettingsRepository,
            )

        @Provides
        internal fun provideDefaultSettingsComponentFactory(settingsStoreFactory: SettingsStoreFactory): DefaultSettingsComponentFactory =
            DefaultSettingsComponentFactory(settingsStoreFactory)
    }
}
