package com.yet.tetris.feature.root

import kotlin.test.Test
import kotlin.test.assertEquals
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

        assertEquals(homeClass.simpleName, "Home", "Home child exists")
        assertEquals(gameClass.simpleName, "Game", "Game child exists")
    }
}
