package com.yet.tetris.wear

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.arkivanov.decompose.defaultComponentContext
import com.yet.tetris.feature.root.createDefaultRootComponent
import com.yet.tetris.wear.ui.WearApp

class WearMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        val root =
            createDefaultRootComponent(
                componentContext = defaultComponentContext(),
            )

        setContent {
            WearApp(root)
        }
    }
}
