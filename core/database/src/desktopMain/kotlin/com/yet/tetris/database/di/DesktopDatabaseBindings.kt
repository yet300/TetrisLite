package com.yet.tetris.database.di

import com.yet.tetris.database.db.DatabaseDriverFactory
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@ContributesTo(AppScope::class)
@BindingContainer
object DesktopDatabaseBindings {
    @SingleIn(AppScope::class)
    @Provides
    fun provideDatabaseDriverFactory(): DatabaseDriverFactory = DatabaseDriverFactory()
}
