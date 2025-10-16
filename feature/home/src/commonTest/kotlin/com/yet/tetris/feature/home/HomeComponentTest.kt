package com.yet.tetris.feature.home

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.yet.tetris.domain.model.game.Difficulty
import com.yet.tetris.domain.model.settings.GameSettings
import com.yet.tetris.feature.home.store.FakeGameSettingsRepository
import com.yet.tetris.feature.home.store.FakeGameStateRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("TestFunctionName")
class HomeComponentTest {

    private lateinit var settingsRepository: FakeGameSettingsRepository
    private lateinit var gameStateRepository: FakeGameStateRepository
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun before() {
        Dispatchers.setMain(testDispatcher)
        
        settingsRepository = FakeGameSettingsRepository()
        gameStateRepository = FakeGameStateRepository()
        
        startKoin {
            modules(
                module {
                    single<com.arkivanov.mvikotlin.core.store.StoreFactory> { DefaultStoreFactory() }
                    single<com.yet.tetris.domain.repository.GameSettingsRepository> { settingsRepository }
                    single<com.yet.tetris.domain.repository.GameStateRepository> { gameStateRepository }
                }
            )
        }
    }

    @AfterTest
    fun after() {
        stopKoin()
        Dispatchers.resetMain()
    }

    @Test
    fun WHEN_created_THEN_model_is_Content() = runTest {
        val component = createComponent()
        testDispatcher.scheduler.advanceUntilIdle()

        val model = component.model.value
        assertTrue(model is HomeComponent.Model.Content, "Expected Content model but got ${model::class.simpleName}")
    }

    @Test
    fun WHEN_created_THEN_loads_settings() = runTest {
        val settings = GameSettings(difficulty = Difficulty.HARD)
        settingsRepository.setInitialSettings(settings)
        
        val component = createComponent()
        testDispatcher.scheduler.advanceUntilIdle()

        val model = component.model.value as HomeComponent.Model.Content
        assertEquals(settings, model.settings)
    }

    @Test
    fun WHEN_no_saved_game_THEN_hasSavedGame_is_false() = runTest {
        gameStateRepository.setSavedState(null)
        
        val component = createComponent()
        testDispatcher.scheduler.advanceUntilIdle()

        val model = component.model.value as HomeComponent.Model.Content
        assertFalse(model.hasSavedGame)
    }

    @Test
    fun WHEN_onDifficultyChanged_THEN_settings_updated() = runTest {
        val component = createComponent()
        testDispatcher.scheduler.advanceUntilIdle()

        component.onDifficultyChanged(Difficulty.HARD)
        testDispatcher.scheduler.advanceUntilIdle()

        val model = component.model.value as HomeComponent.Model.Content
        assertEquals(Difficulty.HARD, model.settings.difficulty)
    }

    @Test
    fun WHEN_onStartNewGame_THEN_navigateToGame_called() = runTest {
        var navigateCalled = false
        val component = createComponent(navigateToGame = { navigateCalled = true })
        testDispatcher.scheduler.advanceUntilIdle()

        component.onStartNewGame()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(navigateCalled, "navigateToGame should have been called")
    }

    @Test
    fun WHEN_onResumeGame_THEN_navigateToGame_called() = runTest {
        var navigateCalled = false
        val component = createComponent(navigateToGame = { navigateCalled = true })
        testDispatcher.scheduler.advanceUntilIdle()

        component.onResumeGame()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(navigateCalled, "navigateToGame should have been called")
    }

    @Test
    fun WHEN_onOpenSettings_THEN_SettingsChild_active() = runTest {
        val component = createComponent()
        testDispatcher.scheduler.advanceUntilIdle()

        component.onOpenSettings()
        testDispatcher.scheduler.advanceUntilIdle()

        val child = component.childBottomSheetNavigation.value.child
        assertTrue(
            child?.instance is HomeComponent.BottomSheetChild.SettingsChild,
            "Expected SettingsChild but got ${child?.instance}"
        )
    }

    @Test
    fun WHEN_onOpenHistory_THEN_HistoryChild_active() = runTest {
        val component = createComponent()
        testDispatcher.scheduler.advanceUntilIdle()

        component.onOpenHistory()
        testDispatcher.scheduler.advanceUntilIdle()

        val child = component.childBottomSheetNavigation.value.child
        assertTrue(
            child?.instance is HomeComponent.BottomSheetChild.HistoryChild,
            "Expected HistoryChild but got ${child?.instance}"
        )
    }

    @Test
    fun WHEN_onDismissBottomSheet_THEN_no_child_active() = runTest {
        val component = createComponent()
        testDispatcher.scheduler.advanceUntilIdle()

        // Open settings
        component.onOpenSettings()
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(component.childBottomSheetNavigation.value.child != null)

        // Dismiss
        component.onDismissBottomSheet()
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(component.childBottomSheetNavigation.value.child, "Child should be null after dismiss")
    }

    @Test
    fun WHEN_initially_THEN_no_bottom_sheet_child() = runTest {
        val component = createComponent()
        testDispatcher.scheduler.advanceUntilIdle()

        val child = component.childBottomSheetNavigation.value.child
        assertNull(child, "Initially there should be no bottom sheet child")
    }

    @Test
    fun WHEN_open_settings_then_dismiss_THEN_can_open_history() = runTest {
        val component = createComponent()
        testDispatcher.scheduler.advanceUntilIdle()

        // Open settings
        component.onOpenSettings()
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(component.childBottomSheetNavigation.value.child?.instance is HomeComponent.BottomSheetChild.SettingsChild)

        // Dismiss
        component.onDismissBottomSheet()
        testDispatcher.scheduler.advanceUntilIdle()

        // Open history
        component.onOpenHistory()
        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(component.childBottomSheetNavigation.value.child?.instance is HomeComponent.BottomSheetChild.HistoryChild)
    }

    @Test
    fun WHEN_difficulty_changed_multiple_times_THEN_all_changes_reflected() = runTest {
        val component = createComponent()
        testDispatcher.scheduler.advanceUntilIdle()

        component.onDifficultyChanged(Difficulty.HARD)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(Difficulty.HARD, (component.model.value as HomeComponent.Model.Content).settings.difficulty)

        component.onDifficultyChanged(Difficulty.EASY)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(Difficulty.EASY, (component.model.value as HomeComponent.Model.Content).settings.difficulty)
    }

    private fun createComponent(
        navigateToGame: () -> Unit = {}
    ): DefaultHomeComponent {
        val lifecycle = LifecycleRegistry()
        val componentContext = DefaultComponentContext(lifecycle = lifecycle)
        
        return DefaultHomeComponent(
            componentContext = componentContext,
            navigateToGame = navigateToGame
        )
    }
}
