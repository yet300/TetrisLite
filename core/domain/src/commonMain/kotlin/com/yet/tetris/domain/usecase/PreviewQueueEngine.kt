package com.yet.tetris.domain.usecase

import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.game.Tetromino
import kotlin.collections.ArrayDeque

/**
 * Shared queue mechanics for "next piece" preview handling.
 * Keeps GameState immutable while using a deque internally for efficient queue operations.
 */
class PreviewQueueEngine(
    private val generateTetrominoUseCase: GenerateTetrominoUseCase,
) {
    data class Preview(
        val nextPiece: Tetromino,
        val nextQueue: List<Tetromino>,
    )

    fun createInitialPreview(): Preview {
        val queue = ArrayDeque<Tetromino>()
        repeat(GameState.PREVIEW_PIECES_COUNT) {
            queue.addLast(createFreshPiece())
        }

        val nextPiece = queue.removeFirst()
        return Preview(
            nextPiece = nextPiece,
            nextQueue = queue.toList(),
        )
    }

    /**
     * Consumes one preview item and returns a replenished preview state.
     */
    fun advance(nextQueue: List<Tetromino>): Preview {
        val queue = normalizedQueue(nextQueue)
        refill(queue)

        val nextPiece = if (queue.isEmpty()) createFreshPiece() else queue.removeFirst()
        refill(queue)

        return Preview(
            nextPiece = nextPiece,
            nextQueue = queue.toList(),
        )
    }

    /**
     * Normalizes next-piece preview data without consuming an item.
     */
    fun normalize(
        nextPiece: Tetromino,
        nextQueue: List<Tetromino>,
    ): Preview {
        val queue = normalizedQueue(nextQueue)
        refill(queue)
        return Preview(
            nextPiece = nextPiece.resetRotation(),
            nextQueue = queue.toList(),
        )
    }

    private fun normalizedQueue(queue: List<Tetromino>): ArrayDeque<Tetromino> {
        val normalized = ArrayDeque<Tetromino>()
        queue
            .asSequence()
            .map { it.resetRotation() }
            .take(GameState.QUEUE_SIZE)
            .forEach { normalized.addLast(it) }
        return normalized
    }

    private fun refill(queue: ArrayDeque<Tetromino>) {
        while (queue.size < GameState.QUEUE_SIZE) {
            queue.addLast(createFreshPiece())
        }
    }

    private fun createFreshPiece(): Tetromino = generateTetrominoUseCase().resetRotation()

    private fun Tetromino.resetRotation(): Tetromino = Tetromino.create(type = type, rotation = 0)
}
