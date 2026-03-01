package com.yet.tetris.feature.history.di

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.yet.tetris.domain.repository.GameHistoryRepository
import com.yet.tetris.feature.history.DefaultHistoryComponentFactory
import com.yet.tetris.feature.history.HistoryComponent
import com.yet.tetris.feature.history.store.HistoryStoreFactory
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
@BindingContainer
abstract class HistoryBindings {
    @Binds
    internal abstract val DefaultHistoryComponentFactory.bindHistoryComponentFactory: HistoryComponent.Factory

    companion object {
        @Provides
        internal fun provideHistoryStoreFactory(
            storeFactory: StoreFactory,
            gameHistoryRepository: GameHistoryRepository,
        ): HistoryStoreFactory =
            HistoryStoreFactory(
                storeFactory = storeFactory,
                gameHistoryRepository = gameHistoryRepository,
            )

        @Provides
        internal fun provideDefaultHistoryComponentFactory(historyStoreFactory: HistoryStoreFactory): DefaultHistoryComponentFactory =
            DefaultHistoryComponentFactory(historyStoreFactory)
    }
}
