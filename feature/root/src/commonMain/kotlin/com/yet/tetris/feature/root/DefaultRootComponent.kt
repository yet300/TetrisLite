package com.yet.tetris.feature.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.childStackWebNavigation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.webhistory.WebNavigation
import com.arkivanov.decompose.value.Value
import com.yet.tetris.feature.tab.main.DefaultMainComponent
import com.yet.tetris.feature.game.DefaultGameComponent
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent

class DefaultRootComponent(
    componentContext: ComponentContext,
) : ComponentContext by componentContext, RootComponent, KoinComponent {

    private val navigation = StackNavigation<Configuration>()

    private val stack = childStack(
        source = navigation,
        serializer = Configuration.serializer(),
        initialConfiguration = Configuration.TabScreen,
        childFactory = ::createChild,
    )


    @OptIn(ExperimentalDecomposeApi::class)
    override val webNavigation: WebNavigation<*> =
        childStackWebNavigation(
            navigator = navigation,
            stack = stack,
            serializer = Configuration.serializer(),
            childSelector = {
                when (val child = it.instance) {
                    is RootComponent.Child.Tab -> child.component
                    is RootComponent.Child.Game -> child.component
                }
            },
        )


    override val childStack: Value<ChildStack<*, RootComponent.Child>>
        get() = stack

    override fun onBackClicked() {
        navigation.pop()
    }

    private fun createChild(
        config: Configuration,
        componentContext: ComponentContext,
    ): RootComponent.Child = when (config) {

        Configuration.TabScreen -> RootComponent.Child.Tab(
            component = DefaultMainComponent(
                componentContext = componentContext,
                onGame = { navigation.push(Configuration.GameScreen) },
            )
        )

        Configuration.GameScreen -> RootComponent.Child.Game(
            component = DefaultGameComponent(
                componentContext = componentContext,
                navigateBack = { navigation.pop() },
            )
        )
    }


    @Serializable
    sealed class Configuration {

        @Serializable
        data object TabScreen : Configuration()

        @Serializable
        data object GameScreen : Configuration()

    }
}