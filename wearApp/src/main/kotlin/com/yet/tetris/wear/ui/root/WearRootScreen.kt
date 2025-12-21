package com.yet.tetris.wear.ui.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.yet.tetris.feature.root.RootComponent
import com.yet.tetris.wear.ui.game.WearGameScreen
import com.yet.tetris.wear.ui.home.WearHomeScreen

@Composable
fun WearRootScreen(component: RootComponent) {
    val childStack by component.childStack.subscribeAsState()

    Children(stack = childStack) { child ->
        when (val instance = child.instance) {
            is RootComponent.Child.Home -> WearHomeScreen(instance.component)
            is RootComponent.Child.Game -> WearGameScreen(instance.component)
        }
    }
}
