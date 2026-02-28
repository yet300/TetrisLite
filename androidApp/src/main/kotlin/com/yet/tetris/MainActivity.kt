package com.yet.tetris

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.defaultComponentContext
import com.yet.tetris.di.createRootComponent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val appGraph = (application as TetrisApp).appGraph

        val root =
            createRootComponent(
                componentContext = defaultComponentContext(),
                graph = appGraph,
            )

        setContent {
            App(root)
        }
    }
}
