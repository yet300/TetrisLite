package com.yet.tetris.database.di

import com.yet.tetris.database.db.DatabaseDriverFactory
import jakarta.inject.Singleton
import org.koin.core.annotation.Module
import org.koin.core.scope.Scope

@Module
actual class DatabasePlatformModule {
    @Singleton
    actual fun provideDatabasePlatformModule(scope: Scope): DatabaseDriverFactory {
     return DatabaseDriverFactory()
    }
}