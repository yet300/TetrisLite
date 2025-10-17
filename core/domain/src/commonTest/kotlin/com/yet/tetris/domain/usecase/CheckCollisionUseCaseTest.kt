package com.yet.tetris.domain.usecase

import com.yet.tetris.domain.model.game.GameBoard
import com.yet.tetris.domain.model.game.Position
import com.yet.tetris.domain.model.game.Tetromino
import com.yet.tetris.domain.model.game.TetrominoType
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CheckCollisionUseCaseTest {
    private val useCase = CheckCollisionUseCase()

    @Test
    fun invoke_pieceInValidPosition_shouldReturnFalse() {
        // Given
        val board = GameBoard()
        val piece = Tetromino.create(TetrominoType.T)
        val position = Position(3, 0)

        // When
        val hasCollision = useCase(board, piece, position)

        // Then
        assertFalse(hasCollision, "Piece in valid position should not collide")
    }

    @Test
    fun invoke_pieceOutsideLeftBoundary_shouldReturnTrue() {
        // Given
        val board = GameBoard()
        val piece = Tetromino.create(TetrominoType.I)
        val position = Position(-1, 0)

        // When
        val hasCollision = useCase(board, piece, position)

        // Then
        assertTrue(hasCollision, "Piece outside left boundary should collide")
    }

    @Test
    fun invoke_pieceOutsideRightBoundary_shouldReturnTrue() {
        // Given
        val board = GameBoard()
        val piece = Tetromino.create(TetrominoType.I)
        val position = Position(10, 0)

        // When
        val hasCollision = useCase(board, piece, position)

        // Then
        assertTrue(hasCollision, "Piece outside right boundary should collide")
    }

    @Test
    fun invoke_pieceOutsideBottomBoundary_shouldReturnTrue() {
        // Given
        val board = GameBoard()
        val piece = Tetromino.create(TetrominoType.I)
        val position = Position(3, 20)

        // When
        val hasCollision = useCase(board, piece, position)

        // Then
        assertTrue(hasCollision, "Piece outside bottom boundary should collide")
    }

    @Test
    fun invoke_pieceOverlapsLockedBlock_shouldReturnTrue() {
        // Given
        val lockedPosition = Position(5, 10)
        val board = GameBoard(cells = mapOf(lockedPosition to TetrominoType.I))
        val piece = Tetromino.create(TetrominoType.O)
        val position = Position(4, 9) // O-piece at this position will overlap (5,10)

        // When
        val hasCollision = useCase(board, piece, position)

        // Then
        assertTrue(hasCollision, "Piece overlapping locked block should collide")
    }

    @Test
    fun invoke_pieceNextToLockedBlock_shouldReturnFalse() {
        // Given
        val board = GameBoard(cells = mapOf(Position(5, 10) to TetrominoType.I))
        val piece = Tetromino.create(TetrominoType.O)
        val position = Position(6, 10) // Next to but not overlapping

        // When
        val hasCollision = useCase(board, piece, position)

        // Then
        assertFalse(hasCollision, "Piece next to locked block should not collide")
    }

    @Test
    fun isPositionValid_withinBoundsAndEmpty_shouldReturnTrue() {
        // Given
        val board = GameBoard()
        val position = Position(5, 10)

        // When
        val isValid = useCase.isPositionValid(board, position)

        // Then
        assertTrue(isValid)
    }

    @Test
    fun isPositionValid_outsideBounds_shouldReturnFalse() {
        // Given
        val board = GameBoard()

        // When/Then
        assertFalse(useCase.isPositionValid(board, Position(-1, 0)))
        assertFalse(useCase.isPositionValid(board, Position(10, 0)))
        assertFalse(useCase.isPositionValid(board, Position(0, 20)))
    }

    @Test
    fun isPositionValid_occupied_shouldReturnFalse() {
        // Given
        val position = Position(5, 10)
        val board = GameBoard(cells = mapOf(position to TetrominoType.I))

        // When
        val isValid = useCase.isPositionValid(board, position)

        // Then
        assertFalse(isValid)
    }

    @Test
    fun canPlacePiece_validPosition_shouldReturnTrue() {
        // Given
        val board = GameBoard()
        val piece = Tetromino.create(TetrominoType.T)
        val position = Position(3, 0)

        // When
        val canPlace = useCase.canPlacePiece(board, piece, position)

        // Then
        assertTrue(canPlace)
    }

    @Test
    fun canPlacePiece_invalidPosition_shouldReturnFalse() {
        // Given
        val board = GameBoard()
        val piece = Tetromino.create(TetrominoType.I)
        val position = Position(-1, 0)

        // When
        val canPlace = useCase.canPlacePiece(board, piece, position)

        // Then
        assertFalse(canPlace)
    }

    @Test
    fun invoke_iPieceHorizontal_shouldCheckAllBlocks() {
        // Given
        val board = GameBoard(width = 10, height = 20)
        val piece = Tetromino.create(TetrominoType.I, 0) // Horizontal
        val position = Position(7, 0) // Would extend beyond right boundary

        // When
        val hasCollision = useCase(board, piece, position)

        // Then
        assertTrue(hasCollision, "Horizontal I-piece extending beyond boundary should collide")
    }

    @Test
    fun invoke_iPieceVertical_shouldCheckAllBlocks() {
        // Given
        val board = GameBoard(width = 10, height = 20)
        val piece = Tetromino.create(TetrominoType.I, 1) // Vertical
        val position = Position(0, 17) // Would extend beyond bottom boundary

        // When
        val hasCollision = useCase(board, piece, position)

        // Then
        assertTrue(hasCollision, "Vertical I-piece extending beyond boundary should collide")
    }
}
