package com.yet.tetris.database.di

import com.yet.tetris.database.db.DatabaseDriverFactory
import org.koin.core.scope.Scope
import jakarta.inject.Singleton
import org.koin.core.annotation.Module


@Module
internal expect class DatabasePlatformModule() {

    @Singleton
    fun provideDatabasePlatformModule(scope: Scope): DatabaseDriverFactory
}

