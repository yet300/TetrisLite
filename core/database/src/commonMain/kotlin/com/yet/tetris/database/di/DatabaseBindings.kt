package com.yet.tetris.database.di

import com.app.common.AppDispatchers
import com.yet.tetris.database.dao.GameHistoryDao
import com.yet.tetris.database.dao.GameStateDao
import com.yet.tetris.database.db.DatabaseDriverFactory
import com.yet.tetris.database.db.DatabaseManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@ContributesTo(AppScope::class)
@BindingContainer
object DatabaseBindings {
    @SingleIn(AppScope::class)
    @Provides
    fun provideDatabaseManager(
        driverFactory: DatabaseDriverFactory,
        dispatchers: AppDispatchers,
    ): DatabaseManager = DatabaseManager(driverFactory, dispatchers)

    @SingleIn(AppScope::class)
    @Provides
    fun provideGameStateDao(
        databaseManager: DatabaseManager,
        dispatchers: AppDispatchers,
    ): GameStateDao = GameStateDao(databaseManager, dispatchers)

    @SingleIn(AppScope::class)
    @Provides
    fun provideGameHistoryDao(
        databaseManager: DatabaseManager,
        dispatchers: AppDispatchers,
    ): GameHistoryDao = GameHistoryDao(databaseManager, dispatchers)
}
