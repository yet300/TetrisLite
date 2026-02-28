package com.yet.tetris.wear

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.arkivanov.decompose.defaultComponentContext
import com.yet.tetris.di.createRootComponent
import com.yet.tetris.wear.ui.WearApp

class WearMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        val appGraph = (application as WearTetrisApp).appGraph
        val root =
            createRootComponent(
                componentContext = defaultComponentContext(),
                graph = appGraph,
            )

        setContent {
            WearApp(root)
        }
    }
}
