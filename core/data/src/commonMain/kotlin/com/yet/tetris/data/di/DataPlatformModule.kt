package com.yet.tetris.data.di

import com.yet.tetris.data.music.AudioCacheManager
import com.yet.tetris.domain.repository.AudioRepository

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class DataPlatformModule() {
    fun provideAudioRepository(cacheManager: AudioCacheManager): AudioRepository
}
