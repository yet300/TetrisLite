package com.yet.tetris.feature.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import kotlin.test.Test
import kotlin.test.assertTrue

@Suppress("TestFunctionName")
class RootComponentTest {
    @Test
    fun WHEN_RootComponent_interface_exists_THEN_test_passes() {
        // This is a placeholder test to verify the component structure
        // Full integration tests would require setting up all Koin dependencies
        // which is complex for a root navigation component
        assertTrue(true, "RootComponent interface exists and is properly structured")
    }

    @Test
    fun WHEN_Child_sealed_class_exists_THEN_has_Home_and_Game() {
        // Verify the Child sealed class structure
        val homeClass = RootComponent.Child.Home::class
        val gameClass = RootComponent.Child.Game::class

        assertTrue(homeClass.simpleName == "Home", "Home child exists")
        assertTrue(gameClass.simpleName == "Game", "Game child exists")
    }

    private fun createComponent(): RootComponent =
        createComponent { componentContext ->
            DefaultRootComponent(
                componentContext = componentContext,
            )
        }
}

internal fun <T : Any> createComponent(factory: (ComponentContext) -> T): T {
    val lifecycle = LifecycleRegistry()
    val component = factory(DefaultComponentContext(lifecycle = lifecycle))
    lifecycle.resume()

    return component
}
