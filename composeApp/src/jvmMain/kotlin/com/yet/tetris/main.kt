package com.yet.tetris

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.yet.tetris.di.initKoin

fun main() = application {
    initKoin()

    Window(
        onCloseRequest = ::exitApplication,
        title = "TetrisLite",
    ) {
        App()
    }
}