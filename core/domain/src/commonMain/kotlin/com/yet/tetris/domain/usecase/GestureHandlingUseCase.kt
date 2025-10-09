package com.yet.tetris.domain.usecase

import jakarta.inject.Singleton
import kotlin.math.abs
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

// Define the inputs for the UseCase
sealed interface GestureEvent {
    data class DragStarted(val boardHeightPx: Float) : GestureEvent
    data class Dragged(val deltaX: Float, val deltaY: Float) : GestureEvent
    data object DragEnded : GestureEvent
}

// Define the possible outcomes of the UseCase
sealed interface GestureResult {
    data object MoveLeft : GestureResult
    data object MoveRight : GestureResult
    data object MoveDown : GestureResult
    data object HardDrop : GestureResult
}

/**
 * Handles raw drag gesture events and translates them into specific game actions.
 * This UseCase is stateful and maintains the context of an ongoing drag gesture.
 */
@OptIn(ExperimentalTime::class)
@Singleton
class GestureHandlingUseCase  {

    // Internal state of the current gesture
    private data class State(
        val accumulatedDragX: Float = 0f,
        val totalDragDistanceY: Float = 0f,
        val dragStartTime: Long = 0L,
        val isHorizontalSwipeDetermined: Boolean = false,
        val boardHeightPx: Float = 0f
    )

    private var state: State? = null
    private val swipeThreshold = 50f

    /**
     * Processes a single gesture event and returns a game action if one is triggered.
     *
     * @param event The raw gesture event from the UI layer.
     * @return A [GestureResult] representing a game action, or null if no action is triggered.
     */
    operator fun invoke(event: GestureEvent): GestureResult? {
        return when (event) {
            is GestureEvent.DragStarted -> {
                state = State(boardHeightPx = event.boardHeightPx, dragStartTime = Clock.System.now().toEpochMilliseconds())
                null // No action on drag start
            }
            is GestureEvent.DragEnded -> {
                val currentState = state ?: return null
                val dragDuration = Clock.System.now().toEpochMilliseconds() - currentState.dragStartTime
                
                val result = when {
                    // Check for a hard drop (fast flick down)
                    !currentState.isHorizontalSwipeDetermined &&
                            currentState.totalDragDistanceY > currentState.boardHeightPx * 0.25f &&
                            dragDuration < 500 -> GestureResult.HardDrop
                    
                    // Check for a regular soft drop
                    !currentState.isHorizontalSwipeDetermined &&
                            currentState.totalDragDistanceY > swipeThreshold -> GestureResult.MoveDown
                    
                    else -> null
                }
                
                state = null // Reset state on drag end
                result
            }
            is GestureEvent.Dragged -> {
                var currentState = state ?: return null
                
                var isHorizontal = currentState.isHorizontalSwipeDetermined
                // Determine swipe direction if not already set
                if (!isHorizontal && abs(event.deltaX) > abs(event.deltaY) * 1.5f) {
                    isHorizontal = true
                }
                
                // Update Y distance for hard drop check
                if (event.deltaY > 0) {
                    currentState = currentState.copy(totalDragDistanceY = currentState.totalDragDistanceY + event.deltaY)
                }

                var result: GestureResult? = null
                if (isHorizontal) {
                    val newAccumulatedX = currentState.accumulatedDragX + event.deltaX
                    if (abs(newAccumulatedX) > swipeThreshold) {
                        result = if (newAccumulatedX > 0) GestureResult.MoveRight else GestureResult.MoveLeft
                        // Reset accumulator for the next horizontal movement within the same drag
                        currentState = currentState.copy(accumulatedDragX = 0f, isHorizontalSwipeDetermined = true)
                    } else {
                        currentState = currentState.copy(accumulatedDragX = newAccumulatedX, isHorizontalSwipeDetermined = true)
                    }
                }
                
                state = currentState
                result
            }
        }
    }
}