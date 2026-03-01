package com.yet.tetris.di

import com.arkivanov.decompose.ComponentContext
import com.yet.tetris.feature.root.RootComponent

fun createRootComponent(
    componentContext: ComponentContext,
    graph: AppGraph,
): RootComponent = graph.rootComponentFactory(componentContext)
