package com.yet.tetris.domain.usecase

import kotlin.test.Test
import kotlin.test.assertEquals

class CalculateScoreUseCaseTest {
    private val useCase = CalculateScoreUseCase()

    @Test
    fun invoke_noLines_shouldReturn0() {
        // When
        val score = useCase(0)

        // Then
        assertEquals(0, score)
    }

    @Test
    fun invoke_singleLine_shouldReturn100() {
        // When
        val score = useCase(1)

        // Then
        assertEquals(100, score)
    }

    @Test
    fun invoke_doubleLine_shouldReturn300() {
        // When
        val score = useCase(2)

        // Then
        assertEquals(300, score)
    }

    @Test
    fun invoke_tripleLine_shouldReturn500() {
        // When
        val score = useCase(3)

        // Then
        assertEquals(500, score)
    }

    @Test
    fun invoke_tetris_shouldReturn800() {
        // When
        val score = useCase(4)

        // Then
        assertEquals(800, score)
    }

    @Test
    fun invoke_moreThan4Lines_shouldReturn0() {
        // When
        val score = useCase(5)

        // Then
        assertEquals(0, score)
    }

    @Test
    fun calculateWithMultiplier_noMultiplier_shouldReturnBase() {
        // When
        val score = useCase.calculateWithMultiplier(1, 1.0f)

        // Then
        assertEquals(100, score)
    }

    @Test
    fun calculateWithMultiplier_doubleMultiplier_shouldDouble() {
        // When
        val score = useCase.calculateWithMultiplier(1, 2.0f)

        // Then
        assertEquals(200, score)
    }

    @Test
    fun calculateWithMultiplier_halfMultiplier_shouldHalve() {
        // When
        val score = useCase.calculateWithMultiplier(4, 0.5f)

        // Then
        assertEquals(400, score)
    }

    @Test
    fun calculateWithMultiplier_tetrisWithMultiplier_shouldApplyCorrectly() {
        // When
        val score = useCase.calculateWithMultiplier(4, 1.5f)

        // Then
        assertEquals(1200, score)
    }
}
