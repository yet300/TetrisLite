package com.yet.tetris.ui.screens.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.yet.tetris.feature.root.RootComponent
import com.yet.tetris.ui.screens.game.GameScreen
import com.yet.tetris.ui.screens.home.HomeScreen

@Composable
fun RootScreen(component: RootComponent) {
    val childStack by component.childStack.subscribeAsState()

    Children(
        stack = childStack,
    ) { child ->
        when (val instance = child.instance) {
            is RootComponent.Child.Home -> HomeScreen(instance.component)
            is RootComponent.Child.Game -> GameScreen(instance.component)
        }
    }
}
