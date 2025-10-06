package com.yet.tetris.domain.usecase

import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.settings.SwipeSensitivity
import kotlin.math.abs

/**
 * Use case for handling swipe gestures on mobile platforms.
 * Interprets swipe direction and velocity to determine the appropriate game action.
 */
class HandleSwipeInputUseCase(
    private val movePiece: MovePieceUseCase,
    private val hardDrop: HardDropUseCase
) {
    
    /**
     * Represents the result of processing a swipe gesture.
     */
    sealed class SwipeAction {
        object MoveLeft : SwipeAction()
        object MoveRight : SwipeAction()
        object SoftDrop : SwipeAction()
        object HardDrop : SwipeAction()
        object None : SwipeAction()
    }
    
    /**
     * Processes a swipe gesture and returns the updated game state.
     * 
     * @param state Current game state
     * @param deltaX Horizontal swipe distance (positive = right, negative = left)
     * @param deltaY Vertical swipe distance (positive = down)
     * @param velocityX Horizontal swipe velocity
     * @param velocityY Vertical swipe velocity
     * @param sensitivity Swipe sensitivity settings
     * @return Updated GameState or null if no valid action
     */
    operator fun invoke(
        state: GameState,
        deltaX: Float,
        deltaY: Float,
        velocityX: Float,
        velocityY: Float,
        sensitivity: SwipeSensitivity
    ): GameState? {
        val action = determineSwipeAction(deltaX, deltaY, velocityX, velocityY, sensitivity)
        
        return when (action) {
            SwipeAction.MoveLeft -> movePiece.moveLeft(state)
            SwipeAction.MoveRight -> movePiece.moveRight(state)
            SwipeAction.SoftDrop -> movePiece.moveDown(state)
            SwipeAction.HardDrop -> {
                // Hard drop moves to lowest position
                val droppedState = hardDrop(state)
                droppedState
            }
            SwipeAction.None -> null
        }
    }
    
    /**
     * Determines the action based on swipe direction and velocity.
     * Vertical swipes: slow = soft drop, fast = hard drop
     * Horizontal swipes: move left or right
     */
    private fun determineSwipeAction(
        deltaX: Float,
        deltaY: Float,
        velocityX: Float,
        velocityY: Float,
        sensitivity: SwipeSensitivity
    ): SwipeAction {
        val absX = abs(deltaX)
        val absY = abs(deltaY)
        
        // Determine primary direction
        return if (absX > absY) {
            // Horizontal swipe
            if (deltaX > 0) SwipeAction.MoveRight else SwipeAction.MoveLeft
        } else if (absY > absX) {
            // Vertical swipe - check velocity to determine soft vs hard drop
            val normalizedVelocity = abs(velocityY) * sensitivity.verticalSensitivity
            if (normalizedVelocity > sensitivity.softDropThreshold) {
                SwipeAction.HardDrop
            } else {
                SwipeAction.SoftDrop
            }
        } else {
            SwipeAction.None
        }
    }
    
    /**
     * Handles a tap gesture (used for rotation).
     */
    fun handleTap(state: GameState): SwipeAction {
        // Tap is handled separately and triggers rotation
        // This is just a marker - actual rotation is handled by RotatePieceUseCase
        return SwipeAction.None
    }
}
