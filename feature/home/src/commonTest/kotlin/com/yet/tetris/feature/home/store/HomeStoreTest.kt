package com.yet.tetris.feature.home.store

import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.yet.tetris.domain.model.game.Difficulty
import com.yet.tetris.domain.model.game.GameBoard
import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.game.Position
import com.yet.tetris.domain.model.game.Tetromino
import com.yet.tetris.domain.model.game.TetrominoType
import com.yet.tetris.domain.model.settings.GameSettings
import com.yet.tetris.feature.home.store.HomeStore.Intent
import com.yet.tetris.feature.home.store.HomeStore.Label
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
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
class HomeStoreTest {

    private lateinit var settingsRepository: FakeGameSettingsRepository
    private lateinit var gameStateRepository: FakeGameStateRepository
    private lateinit var store: HomeStore
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun before() {
        isAssertOnMainThreadEnabled = false
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
        isAssertOnMainThreadEnabled = true
    }

    @Test
    fun loads_settings_from_repository_WHEN_created() = runTest {
        val initialSettings = GameSettings(difficulty = Difficulty.HARD)
        settingsRepository.setInitialSettings(initialSettings)

        createStore()

        assertEquals(initialSettings, store.state.settings)
        assertEquals(1, settingsRepository.getSettingsCallCount)
    }

    @Test
    fun checks_for_saved_game_WHEN_created() = runTest {
        createStore()

        assertEquals(1, gameStateRepository.hasSavedStateCallCount)
    }

    @Test
    fun sets_hasSavedGame_to_true_WHEN_saved_game_exists() = runTest {
        val savedState = createTestGameState()
        gameStateRepository.setSavedState(savedState)

        createStore()

        assertTrue(store.state.hasSavedGame)
    }

    @Test
    fun sets_hasSavedGame_to_false_WHEN_no_saved_game_exists() = runTest {
        gameStateRepository.setSavedState(null)

        createStore()

        assertFalse(store.state.hasSavedGame)
    }

    @Test
    fun sets_isLoading_during_initialization() = runTest {
        createStore()

        // After initialization completes, isLoading should be false
        assertFalse(store.state.isLoading)
    }

    @Test
    fun clears_saved_game_WHEN_Intent_StartNewGame() = runTest {
        val savedState = createTestGameState()
        gameStateRepository.setSavedState(savedState)
        createStore()

        store.accept(Intent.StartNewGame)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, gameStateRepository.clearGameStateCallCount)
        assertFalse(store.state.hasSavedGame)
    }

    @Test
    fun publishes_Label_NavigateToGame_WHEN_Intent_StartNewGame() = runTest {
        createStore()
        val labels = store.labels.test()

        store.accept(Intent.StartNewGame)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(labels.any { it is Label.NavigateToGame })
    }

    @Test
    fun publishes_Label_NavigateToGame_WHEN_Intent_ResumeGame() = runTest {
        val savedState = createTestGameState()
        gameStateRepository.setSavedState(savedState)
        createStore()
        val labels = store.labels.test()

        store.accept(Intent.ResumeGame)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(labels.any { it is Label.NavigateToGame })
    }

    @Test
    fun does_not_clear_saved_game_WHEN_Intent_ResumeGame() = runTest {
        val savedState = createTestGameState()
        gameStateRepository.setSavedState(savedState)
        createStore()

        store.accept(Intent.ResumeGame)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(0, gameStateRepository.clearGameStateCallCount)
        assertTrue(store.state.hasSavedGame)
    }

    @Test
    fun updates_difficulty_in_state_WHEN_Intent_ChangeDifficulty() = runTest {
        createStore()

        store.accept(Intent.ChangeDifficulty(Difficulty.HARD))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(Difficulty.HARD, store.state.settings.difficulty)
    }

    @Test
    fun saves_difficulty_to_repository_WHEN_Intent_ChangeDifficulty() = runTest {
        createStore()

        store.accept(Intent.ChangeDifficulty(Difficulty.HARD))
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, settingsRepository.saveSettingsCallCount)
        assertEquals(Difficulty.HARD, settingsRepository.getSettings().difficulty)
    }

    @Test
    fun publishes_Label_ShowError_WHEN_load_fails() = runTest {
        settingsRepository.shouldThrowOnGet = true
        
        store = HomeStoreFactory().create()
        val labels = store.labels.test()
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(labels.any { it is Label.ShowError })
        assertFalse(store.state.isLoading)
    }

    @Test
    fun publishes_Label_ShowError_WHEN_start_new_game_fails() = runTest {
        gameStateRepository.shouldThrowOnClear = true
        createStore()
        val labels = store.labels.test()

        store.accept(Intent.StartNewGame)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(labels.any { it is Label.ShowError })
    }

    @Test
    fun publishes_Label_ShowError_WHEN_change_difficulty_fails() = runTest {
        settingsRepository.shouldThrowOnSave = true
        createStore()
        val labels = store.labels.test()

        store.accept(Intent.ChangeDifficulty(Difficulty.HARD))
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(labels.any { it is Label.ShowError })
    }

    @Test
    fun loads_both_settings_and_saved_game_state_on_initialization() = runTest {
        val initialSettings = GameSettings(difficulty = Difficulty.EASY)
        settingsRepository.setInitialSettings(initialSettings)
        val savedState = createTestGameState()
        gameStateRepository.setSavedState(savedState)

        createStore()

        assertEquals(initialSettings, store.state.settings)
        assertTrue(store.state.hasSavedGame)
        assertEquals(1, settingsRepository.getSettingsCallCount)
        assertEquals(1, gameStateRepository.hasSavedStateCallCount)
    }

    @Test
    fun multiple_difficulty_changes_update_state_correctly() = runTest {
        createStore()

        store.accept(Intent.ChangeDifficulty(Difficulty.HARD))
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(Difficulty.HARD, store.state.settings.difficulty)

        store.accept(Intent.ChangeDifficulty(Difficulty.EASY))
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(Difficulty.EASY, store.state.settings.difficulty)

        assertEquals(2, settingsRepository.saveSettingsCallCount)
    }

    private fun createStore() {
        store = HomeStoreFactory().create()
        testDispatcher.scheduler.advanceUntilIdle()
    }

    private fun createTestGameState(): GameState {
        return GameState(
            board = GameBoard(width = 10, height = 20),
            currentPiece = Tetromino.create(TetrominoType.I),
            currentPosition = Position(x = 4, y = 0),
            nextPiece = Tetromino.create(TetrominoType.T),
            score = 100,
            linesCleared = 5,
            isGameOver = false,
            isPaused = false
        )
    }
}

/**
 * Collects all emitted labels from a Flow for testing purposes.
 * Returns a list that will be populated as labels are emitted.
 */
private fun <T> Flow<T>.test(): MutableList<T> {
    val list = ArrayList<T>()
    @Suppress("OPT_IN_USAGE")
    GlobalScope.launch(Dispatchers.Unconfined) { collect { list += it } }

    return list
}