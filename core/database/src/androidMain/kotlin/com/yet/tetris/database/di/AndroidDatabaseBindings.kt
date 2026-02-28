package com.yet.tetris.database.di

import android.content.Context
import com.yet.tetris.database.db.DatabaseDriverFactory
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@ContributesTo(AppScope::class)
@BindingContainer
object AndroidDatabaseBindings {
    @SingleIn(AppScope::class)
    @Provides
    fun provideDatabaseDriverFactory(appContext: Context): DatabaseDriverFactory =
        DatabaseDriverFactory(appContext)
}
