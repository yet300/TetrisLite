package com.yet.tetris.domain.usecase

import com.yet.tetris.domain.model.game.TetrominoType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GenerateTetrominoUseCaseTest {
    private val useCase = GenerateTetrominoUseCase()

    @Test
    fun invoke_shouldReturnTetromino() {
        // When
        val tetromino = useCase()

        // Then
        assertEquals(4, tetromino.blocks.size)
        assertEquals(0, tetromino.rotation)
    }

    @Test
    fun invoke_7Times_shouldReturnAll7Types() {
        // Given
        useCase.reset()
        val types = mutableSetOf<TetrominoType>()

        // When - Generate 7 pieces (one full bag)
        repeat(7) {
            val tetromino = useCase()
            types.add(tetromino.type)
        }

        // Then - Should have all 7 types
        assertEquals(7, types.size)
        assertTrue(types.containsAll(TetrominoType.entries))
    }

    @Test
    fun invoke_14Times_shouldReturnAll7TypesTwice() {
        // Given
        useCase.reset()
        val types = mutableListOf<TetrominoType>()

        // When - Generate 14 pieces (two full bags)
        repeat(14) {
            val tetromino = useCase()
            types.add(tetromino.type)
        }

        // Then - Each type should appear exactly twice
        TetrominoType.entries.forEach { type ->
            val count = types.count { it == type }
            assertEquals(2, count, "Type $type should appear exactly twice")
        }
    }

    @Test
    fun reset_shouldClearBag() {
        // Given - Generate some pieces
        repeat(3) { useCase() }

        // When
        useCase.reset()

        // Then - Next 7 pieces should be all different types
        val types = mutableSetOf<TetrominoType>()
        repeat(7) {
            types.add(useCase().type)
        }
        assertEquals(7, types.size)
    }

    @Test
    fun invoke_multipleGenerations_shouldFollowBagRandomizer() {
        // Given
        useCase.reset()

        // When - Generate 21 pieces (3 full bags)
        val types = mutableListOf<TetrominoType>()
        repeat(21) {
            types.add(useCase().type)
        }

        // Then - Each type should appear exactly 3 times
        TetrominoType.entries.forEach { type ->
            val count = types.count { it == type }
            assertEquals(3, count, "Type $type should appear exactly 3 times")
        }
    }

    @Test
    fun invoke_shouldAlwaysReturnRotation0() {
        // Given
        useCase.reset()

        // When/Then - All generated pieces should have rotation 0
        repeat(14) {
            val tetromino = useCase()
            assertEquals(0, tetromino.rotation)
        }
    }
}
