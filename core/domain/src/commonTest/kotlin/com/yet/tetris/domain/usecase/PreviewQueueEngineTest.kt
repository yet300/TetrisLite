package com.yet.tetris.domain.usecase

import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.game.Tetromino
import com.yet.tetris.domain.model.game.TetrominoType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PreviewQueueEngineTest {
    private val generateTetrominoUseCase = GenerateTetrominoUseCase()
    private val engine = PreviewQueueEngine(generateTetrominoUseCase)

    @Test
    fun createInitialPreview_produces_fixed_preview_size() {
        val preview = engine.createInitialPreview()

        assertEquals(0, preview.nextPiece.rotation)
        assertEquals(GameState.QUEUE_SIZE, preview.nextQueue.size)
        assertTrue(preview.nextQueue.all { it.rotation == 0 })
    }

    @Test
    fun advance_consumes_head_and_refills_tail() {
        val queue =
            listOf(
                Tetromino.create(TetrominoType.O, rotation = 1),
                Tetromino.create(TetrominoType.J, rotation = 2),
                Tetromino.create(TetrominoType.L, rotation = 3),
                Tetromino.create(TetrominoType.S, rotation = 1),
            )

        val preview = engine.advance(queue)

        assertEquals(TetrominoType.O, preview.nextPiece.type)
        assertEquals(0, preview.nextPiece.rotation)
        assertEquals(GameState.QUEUE_SIZE, preview.nextQueue.size)
        assertEquals(TetrominoType.J, preview.nextQueue[0].type)
        assertEquals(TetrominoType.L, preview.nextQueue[1].type)
        assertEquals(TetrominoType.S, preview.nextQueue[2].type)
        assertTrue(preview.nextQueue.all { it.rotation == 0 })
    }

    @Test
    fun normalize_resets_rotations_and_backfills_queue() {
        val preview =
            engine.normalize(
                nextPiece = Tetromino.create(TetrominoType.T, rotation = 3),
                nextQueue = listOf(Tetromino.create(TetrominoType.I, rotation = 2)),
            )

        assertEquals(TetrominoType.T, preview.nextPiece.type)
        assertEquals(0, preview.nextPiece.rotation)
        assertEquals(GameState.QUEUE_SIZE, preview.nextQueue.size)
        assertEquals(TetrominoType.I, preview.nextQueue.first().type)
        assertTrue(preview.nextQueue.all { it.rotation == 0 })
    }
}
