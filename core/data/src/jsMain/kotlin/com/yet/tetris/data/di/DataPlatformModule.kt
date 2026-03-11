package com.yet.tetris.data.di
import com.app.common.AppDispatchers
import com.yet.tetris.data.music.AudioCacheManager
import com.yet.tetris.data.repository.JsAudioRepositoryImpl
import com.yet.tetris.domain.repository.AudioRepository

actual class DataPlatformModule actual constructor() {
    actual fun provideAudioRepository(
        cacheManager: AudioCacheManager,
        dispatchers: AppDispatchers,
    ): AudioRepository = JsAudioRepositoryImpl(cacheManager, dispatchers)
}
