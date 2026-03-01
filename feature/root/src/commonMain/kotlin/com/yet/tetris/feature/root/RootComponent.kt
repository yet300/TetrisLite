package com.yet.tetris.feature.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.webhistory.WebNavigationOwner
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import com.yet.tetris.feature.game.GameComponent
import com.yet.tetris.feature.home.HomeComponent

@OptIn(ExperimentalDecomposeApi::class)
interface RootComponent :
    BackHandlerOwner,
    WebNavigationOwner {
    val childStack: Value<ChildStack<*, Child>>

    fun onBackClicked()

    sealed class Child {
        data class Home(
            val component: HomeComponent,
        ) : Child()

        data class Game(
            val component: GameComponent,
        ) : Child()
    }

    fun interface Factory {
        operator fun invoke(componentContext: ComponentContext): RootComponent
    }
}
