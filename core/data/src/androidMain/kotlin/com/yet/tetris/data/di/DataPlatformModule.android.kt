package com.yet.tetris.data.di

import com.yet.tetris.data.music.AudioCacheManager
import com.yet.tetris.data.repository.AndroidAudioRepositoryImpl
import com.yet.tetris.domain.repository.AudioRepository
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
actual class DataPlatformModule actual constructor() {
    @Single
    actual fun provideAudioRepository(cacheManager: AudioCacheManager): AudioRepository = AndroidAudioRepositoryImpl(cacheManager)
}
