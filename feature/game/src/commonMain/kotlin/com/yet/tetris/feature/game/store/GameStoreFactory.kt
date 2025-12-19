package com.yet.tetris.feature.game.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.yet.tetris.domain.model.audio.SoundEffect
import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.history.GameRecord
import com.yet.tetris.domain.model.settings.GameSettings
import com.yet.tetris.domain.repository.AudioRepository
import com.yet.tetris.domain.repository.GameHistoryRepository
import com.yet.tetris.domain.repository.GameSettingsRepository
import com.yet.tetris.domain.repository.GameStateRepository
import com.yet.tetris.domain.usecase.CalculateGhostPositionUseCase
import com.yet.tetris.domain.usecase.GameLoopEvent
import com.yet.tetris.domain.usecase.GameLoopUseCase
import com.yet.tetris.domain.usecase.GestureEvent
import com.yet.tetris.domain.usecase.GestureHandlingUseCase
import com.yet.tetris.domain.usecase.GestureResult
import com.yet.tetris.domain.usecase.HandleSwipeInputUseCase
import com.yet.tetris.domain.usecase.HardDropUseCase
import com.yet.tetris.domain.usecase.LockPieceUseCase
import com.yet.tetris.domain.usecase.MovePieceUseCase
import com.yet.tetris.domain.usecase.RotatePieceUseCase
import com.yet.tetris.domain.usecase.StartGameUseCase
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal class GameStoreFactory : KoinComponent {
    private val storeFactory: StoreFactory by inject()
    private val gameSettingsRepository: GameSettingsRepository by inject()
    private val gameStateRepository: GameStateRepository by inject()
    private val gameHistoryRepository: GameHistoryRepository by inject()
    private val audioRepository: AudioRepository by inject()
    private val startGameUseCase: StartGameUseCase by inject()
    private val movePieceUseCase: MovePieceUseCase by inject()
    private val rotatePieceUseCase: RotatePieceUseCase by inject()
    private val hardDropUseCase: HardDropUseCase by inject()
    private val lockPieceUseCase: LockPieceUseCase by inject()
    private val handleSwipeInputUseCase: HandleSwipeInputUseCase by inject()
    private val calculateGhostPositionUseCase: CalculateGhostPositionUseCase by inject()
    private val gestureHandlingUseCase: GestureHandlingUseCase by inject()

    fun create(): GameStore =
        object :
            GameStore,
            Store<GameStore.Intent, GameStore.State, GameStore.Label> by storeFactory.create(
                name = "GameStore",
                initialState = GameStore.State(),
                bootstrapper = SimpleBootstrapper(GameStore.Action.GameLoadStarted(forceNewGame = false)),
                executorFactory = ::ExecutorImpl,
                reducer = ReducerImpl,
            ) {}

    private object ReducerImpl : Reducer<GameStore.State, GameStore.Msg> {
        override fun GameStore.State.reduce(msg: GameStore.Msg): GameStore.State =
            when (msg) {
                is GameStore.Msg.GameInitialized ->
                    copy(
                        isLoading = false,
                        gameState = msg.gameState,
                        settings = msg.settings,
                        elapsedTime = 0L,
                    )
                is GameStore.Msg.GameStateUpdated ->
                    copy(
                        gameState = msg.gameState,
                        ghostPieceY = msg.ghostPieceY,
                    )
                is GameStore.Msg.PausedChanged -> copy(isPaused = msg.isPaused)
                is GameStore.Msg.ElapsedTimeUpdated -> copy(elapsedTime = msg.elapsedTime)
                is GameStore.Msg.LoadingChanged -> copy(isLoading = msg.isLoading)
                is GameStore.Msg.SettingsUpdated -> copy(settings = msg.settings)
            }
    }

    @OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)
    private inner class ExecutorImpl :
        CoroutineExecutor<GameStore.Intent, GameStore.Action, GameStore.State, GameStore.Msg, GameStore.Label>() {
        private val gameLoopUseCase = GameLoopUseCase(scope = this.scope)

        init {
            scope.launch {
                gameLoopUseCase.events.collect { event ->
                    when (event) {
                        is GameLoopEvent.GameTick -> {
                            // When the game loop ticks, we trigger the move down logic
                            autoMoveDown(state())
                        }

                        is GameLoopEvent.TimerUpdated -> {
                            // When the timer updates, we dispatch a message to update the state
                            dispatch(GameStore.Msg.ElapsedTimeUpdated(event.elapsedTime))
                        }
                    }
                }
            }

            scope.launch {
                gameSettingsRepository
                    .observeSettings()
                    .drop(1)
                    .collect { newSettings ->
                        dispatch(GameStore.Msg.SettingsUpdated(newSettings))

                        audioRepository.applySettings(newSettings.audioSettings)
                    }
            }
        }

        override fun executeAction(action: GameStore.Action) {
            when (action) {
                is GameStore.Action.GameLoadStarted -> initializeGame(action.forceNewGame)
            }
        }

        override fun executeIntent(intent: GameStore.Intent) {
            val getState = state()
            when (intent) {
                is GameStore.Intent.PauseGame -> pauseGame(getState)
                is GameStore.Intent.ResumeGame -> resumeGame(getState)
                is GameStore.Intent.QuitGame -> quitGame(getState)
                is GameStore.Intent.RetryGame -> {
                    gameLoopUseCase.stop()
                    initializeGame(forceNewGame = true)
                }
                is GameStore.Intent.MoveLeft -> moveLeft(getState)
                is GameStore.Intent.MoveRight -> moveRight(getState)
                is GameStore.Intent.MoveDown -> moveDown(getState)
                is GameStore.Intent.Rotate -> rotate(getState)
                is GameStore.Intent.HardDrop -> hardDrop(getState)
                is GameStore.Intent.HandleSwipe -> handleSwipe(intent, getState)

                is GameStore.Intent.OnBoardSizeChanged -> {
                    handleGestureEvent(GestureEvent.DragStarted(intent.height), getState)
                }
                is GameStore.Intent.DragStarted -> {
                    // Assuming board size is already known from OnBoardSizeChanged
                    handleGestureEvent(GestureEvent.DragStarted(0f), getState)
                }
                is GameStore.Intent.Dragged -> {
                    handleGestureEvent(GestureEvent.Dragged(intent.deltaX, intent.deltaY), getState)
                }
                is GameStore.Intent.DragEnded -> {
                    handleGestureEvent(GestureEvent.DragEnded, getState)
                }
            }
        }

        private fun initializeGame(forceNewGame: Boolean) {
            scope.launch {
                try {
                    dispatch(GameStore.Msg.LoadingChanged(true))
                    audioRepository.initialize()

                    // Load settings
                    val settings = gameSettingsRepository.getSettings()

                    audioRepository.applySettings(settings.audioSettings)

                    // Try to load saved game state, otherwise start new game
                    val gameState =
                        if (forceNewGame) {
                            gameStateRepository.clearGameState()
                            startGameUseCase(settings)
                        } else {
                            gameStateRepository.loadGameState() ?: startGameUseCase(settings)
                        }

                    dispatch(GameStore.Msg.GameInitialized(gameState, settings))
                    dispatch(GameStore.Msg.LoadingChanged(false))

                    // Start the game loop and timer via the UseCase.
                    gameLoopUseCase.start(settings)

                    if (settings.audioSettings.musicEnabled) {
                        audioRepository.playMusic(settings.audioSettings.selectedMusicTheme)
                    }
                } catch (e: Exception) {
                    dispatch(GameStore.Msg.LoadingChanged(false))
                    publish(GameStore.Label.ShowError(e.message ?: "Failed to initialize game"))
                }
            }
        }

        /**
         * Handles the auto-move down event from the game loop tick.
         */
        private fun autoMoveDown(state: GameStore.State) {
            if (!state.isPaused && state.gameState != null && !state.gameState.isGameOver) {
                val newState = movePieceUseCase.moveDown(state.gameState)
                if (newState != null) {
                    val ghostY = calculateGhostY(newState)
                    dispatch(GameStore.Msg.GameStateUpdated(newState, ghostY))
                } else {
                    // Piece can't move down, so lock it.
                    lockPiece(state)
                }
            }
        }

        private fun pauseGame(state: GameStore.State) {
            gameLoopUseCase.pause()
            dispatch(GameStore.Msg.PausedChanged(true))

            // Save game state
            scope.launch {
                state.gameState?.let { gameStateRepository.saveGameState(it) }
            }
            publish(GameStore.Label.GamePaused)
            audioRepository.stopMusic()
        }

        private fun resumeGame(state: GameStore.State) {
            gameLoopUseCase.resume()
            dispatch(GameStore.Msg.PausedChanged(false))
            if (state.settings.audioSettings.musicEnabled) {
                scope.launch {
                    audioRepository.playMusic(state.settings.audioSettings.selectedMusicTheme)
                }
            }
            publish(GameStore.Label.ResumeGame)
        }

        private fun quitGame(state: GameStore.State) {
            gameLoopUseCase.stop()

            audioRepository.stopMusic()

            // Save game state before quitting.
            scope.launch {
                state.gameState?.let { gameStateRepository.saveGameState(it) }
            }

            publish(GameStore.Label.NavigateBack)
        }

        private fun moveLeft(state: GameStore.State) {
            state.gameState?.let { gameState ->
                if (!state.isPaused && !gameState.isGameOver) {
                    movePieceUseCase.moveLeft(gameState)?.let { newState ->
                        audioRepository.playSoundEffect(SoundEffect.PIECE_MOVE)
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

        private fun handleSwipe(
            intent: GameStore.Intent.HandleSwipe,
            state: GameStore.State,
        ) {
            state.gameState?.let { gameState ->
                if (!state.isPaused && !gameState.isGameOver) {
                    handleSwipeInputUseCase(
                        gameState,
                        intent.deltaX,
                        intent.deltaY,
                        intent.velocityX,
                        intent.velocityY,
                    )?.let { result ->
                        val ghostY = calculateGhostY(result.state)
                        dispatch(GameStore.Msg.GameStateUpdated(result.state, ghostY))

                        if (result.action == HandleSwipeInputUseCase.SwipeAction.HardDrop) {
                            lockPiece(state.copy(gameState = result.state))
                        }
                    }
                }
            }
        }

        private fun lockPiece(state: GameStore.State) {
            state.gameState?.let { gameState ->
                val oldLines = gameState.linesCleared
                val newState = lockPieceUseCase(gameState)

                val linesCleared = newState.linesCleared - oldLines
                when (linesCleared) {
                    0L -> audioRepository.playSoundEffect(SoundEffect.PIECE_DROP)
                    4L -> audioRepository.playSoundEffect(SoundEffect.TETRIS)
                    else -> audioRepository.playSoundEffect(SoundEffect.LINE_CLEAR)
                }

//                if (newState.level > gameState.level) {
//                    audioRepository.playSoundEffect(SoundEffect.LEVEL_UP)
//                }

                val ghostY = calculateGhostY(newState)
                dispatch(GameStore.Msg.GameStateUpdated(newState, ghostY))

                if (newState.isGameOver) {
                    handleGameOver(newState, state.settings)
                }
            }
        }

        private fun calculateGhostY(gameState: GameState): Int? =
            gameState.currentPiece?.let { piece ->
                calculateGhostPositionUseCase(
                    gameState = gameState,
                    piece = piece,
                    currentPosition = gameState.currentPosition,
                )
            }

        private fun handleGameOver(
            gameState: GameState,
            settings: GameSettings,
        ) {
            gameLoopUseCase.stop()

            audioRepository.stopMusic()
            audioRepository.playSoundEffect(SoundEffect.GAME_OVER)
            scope.launch {
                try {
                    // Save game record
                    val record =
                        GameRecord(
                            id = Uuid.random().toString(),
                            score = gameState.score,
                            linesCleared = gameState.linesCleared,
                            difficulty = settings.difficulty,
                            timestamp = Clock.System.now().toEpochMilliseconds(),
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

        /**
         * A helper function to process a gesture event and execute the resulting action.
         */
        private fun handleGestureEvent(
            event: GestureEvent,
            state: GameStore.State,
        ) {
            val result = gestureHandlingUseCase(event)
            when (result) {
                is GestureResult.MoveLeft -> moveLeft(state)
                is GestureResult.MoveRight -> moveRight(state)
                is GestureResult.MoveDown -> moveDown(state)
                is GestureResult.HardDrop -> hardDrop(state)
                null -> { /* No action triggered, do nothing */ }
            }
        }
    }
}
