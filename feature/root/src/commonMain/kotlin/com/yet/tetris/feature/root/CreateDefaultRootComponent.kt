package com.yet.tetris.feature.root

import com.arkivanov.decompose.ComponentContext
import org.koin.mp.KoinPlatform

fun createDefaultRootComponent(componentContext: ComponentContext): RootComponent {
    val rootFactory = KoinPlatform.getKoin().get<RootComponent.Factory>()
    return rootFactory(componentContext)
}
