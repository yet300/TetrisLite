package com.yet.tetris.domain.model.game

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class TetrominoTest {
    @Test
    fun create_allTypes_shouldHave4Blocks() {
        TetrominoType.entries.forEach { type ->
            // When
            val tetromino = Tetromino.create(type)

            // Then
            assertEquals(4, tetromino.blocks.size, "Tetromino $type should have 4 blocks")
        }
    }

    @Test
    fun create_withRotation0_shouldReturnCorrectShape() {
        // When
        val iPiece = Tetromino.create(TetrominoType.I, 0)

        // Then
        assertEquals(TetrominoType.I, iPiece.type)
        assertEquals(0, iPiece.rotation)
        assertEquals(4, iPiece.blocks.size)
    }

    @Test
    fun rotate_shouldIncrementRotation() {
        // Given
        val piece = Tetromino.create(TetrominoType.T, 0)

        // When
        val rotated = piece.rotate()

        // Then
        assertEquals(1, rotated.rotation)
        assertEquals(TetrominoType.T, rotated.type)
    }

    @Test
    fun rotate_from3_shouldWrapTo0() {
        // Given
        val piece = Tetromino.create(TetrominoType.T, 3)

        // When
        val rotated = piece.rotate()

        // Then
        assertEquals(0, rotated.rotation)
    }

    @Test
    fun rotate_oPiece_shouldHaveSameShape() {
        // Given
        val piece = Tetromino.create(TetrominoType.O, 0)
        val originalBlocks = piece.blocks

        // When
        val rotated = piece.rotate()

        // Then - O piece doesn't change shape when rotated
        assertEquals(originalBlocks, rotated.blocks)
    }

    @Test
    fun rotate_iPiece_shouldChangeOrientation() {
        // Given
        val horizontal = Tetromino.create(TetrominoType.I, 0)
        val vertical = Tetromino.create(TetrominoType.I, 1)

        // Then - Shapes should be different
        assertNotEquals(horizontal.blocks, vertical.blocks)
    }

    @Test
    fun getAbsolutePositions_withZeroOffset_shouldReturnOriginalBlocks() {
        // Given
        val piece = Tetromino.create(TetrominoType.T)
        val offset = Position(0, 0)

        // When
        val absolute = piece.getAbsolutePositions(offset)

        // Then
        assertEquals(piece.blocks, absolute)
    }

    @Test
    fun getAbsolutePositions_withOffset_shouldAddOffset() {
        // Given
        val piece = Tetromino.create(TetrominoType.T)
        val offset = Position(5, 3)

        // When
        val absolute = piece.getAbsolutePositions(offset)

        // Then
        assertEquals(4, absolute.size)
        absolute.forEach { pos ->
            assertTrue(pos.x >= 5, "X should be offset by 5")
            assertTrue(pos.y >= 3, "Y should be offset by 3")
        }
    }

    @Test
    fun create_allRotations_shouldHave4UniqueStates() {
        TetrominoType.entries.forEach { type ->
            // When
            val rotations = (0..3).map { Tetromino.create(type, it) }

            // Then
            assertEquals(4, rotations.size)
            rotations.forEach { piece ->
                assertEquals(4, piece.blocks.size)
                assertEquals(type, piece.type)
            }
        }
    }

    @Test
    fun rotate_multipleRotations_shouldCycle() {
        // Given
        val piece = Tetromino.create(TetrominoType.T, 0)

        // When - Rotate 4 times
        val rotated4Times =
            piece
                .rotate()
                .rotate()
                .rotate()
                .rotate()

        // Then - Should be back to original rotation
        assertEquals(0, rotated4Times.rotation)
        assertEquals(piece.blocks, rotated4Times.blocks)
    }

    @Test
    fun tetromino_differentTypes_shouldHaveDifferentShapes() {
        // Given
        val iPiece = Tetromino.create(TetrominoType.I)
        val oPiece = Tetromino.create(TetrominoType.O)
        val tPiece = Tetromino.create(TetrominoType.T)

        // Then
        assertNotEquals(iPiece.blocks, oPiece.blocks)
        assertNotEquals(iPiece.blocks, tPiece.blocks)
        assertNotEquals(oPiece.blocks, tPiece.blocks)
    }
}
