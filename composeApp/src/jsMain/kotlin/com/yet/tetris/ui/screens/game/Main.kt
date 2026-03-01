package com.yet.tetris.ui.screens.game

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.webhistory.withWebHistory
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.stop
import com.yet.tetris.App
import com.yet.tetris.di.createJsAppGraph
import com.yet.tetris.di.createRootComponent
import org.jetbrains.skiko.wasm.onWasmReady
import web.dom.DocumentVisibilityState
import web.dom.visible
import web.events.Event
import web.events.EventHandler

@OptIn(ExperimentalDecomposeApi::class, ExperimentalComposeUiApi::class)
fun main() {
    val lifecycle = LifecycleRegistry()
    val appGraph = createJsAppGraph()

    val root =
        withWebHistory { stateKeeper, deepLink ->
            createRootComponent(
                componentContext =
                    DefaultComponentContext(
                        lifecycle = lifecycle,
                        stateKeeper = stateKeeper,
                    ),
                graph = appGraph,
            )
        }

    lifecycle.attachToDocument()

    onWasmReady {
        ComposeViewport(content = {
            App(rootComponent = root)
        })
    }
}

private fun LifecycleRegistry.attachToDocument() {
    fun onVisibilityChanged() {
        if (web.dom.document.visibilityState == DocumentVisibilityState.visible) {
            resume()
        } else {
            stop()
        }
    }

    onVisibilityChanged()

    web.dom.document.onvisibilitychange =
        EventHandler { event: Event ->
            onVisibilityChanged()
        }
}
