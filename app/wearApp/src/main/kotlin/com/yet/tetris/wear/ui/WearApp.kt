package com.yet.tetris.wear.ui

import androidx.compose.runtime.Composable
import com.yet.tetris.feature.root.RootComponent
import com.yet.tetris.uikit.theme.TetrisLiteAppTheme
import com.yet.tetris.wear.ui.root.WearRootScreen

@Composable
fun WearApp(component: RootComponent) {
    TetrisLiteAppTheme {
        WearRootScreen(component)
    }
}
