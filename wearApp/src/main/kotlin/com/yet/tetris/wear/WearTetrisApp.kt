package com.yet.tetris.wear

import android.app.Application
import com.yet.tetris.di.AppGraph
import com.yet.tetris.di.createAndroidAppGraph

class WearTetrisApp : Application() {
    val appGraph: AppGraph by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        createAndroidAppGraph(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()
        appGraph
    }
}
