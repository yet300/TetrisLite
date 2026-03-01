package com.yet.tetris.di

import com.yet.tetris.feature.root.RootComponent

interface AppGraph {
    val rootComponentFactory: RootComponent.Factory
}
