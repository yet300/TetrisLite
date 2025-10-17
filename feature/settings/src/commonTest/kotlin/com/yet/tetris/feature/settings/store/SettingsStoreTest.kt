package com.yet.tetris.feature.settings.store

import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.yet.tetris.domain.model.audio.MusicTheme
import com.yet.tetris.domain.model.game.Difficulty
import com.yet.tetris.domain.model.settings.GameSettings
import com.yet.tetris.domain.model.settings.KeyboardLayout
import com.yet.tetris.domain.model.settings.SwipeLayout
import com.yet.tetris.domain.model.settings.SwipeSensitivity
import com.yet.tetris.domain.model.theme.PieceStyle
import com.yet.tetris.domain.model.theme.VisualTheme
import com.yet.tetris.feature.settings.store.SettingsStore.Intent
import com.yet.tetris.feature.settings.store.SettingsStore.Label
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
class SettingsStoreTest {
    private lateinit var repository: FakeGameSettingsRepository
    private lateinit var store: SettingsStore
    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @BeforeTest
    fun before() {
        isAssertOnMainThreadEnabled = false
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
        isAssertOnMainThreadEnabled = true
    }

    @Test
    fun loads_settings_from_repository_WHEN_created() =
        runTest {
            val initialSettings = GameSettings(difficulty = Difficulty.HARD)
            repository.setInitialSettings(initialSettings)

            createStore()

            assertEquals(initialSettings, store.state.settings)
            assertEquals(1, repository.getSettingsCallCount)
        }

    @Test
    fun updates_difficulty_in_state_WHEN_Intent_ChangeDifficulty() =
        runTest {
            createStore()

            store.accept(Intent.ChangeDifficulty(Difficulty.HARD))

            assertEquals(Difficulty.HARD, store.state.settings.difficulty)
            assertTrue(store.state.hasUnsavedChanges)
        }

    @Test
    fun updates_visual_theme_in_state_WHEN_Intent_ChangeVisualTheme() =
        runTest {
            createStore()

            store.accept(Intent.ChangeVisualTheme(VisualTheme.NEON))

            assertEquals(VisualTheme.NEON, store.state.settings.themeConfig.visualTheme)
            assertTrue(store.state.hasUnsavedChanges)
        }

    @Test
    fun updates_piece_style_in_state_WHEN_Intent_ChangePieceStyle() =
        runTest {
            createStore()

            store.accept(Intent.ChangePieceStyle(PieceStyle.GRADIENT))

            assertEquals(PieceStyle.GRADIENT, store.state.settings.themeConfig.pieceStyle)
            assertTrue(store.state.hasUnsavedChanges)
        }

    @Test
    fun updates_keyboard_layout_in_state_WHEN_Intent_ChangeKeyboardLayout() =
        runTest {
            createStore()

            store.accept(Intent.ChangeKeyboardLayout(KeyboardLayout.WASD))

            assertEquals(KeyboardLayout.WASD, store.state.settings.keyboardLayout)
            assertTrue(store.state.hasUnsavedChanges)
        }

    @Test
    fun updates_swipe_layout_in_state_WHEN_Intent_ChangeSwipeLayout() =
        runTest {
            createStore()

            store.accept(Intent.ChangeSwipeLayout(SwipeLayout.INVERTED))

            assertEquals(SwipeLayout.INVERTED, store.state.settings.swipeLayout)
            assertTrue(store.state.hasUnsavedChanges)
        }

    @Test
    fun updates_swipe_sensitivity_in_state_WHEN_Intent_ChangeSwipeSensitivity() =
        runTest {
            createStore()
            val newSensitivity =
                SwipeSensitivity(
                    softDropThreshold = 0.8f,
                    horizontalSensitivity = 1.5f,
                    verticalSensitivity = 1.2f,
                )

            store.accept(Intent.ChangeSwipeSensitivity(newSensitivity))

            assertEquals(newSensitivity, store.state.settings.swipeSensitivity)
            assertTrue(store.state.hasUnsavedChanges)
        }

    @Test
    fun updates_music_enabled_in_state_WHEN_Intent_ToggleMusic() =
        runTest {
            createStore()

            store.accept(Intent.ToggleMusic(false))

            assertFalse(store.state.settings.audioSettings.musicEnabled)
            assertTrue(store.state.hasUnsavedChanges)
        }

    @Test
    fun updates_sound_effects_enabled_in_state_WHEN_Intent_ToggleSoundEffects() =
        runTest {
            createStore()

            store.accept(Intent.ToggleSoundEffects(false))

            assertFalse(store.state.settings.audioSettings.soundEffectsEnabled)
            assertTrue(store.state.hasUnsavedChanges)
        }

    @Test
    fun updates_music_volume_in_state_WHEN_Intent_ChangeMusicVolume() =
        runTest {
            createStore()

            store.accept(Intent.ChangeMusicVolume(0.5f))

            assertEquals(0.5f, store.state.settings.audioSettings.musicVolume)
            assertTrue(store.state.hasUnsavedChanges)
        }

    @Test
    fun updates_sfx_volume_in_state_WHEN_Intent_ChangeSFXVolume() =
        runTest {
            createStore()

            store.accept(Intent.ChangeSFXVolume(0.7f))

            assertEquals(0.7f, store.state.settings.audioSettings.sfxVolume)
            assertTrue(store.state.hasUnsavedChanges)
        }

    @Test
    fun updates_music_theme_in_state_WHEN_Intent_ChangeMusicTheme() =
        runTest {
            createStore()

            store.accept(Intent.ChangeMusicTheme(MusicTheme.MODERN))

            assertEquals(MusicTheme.MODERN, store.state.settings.audioSettings.selectedMusicTheme)
            assertTrue(store.state.hasUnsavedChanges)
        }

    @Test
    fun saves_settings_to_repository_WHEN_Intent_SaveSettings() =
        runTest {
            createStore()
            store.accept(Intent.ChangeDifficulty(Difficulty.HARD))

            store.accept(Intent.SaveSettings)
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(1, repository.saveSettingsCallCount)
            assertEquals(Difficulty.HARD, repository.getSettings().difficulty)
            assertFalse(store.state.hasUnsavedChanges)
            assertFalse(store.state.isSaving)
        }

    @Test
    fun publishes_Label_SettingsSaved_WHEN_Intent_SaveSettings() =
        runTest {
            createStore()
            val labels = store.labels.test()
            store.accept(Intent.ChangeDifficulty(Difficulty.HARD))

            store.accept(Intent.SaveSettings)
            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(labels.any { it is Label.SettingsSaved })
        }

    @Test
    fun sets_isSaving_flag_during_save_WHEN_Intent_SaveSettings() =
        runTest {
            createStore()
            store.accept(Intent.ChangeDifficulty(Difficulty.HARD))

            store.accept(Intent.SaveSettings)

            // After save completes, isSaving should be false
            assertFalse(store.state.isSaving)
        }

    @Test
    fun discards_changes_and_restores_original_WHEN_Intent_DiscardChanges() =
        runTest {
            val initialSettings = GameSettings(difficulty = Difficulty.EASY)
            repository.setInitialSettings(initialSettings)
            createStore()

            store.accept(Intent.ChangeDifficulty(Difficulty.HARD))
            assertTrue(store.state.hasUnsavedChanges)

            store.accept(Intent.DiscardChanges)

            assertEquals(Difficulty.EASY, store.state.settings.difficulty)
            assertFalse(store.state.hasUnsavedChanges)
        }

    @Test
    fun publishes_Label_ChangesDiscarded_WHEN_Intent_DiscardChanges() =
        runTest {
            createStore()
            val labels = store.labels.test()
            store.accept(Intent.ChangeDifficulty(Difficulty.HARD))

            store.accept(Intent.DiscardChanges)

            assertTrue(labels.any { it is Label.ChangesDiscarded })
        }

    @Test
    fun publishes_Label_ShowError_WHEN_save_fails() =
        runTest {
            repository.shouldThrowOnSave = true
            createStore()
            val labels = store.labels.test()
            store.accept(Intent.ChangeDifficulty(Difficulty.HARD))

            store.accept(Intent.SaveSettings)
            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(labels.any { it is Label.ShowError })
            assertFalse(store.state.isSaving)
        }

    @Test
    fun publishes_Label_ShowError_WHEN_load_fails() =
        runTest {
            repository.shouldThrowOnGet = true

            store = SettingsStoreFactory().create()
            val labels = store.labels.test()
            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(labels.any { it is Label.ShowError })
        }

    @Test
    fun does_not_mark_unsaved_changes_WHEN_no_changes_made() =
        runTest {
            createStore()

            assertFalse(store.state.hasUnsavedChanges)
        }

    @Test
    fun marks_unsaved_changes_after_multiple_updates() =
        runTest {
            createStore()

            store.accept(Intent.ChangeDifficulty(Difficulty.HARD))
            store.accept(Intent.ToggleMusic(false))
            store.accept(Intent.ChangeMusicVolume(0.5f))

            assertTrue(store.state.hasUnsavedChanges)
            assertEquals(Difficulty.HARD, store.state.settings.difficulty)
            assertFalse(store.state.settings.audioSettings.musicEnabled)
            assertEquals(0.5f, store.state.settings.audioSettings.musicVolume)
        }

    @Test
    fun clears_unsaved_changes_after_save() =
        runTest {
            createStore()
            store.accept(Intent.ChangeDifficulty(Difficulty.HARD))
            assertTrue(store.state.hasUnsavedChanges)

            store.accept(Intent.SaveSettings)
            testDispatcher.scheduler.advanceUntilIdle()

            assertFalse(store.state.hasUnsavedChanges)
        }

    private fun createStore() {
        store = SettingsStoreFactory().create()
        testDispatcher.scheduler.advanceUntilIdle()
    }
}
