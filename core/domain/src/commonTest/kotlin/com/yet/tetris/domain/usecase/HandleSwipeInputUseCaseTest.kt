package com.yet.tetris.domain.usecase

import com.yet.tetris.domain.model.game.GameBoard
import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.game.Position
import com.yet.tetris.domain.model.game.Tetromino
import com.yet.tetris.domain.model.game.TetrominoType
import com.yet.tetris.domain.model.settings.SwipeSensitivity
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class HandleSwipeInputUseCaseTest {

    private val checkCollision = CheckCollisionUseCase()
    private val movePiece = MovePieceUseCase(checkCollision)
    private val hardDrop = HardDropUseCase(checkCollision)
    private val useCase = HandleSwipeInputUseCase(movePiece, hardDrop)

    private fun createTestState(): GameState {
        return GameState(
            board = GameBoard(width = 10, height = 20),
            currentPiece = Tetromino.create(TetrominoType.I),
            currentPosition = Position(x = 4, y = 0),
            nextPiece = Tetromino.create(TetrominoType.T),
            score = 0,
            linesCleared = 0,
            isGameOver = false,
            isPaused = false
        )
    }

    @Test
    fun `horizontal swipe right moves piece right`() {
        val state = createTestState()
        val sensitivity = SwipeSensitivity()
        
        val result = useCase(
            state = state,
            deltaX = 100f,
            deltaY = 10f,
            velocityX = 500f,
            velocityY = 50f,
            sensitivity = sensitivity
        )
        
        assertNotNull(result)
        assertEquals(state.currentPosition.x + 1, result.currentPosition.x)
    }

    @Test
    fun `horizontal swipe left moves piece left`() {
        val state = createTestState()
        val sensitivity = SwipeSensitivity()
        
        val result = useCase(
            state = state,
            deltaX = -100f,
            deltaY = 10f,
            velocityX = -500f,
            velocityY = 50f,
            sensitivity = sensitivity
        )
        
        assertNotNull(result)
        assertEquals(state.currentPosition.x - 1, result.currentPosition.x)
    }

    @Test
    fun `slow vertical swipe triggers soft drop`() {
        val state = createTestState()
        val sensitivity = SwipeSensitivity(
            softDropThreshold = 1000f,
            verticalSensitivity = 1f
        )
        
        val result = useCase(
            state = state,
            deltaX = 10f,
            deltaY = 100f,
            velocityX = 50f,
            velocityY = 500f,
            sensitivity = sensitivity
        )
        
        assertNotNull(result)
        assertEquals(state.currentPosition.y + 1, result.currentPosition.y)
    }

    @Test
    fun `fast vertical swipe triggers hard drop`() {
        val state = createTestState()
        val sensitivity = SwipeSensitivity(
            softDropThreshold = 500f,
            verticalSensitivity = 1f
        )
        
        val result = useCase(
            state = state,
            deltaX = 10f,
            deltaY = 100f,
            velocityX = 50f,
            velocityY = 2000f,
            sensitivity = sensitivity
        )
        
        assertNotNull(result)
        // Hard drop should move piece significantly down
        assertTrue(result.currentPosition.y > state.currentPosition.y + 1)
    }

    @Test
    fun `equal horizontal and vertical delta returns None action`() {
        val state = createTestState()
        val sensitivity = SwipeSensitivity()
        
        val result = useCase(
            state = state,
            deltaX = 50f,
            deltaY = 50f,
            velocityX = 500f,
            velocityY = 500f,
            sensitivity = sensitivity
        )
        
        assertNull(result)
    }

    @Test
    fun `swipe sensitivity affects hard drop threshold`() {
        val state = createTestState()
        val highSensitivity = SwipeSensitivity(
            softDropThreshold = 100f,
            verticalSensitivity = 2f
        )
        
        val result = useCase(
            state = state,
            deltaX = 10f,
            deltaY = 100f,
            velocityX = 50f,
            velocityY = 300f,
            sensitivity = highSensitivity
        )
        
        assertNotNull(result)
        // With high sensitivity, lower velocity should trigger hard drop
        assertTrue(result.currentPosition.y > state.currentPosition.y + 1)
    }

    @Test
    fun `handleTap returns None action`() {
        val state = createTestState()
        
        val result = useCase.handleTap(state)
        
        assertEquals(HandleSwipeInputUseCase.SwipeAction.None, result)
    }

    @Test
    fun `swipe at board edge does not move piece out of bounds`() {
        val state = createTestState().copy(
            currentPosition = Position(x = 0, y = 0)
        )
        val sensitivity = SwipeSensitivity()
        
        val result = useCase(
            state = state,
            deltaX = -100f,
            deltaY = 10f,
            velocityX = -500f,
            velocityY = 50f,
            sensitivity = sensitivity
        )
        
        // Should return null or same position since piece can't move left
        if (result != null) {
            assertEquals(state.currentPosition.x, result.currentPosition.x)
        }
    }
}
