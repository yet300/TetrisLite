package com.yet.tetris

import android.app.Application
import com.yet.tetris.di.InitKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class TetrisApp : Application() {
    override fun onCreate() {
        super.onCreate()
        InitKoin {
            androidLogger()
            androidContext(this@TetrisApp)
        }
    }
}
