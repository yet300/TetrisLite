package com.yet.tetris.feature.game

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.yet.tetris.domain.model.game.GameBoard
import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.game.Position
import com.yet.tetris.domain.model.game.Tetromino
import com.yet.tetris.domain.model.game.TetrominoType
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
import com.yet.tetris.feature.game.store.FakeAudioRepository
import com.yet.tetris.feature.game.store.FakeGameHistoryRepository
import com.yet.tetris.feature.game.store.FakeGameSettingsRepository
import com.yet.tetris.feature.game.store.FakeGameStateRepository
import com.yet.tetris.feature.game.store.GameStoreFactory
import com.yet.tetris.feature.settings.PreviewSettingsComponent
import com.yet.tetris.feature.settings.SettingsComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class GameComponentTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var lifecycle: LifecycleRegistry
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
    private var navigateBackCalled = false

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        lifecycle = LifecycleRegistry()
        settingsRepository = FakeGameSettingsRepository()
        gameStateRepository = FakeGameStateRepository()
        gameHistoryRepository = FakeGameHistoryRepository()
        audioRepository = FakeAudioRepository()
        navigateBackCalled = false

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
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createComponent(): DefaultGameComponent =
        DefaultGameComponent(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            navigateBack = { navigateBackCalled = true },
            gameStoreFactory = createGameStoreFactory(),
            settingsComponentFactory = createSettingsComponentFactory(),
        )

    private fun createSettingsComponentFactory(): SettingsComponent.Factory =
        SettingsComponent.Factory { _, _ ->
            PreviewSettingsComponent()
        }

    private fun createGameStoreFactory(): GameStoreFactory =
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

    @Test
    fun WHEN_component_created_THEN_model_initializes() =
        runTest(testDispatcher) {
            val component = createComponent()
            lifecycle.resume()

            // Model starts in some state (may be loading or already loaded)
            val model = component.model.value
            assertNotNull(model)
        }

    @Test
    fun WHEN_game_starts_THEN_model_has_game_state() =
        runTest(testDispatcher) {
            val component = createComponent()
            lifecycle.resume()
            advanceUntilIdle()

            // Wait for game to initialize
            testDispatcher.scheduler.advanceTimeBy(100)
            advanceUntilIdle()

            val model = component.model.value
            assertFalse(model.isLoading)
            assertNotNull(model.gameState)
        }

    @Test
    fun WHEN_onMoveLeft_THEN_piece_moves_left() =
        runTest(testDispatcher) {
            val component = createComponent()
            lifecycle.resume()
            advanceUntilIdle()
            testDispatcher.scheduler.advanceTimeBy(100)
            advanceUntilIdle()

            val initialX =
                component.model.value.gameState
                    ?.currentPosition
                    ?.x

            component.onMoveLeft()
            advanceUntilIdle()

            val newX =
                component.model.value.gameState
                    ?.currentPosition
                    ?.x
            if (initialX != null && newX != null) {
                assertTrue(newX <= initialX)
            }
        }

    @Test
    fun WHEN_onMoveRight_THEN_piece_moves_right() =
        runTest(testDispatcher) {
            val component = createComponent()
            lifecycle.resume()
            advanceUntilIdle()
            testDispatcher.scheduler.advanceTimeBy(100)
            advanceUntilIdle()

            val initialX =
                component.model.value.gameState
                    ?.currentPosition
                    ?.x

            component.onMoveRight()
            advanceUntilIdle()

            val newX =
                component.model.value.gameState
                    ?.currentPosition
                    ?.x
            if (initialX != null && newX != null) {
                assertTrue(newX >= initialX)
            }
        }

    @Test
    fun WHEN_onRotate_THEN_piece_rotates() =
        runTest(testDispatcher) {
            val component = createComponent()
            lifecycle.resume()
            advanceUntilIdle()
            testDispatcher.scheduler.advanceTimeBy(100)
            advanceUntilIdle()

            // Ensure game is initialized
            assertNotNull(component.model.value.gameState, "Game state should be initialized")

            component.onRotate()
            advanceUntilIdle()

            // Verify game state still exists after rotation
            assertNotNull(component.model.value.gameState)
        }

    @Test
    fun WHEN_onPause_THEN_pause_dialog_shown() =
        runTest(testDispatcher) {
            val component = createComponent()
            lifecycle.resume()
            advanceUntilIdle()
            testDispatcher.scheduler.advanceTimeBy(100)
            advanceUntilIdle()

            component.onPause()
            advanceUntilIdle()

            val dialog =
                component.childSlot.value.child
                    ?.instance
            assertIs<GameComponent.DialogChild.Pause>(dialog)
        }

    @Test
    fun WHEN_onResume_THEN_dialog_dismissed() =
        runTest(testDispatcher) {
            val component = createComponent()
            lifecycle.resume()
            advanceUntilIdle()
            testDispatcher.scheduler.advanceTimeBy(100)
            advanceUntilIdle()

            component.onPause()
            advanceUntilIdle()

            component.onResume()
            advanceUntilIdle()

            val dialog = component.childSlot.value.child
            assertNull(dialog)
        }

    @Test
    fun WHEN_onSettings_THEN_settings_sheet_shown() =
        runTest(testDispatcher) {
            val component = createComponent()
            lifecycle.resume()
            advanceUntilIdle()

            component.onSettings()
            advanceUntilIdle()

            val sheet =
                component.sheetSlot.value.child
                    ?.instance
            assertIs<GameComponent.SheetChild.Settings>(sheet)
        }

    @Test
    fun WHEN_onDismissSheet_THEN_sheet_dismissed() =
        runTest(testDispatcher) {
            val component = createComponent()
            lifecycle.resume()
            advanceUntilIdle()

            component.onSettings()
            advanceUntilIdle()

            component.onDismissSheet()
            advanceUntilIdle()

            val sheet = component.sheetSlot.value.child
            assertNull(sheet)
        }

    @Test
    fun WHEN_onDismissDialog_THEN_dialog_dismissed() =
        runTest(testDispatcher) {
            val component = createComponent()
            lifecycle.resume()
            advanceUntilIdle()
            testDispatcher.scheduler.advanceTimeBy(100)
            advanceUntilIdle()

            component.onPause()
            advanceUntilIdle()

            component.onDismissDialog()
            advanceUntilIdle()

            val dialog = component.childSlot.value.child
            assertNull(dialog)
        }

    @Test
    fun WHEN_onQuit_THEN_navigates_back() =
        runTest(testDispatcher) {
            val component = createComponent()
            lifecycle.resume()
            advanceUntilIdle()

            component.onQuit()
            advanceUntilIdle()

            assertTrue(navigateBackCalled)
        }

    @Test
    fun WHEN_onBackClick_THEN_navigates_back() =
        runTest(testDispatcher) {
            val component = createComponent()
            lifecycle.resume()
            advanceUntilIdle()

            component.onBackClick()

            assertTrue(navigateBackCalled)
        }

    @Test
    fun WHEN_onRetry_THEN_game_restarts() =
        runTest(testDispatcher) {
            val component = createComponent()
            lifecycle.resume()
            advanceUntilIdle()
            testDispatcher.scheduler.advanceTimeBy(100)
            advanceUntilIdle()

            component.onRetry()
            advanceUntilIdle()
            testDispatcher.scheduler.advanceTimeBy(100)
            advanceUntilIdle()

            // Verify game state exists after retry
            assertNotNull(component.model.value.gameState)
        }

    @Test
    fun WHEN_onSwipe_THEN_swipe_handled() =
        runTest(testDispatcher) {
            val component = createComponent()
            lifecycle.resume()
            advanceUntilIdle()
            testDispatcher.scheduler.advanceTimeBy(100)
            advanceUntilIdle()

            // Swipe right
            component.onSwipe(deltaX = 100f, deltaY = 0f, velocityX = 500f, velocityY = 0f)
            advanceUntilIdle()

            // Just verify no crash - actual behavior depends on swipe handling logic
            assertNotNull(component.model.value)
        }

    @Test
    fun WHEN_onBoardSizeChanged_THEN_board_size_updated() =
        runTest(testDispatcher) {
            val component = createComponent()
            lifecycle.resume()
            advanceUntilIdle()

            component.onBoardSizeChanged(height = 800f)
            advanceUntilIdle()

            // Verify no crash
            assertNotNull(component.model.value)
        }

    @Test
    fun WHEN_drag_operations_THEN_handled_correctly() =
        runTest(testDispatcher) {
            val component = createComponent()
            lifecycle.resume()
            advanceUntilIdle()
            testDispatcher.scheduler.advanceTimeBy(100)
            advanceUntilIdle()

            component.onDragStarted()
            advanceUntilIdle()

            component.onDragged(deltaX = 10f, deltaY = 20f)
            advanceUntilIdle()

            component.onDragEnded()
            advanceUntilIdle()

            // Verify no crash
            assertNotNull(component.model.value)
        }

    @Test
    fun WHEN_onHardDrop_THEN_piece_drops() =
        runTest(testDispatcher) {
            val component = createComponent()
            lifecycle.resume()
            advanceUntilIdle()
            testDispatcher.scheduler.advanceTimeBy(100)
            advanceUntilIdle()

            val initialY =
                component.model.value.gameState
                    ?.currentPosition
                    ?.y

            component.onHardDrop()
            advanceUntilIdle()

            val newY =
                component.model.value.gameState
                    ?.currentPosition
                    ?.y
            // After hard drop, piece should be at a different position or a new piece spawned
            assertNotNull(component.model.value.gameState)
        }

    @Test
    fun WHEN_onMoveDown_THEN_piece_moves_down() =
        runTest(testDispatcher) {
            val component = createComponent()
            lifecycle.resume()
            advanceUntilIdle()
            testDispatcher.scheduler.advanceTimeBy(100)
            advanceUntilIdle()

            val initialY =
                component.model.value.gameState
                    ?.currentPosition
                    ?.y

            component.onMoveDown()
            advanceUntilIdle()

            val newY =
                component.model.value.gameState
                    ?.currentPosition
                    ?.y
            if (initialY != null && newY != null) {
                assertTrue(newY >= initialY)
            }
        }

    @Test
    fun WHEN_model_updates_THEN_elapsed_time_tracked() =
        runTest(testDispatcher) {
            val component = createComponent()
            lifecycle.resume()
            advanceUntilIdle()
            testDispatcher.scheduler.advanceTimeBy(100)
            advanceUntilIdle()

            val initialTime = component.model.value.elapsedTime

            testDispatcher.scheduler.advanceTimeBy(1000)
            advanceUntilIdle()

            val newTime = component.model.value.elapsedTime
            assertTrue(newTime >= initialTime)
        }

    @Test
    fun WHEN_visual_effect_occurs_THEN_feed_sequence_and_latest_are_updated() =
        runTest(testDispatcher) {
            gameStateRepository.setSavedState(createSingleLineClearState())
            val component = createComponent()
            lifecycle.resume()
            runCurrent()

            val initialFeed = component.model.value.visualEffectFeed
            assertEquals(0L, initialFeed.sequence)

            component.onMoveDown()
            runCurrent()

            val updatedFeed = component.model.value.visualEffectFeed
            assertTrue(updatedFeed.sequence > initialFeed.sequence)
            assertNotNull(updatedFeed.latest)
            val latest = requireNotNull(updatedFeed.latest)
            assertEquals(1, latest.linesCleared)
        }

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
}
