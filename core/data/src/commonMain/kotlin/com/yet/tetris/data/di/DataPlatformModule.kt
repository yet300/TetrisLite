package com.yet.tetris.data.di

import com.yet.tetris.data.music.AudioCacheManager
import com.yet.tetris.domain.repository.AudioRepository
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
expect class DataPlatformModule() {

    @Single
    fun provideAudioRepository(cacheManager: AudioCacheManager) : AudioRepository
}