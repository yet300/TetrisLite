package com.yet.tetris.domain.model.game

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GameBoardTest {

    @Test
    fun create_default_shouldHaveStandardDimensions() {
        // When
        val board = GameBoard()

        // Then
        assertEquals(10, board.width)
        assertEquals(20, board.height)
        assertTrue(board.cells.isEmpty())
    }

    @Test
    fun isPositionOccupied_emptyBoard_shouldReturnFalse() {
        // Given
        val board = GameBoard()

        // When/Then
        assertFalse(board.isPositionOccupied(Position(5, 10)))
    }

    @Test
    fun isPositionOccupied_withBlock_shouldReturnTrue() {
        // Given
        val board = GameBoard(cells = mapOf(Position(5, 10) to TetrominoType.I))

        // When/Then
        assertTrue(board.isPositionOccupied(Position(5, 10)))
    }

    @Test
    fun isPositionValid_withinBounds_shouldReturnTrue() {
        // Given
        val board = GameBoard()

        // When/Then
        assertTrue(board.isPositionValid(Position(0, 0)))
        assertTrue(board.isPositionValid(Position(9, 19)))
        assertTrue(board.isPositionValid(Position(5, 10)))
    }

    @Test
    fun isPositionValid_outsideBounds_shouldReturnFalse() {
        // Given
        val board = GameBoard()

        // When/Then
        assertFalse(board.isPositionValid(Position(-1, 0)))
        assertFalse(board.isPositionValid(Position(10, 0)))
        assertFalse(board.isPositionValid(Position(0, -1)))
        assertFalse(board.isPositionValid(Position(0, 20)))
    }

    @Test
    fun lockPiece_shouldAddBlocksToBoard() {
        // Given
        val board = GameBoard()
        val piece = Tetromino.create(TetrominoType.I)
        val offset = Position(3, 0)

        // When
        val newBoard = board.lockPiece(piece, offset)

        // Then
        assertEquals(4, newBoard.cells.size)
        piece.getAbsolutePositions(offset).forEach { pos ->
            assertTrue(newBoard.isPositionOccupied(pos))
            assertEquals(TetrominoType.I, newBoard.cells[pos])
        }
    }

    @Test
    fun lockPiece_shouldNotModifyOriginalBoard() {
        // Given
        val board = GameBoard()
        val piece = Tetromino.create(TetrominoType.T)

        // When
        board.lockPiece(piece, Position(3, 0))

        // Then
        assertTrue(board.cells.isEmpty())
    }

    @Test
    fun clearLines_noCompleteLines_shouldReturnSameBoard() {
        // Given
        val cells = mapOf(
            Position(0, 19) to TetrominoType.I,
            Position(1, 19) to TetrominoType.I,
            Position(2, 19) to TetrominoType.I
        )
        val board = GameBoard(cells = cells)

        // When
        val (newBoard, linesCleared) = board.clearLines()

        // Then
        assertEquals(0, linesCleared)
        assertEquals(cells.size, newBoard.cells.size)
    }

    @Test
    fun clearLines_oneCompleteLine_shouldClearAndDrop() {
        // Given - Complete bottom line
        val cells = (0 until 10).associate { x ->
            Position(x, 19) to TetrominoType.I
        }
        val board = GameBoard(cells = cells)

        // When
        val (newBoard, linesCleared) = board.clearLines()

        // Then
        assertEquals(1, linesCleared)
        assertTrue(newBoard.cells.isEmpty())
    }

    @Test
    fun clearLines_multipleLines_shouldClearAll() {
        // Given - Two complete lines
        val cells = (0 until 10).flatMap { x ->
            listOf(
                Position(x, 18) to TetrominoType.I,
                Position(x, 19) to TetrominoType.O
            )
        }.toMap()
        val board = GameBoard(cells = cells)

        // When
        val (newBoard, linesCleared) = board.clearLines()

        // Then
        assertEquals(2, linesCleared)
        assertTrue(newBoard.cells.isEmpty())
    }

    @Test
    fun clearLines_withBlocksAbove_shouldDropBlocks() {
        // Given - Complete line at bottom with blocks above
        val cells = mutableMapOf<Position, TetrominoType>()
        // Complete line at y=19
        for (x in 0 until 10) {
            cells[Position(x, 19)] = TetrominoType.I
        }
        // Block above at y=18
        cells[Position(5, 18)] = TetrominoType.T

        val board = GameBoard(cells = cells)

        // When
        val (newBoard, linesCleared) = board.clearLines()

        // Then
        assertEquals(1, linesCleared)
        assertEquals(1, newBoard.cells.size)
        // Block should have dropped from y=18 to y=19
        assertTrue(newBoard.isPositionOccupied(Position(5, 19)))
        assertEquals(TetrominoType.T, newBoard.cells[Position(5, 19)])
    }

    @Test
    fun clearLines_middleLine_shouldDropBlocksAbove() {
        // Given - Complete line in middle with blocks above
        val cells = mutableMapOf<Position, TetrominoType>()
        // Complete line at y=15
        for (x in 0 until 10) {
            cells[Position(x, 15)] = TetrominoType.I
        }
        // Blocks above
        cells[Position(3, 14)] = TetrominoType.T
        cells[Position(4, 13)] = TetrominoType.S

        val board = GameBoard(cells = cells)

        // When
        val (newBoard, linesCleared) = board.clearLines()

        // Then
        assertEquals(1, linesCleared)
        assertEquals(2, newBoard.cells.size)
        // Blocks should have dropped by 1
        assertTrue(newBoard.isPositionOccupied(Position(3, 15)))
        assertTrue(newBoard.isPositionOccupied(Position(4, 14)))
    }

    @Test
    fun lockPiece_multiplePieces_shouldAccumulate() {
        // Given
        var board = GameBoard()
        val piece1 = Tetromino.create(TetrominoType.I)
        val piece2 = Tetromino.create(TetrominoType.O)

        // When
        board = board.lockPiece(piece1, Position(0, 16))
        board = board.lockPiece(piece2, Position(5, 18))

        // Then
        assertEquals(8, board.cells.size) // 4 blocks from each piece
    }
}
