package com.yet.tetris

import androidx.compose.runtime.Composable
import com.yet.tetris.feature.root.RootComponent
import com.yet.tetris.ui.screens.root.RootScreen
import com.yet.tetris.uikit.theme.TetrisLiteAppTheme

@Composable
fun App(rootComponent: RootComponent) {
    TetrisLiteAppTheme {
        RootScreen(rootComponent)
    }
}