package com.yet.tetris.domain.model.game

import kotlin.test.Test
import kotlin.test.assertEquals

class PositionTest {

    @Test
    fun plus_shouldAddCoordinates() {
        // Given
        val pos1 = Position(3, 5)
        val pos2 = Position(2, 4)

        // When
        val result = pos1 + pos2

        // Then
        assertEquals(Position(5, 9), result)
    }

    @Test
    fun plus_withNegativeValues_shouldWork() {
        // Given
        val pos1 = Position(5, 10)
        val pos2 = Position(-2, -3)

        // When
        val result = pos1 + pos2

        // Then
        assertEquals(Position(3, 7), result)
    }

    @Test
    fun minus_shouldSubtractCoordinates() {
        // Given
        val pos1 = Position(5, 10)
        val pos2 = Position(2, 3)

        // When
        val result = pos1 - pos2

        // Then
        assertEquals(Position(3, 7), result)
    }

    @Test
    fun minus_resultingInNegative_shouldWork() {
        // Given
        val pos1 = Position(2, 3)
        val pos2 = Position(5, 10)

        // When
        val result = pos1 - pos2

        // Then
        assertEquals(Position(-3, -7), result)
    }

    @Test
    fun plus_withZero_shouldReturnSame() {
        // Given
        val pos = Position(5, 10)
        val zero = Position(0, 0)

        // When
        val result = pos + zero

        // Then
        assertEquals(pos, result)
    }

    @Test
    fun minus_withZero_shouldReturnSame() {
        // Given
        val pos = Position(5, 10)
        val zero = Position(0, 0)

        // When
        val result = pos - zero

        // Then
        assertEquals(pos, result)
    }

    @Test
    fun operations_shouldBeCommutative() {
        // Given
        val pos1 = Position(3, 5)
        val pos2 = Position(2, 4)

        // When/Then
        assertEquals(pos1 + pos2, pos2 + pos1)
    }

    @Test
    fun operations_shouldBeAssociative() {
        // Given
        val pos1 = Position(1, 2)
        val pos2 = Position(3, 4)
        val pos3 = Position(5, 6)

        // When/Then
        assertEquals((pos1 + pos2) + pos3, pos1 + (pos2 + pos3))
    }

    @Test
    fun equality_sameCoordinates_shouldBeEqual() {
        // Given
        val pos1 = Position(5, 10)
        val pos2 = Position(5, 10)

        // Then
        assertEquals(pos1, pos2)
    }

    @Test
    fun plus_chainedOperations_shouldWork() {
        // Given
        val start = Position(0, 0)
        val offset1 = Position(1, 0)
        val offset2 = Position(0, 1)
        val offset3 = Position(1, 1)

        // When
        val result = start + offset1 + offset2 + offset3

        // Then
        assertEquals(Position(2, 2), result)
    }
}
