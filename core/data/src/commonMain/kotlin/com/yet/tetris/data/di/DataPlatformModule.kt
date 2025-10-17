package com.yet.tetris.data.di

import com.yet.tetris.data.music.AudioCacheManager
import com.yet.tetris.domain.repository.AudioRepository
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@Module
expect class DataPlatformModule() {
    @Single
    fun provideAudioRepository(cacheManager: AudioCacheManager): AudioRepository
}
