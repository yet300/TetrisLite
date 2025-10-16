package com.yet.tetris.domain.usecase

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GestureHandlingUseCaseTest {

    private val useCase = GestureHandlingUseCase()
    private val boardHeightPx = 1000f

    @Test
    fun `DragStarted returns null and initializes state`() {
        val result = useCase(GestureEvent.DragStarted(boardHeightPx))
        assertNull(result)
    }

    @Test
    fun `horizontal swipe right triggers MoveRight`() {
        useCase(GestureEvent.DragStarted(boardHeightPx))
        
        // Accumulate horizontal drag beyond threshold
        val result1 = useCase(GestureEvent.Dragged(deltaX = 60f, deltaY = 10f))
        
        assertEquals(GestureResult.MoveRight, result1)
    }

    @Test
    fun `horizontal swipe left triggers MoveLeft`() {
        useCase(GestureEvent.DragStarted(boardHeightPx))
        
        val result = useCase(GestureEvent.Dragged(deltaX = -60f, deltaY = 10f))
        
        assertEquals(GestureResult.MoveLeft, result)
    }

    @Test
    fun `small horizontal drag does not trigger action`() {
        useCase(GestureEvent.DragStarted(boardHeightPx))
        
        val result = useCase(GestureEvent.Dragged(deltaX = 30f, deltaY = 5f))
        
        assertNull(result)
    }

    @Test
    fun `vertical swipe down triggers MoveDown on DragEnded`() {
        useCase(GestureEvent.DragStarted(boardHeightPx))
        
        useCase(GestureEvent.Dragged(deltaX = 0f, deltaY = 60f))
        val result = useCase(GestureEvent.DragEnded)
        
        assertEquals(GestureResult.MoveDown, result)
    }

    @Test
    fun `fast vertical swipe triggers HardDrop`() {
        useCase(GestureEvent.DragStarted(boardHeightPx))
        
        // Large vertical distance in short time
        useCase(GestureEvent.Dragged(deltaX = 0f, deltaY = 300f))
        val result = useCase(GestureEvent.DragEnded)
        
        assertEquals(GestureResult.HardDrop, result)
    }

    @Test
    fun `accumulated horizontal drags trigger multiple moves`() {
        useCase(GestureEvent.DragStarted(boardHeightPx))
        
        val result1 = useCase(GestureEvent.Dragged(deltaX = 60f, deltaY = 5f))
        assertEquals(GestureResult.MoveRight, result1)
        
        val result2 = useCase(GestureEvent.Dragged(deltaX = 60f, deltaY = 5f))
        assertEquals(GestureResult.MoveRight, result2)
    }

    @Test
    fun `DragEnded resets state`() {
        useCase(GestureEvent.DragStarted(boardHeightPx))
        useCase(GestureEvent.Dragged(deltaX = 30f, deltaY = 10f))
        useCase(GestureEvent.DragEnded)
        
        // New drag should start fresh
        val result = useCase(GestureEvent.Dragged(deltaX = 30f, deltaY = 10f))
        assertNull(result)
    }

    @Test
    fun `zero delta drag returns null`() {
        useCase(GestureEvent.DragStarted(boardHeightPx))
        
        val result = useCase(GestureEvent.Dragged(deltaX = 0f, deltaY = 0f))
        
        assertNull(result)
    }

    @Test
    fun `horizontal swipe determination is sticky`() {
        useCase(GestureEvent.DragStarted(boardHeightPx))
        
        // First drag determines horizontal
        useCase(GestureEvent.Dragged(deltaX = 40f, deltaY = 10f))
        
        // Subsequent vertical drag should not trigger vertical action
        val result = useCase(GestureEvent.Dragged(deltaX = 5f, deltaY = 100f))
        assertNull(result)
        
        val endResult = useCase(GestureEvent.DragEnded)
        assertNull(endResult) // Should not be MoveDown since horizontal was determined
    }

    @Test
    fun `DragEnded without DragStarted returns null`() {
        val result = useCase(GestureEvent.DragEnded)
        assertNull(result)
    }

    @Test
    fun `Dragged without DragStarted returns null`() {
        val result = useCase(GestureEvent.Dragged(deltaX = 50f, deltaY = 10f))
        assertNull(result)
    }
}
