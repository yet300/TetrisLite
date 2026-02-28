package com.yet.tetris.feature.root

import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("TestFunctionName")
class RootComponentTest {
    @Test
    fun WHEN_Child_sealed_class_exists_THEN_has_Home_and_Game() {
        // Verify the Child sealed class structure
        val homeClass = RootComponent.Child.Home::class
        val gameClass = RootComponent.Child.Game::class

        assertEquals(homeClass.simpleName, "Home", "Home child exists")
        assertEquals(gameClass.simpleName, "Game", "Game child exists")
    }
}
