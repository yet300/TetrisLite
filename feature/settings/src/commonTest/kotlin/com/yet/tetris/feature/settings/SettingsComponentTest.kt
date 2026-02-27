package com.yet.tetris.feature.settings

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.yet.tetris.domain.model.audio.MusicTheme
import com.yet.tetris.domain.model.game.Difficulty
import com.yet.tetris.domain.model.settings.GameSettings
import com.yet.tetris.domain.model.theme.VisualTheme
import com.yet.tetris.feature.settings.store.FakeGameSettingsRepository
import com.yet.tetris.feature.settings.store.SettingsStoreFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
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
    }

    @AfterTest
    fun after() {
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
        }

    @Test
    fun WHEN_onDifficultyChanged_THEN_model_and_repository_updated() =
        runTest {
            val component = createComponent()
            testDispatcher.scheduler.advanceUntilIdle()

            component.onDifficultyChanged(Difficulty.HARD)
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(Difficulty.HARD, component.model.value.settings.difficulty)
            assertEquals(1, repository.saveSettingsCallCount)
        }

    @Test
    fun WHEN_onVisualThemeChanged_THEN_model_updated() =
        runTest {
            val component = createComponent()
            testDispatcher.scheduler.advanceUntilIdle()

            component.onVisualThemeChanged(VisualTheme.NEON)
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(VisualTheme.NEON, component.model.value.settings.themeConfig.visualTheme)
        }

    @Test
    fun WHEN_audio_settings_changed_THEN_model_reflects_changes() =
        runTest {
            val component = createComponent()
            testDispatcher.scheduler.advanceUntilIdle()

            component.onMusicToggled(false)
            component.onMusicVolumeChanged(0.5f)
            component.onMusicThemeChanged(MusicTheme.MODERN)
            component.onSoundEffectsToggled(false)
            component.onSFXVolumeChanged(0.7f)
            testDispatcher.scheduler.advanceUntilIdle()

            val audio = component.model.value.settings.audioSettings
            assertFalse(audio.musicEnabled)
            assertEquals(0.5f, audio.musicVolume)
            assertEquals(MusicTheme.MODERN, audio.selectedMusicTheme)
            assertFalse(audio.soundEffectsEnabled)
            assertEquals(0.7f, audio.sfxVolume)
        }

    @Test
    fun WHEN_onClose_called_THEN_callback_invoked() =
        runTest {
            var closed = false
            val component =
                createComponent(
                    onClose = { closed = true },
                )
            testDispatcher.scheduler.advanceUntilIdle()

            component.onClose()

            assertTrue(closed)
        }

    private fun createComponent(onClose: () -> Unit = {}): DefaultSettingsComponent {
        val lifecycle = LifecycleRegistry()
        val componentContext = DefaultComponentContext(lifecycle = lifecycle)

        return DefaultSettingsComponent(
            componentContext = componentContext,
            onCloseRequest = onClose,
            settingsStoreFactory = createStoreFactory(),
        )
    }

    private fun createStoreFactory(): SettingsStoreFactory =
        SettingsStoreFactory(
            storeFactory = DefaultStoreFactory(),
            gameSettingsRepository = repository,
        )
}
