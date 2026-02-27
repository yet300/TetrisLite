package com.yet.tetris.feature.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.childStackWebNavigation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.webhistory.WebNavigation
import com.arkivanov.decompose.value.Value
import com.yet.tetris.feature.game.GameComponent
import com.yet.tetris.feature.game.di.GAME_COMPONENT_FACTORY_QUALIFIER
import com.yet.tetris.feature.home.HOME_COMPONENT_FACTORY_QUALIFIER
import com.yet.tetris.feature.home.HomeComponent
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class DefaultRootComponent(
    componentContext: ComponentContext,
) : ComponentContext by componentContext,
    RootComponent,
    KoinComponent {
    private val homeComponentFactory: HomeComponent.Factory by inject(
        qualifier = named(HOME_COMPONENT_FACTORY_QUALIFIER),
    )
    private val gameComponentFactory: GameComponent.Factory by inject(
        qualifier = named(GAME_COMPONENT_FACTORY_QUALIFIER),
    )
    private val navigation = StackNavigation<Configuration>()

    private val stack =
        childStack(
            source = navigation,
            serializer = Configuration.serializer(),
            initialConfiguration = Configuration.HomeScreen,
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
                    is RootComponent.Child.Home -> null
                    is RootComponent.Child.Game -> null
                }
            },
        )

    override val childStack: Value<ChildStack<*, RootComponent.Child>>
        get() = stack

    override fun onBackClicked() {
        navigation.pop()
    }

    @OptIn(DelicateDecomposeApi::class)
    private fun createChild(
        config: Configuration,
        componentContext: ComponentContext,
    ): RootComponent.Child =
        when (config) {
            Configuration.HomeScreen ->
                RootComponent.Child.Home(
                    component =
                        homeComponentFactory(
                            componentContext = componentContext,
                            navigateToGame = { navigation.push(Configuration.GameScreen) },
                        ),
                )

            Configuration.GameScreen ->
                RootComponent.Child.Game(
                    component =
                        gameComponentFactory(
                            componentContext = componentContext,
                            navigateBack = { navigation.pop() },
                        ),
                )
        }

    @Serializable
    sealed class Configuration {
        @Serializable
        data object HomeScreen : Configuration()

        @Serializable
        data object GameScreen : Configuration()
    }
}
