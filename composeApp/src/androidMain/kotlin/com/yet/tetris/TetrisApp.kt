package com.yet.tetris

import android.app.Application
import com.yet.tetris.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class TetrisApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@TetrisApp)
        }
    }
}