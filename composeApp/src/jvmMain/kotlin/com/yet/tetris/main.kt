package com.yet.tetris

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.yet.tetris.di.initKoin
import com.yet.tetris.feature.root.DefaultRootComponent
import java.awt.Dimension
import javax.swing.SwingUtilities

fun main() = application {
    val lifecycle = LifecycleRegistry()

    initKoin()

    val root =
        runOnUiThread {
            DefaultRootComponent(
                componentContext = DefaultComponentContext(lifecycle = lifecycle),
            )
        }

    Window(
        onCloseRequest = ::exitApplication,
        title = "TetrisLite",
    ) {
        window.minimumSize = Dimension(400.dp.value.toInt(), 600.dp.value.toInt())

        App(root)
    }
}

private fun <T> runOnUiThread(block: () -> T): T {
    if (SwingUtilities.isEventDispatchThread()) {
        return block()
    }

    var error: Throwable? = null
    var result: T? = null

    SwingUtilities.invokeAndWait {
        try {
            result = block()
        } catch (e: Throwable) {
            error = e
        }
    }

    error?.also { throw it }

    @Suppress("UNCHECKED_CAST")
    return result as T
}