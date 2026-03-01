package com.yet.tetris.data.di

import com.yet.tetris.data.music.AudioCacheManager
import com.yet.tetris.data.repository.IosAudioRepositoryImpl
import com.yet.tetris.domain.repository.AudioRepository

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DataPlatformModule actual constructor() {
    actual fun provideAudioRepository(cacheManager: AudioCacheManager): AudioRepository = IosAudioRepositoryImpl(cacheManager)
}
