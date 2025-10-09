package com.yet.tetris.feature.game.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.history.GameRecord
import com.yet.tetris.domain.model.settings.GameSettings
import com.yet.tetris.domain.repository.GameHistoryRepository
import com.yet.tetris.domain.repository.GameSettingsRepository
import com.yet.tetris.domain.repository.GameStateRepository
import com.yet.tetris.domain.usecase.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.abs
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal class GameStoreFactory : KoinComponent {
    
    private val storeFactory: StoreFactory by inject()
    private val gameSettingsRepository: GameSettingsRepository by inject()
    private val gameStateRepository: GameStateRepository by inject()
    private val gameHistoryRepository: GameHistoryRepository by inject()
    private val startGameUseCase: StartGameUseCase by inject()
    private val movePieceUseCase: MovePieceUseCase by inject()
    private val rotatePieceUseCase: RotatePieceUseCase by inject()
    private val hardDropUseCase: HardDropUseCase by inject()
    private val lockPieceUseCase: LockPieceUseCase by inject()
    private val handleSwipeInputUseCase: HandleSwipeInputUseCase by inject()
    private val calculateGhostPositionUseCase: CalculateGhostPositionUseCase by inject()
    
    fun create(): GameStore =
        object : GameStore, Store<GameStore.Intent, GameStore.State, GameStore.Label> by storeFactory.create(
            name = "GameStore",
            initialState = GameStore.State(),
            bootstrapper = SimpleBootstrapper(GameStore.Action.GameLoadStarted),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}



    private object ReducerImpl : Reducer<GameStore.State, GameStore.Msg> {
        override fun GameStore.State.reduce(msg: GameStore.Msg): GameStore.State =
            when (msg) {
                is GameStore.Msg.GameInitialized -> copy(
                    gameState = msg.gameState,
                    settings = msg.settings
                )
                is GameStore.Msg.GameStateUpdated -> copy(
                    gameState = msg.gameState,
                    ghostPieceY = msg.ghostPieceY
                )
                is GameStore.Msg.PausedChanged -> copy(isPaused = msg.isPaused)
                is GameStore.Msg.ElapsedTimeUpdated -> copy(elapsedTime = msg.elapsedTime)
                is GameStore.Msg.LoadingChanged -> copy(isLoading = msg.isLoading)
                is GameStore.Msg.GestureStateUpdated -> copy(gestureState = msg.gestureState)
            }
    }

    @OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)
    private inner class ExecutorImpl : CoroutineExecutor<GameStore.Intent, GameStore.Action, GameStore.State, GameStore.Msg, GameStore.Label>() {
        
        private var gameLoopJob: Job? = null
        private var timerJob: Job? = null
        private var gameStartTime: Long = 0

        private val swipeThreshold = 50f

        override fun executeAction(action: GameStore.Action) {
            when (action) {
                GameStore.Action.GameLoadStarted ->  initializeGame()
            }
        }
        override fun executeIntent(intent: GameStore.Intent) {
            val getState  = state()
            when (intent) {
                is GameStore.Intent.PauseGame -> pauseGame(getState)
                is GameStore.Intent.ResumeGame -> resumeGame()
                is GameStore.Intent.QuitGame -> quitGame(getState)
                is GameStore.Intent.MoveLeft -> moveLeft(getState)
                is GameStore.Intent.MoveRight -> moveRight(getState)
                is GameStore.Intent.MoveDown -> moveDown(getState)
                is GameStore.Intent.Rotate -> rotate(getState)
                is GameStore.Intent.HardDrop -> hardDrop(getState)
                is GameStore.Intent.HandleSwipe -> handleSwipe(intent, getState)

                is GameStore.Intent.OnBoardSizeChanged -> dispatch(GameStore.Msg.GestureStateUpdated(GameStore.GestureState(boardHeightPx = intent.height)))
                is GameStore.Intent.DragStarted -> {
                    val currentGestureState = getState.gestureState ?: GameStore.GestureState()
                    dispatch(GameStore.Msg.GestureStateUpdated(currentGestureState.copy(
                        accumulatedDragX = 0f,
                        totalDragDistanceY = 0f,
                        dragStartTime = Clock.System.now().toEpochMilliseconds(),
                        isHorizontalSwipeDetermined = false
                    )))
                }
                is GameStore.Intent.DragEnded -> {
                    getState.gestureState?.let { gesture ->
                        val dragDuration = Clock.System.now().toEpochMilliseconds() - gesture.dragStartTime
                        // Проверка на Hard Drop
                        if (!gesture.isHorizontalSwipeDetermined && gesture.totalDragDistanceY > gesture.boardHeightPx * 0.25f && dragDuration < 500) {
                            hardDrop(getState)
                        } else if (!gesture.isHorizontalSwipeDetermined && gesture.totalDragDistanceY > swipeThreshold) {
                            // Простой свайп вниз
                            moveDown(getState)
                        }
                    }
                    // Сбрасываем состояние жеста после окончания
                    dispatch(GameStore.Msg.GestureStateUpdated(getState.gestureState?.copy(accumulatedDragX = 0f)))
                }
                is GameStore.Intent.Dragged -> {
                    var gesture = getState.gestureState ?: return

                    var isHorizontal = gesture.isHorizontalSwipeDetermined
                    // Определяем направление, если еще не определили
                    if (!isHorizontal && abs(intent.deltaX) > abs(intent.deltaY) * 1.5f) {
                        isHorizontal = true
                    }

                    if (isHorizontal) {
                        val newAccumulatedX = gesture.accumulatedDragX + intent.deltaX
                        if (abs(newAccumulatedX) > swipeThreshold) {
                            if (newAccumulatedX > 0) moveRight(getState) else moveLeft(getState)
                            gesture = gesture.copy(accumulatedDragX = 0f, isHorizontalSwipeDetermined = true)
                        } else {
                            gesture = gesture.copy(accumulatedDragX = newAccumulatedX, isHorizontalSwipeDetermined = true)
                        }
                    }

                    // Накапливаем вертикальное смещение для Hard Drop
                    if (intent.deltaY > 0) {
                        gesture = gesture.copy(totalDragDistanceY = gesture.totalDragDistanceY + intent.deltaY)
                    }

                    dispatch(GameStore.Msg.GestureStateUpdated(gesture))
                }
            }
        }
        
        private fun initializeGame() {
            scope.launch {
                try {
                    dispatch(GameStore.Msg.LoadingChanged(true))
                    
                    // Load settings
                    val settings = gameSettingsRepository.getSettings()
                    
                    // Try to load saved game state, otherwise start new game
                    val gameState = gameStateRepository.loadGameState()
                        ?: startGameUseCase(settings)
                    
                    dispatch(GameStore.Msg.GameInitialized(gameState, settings))
                    dispatch(GameStore.Msg.LoadingChanged(false))
                    
                    // Start game loop and timer
                    gameStartTime = Clock.System.now().toEpochMilliseconds()
                    startGameLoop(settings)
                    startTimer()
                    
                } catch (e: Exception) {
                    dispatch(GameStore.Msg.LoadingChanged(false))
                    publish(GameStore.Label.ShowError(e.message ?: "Failed to initialize game"))
                }
            }
        }
        
        private fun startGameLoop(settings: GameSettings) {
            gameLoopJob?.cancel()
            gameLoopJob = scope.launch {
                while (isActive) {
                    delay(settings.difficulty.fallDelayMs)
                    
                    val state = state()
                    if (!state.isPaused && state.gameState != null && !state.gameState.isGameOver) {
                        // Auto-move piece down
                        val newState = movePieceUseCase.moveDown(state.gameState)
                        if (newState != null) {
                            val ghostY = calculateGhostY(newState)
                            dispatch(GameStore.Msg.GameStateUpdated(newState, ghostY))
                        } else {
                            // Piece can't move down, lock it
                            lockPiece(state)
                        }
                    }
                }
            }
        }
        
        private fun startTimer() {
            timerJob?.cancel()
            timerJob = scope.launch {
                while (isActive) {
                    delay(100)
                    val state = state()
                    if (!state.isPaused && state.gameState != null && !state.gameState.isGameOver) {
                        val elapsed = Clock.System.now().toEpochMilliseconds() - gameStartTime
                        dispatch(GameStore.Msg.ElapsedTimeUpdated(elapsed))
                    }
                }
            }
        }
        
        private fun pauseGame(state: GameStore.State) {
            dispatch(GameStore.Msg.PausedChanged(true))
            
            // Save game state
            scope.launch {
                state.gameState?.let { gameStateRepository.saveGameState(it) }
            }
        }
        
        private fun resumeGame() {
            dispatch(GameStore.Msg.PausedChanged(false))
        }
        
        private fun quitGame(state: GameStore.State) {
            gameLoopJob?.cancel()
            timerJob?.cancel()
            
            scope.launch {
                // Save game state before quitting
                state.gameState?.let { gameStateRepository.saveGameState(it) }
            }
            
            publish(GameStore.Label.NavigateBack)
        }
        
        private fun moveLeft(state: GameStore.State) {
            state.gameState?.let { gameState ->
                if (!state.isPaused && !gameState.isGameOver) {
                    movePieceUseCase.moveLeft(gameState)?.let { newState ->
                        val ghostY = calculateGhostY(newState)
                        dispatch(GameStore.Msg.GameStateUpdated(newState, ghostY))
                    }
                }
            }
        }
        
        private fun moveRight(state: GameStore.State) {
            state.gameState?.let { gameState ->
                if (!state.isPaused && !gameState.isGameOver) {
                    movePieceUseCase.moveRight(gameState)?.let { newState ->
                        val ghostY = calculateGhostY(newState)
                        dispatch(GameStore.Msg.GameStateUpdated(newState, ghostY))
                    }
                }
            }
        }
        
        private fun moveDown(state: GameStore.State) {
            state.gameState?.let { gameState ->
                if (!state.isPaused && !gameState.isGameOver) {
                    val newState = movePieceUseCase.moveDown(gameState)
                    if (newState != null) {
                        val ghostY = calculateGhostY(newState)
                        dispatch(GameStore.Msg.GameStateUpdated(newState, ghostY))
                    } else {
                        lockPiece(state)
                    }
                }
            }
        }
        
        private fun rotate(state: GameStore.State) {
            state.gameState?.let { gameState ->
                if (!state.isPaused && !gameState.isGameOver) {
                    rotatePieceUseCase(gameState)?.let { newState ->
                        val ghostY = calculateGhostY(newState)
                        dispatch(GameStore.Msg.GameStateUpdated(newState, ghostY))
                    }
                }
            }
        }
        
        private fun hardDrop(state: GameStore.State) {
            state.gameState?.let { gameState ->
                if (!state.isPaused && !gameState.isGameOver) {
                    hardDropUseCase(gameState)?.let { droppedState ->
                        val ghostY = calculateGhostY(droppedState)
                        dispatch(GameStore.Msg.GameStateUpdated(droppedState, ghostY))
                        // Lock the piece immediately after hard drop
                        lockPiece(state.copy(gameState = droppedState))
                    }
                }
            }
        }
        
        private fun handleSwipe(intent: GameStore.Intent.HandleSwipe, state: GameStore.State) {
            state.gameState?.let { gameState ->
                if (!state.isPaused && !gameState.isGameOver) {
                    handleSwipeInputUseCase(
                        gameState,
                        intent.deltaX,
                        intent.deltaY,
                        intent.velocityX,
                        intent.velocityY,
                        state.settings.swipeSensitivity
                    )?.let { newState ->
                        val ghostY = calculateGhostY(newState)
                        dispatch(GameStore.Msg.GameStateUpdated(newState, ghostY))
                        
                        // If it was a hard drop, lock the piece
                        if (intent.velocityY > state.settings.swipeSensitivity.softDropThreshold) {
                            lockPiece(state.copy(gameState = newState))
                        }
                    }
                }
            }
        }
        

        private fun lockPiece(state: GameStore.State) {
            state.gameState?.let { gameState ->
                val newState = lockPieceUseCase(gameState)
                val ghostY = calculateGhostY(newState)
                dispatch(GameStore.Msg.GameStateUpdated(newState, ghostY))
                
                if (newState.isGameOver) {
                    handleGameOver(newState, state.settings)
                }
            }
        }
        
        private fun calculateGhostY(gameState: GameState): Int? {
            return gameState.currentPiece?.let { piece ->
                calculateGhostPositionUseCase(
                    gameState = gameState,
                    piece = piece,
                    currentPosition = gameState.currentPosition
                )
            }
        }
        

        private fun handleGameOver(gameState: GameState, settings: GameSettings) {
            gameLoopJob?.cancel()
            timerJob?.cancel()
            
            scope.launch {
                try {
                    // Save game record
                    val record = GameRecord(
                        id = Uuid.random().toString(),
                        score = gameState.score,
                        linesCleared = gameState.linesCleared,
                        difficulty = settings.difficulty,
                        timestamp = Clock.System.now().toEpochMilliseconds()
                    )
                    gameHistoryRepository.saveGame(record)
                    
                    // Clear saved game state
                    gameStateRepository.clearGameState()
                    
                    publish(GameStore.Label.GameOver)
                } catch (e: Exception) {
                    publish(GameStore.Label.ShowError(e.message ?: "Failed to save game"))
                }
            }
        }
    }
}
