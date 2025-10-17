package com.yet.tetris.database.di

import com.yet.tetris.database.db.DatabaseDriverFactory
import jakarta.inject.Singleton
import org.koin.core.annotation.Module
import org.koin.core.scope.Scope

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@Module
internal expect class DatabasePlatformModule() {
    @Singleton
    fun provideDatabasePlatformModule(scope: Scope): DatabaseDriverFactory
}
