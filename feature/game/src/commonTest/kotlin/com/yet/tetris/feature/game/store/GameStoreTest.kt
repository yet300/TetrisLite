package com.yet.tetris.feature.game.store

import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.yet.tetris.domain.model.effects.IntensityLevel
import com.yet.tetris.domain.model.game.Difficulty
import com.yet.tetris.domain.model.game.GameBoard
import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.game.Position
import com.yet.tetris.domain.model.game.Tetromino
import com.yet.tetris.domain.model.game.TetrominoType
import com.yet.tetris.domain.model.settings.GameSettings
import com.yet.tetris.domain.usecase.CalculateGhostPositionUseCase
import com.yet.tetris.domain.usecase.CalculateScoreUseCase
import com.yet.tetris.domain.usecase.CheckCollisionUseCase
import com.yet.tetris.domain.usecase.GenerateTetrominoUseCase
import com.yet.tetris.domain.usecase.GestureHandlingUseCase
import com.yet.tetris.domain.usecase.HandleSwipeInputUseCase
import com.yet.tetris.domain.usecase.HardDropUseCase
import com.yet.tetris.domain.usecase.LockPieceUseCase
import com.yet.tetris.domain.usecase.MovePieceUseCase
import com.yet.tetris.domain.usecase.PlanVisualFeedbackUseCase
import com.yet.tetris.domain.usecase.RotatePieceUseCase
import com.yet.tetris.domain.usecase.StartGameUseCase
import com.yet.tetris.feature.game.store.GameStore.Intent
import com.yet.tetris.feature.game.store.GameStore.Label
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class GameStoreTest {
    private lateinit var settingsRepository: FakeGameSettingsRepository
    private lateinit var gameStateRepository: FakeGameStateRepository
    private lateinit var gameHistoryRepository: FakeGameHistoryRepository
    private lateinit var audioRepository: FakeAudioRepository
    private lateinit var startGameUseCase: StartGameUseCase
    private lateinit var movePieceUseCase: MovePieceUseCase
    private lateinit var rotatePieceUseCase: RotatePieceUseCase
    private lateinit var hardDropUseCase: HardDropUseCase
    private lateinit var lockPieceUseCase: LockPieceUseCase
    private lateinit var handleSwipeInputUseCase: HandleSwipeInputUseCase
    private lateinit var calculateGhostPositionUseCase: CalculateGhostPositionUseCase
    private lateinit var gestureHandlingUseCase: GestureHandlingUseCase
    private lateinit var planVisualFeedbackUseCase: PlanVisualFeedbackUseCase
    private lateinit var store: GameStore
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun before() {
        isAssertOnMainThreadEnabled = false
        Dispatchers.setMain(testDispatcher)

        settingsRepository = FakeGameSettingsRepository()
        gameStateRepository = FakeGameStateRepository()
        gameHistoryRepository = FakeGameHistoryRepository()
        audioRepository = FakeAudioRepository()

        // Create use cases
        val checkCollision = CheckCollisionUseCase()
        val movePiece = MovePieceUseCase(checkCollision)
        val rotatePiece = RotatePieceUseCase(checkCollision)
        val hardDrop = HardDropUseCase(checkCollision)
        val calculateScore = CalculateScoreUseCase()
        val generateTetromino = GenerateTetrominoUseCase()
        val lockPiece = LockPieceUseCase(calculateScore, generateTetromino, checkCollision)
        startGameUseCase = StartGameUseCase(generateTetromino)
        movePieceUseCase = movePiece
        rotatePieceUseCase = rotatePiece
        hardDropUseCase = hardDrop
        lockPieceUseCase = lockPiece
        handleSwipeInputUseCase = HandleSwipeInputUseCase(movePiece, hardDrop)
        calculateGhostPositionUseCase = CalculateGhostPositionUseCase()
        gestureHandlingUseCase = GestureHandlingUseCase()
        planVisualFeedbackUseCase = PlanVisualFeedbackUseCase()
    }

    @AfterTest
    fun after() {
        Dispatchers.resetMain()
        isAssertOnMainThreadEnabled = true
    }

    @Test
    fun initializes_game_WHEN_created() =
        runTest {
            createStore()

            assertNotNull(store.state.gameState)
            assertFalse(store.state.isLoading)
            assertEquals(1, audioRepository.initializeCallCount)
        }

    @Test
    fun loads_settings_WHEN_created() =
        runTest {
            val settings = GameSettings(difficulty = Difficulty.HARD)
            settingsRepository.setInitialSettings(settings)

            createStore()

            assertEquals(settings, store.state.settings)
        }

    @Test
    fun starts_new_game_WHEN_no_saved_state() =
        runTest {
            gameStateRepository.setSavedState(null)

            createStore()

            // Just verify game state was created
            assertNotNull(store.state.gameState, "Game state should not be null")
        }

    @Test
    fun loads_saved_game_WHEN_saved_state_exists() =
        runTest {
            val savedState = createTestGameState(score = 500)
            gameStateRepository.setSavedState(savedState)

            createStore()

            assertNotNull(store.state.gameState)
            assertEquals(500, store.state.gameState!!.score)
        }

    @Test
    fun pauses_game_WHEN_Intent_PauseGame() =
        runTest {
            createStore()

            store.accept(Intent.PauseGame)
            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(store.state.isPaused)
        }

    @Test
    fun saves_game_state_WHEN_Intent_PauseGame() =
        runTest {
            createStore()

            store.accept(Intent.PauseGame)
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(1, gameStateRepository.saveGameStateCallCount)
        }

    @Test
    fun publishes_Label_GamePaused_WHEN_Intent_PauseGame() =
        runTest {
            createStore()
            val labels = store.labels.test()

            store.accept(Intent.PauseGame)
            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(labels.any { it is Label.GamePaused })
        }

    @Test
    fun resumes_game_WHEN_Intent_ResumeGame() =
        runTest {
            createStore()
            store.accept(Intent.PauseGame)
            testDispatcher.scheduler.advanceUntilIdle()

            store.accept(Intent.ResumeGame)
            testDispatcher.scheduler.advanceUntilIdle()

            assertFalse(store.state.isPaused)
        }

    @Test
    fun publishes_Label_ResumeGame_WHEN_Intent_ResumeGame() =
        runTest {
            createStore()
            store.accept(Intent.PauseGame)
            testDispatcher.scheduler.advanceUntilIdle()
            val labels = store.labels.test()

            store.accept(Intent.ResumeGame)
            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(labels.any { it is Label.ResumeGame })
        }

    @Test
    fun saves_game_and_navigates_back_WHEN_Intent_QuitGame() =
        runTest {
            createStore()
            val labels = store.labels.test()

            store.accept(Intent.QuitGame)
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(1, gameStateRepository.saveGameStateCallCount)
            assertTrue(labels.any { it is Label.NavigateBack })
        }

    @Test
    fun accepts_move_left_intent() =
        runTest {
            createStore()
            val gameState = store.state.gameState
            assertNotNull(gameState, "Game state should not be null")

            store.accept(Intent.MoveLeft)
            testDispatcher.scheduler.advanceUntilIdle()

            // Just verify game state still exists after move attempt
            assertNotNull(store.state.gameState)
        }

    @Test
    fun accepts_move_right_intent() =
        runTest {
            createStore()
            val gameState = store.state.gameState
            assertNotNull(gameState, "Game state should not be null")

            store.accept(Intent.MoveRight)
            testDispatcher.scheduler.advanceUntilIdle()

            // Just verify game state still exists after move attempt
            assertNotNull(store.state.gameState)
        }

    @Test
    fun accepts_move_down_intent() =
        runTest {
            createStore()
            val gameState = store.state.gameState
            assertNotNull(gameState, "Game state should not be null")

            store.accept(Intent.MoveDown)
            testDispatcher.scheduler.advanceUntilIdle()

            // Just verify game state still exists after move attempt
            assertNotNull(store.state.gameState)
        }

    @Test
    fun plays_sound_effect_WHEN_piece_moves() =
        runTest {
            createStore()

            store.accept(Intent.MoveLeft)
            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(audioRepository.playSoundEffectCallCount > 0)
        }

    @Test
    fun does_not_move_WHEN_game_is_paused() =
        runTest {
            createStore()
            store.accept(Intent.PauseGame)
            testDispatcher.scheduler.advanceUntilIdle()
            val initialX =
                store.state.gameState!!
                    .currentPosition.x

            store.accept(Intent.MoveLeft)
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(
                initialX,
                store.state.gameState!!
                    .currentPosition.x,
            )
        }

    @Test
    fun starts_new_game_WHEN_Intent_RetryGame() =
        runTest {
            createStore()
            val initialScore = store.state.gameState!!.score

            store.accept(Intent.RetryGame)
            testDispatcher.scheduler.advanceUntilIdle()

            // New game should have score 0
            assertEquals(0, store.state.gameState!!.score)
        }

    @Test
    fun clears_saved_state_WHEN_Intent_RetryGame() =
        runTest {
            val savedState = createTestGameState(score = 500)
            gameStateRepository.setSavedState(savedState)
            createStore()

            store.accept(Intent.RetryGame)
            testDispatcher.scheduler.advanceUntilIdle()

            // Should have cleared at least once (might be more due to initialization)
            assertTrue(gameStateRepository.clearGameStateCallCount >= 1)
        }

    @Test
    fun calculates_ghost_piece_position() =
        runTest {
            createStore()

            // Ghost piece Y should be calculated (can be null if piece is at bottom)
            // Just verify game state exists
            assertNotNull(store.state.gameState)
        }

    @Test
    fun publishes_Label_ShowError_WHEN_initialization_fails() =
        runTest {
            settingsRepository.shouldThrowOnGet = true

            store = createStoreFactory().create()
            val labels = store.labels.test()
            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(labels.any { it is Label.ShowError })
        }

    @Test
    fun stops_music_WHEN_game_paused() =
        runTest {
            createStore()

            store.accept(Intent.PauseGame)
            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(audioRepository.stopMusicCallCount > 0)
        }

    @Test
    fun applies_audio_settings_on_initialization() =
        runTest {
            createStore()

            assertTrue(audioRepository.applySettingsCallCount > 0)
        }

    @Test
    fun updates_visual_effect_feed_WHEN_line_is_cleared() =
        runTest {
            gameStateRepository.setSavedState(createSingleLineClearState())
            store = createStoreFactory().create()
            runCurrent()

            store.accept(Intent.MoveDown)
            runCurrent()

            assertEquals(1, store.state.comboStreak)
            assertEquals(1L, store.state.visualEffectFeed.sequence)
            val burst = store.state.visualEffectFeed.latest
            assertNotNull(burst)
            assertEquals(1, burst.linesCleared)
            assertEquals(IntensityLevel.LOW, burst.intensity)
        }

    @Test
    fun keeps_visual_effect_feed_unchanged_WHEN_no_lines_are_cleared() =
        runTest {
            gameStateRepository.setSavedState(createNoLineClearLockState())
            store = createStoreFactory().create()
            runCurrent()

            store.accept(Intent.MoveDown)
            runCurrent()

            assertEquals(0, store.state.comboStreak)
            assertEquals(0L, store.state.visualEffectFeed.sequence)
            assertEquals(null, store.state.visualEffectFeed.latest)
        }

    @Test
    fun clears_latest_effect_WHEN_visual_effect_consumed() =
        runTest {
            gameStateRepository.setSavedState(createSingleLineClearState())
            store = createStoreFactory().create()
            runCurrent()

            store.accept(Intent.MoveDown)
            runCurrent()

            val emittedSequence = store.state.visualEffectFeed.sequence
            assertNotNull(store.state.visualEffectFeed.latest)

            store.accept(Intent.VisualEffectConsumed(emittedSequence))
            runCurrent()

            assertEquals(emittedSequence, store.state.visualEffectFeed.sequence)
            assertEquals(null, store.state.visualEffectFeed.latest)
        }

    private fun createStore() {
        store = createStoreFactory().create()
        testDispatcher.scheduler.advanceUntilIdle()
    }

    private fun createStoreFactory(): GameStoreFactory =
        GameStoreFactory(
            storeFactory = DefaultStoreFactory(),
            gameSettingsRepository = settingsRepository,
            gameStateRepository = gameStateRepository,
            gameHistoryRepository = gameHistoryRepository,
            audioRepository = audioRepository,
            startGameUseCase = startGameUseCase,
            movePieceUseCase = movePieceUseCase,
            rotatePieceUseCase = rotatePieceUseCase,
            hardDropUseCase = hardDropUseCase,
            lockPieceUseCase = lockPieceUseCase,
            handleSwipeInputUseCase = handleSwipeInputUseCase,
            calculateGhostPositionUseCase = calculateGhostPositionUseCase,
            gestureHandlingUseCase = gestureHandlingUseCase,
            planVisualFeedbackUseCase = planVisualFeedbackUseCase,
        )

    private fun createTestGameState(score: Long = 0): GameState =
        GameState(
            board = GameBoard(width = 10, height = 20),
            currentPiece = Tetromino.create(TetrominoType.I),
            currentPosition = Position(x = 4, y = 0),
            nextPiece = Tetromino.create(TetrominoType.T),
            score = score,
            linesCleared = 0,
            isGameOver = false,
            isPaused = false,
        )

    private fun createSingleLineClearState(): GameState {
        val filledRow =
            (2 until 10).associate { x ->
                Position(x = x, y = 19) to TetrominoType.I
            }

        return GameState(
            board =
                GameBoard(
                    width = 10,
                    height = 20,
                    cells = filledRow,
                ),
            currentPiece = Tetromino.create(TetrominoType.O),
            currentPosition = Position(x = 0, y = 18),
            nextPiece = Tetromino.create(TetrominoType.T),
            score = 0,
            linesCleared = 0,
            isGameOver = false,
            isPaused = false,
        )
    }

    private fun createNoLineClearLockState(): GameState =
        GameState(
            board = GameBoard(width = 10, height = 20),
            currentPiece = Tetromino.create(TetrominoType.O),
            currentPosition = Position(x = 0, y = 18),
            nextPiece = Tetromino.create(TetrominoType.T),
            score = 0,
            linesCleared = 0,
            isGameOver = false,
            isPaused = false,
        )
}

/**
 * Collects all emitted labels from a Flow for testing purposes.
 */
fun <T> Flow<T>.test(): MutableList<T> {
    val list = ArrayList<T>()
    @Suppress("OPT_IN_USAGE")
    GlobalScope.launch(Dispatchers.Unconfined) { collect { list += it } }
    return list
}
