package com.yet.tetris

import androidx.compose.runtime.Composable
import com.yet.tetris.feature.root.PreviewRootComponent
import com.yet.tetris.feature.root.RootComponent
import com.yet.tetris.ui.screens.root.RootScreen
import com.yet.tetris.uikit.theme.TetrisLiteAppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(rootComponent: RootComponent = PreviewRootComponent()) {
    TetrisLiteAppTheme {
        RootScreen(rootComponent)
    }
}
