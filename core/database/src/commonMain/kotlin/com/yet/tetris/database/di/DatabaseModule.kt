package com.yet.tetris.database.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module([DatabasePlatformModule::class])
@ComponentScan("com.yet.tetris.database")
class DatabaseModule