package com.yet.tetris.data.di

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.settings.observable.makeObservable
import com.yet.tetris.data.music.AudioCacheManager
import com.yet.tetris.data.repository.GameHistoryRepositoryImpl
import com.yet.tetris.data.repository.GameSettingsRepositoryImpl
import com.yet.tetris.data.repository.GameStateRepositoryImpl
import com.yet.tetris.database.dao.GameHistoryDao
import com.yet.tetris.database.dao.GameStateDao
import com.yet.tetris.domain.repository.AudioRepository
import com.yet.tetris.domain.repository.GameHistoryRepository
import com.yet.tetris.domain.repository.GameSettingsRepository
import com.yet.tetris.domain.repository.GameStateRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.serialization.json.Json

@ContributesTo(AppScope::class)
@BindingContainer
object DataBindings {
    @SingleIn(AppScope::class)
    @Provides
    fun provideJson(): Json =
        Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        }

    @SingleIn(AppScope::class)
    @Provides
    fun provideSettings(): Settings = Settings()

    @OptIn(ExperimentalSettingsApi::class)
    @SingleIn(AppScope::class)
    @Provides
    fun provideFlowSettings(settings: Settings): FlowSettings {
        val observableSettings: ObservableSettings = settings.makeObservable()
        return observableSettings.toFlowSettings()
    }

    @SingleIn(AppScope::class)
    @Provides
    fun provideAudioCacheManager(): AudioCacheManager = AudioCacheManager()

    @SingleIn(AppScope::class)
    @Provides
    fun provideAudioRepository(cacheManager: AudioCacheManager): AudioRepository =
        DataPlatformModule().provideAudioRepository(cacheManager)

    @OptIn(ExperimentalSettingsApi::class)
    @SingleIn(AppScope::class)
    @Provides
    fun provideGameSettingsRepository(
        flowSettings: FlowSettings,
        json: Json,
    ): GameSettingsRepository = GameSettingsRepositoryImpl(flowSettings = flowSettings, json = json)

    @SingleIn(AppScope::class)
    @Provides
    fun provideGameStateRepository(gameStateDao: GameStateDao): GameStateRepository =
        GameStateRepositoryImpl(gameStateDao)

    @SingleIn(AppScope::class)
    @Provides
    fun provideGameHistoryRepository(gameHistoryDao: GameHistoryDao): GameHistoryRepository =
        GameHistoryRepositoryImpl(gameHistoryDao)
}
