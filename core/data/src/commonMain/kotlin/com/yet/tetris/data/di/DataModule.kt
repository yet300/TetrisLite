package com.yet.tetris.data.di

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.settings.observable.makeObservable
import com.yet.tetris.database.di.DatabaseModule
import kotlinx.serialization.json.Json
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module([DatabaseModule::class, DataPlatformModule::class])
@ComponentScan("com.yet.tetris.data")
class DataModule {
    @Single
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    @Single
    fun provideSettings(): Settings = Settings()

    @OptIn(ExperimentalSettingsApi::class)
    @Single
    fun provideFlowSettings(settings: Settings): FlowSettings {
        val observableSettings: ObservableSettings = settings.makeObservable()
        return observableSettings.toFlowSettings()
    }
}