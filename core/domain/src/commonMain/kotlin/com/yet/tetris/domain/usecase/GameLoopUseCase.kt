package com.yet.tetris.domain.usecase

import com.yet.tetris.domain.model.settings.GameSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

// Define the events that the UseCase can emit
sealed interface GameLoopEvent {
    data object GameTick : GameLoopEvent // Event for the piece to fall
    data class TimerUpdated(val elapsedTime: Long) : GameLoopEvent
}

/**
 * Manages the main game loop and the elapsed time timer.
 * This UseCase encapsulates the complexity of starting, stopping, pausing,
 * and resuming the time-based events of the game.
 */
@OptIn(ExperimentalTime::class)
class GameLoopUseCase(
    private val scope: CoroutineScope
) {

    private val _events = MutableSharedFlow<GameLoopEvent>()
    val events: Flow<GameLoopEvent> = _events.asSharedFlow()

    private var gameLoopJob: Job? = null
    private var timerJob: Job? = null

    private var gameStartTime: Long = 0
    private var timePaused: Long = 0
    private var totalPausedDuration: Long = 0

    private var isPaused = false

    /**
     * Starts the game loop and the timer.
     * @param settings The current game settings, used to determine the fall delay.
     */
    fun start(settings: GameSettings) {
        stop() // Ensure previous loops are stopped

        isPaused = false
        gameStartTime = Clock.System.now().toEpochMilliseconds()
        totalPausedDuration = 0

        gameLoopJob = scope.launch {
            while (isActive) {
                delay(settings.difficulty.fallDelayMs)
                if (!isPaused) {
                    _events.emit(GameLoopEvent.GameTick)
                }
            }
        }

        timerJob = scope.launch {
            while (isActive) {
                delay(100)
                if (!isPaused) {
                    val elapsed = (Clock.System.now()
                        .toEpochMilliseconds() - gameStartTime) - totalPausedDuration
                    _events.emit(GameLoopEvent.TimerUpdated(elapsed))
                }
            }
        }
    }

    /**
     * Pauses the game loop and the timer.
     */
    fun pause() {
        if (isPaused) return
        isPaused = true
        timePaused = Clock.System.now().toEpochMilliseconds()
    }

    /**
     * Resumes the game loop and the timer.
     */
    fun resume() {
        if (!isPaused) return
        isPaused = false
        totalPausedDuration += (Clock.System.now().toEpochMilliseconds() - timePaused)
    }

    /**
     * Stops and cancels all running loops and timers.
     */
    fun stop() {
        gameLoopJob?.cancel()
        timerJob?.cancel()
        gameLoopJob = null
        timerJob = null
        isPaused = false
    }
}