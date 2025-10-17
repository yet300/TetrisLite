package com.yet.tetris.data.di

import com.yet.tetris.data.music.AudioCacheManager
import com.yet.tetris.data.repository.IosAudioRepositoryImpl
import com.yet.tetris.domain.repository.AudioRepository
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
@Module
actual class DataPlatformModule actual constructor() {
    @Single
    actual fun provideAudioRepository(cacheManager: AudioCacheManager): AudioRepository = IosAudioRepositoryImpl(cacheManager)
}
