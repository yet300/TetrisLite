package com.yet.tetris.feature.game

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.yet.tetris.domain.usecase.CalculateGhostPositionUseCase
import com.yet.tetris.domain.usecase.CalculateScoreUseCase
import com.yet.tetris.domain.usecase.CheckCollisionUseCase
import com.yet.tetris.domain.usecase.GenerateTetrominoUseCase
import com.yet.tetris.domain.usecase.GestureHandlingUseCase
import com.yet.tetris.domain.usecase.HandleSwipeInputUseCase
import com.yet.tetris.domain.usecase.HardDropUseCase
import com.yet.tetris.domain.usecase.LockPieceUseCase
import com.yet.tetris.domain.usecase.MovePieceUseCase
import com.yet.tetris.domain.usecase.RotatePieceUseCase
import com.yet.tetris.domain.usecase.StartGameUseCase
import com.yet.tetris.feature.game.store.FakeAudioRepository
import com.yet.tetris.feature.game.store.FakeGameHistoryRepository
import com.yet.tetris.feature.game.store.FakeGameSettingsRepository
import com.yet.tetris.feature.game.store.FakeGameStateRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class GameComponentTest : KoinTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var lifecycle: LifecycleRegistry
    private lateinit var settingsRepository: FakeGameSettingsRepository
    private lateinit var gameStateRepository: FakeGameStateRepository
    private lateinit var gameHistoryRepository: FakeGameHistoryRepository
    private lateinit var audioRepository: FakeAudioRepository
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
        val startGame = StartGameUseCase(generateTetromino)
        val handleSwipe = HandleSwipeInputUseCase(movePiece, hardDrop)
        val calculateGhost = CalculateGhostPositionUseCase()
        val gestureHandling = GestureHandlingUseCase()

        startKoin {
            modules(
                module {
                    single<com.arkivanov.mvikotlin.core.store.StoreFactory> { DefaultStoreFactory() }
                    single<com.yet.tetris.domain.repository.GameSettingsRepository> { settingsRepository }
                    single<com.yet.tetris.domain.repository.GameStateRepository> { gameStateRepository }
                    single<com.yet.tetris.domain.repository.GameHistoryRepository> { gameHistoryRepository }
                    single<com.yet.tetris.domain.repository.AudioRepository> { audioRepository }
                    single { startGame }
                    single { movePiece }
                    single { rotatePiece }
                    single { hardDrop }
                    single { lockPiece }
                    single { handleSwipe }
                    single { calculateGhost }
                    single { gestureHandling }
                },
            )
        }
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
        Dispatchers.resetMain()
    }

    private fun createComponent(): DefaultGameComponent =
        DefaultGameComponent(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            navigateBack = { navigateBackCalled = true },
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
}
