package com.yet.tetris

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.webhistory.withWebHistory
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.stop
import com.yet.tetris.di.InitKoin
import com.yet.tetris.feature.root.RootComponent
import com.yet.tetris.feature.root.createDefaultRootComponent
import com.yet.tetris.ui.theme.ThemeModule
import com.yet.tetris.ui.view.root.RootContent
import com.yet.tetris.utils.RProps
import kotlinx.browser.document
import react.FC
import react.create
import react.dom.client.createRoot
import web.dom.DocumentVisibilityState
import web.dom.Element
import web.dom.visible

@OptIn(ExperimentalDecomposeApi::class)
fun main() {
    InitKoin()

    val lifecycle = LifecycleRegistry()

    val root =
        withWebHistory { stateKeeper, _ ->
            createDefaultRootComponent(
                componentContext =
                    DefaultComponentContext(
                        lifecycle = lifecycle,
                        stateKeeper = stateKeeper,
                    ),
            )
        }

    lifecycle.attachToDocument()

    val container = document.getElementById("root") ?: error("Root element not found")

    val reactRoot = createRoot(container.unsafeCast<Element>())

    reactRoot.render(
        children =
            App.create {
                component = root
            },
    )
}

private val App =
    FC<RProps<RootComponent>> { props ->
        ThemeModule {
            RootContent {
                component = props.component
            }
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

    document.addEventListener("visibilitychange", { onVisibilityChanged() })
}
