package com.yet.tetris.feature.game.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.yet.tetris.domain.model.effects.VisualEffectFeed
import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.game.RotationDirection
import com.yet.tetris.domain.model.settings.GameSettings
import com.yet.tetris.domain.repository.GameSettingsRepository
import com.yet.tetris.domain.usecase.AdvanceGameTickUseCase
import com.yet.tetris.domain.usecase.GameLoopEvent
import com.yet.tetris.domain.usecase.GameLoopUseCase
import com.yet.tetris.domain.usecase.GestureEvent
import com.yet.tetris.domain.usecase.GestureHandlingUseCase
import com.yet.tetris.domain.usecase.GestureResult
import com.yet.tetris.domain.usecase.HandleSwipeInputUseCase
import com.yet.tetris.domain.usecase.HardDropUseCase
import com.yet.tetris.domain.usecase.HoldPieceUseCase
import com.yet.tetris.domain.usecase.InitializeGameSessionUseCase
import com.yet.tetris.domain.usecase.MovePieceUseCase
import com.yet.tetris.domain.usecase.PersistGameAudioUseCase
import com.yet.tetris.domain.usecase.ProcessLockedPieceUseCase
import com.yet.tetris.domain.usecase.RotatePieceUseCase
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

internal class GameStoreFactory
    constructor(
        private val storeFactory: StoreFactory,
        private val gameSettingsRepository: GameSettingsRepository,
        private val movePieceUseCase: MovePieceUseCase,
        private val rotatePieceUseCase: RotatePieceUseCase,
        private val hardDropUseCase: HardDropUseCase,
        private val holdPieceUseCase: HoldPieceUseCase,
        private val handleSwipeInputUseCase: HandleSwipeInputUseCase,
        private val gestureHandlingUseCase: GestureHandlingUseCase,
        private val initializeGameSessionUseCase: InitializeGameSessionUseCase,
        private val advanceGameTickUseCase: AdvanceGameTickUseCase,
        private val processLockedPieceUseCase: ProcessLockedPieceUseCase,
        private val persistGameAudioUseCase: PersistGameAudioUseCase,
    ) {
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
                            comboStreak = 0,
                            visualEffectFeed = VisualEffectFeed(),
                        )

                    is GameStore.Msg.GameStateUpdated ->
                        copy(
                            gameState = msg.gameState,
                            ghostPieceY = msg.ghostPieceY,
                        )

                    is GameStore.Msg.PausedChanged -> copy(gameState = gameState?.copy(isPaused = msg.isPaused))
                    is GameStore.Msg.ElapsedTimeUpdated -> copy(elapsedTime = msg.elapsedTime)
                    is GameStore.Msg.LoadingChanged -> copy(isLoading = msg.isLoading)
                    is GameStore.Msg.SettingsUpdated -> copy(settings = msg.settings)
                    is GameStore.Msg.ComboStreakUpdated -> copy(comboStreak = msg.comboStreak)
                    is GameStore.Msg.VisualEffectFeedUpdated ->
                        copy(
                            comboStreak = msg.comboStreak,
                            visualEffectFeed = msg.visualEffectFeed,
                        )

                    is GameStore.Msg.VisualEffectConsumed ->
                        if (visualEffectFeed.sequence == msg.sequence) {
                            copy(visualEffectFeed = visualEffectFeed.copy(latest = null))
                        } else {
                            this
                        }
                }
        }

        private inner class ExecutorImpl :
            CoroutineExecutor<GameStore.Intent, GameStore.Action, GameStore.State, GameStore.Msg, GameStore.Label>() {
            private val gameLoopUseCase = GameLoopUseCase(scope = this.scope)
            private var boardHeightPx: Float = 0f

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
                            persistGameAudioUseCase.applyAudioSettings(newSettings)
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
                    is GameStore.Intent.RotateClockwise -> rotate(getState, RotationDirection.CLOCKWISE)
                    is GameStore.Intent.RotateCounterClockwise -> rotate(getState, RotationDirection.COUNTERCLOCKWISE)
                    is GameStore.Intent.Rotate180 -> rotate(getState, RotationDirection.ONE_EIGHTY)
                    is GameStore.Intent.HardDrop -> hardDrop(getState)
                    is GameStore.Intent.Hold -> hold(getState)
                    is GameStore.Intent.HandleSwipe -> handleSwipe(intent, getState)

                    is GameStore.Intent.ToggleMusic -> {
                        val currentState = state()
                        val newSettings =
                            currentState.settings.copy(
                                audioSettings =
                                    currentState.settings.audioSettings.copy(
                                        musicEnabled = intent.enabled,
                                    ),
                            )
                        dispatch(GameStore.Msg.SettingsUpdated(newSettings))

                        scope.launch {
                            try {
                                gameSettingsRepository.saveSettings(newSettings)
                            } catch (e: Exception) {
                                publish(
                                    GameStore.Label.ShowError(
                                        e.message ?: "Failed to save settings",
                                    ),
                                )
                            }
                            persistGameAudioUseCase.applyAudioSettings(newSettings)
                            if (intent.enabled) {
                                persistGameAudioUseCase.playMusicIfEnabled(newSettings)
                            } else {
                                persistGameAudioUseCase.stopMusic()
                            }
                        }
                    }

                    is GameStore.Intent.OnBoardSizeChanged -> {
                        boardHeightPx = intent.height
                    }

                    is GameStore.Intent.DragStarted -> {
                        if (boardHeightPx > 0f) {
                            handleGestureEvent(GestureEvent.DragStarted(boardHeightPx), getState)
                        }
                    }

                    is GameStore.Intent.Dragged -> {
                        handleGestureEvent(GestureEvent.Dragged(intent.deltaX, intent.deltaY), getState)
                    }

                    is GameStore.Intent.DragEnded -> {
                        handleGestureEvent(GestureEvent.DragEnded, getState)
                    }

                    is GameStore.Intent.VisualEffectConsumed -> {
                        dispatch(GameStore.Msg.VisualEffectConsumed(intent.sequence))
                    }
                }
            }

            private fun initializeGame(forceNewGame: Boolean) {
                scope.launch {
                    try {
                        dispatch(GameStore.Msg.LoadingChanged(true))
                        persistGameAudioUseCase.initializeAudio()

                        val gameSession = initializeGameSessionUseCase(forceNewGame)
                        persistGameAudioUseCase.applyAudioSettings(gameSession.settings)
                        val gameState =
                            if (gameSession.gameState.isPaused) {
                                gameSession.gameState.copy(isPaused = false)
                            } else {
                                gameSession.gameState
                            }

                        dispatch(
                            GameStore.Msg.GameInitialized(
                                gameState,
                                gameSession.settings,
                            ),
                        )
                        dispatch(GameStore.Msg.LoadingChanged(false))

                        gameLoopUseCase.start(
                            gameSession.settings,
                            initialLevel = gameState.level,
                        )
                        persistGameAudioUseCase.playMusicIfEnabled(gameSession.settings)
                    } catch (e: Exception) {
                        dispatch(GameStore.Msg.LoadingChanged(false))
                        publish(GameStore.Label.ShowError(e.message ?: "Failed to initialize game"))
                    }
                }
            }

            private fun autoMoveDown(state: GameStore.State) {
                val gameState = state.playableGameState() ?: return
                when (val result = advanceGameTickUseCase(gameState)) {
                    is AdvanceGameTickUseCase.Result.Moved -> {
                        dispatch(GameStore.Msg.GameStateUpdated(result.gameState, result.ghostPieceY))
                    }

                    is AdvanceGameTickUseCase.Result.RequiresLock -> {
                        lockPiece(state.copy(gameState = result.gameState))
                    }
                }
            }

            private fun pauseGame(state: GameStore.State) {
                gameLoopUseCase.pause()
                dispatch(GameStore.Msg.PausedChanged(true))

                scope.launch {
                    persistGameAudioUseCase.saveCurrentState(state.gameState?.copy(isPaused = true))
                }
                publish(GameStore.Label.GamePaused)
                persistGameAudioUseCase.stopMusic()
            }

            private fun resumeGame(state: GameStore.State) {
                gameLoopUseCase.resume()
                dispatch(GameStore.Msg.PausedChanged(false))
                scope.launch {
                    persistGameAudioUseCase.playMusicIfEnabled(state.settings)
                }
                publish(GameStore.Label.ResumeGame)
            }

            private fun quitGame(state: GameStore.State) {
                gameLoopUseCase.stop()
                persistGameAudioUseCase.stopMusic()
                scope.launch {
                    persistGameAudioUseCase.saveCurrentState(state.gameState)
                }

                publish(GameStore.Label.NavigateBack)
            }

            private fun moveLeft(state: GameStore.State) {
                val gameState = state.playableGameState() ?: return
                when (val result = movePieceUseCase.moveLeft(gameState)) {
                    is MovePieceUseCase.Result.Applied -> {
                        val newState = result.gameState.copy(isTSpinEligible = false)
                        persistGameAudioUseCase.playMoveSound()
                        dispatch(
                            GameStore.Msg.GameStateUpdated(
                                newState,
                                advanceGameTickUseCase.calculateGhostY(newState),
                            ),
                        )
                    }

                    is MovePieceUseCase.Result.Blocked -> Unit
                }
            }

            private fun moveRight(state: GameStore.State) {
                val gameState = state.playableGameState() ?: return
                when (val result = movePieceUseCase.moveRight(gameState)) {
                    is MovePieceUseCase.Result.Applied -> {
                        val newState = result.gameState.copy(isTSpinEligible = false)
                        dispatch(
                            GameStore.Msg.GameStateUpdated(
                                newState,
                                advanceGameTickUseCase.calculateGhostY(newState),
                            ),
                        )
                    }

                    is MovePieceUseCase.Result.Blocked -> Unit
                }
            }

            private fun moveDown(state: GameStore.State) {
                val gameState = state.playableGameState() ?: return
                when (val result = movePieceUseCase.moveDown(gameState)) {
                    is MovePieceUseCase.Result.Applied -> {
                        val movedState =
                            result.gameState.copy(
                                score = result.gameState.score + 1,
                                softDropCells = result.gameState.softDropCells + 1,
                                isTSpinEligible = false,
                            )
                        dispatch(
                            GameStore.Msg.GameStateUpdated(
                                movedState,
                                advanceGameTickUseCase.calculateGhostY(movedState),
                            ),
                        )
                    }

                    is MovePieceUseCase.Result.Blocked -> lockPiece(state.copy(gameState = gameState))
                }
            }

            private fun rotate(
                state: GameStore.State,
                direction: RotationDirection = state.settings.controlSettings.primaryRotateDirection,
            ) {
                if (direction == RotationDirection.ONE_EIGHTY && !state.settings.controlSettings.enable180Rotation) {
                    return
                }
                val gameState = state.playableGameState() ?: return
                when (val result = rotatePieceUseCase(gameState, direction)) {
                    is RotatePieceUseCase.Result.Applied -> {
                        val newState = result.gameState.copy(isTSpinEligible = true)
                        dispatch(
                            GameStore.Msg.GameStateUpdated(
                                newState,
                                advanceGameTickUseCase.calculateGhostY(newState),
                            ),
                        )
                    }

                    is RotatePieceUseCase.Result.Blocked -> Unit
                }
            }

            private fun hardDrop(state: GameStore.State) {
                val gameState = state.playableGameState() ?: return
                val dropDistance = hardDropUseCase.calculateDropDistance(gameState)
                hardDropUseCase(gameState)?.let { droppedState ->
                    val scoredState =
                        droppedState.copy(
                            score = droppedState.score + (dropDistance * 2),
                            hardDrops = droppedState.hardDrops + 1,
                            hardDropCells = droppedState.hardDropCells + dropDistance,
                        )
                    dispatch(
                        GameStore.Msg.GameStateUpdated(
                            scoredState,
                            advanceGameTickUseCase.calculateGhostY(scoredState),
                        ),
                    )
                    lockPiece(state.copy(gameState = scoredState))
                }
            }

            private fun hold(state: GameStore.State) {
                val gameState = state.playableGameState() ?: return
                when (val result = holdPieceUseCase(gameState)) {
                    is HoldPieceUseCase.Result.Applied -> {
                        val heldState = result.gameState.copy(isTSpinEligible = false)
                        dispatch(
                            GameStore.Msg.GameStateUpdated(
                                heldState,
                                advanceGameTickUseCase.calculateGhostY(heldState),
                            ),
                        )

                        if (heldState.isGameOver) {
                            handleGameOver(
                                gameState = heldState,
                                settings = state.settings,
                                elapsedTime = state.elapsedTime,
                            )
                        }
                    }

                    is HoldPieceUseCase.Result.Blocked -> Unit
                }
            }

            private fun handleSwipe(
                intent: GameStore.Intent.HandleSwipe,
                state: GameStore.State,
            ) {
                val gameState = state.playableGameState() ?: return
                handleSwipeInputUseCase(
                    gameState,
                    intent.deltaX,
                    intent.deltaY,
                    intent.velocityX,
                    intent.velocityY,
                    state.settings.controlSettings.gestureSensitivity,
                )?.let { result ->
                    val updatedState =
                        when (result.action) {
                            HandleSwipeInputUseCase.SwipeAction.SoftDrop ->
                                result.state.copy(
                                    score = result.state.score + 1,
                                    softDropCells = result.state.softDropCells + 1,
                                    isTSpinEligible = false,
                                )

                            HandleSwipeInputUseCase.SwipeAction.HardDrop -> {
                                val cellsDropped = result.state.currentPosition.y - gameState.currentPosition.y
                                result.state.copy(
                                    score = result.state.score + (cellsDropped * 2),
                                    hardDrops = result.state.hardDrops + 1,
                                    hardDropCells = result.state.hardDropCells + cellsDropped,
                                )
                            }

                            HandleSwipeInputUseCase.SwipeAction.MoveLeft,
                            HandleSwipeInputUseCase.SwipeAction.MoveRight,
                            HandleSwipeInputUseCase.SwipeAction.None,
                            ->
                                result.state.copy(isTSpinEligible = false)
                        }
                    dispatch(
                        GameStore.Msg.GameStateUpdated(
                            updatedState,
                            advanceGameTickUseCase.calculateGhostY(updatedState),
                        ),
                    )

                    if (result.action == HandleSwipeInputUseCase.SwipeAction.HardDrop) {
                        lockPiece(state.copy(gameState = updatedState))
                    }
                }
            }

            private fun lockPiece(state: GameStore.State) {
                val gameState = state.gameState ?: return
                val lockResult =
                    processLockedPieceUseCase(
                        gameState = gameState,
                        currentComboStreak = state.comboStreak,
                        currentVisualSequence = state.visualEffectFeed.sequence,
                    )

                lockResult.visualEffectFeed?.let { feed ->
                    dispatch(
                        GameStore.Msg.VisualEffectFeedUpdated(
                            comboStreak = lockResult.nextComboStreak,
                            visualEffectFeed = feed,
                        ),
                    )
                } ?: dispatch(GameStore.Msg.ComboStreakUpdated(lockResult.nextComboStreak))

                persistGameAudioUseCase.playLockSounds(
                    linesCleared = lockResult.linesCleared,
                    levelIncreased = lockResult.levelIncreased,
                )
                gameLoopUseCase.updateLevel(lockResult.gameState.level)

                dispatch(
                    GameStore.Msg.GameStateUpdated(
                        lockResult.gameState,
                        lockResult.ghostPieceY,
                    ),
                )
                if (lockResult.gameState.isGameOver) {
                    handleGameOver(
                        gameState = lockResult.gameState,
                        settings = state.settings,
                        elapsedTime = state.elapsedTime,
                    )
                }
            }

            private fun handleGameOver(
                gameState: GameState,
                settings: GameSettings,
                elapsedTime: Long,
            ) {
                gameLoopUseCase.stop()
                dispatch(GameStore.Msg.ComboStreakUpdated(0))
                persistGameAudioUseCase.playGameOverSound()

                scope.launch {
                    try {
                        persistGameAudioUseCase.saveCompletedGame(gameState, settings, elapsedTime)
                        publish(GameStore.Label.GameOver)
                    } catch (e: Exception) {
                        publish(GameStore.Label.ShowError(e.message ?: "Failed to save game"))
                    }
                }
            }

            private fun handleGestureEvent(
                event: GestureEvent,
                state: GameStore.State,
            ) {
                when (gestureHandlingUseCase(event, state.settings.controlSettings.gestureSensitivity)) {
                    is GestureResult.MoveLeft -> moveLeft(state)
                    is GestureResult.MoveRight -> moveRight(state)
                    is GestureResult.MoveDown -> moveDown(state)
                    is GestureResult.HardDrop -> hardDrop(state)
                    null -> Unit
                }
            }

            private fun GameStore.State.playableGameState(): GameState? {
                val gameState = gameState ?: return null
                if (isPaused || gameState.isGameOver) {
                    return null
                }
                return gameState
            }
        }
    }
