package com.yet.tetris.feature.settings.store

import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.yet.tetris.domain.model.audio.MusicTheme
import com.yet.tetris.domain.model.game.Difficulty
import com.yet.tetris.domain.model.settings.GameSettings
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

    @BeforeTest
    fun before() {
        isAssertOnMainThreadEnabled = false
        Dispatchers.setMain(testDispatcher)
        repository = FakeGameSettingsRepository()
    }

    @AfterTest
    fun after() {
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
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(Difficulty.HARD, store.state.settings.difficulty)
            assertEquals(1, repository.saveSettingsCallCount)
        }

    @Test
    fun updates_visual_theme_in_state_WHEN_Intent_ChangeVisualTheme() =
        runTest {
            createStore()

            store.accept(Intent.ChangeVisualTheme(VisualTheme.NEON))
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(VisualTheme.NEON, store.state.settings.themeConfig.visualTheme)
            assertEquals(1, repository.saveSettingsCallCount)
        }

    @Test
    fun updates_piece_style_in_state_WHEN_Intent_ChangePieceStyle() =
        runTest {
            createStore()

            store.accept(Intent.ChangePieceStyle(PieceStyle.GRADIENT))
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(PieceStyle.GRADIENT, store.state.settings.themeConfig.pieceStyle)
            assertEquals(1, repository.saveSettingsCallCount)
        }

    @Test
    fun updates_audio_settings_WHEN_corresponding_Intents_sent() =
        runTest {
            createStore()

            store.accept(Intent.ToggleMusic(false))
            testDispatcher.scheduler.advanceUntilIdle()
            store.accept(Intent.ChangeMusicVolume(0.5f))
            testDispatcher.scheduler.advanceUntilIdle()
            store.accept(Intent.ChangeMusicTheme(MusicTheme.MODERN))
            testDispatcher.scheduler.advanceUntilIdle()
            store.accept(Intent.ToggleSoundEffects(false))
            testDispatcher.scheduler.advanceUntilIdle()
            store.accept(Intent.ChangeSFXVolume(0.7f))
            testDispatcher.scheduler.advanceUntilIdle()

            val audio = store.state.settings.audioSettings
            assertFalse(audio.musicEnabled)
            assertEquals(0.5f, audio.musicVolume)
            assertEquals(MusicTheme.MODERN, audio.selectedMusicTheme)
            assertFalse(audio.soundEffectsEnabled)
            assertEquals(0.7f, audio.sfxVolume)
            assertEquals(5, repository.saveSettingsCallCount)
        }

    @Test
    fun auto_saves_latest_state_WHEN_multiple_changes_queued() =
        runTest {
            createStore()

            store.accept(Intent.ChangeDifficulty(Difficulty.NORMAL))
            store.accept(Intent.ChangeDifficulty(Difficulty.HARD))
            store.accept(Intent.ChangeDifficulty(Difficulty.EASY))
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(Difficulty.EASY, repository.getSettings().difficulty)
            assertEquals(1, repository.saveSettingsCallCount)
        }

    @Test
    fun does_not_trigger_save_WHEN_value_unchanged() =
        runTest {
            createStore()

            store.accept(Intent.ChangeDifficulty(GameSettings().difficulty))
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(0, repository.saveSettingsCallCount)
        }

    @Test
    fun resets_isSaving_flag_after_auto_save() =
        runTest {
            createStore()

            store.accept(Intent.ChangeDifficulty(Difficulty.HARD))
            testDispatcher.scheduler.advanceUntilIdle()

            assertFalse(store.state.isSaving)
        }

    @Test
    fun publishes_Label_ShowError_WHEN_save_fails() =
        runTest {
            repository.shouldThrowOnSave = true
            createStore()
            val labels = store.labels.test()

            store.accept(Intent.ChangeDifficulty(Difficulty.HARD))
            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(labels.any { it is Label.ShowError })
            assertEquals(1, repository.saveSettingsCallCount)
        }

    @Test
    fun publishes_Label_ShowError_WHEN_load_fails() =
        runTest {
            repository.shouldThrowOnGet = true

            store = createStoreFactory().create()
            val labels = store.labels.test()
            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(labels.any { it is Label.ShowError })
        }

    private fun createStore() {
        store = createStoreFactory().create()
        testDispatcher.scheduler.advanceUntilIdle()
    }

    private fun createStoreFactory(): SettingsStoreFactory =
        SettingsStoreFactory(
            storeFactory = DefaultStoreFactory(),
            gameSettingsRepository = repository,
        )
}
