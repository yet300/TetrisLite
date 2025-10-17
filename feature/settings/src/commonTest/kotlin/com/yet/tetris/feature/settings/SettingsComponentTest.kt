package com.yet.tetris.feature.settings

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.yet.tetris.domain.model.audio.MusicTheme
import com.yet.tetris.domain.model.game.Difficulty
import com.yet.tetris.domain.model.settings.GameSettings
import com.yet.tetris.domain.model.settings.KeyboardLayout
import com.yet.tetris.domain.model.settings.SwipeLayout
import com.yet.tetris.domain.model.settings.SwipeSensitivity
import com.yet.tetris.domain.model.theme.PieceStyle
import com.yet.tetris.domain.model.theme.VisualTheme
import com.yet.tetris.feature.settings.store.FakeGameSettingsRepository
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("TestFunctionName")
class SettingsComponentTest {
    private lateinit var repository: FakeGameSettingsRepository
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun before() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeGameSettingsRepository()

        startKoin {
            modules(
                module {
                    single<com.arkivanov.mvikotlin.core.store.StoreFactory> { DefaultStoreFactory() }
                    single<com.yet.tetris.domain.repository.GameSettingsRepository> { repository }
                },
            )
        }
    }

    @AfterTest
    fun after() {
        stopKoin()
        Dispatchers.resetMain()
    }

    @Test
    fun WHEN_created_THEN_model_contains_settings() =
        runTest {
            val component = createComponent()
            testDispatcher.scheduler.advanceUntilIdle()

            val model = component.model.value
            assertEquals(GameSettings(), model.settings)
            assertFalse(model.isSaving)
            assertFalse(model.hasUnsavedChanges)
        }

    @Test
    fun WHEN_onDifficultyChanged_THEN_model_updated() =
        runTest {
            val component = createComponent()
            testDispatcher.scheduler.advanceUntilIdle()

            component.onDifficultyChanged(Difficulty.HARD)
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(Difficulty.HARD, component.model.value.settings.difficulty)
            assertTrue(component.model.value.hasUnsavedChanges)
        }

    @Test
    fun WHEN_onVisualThemeChanged_THEN_model_updated() =
        runTest {
            val component = createComponent()
            testDispatcher.scheduler.advanceUntilIdle()

            component.onVisualThemeChanged(VisualTheme.NEON)
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(VisualTheme.NEON, component.model.value.settings.themeConfig.visualTheme)
            assertTrue(component.model.value.hasUnsavedChanges)
        }

    @Test
    fun WHEN_onPieceStyleChanged_THEN_model_updated() =
        runTest {
            val component = createComponent()
            testDispatcher.scheduler.advanceUntilIdle()

            component.onPieceStyleChanged(PieceStyle.GRADIENT)
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(PieceStyle.GRADIENT, component.model.value.settings.themeConfig.pieceStyle)
            assertTrue(component.model.value.hasUnsavedChanges)
        }

    @Test
    fun WHEN_onKeyboardLayoutChanged_THEN_model_updated() =
        runTest {
            val component = createComponent()
            testDispatcher.scheduler.advanceUntilIdle()

            component.onKeyboardLayoutChanged(KeyboardLayout.WASD)
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(KeyboardLayout.WASD, component.model.value.settings.keyboardLayout)
            assertTrue(component.model.value.hasUnsavedChanges)
        }

    @Test
    fun WHEN_onSwipeLayoutChanged_THEN_model_updated() =
        runTest {
            val component = createComponent()
            testDispatcher.scheduler.advanceUntilIdle()

            component.onSwipeLayoutChanged(SwipeLayout.INVERTED)
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(SwipeLayout.INVERTED, component.model.value.settings.swipeLayout)
            assertTrue(component.model.value.hasUnsavedChanges)
        }

    @Test
    fun WHEN_onSwipeSensitivityChanged_THEN_model_updated() =
        runTest {
            val component = createComponent()
            testDispatcher.scheduler.advanceUntilIdle()
            val newSensitivity =
                SwipeSensitivity(
                    softDropThreshold = 0.8f,
                    horizontalSensitivity = 1.5f,
                    verticalSensitivity = 1.2f,
                )

            component.onSwipeSensitivityChanged(newSensitivity)
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(newSensitivity, component.model.value.settings.swipeSensitivity)
            assertTrue(component.model.value.hasUnsavedChanges)
        }

    @Test
    fun WHEN_onMusicToggled_THEN_model_updated() =
        runTest {
            val component = createComponent()
            testDispatcher.scheduler.advanceUntilIdle()

            component.onMusicToggled(false)
            testDispatcher.scheduler.advanceUntilIdle()

            assertFalse(component.model.value.settings.audioSettings.musicEnabled)
            assertTrue(component.model.value.hasUnsavedChanges)
        }

    @Test
    fun WHEN_onSoundEffectsToggled_THEN_model_updated() =
        runTest {
            val component = createComponent()
            testDispatcher.scheduler.advanceUntilIdle()

            component.onSoundEffectsToggled(false)
            testDispatcher.scheduler.advanceUntilIdle()

            assertFalse(component.model.value.settings.audioSettings.soundEffectsEnabled)
            assertTrue(component.model.value.hasUnsavedChanges)
        }

    @Test
    fun WHEN_onMusicVolumeChanged_THEN_model_updated() =
        runTest {
            val component = createComponent()
            testDispatcher.scheduler.advanceUntilIdle()

            component.onMusicVolumeChanged(0.5f)
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(0.5f, component.model.value.settings.audioSettings.musicVolume)
            assertTrue(component.model.value.hasUnsavedChanges)
        }

    @Test
    fun WHEN_onSFXVolumeChanged_THEN_model_updated() =
        runTest {
            val component = createComponent()
            testDispatcher.scheduler.advanceUntilIdle()

            component.onSFXVolumeChanged(0.7f)
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(0.7f, component.model.value.settings.audioSettings.sfxVolume)
            assertTrue(component.model.value.hasUnsavedChanges)
        }

    @Test
    fun WHEN_onMusicThemeChanged_THEN_model_updated() =
        runTest {
            val component = createComponent()
            testDispatcher.scheduler.advanceUntilIdle()

            component.onMusicThemeChanged(MusicTheme.MODERN)
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(MusicTheme.MODERN, component.model.value.settings.audioSettings.selectedMusicTheme)
            assertTrue(component.model.value.hasUnsavedChanges)
        }

    @Test
    fun WHEN_onSave_THEN_onSettingsSaved_and_onDismiss_called() =
        runTest {
            var settingsSavedCalled = false
            var dismissCalled = false
            val component =
                createComponent(
                    onSettingsSaved = { settingsSavedCalled = true },
                    onDismiss = { dismissCalled = true },
                )
            testDispatcher.scheduler.advanceUntilIdle()

            component.onDifficultyChanged(Difficulty.HARD)
            testDispatcher.scheduler.advanceUntilIdle()

            component.onSave()
            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(settingsSavedCalled)
            assertTrue(dismissCalled)
        }

    @Test
    fun WHEN_onDiscard_THEN_onDismiss_called() =
        runTest {
            var dismissCalled = false
            val component =
                createComponent(
                    onDismiss = { dismissCalled = true },
                )
            testDispatcher.scheduler.advanceUntilIdle()

            component.onDifficultyChanged(Difficulty.HARD)
            testDispatcher.scheduler.advanceUntilIdle()

            component.onDiscard()
            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(dismissCalled)
        }

    @Test
    fun WHEN_onDiscard_THEN_changes_reverted() =
        runTest {
            val initialSettings = GameSettings(difficulty = Difficulty.EASY)
            repository.setInitialSettings(initialSettings)
            val component = createComponent()
            testDispatcher.scheduler.advanceUntilIdle()

            component.onDifficultyChanged(Difficulty.HARD)
            testDispatcher.scheduler.advanceUntilIdle()
            assertTrue(component.model.value.hasUnsavedChanges)

            component.onDiscard()
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(Difficulty.EASY, component.model.value.settings.difficulty)
            assertFalse(component.model.value.hasUnsavedChanges)
        }

    @Test
    fun WHEN_multiple_changes_THEN_all_reflected_in_model() =
        runTest {
            val component = createComponent()
            testDispatcher.scheduler.advanceUntilIdle()

            component.onDifficultyChanged(Difficulty.HARD)
            component.onMusicToggled(false)
            component.onMusicVolumeChanged(0.5f)
            testDispatcher.scheduler.advanceUntilIdle()

            val model = component.model.value
            assertEquals(Difficulty.HARD, model.settings.difficulty)
            assertFalse(model.settings.audioSettings.musicEnabled)
            assertEquals(0.5f, model.settings.audioSettings.musicVolume)
            assertTrue(model.hasUnsavedChanges)
        }

    @Test
    fun WHEN_onSave_THEN_settings_saved_to_repository() =
        runTest {
            val component = createComponent()
            testDispatcher.scheduler.advanceUntilIdle()

            component.onDifficultyChanged(Difficulty.HARD)
            testDispatcher.scheduler.advanceUntilIdle()

            component.onSave()
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(1, repository.saveSettingsCallCount)
            assertEquals(Difficulty.HARD, repository.getSettings().difficulty)
        }

    private fun createComponent(
        onSettingsSaved: () -> Unit = {},
        onDismiss: () -> Unit = {},
    ): DefaultSettingsComponent {
        val lifecycle = LifecycleRegistry()
        val componentContext = DefaultComponentContext(lifecycle = lifecycle)

        return DefaultSettingsComponent(
            componentContext = componentContext,
            onSettingsSaved = onSettingsSaved,
            onDismiss = onDismiss,
        )
    }
}
