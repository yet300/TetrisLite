package com.yet.tetris.database.mapper

import com.yet.tetris.database.BoardCells
import com.yet.tetris.database.CurrentGameState
import com.yet.tetris.domain.model.game.GameBoard
import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.game.Position
import com.yet.tetris.domain.model.game.Tetromino
import com.yet.tetris.domain.model.game.TetrominoType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class GameStateMapperTest {
    @Test
    fun toDomain_shouldMapGameStateWithCurrentPiece() {
        // Given
        val dbState =
            CurrentGameState(
                id = 1,
                score = 1000,
                linesCleared = 10,
                level = 2,
                piecesPlaced = 0,
                maxCombo = 0,
                tetrisesCleared = 0,
                tSpinClears = 0,
                perfectClears = 0,
                hardDrops = 0,
                hardDropCells = 0,
                softDropCells = 0,
                backToBackChain = 0,
                isTSpinEligible = false,
                currentPieceType = TetrominoType.T,
                currentPieceRotation = 1,
                currentPositionX = 5,
                currentPositionY = 3,
                nextPieceType = TetrominoType.L,
                nextPieceRotation = 2,
                nextQueue = "I:0|O:0|T:0|S:0",
                holdPieceType = TetrominoType.Z,
                holdPieceRotation = 0,
                canHold = false,
                isGameOver = false,
                isPaused = false,
                boardWidth = 10,
                boardHeight = 20,
            )
        val boardCells =
            listOf(
                BoardCells(positionX = 0, positionY = 0, pieceType = TetrominoType.I),
                BoardCells(positionX = 1, positionY = 0, pieceType = TetrominoType.O),
            )

        // When
        val domain = dbState.toDomain(boardCells)

        // Then
        assertEquals(1000, domain.score)
        assertEquals(10, domain.linesCleared)
        assertEquals(2, domain.level)
        assertNotNull(domain.currentPiece)
        assertEquals(TetrominoType.T, domain.currentPiece?.type)
        assertEquals(1, domain.currentPiece?.rotation)
        assertEquals(5, domain.currentPosition.x)
        assertEquals(3, domain.currentPosition.y)
        assertEquals(TetrominoType.L, domain.nextPiece.type)
        assertEquals(2, domain.nextPiece.rotation)
        assertEquals(4, domain.nextQueue.size)
        assertEquals(TetrominoType.Z, domain.holdPiece?.type)
        assertEquals(false, domain.canHold)
        assertEquals(false, domain.isGameOver)
        assertEquals(false, domain.isPaused)
        assertEquals(10, domain.board.width)
        assertEquals(20, domain.board.height)
        assertEquals(2, domain.board.cells.size)
    }

    @Test
    fun toDomain_shouldMapGameStateWithoutCurrentPiece() {
        // Given
        val dbState =
            CurrentGameState(
                id = 1,
                score = 500,
                linesCleared = 5,
                level = 1,
                piecesPlaced = 0,
                maxCombo = 0,
                tetrisesCleared = 0,
                tSpinClears = 0,
                perfectClears = 0,
                hardDrops = 0,
                hardDropCells = 0,
                softDropCells = 0,
                backToBackChain = 0,
                isTSpinEligible = false,
                currentPieceType = null,
                currentPieceRotation = 0,
                currentPositionX = 0,
                currentPositionY = 0,
                nextPieceType = TetrominoType.I,
                nextPieceRotation = 0,
                nextQueue = "",
                holdPieceType = null,
                holdPieceRotation = 0,
                canHold = true,
                isGameOver = true,
                isPaused = false,
                boardWidth = 10,
                boardHeight = 20,
            )

        // When
        val domain = dbState.toDomain(emptyList())

        // Then
        assertNull(domain.currentPiece)
        assertEquals(TetrominoType.I, domain.nextPiece.type)
        assertEquals(1, domain.level)
        assertEquals(true, domain.isGameOver)
    }

    @Test
    fun toEntities_shouldMapGameStateToEntities() {
        // Given
        val board =
            GameBoard(
                width = 10,
                height = 20,
                cells =
                    mapOf(
                        Position(0, 0) to TetrominoType.I,
                        Position(1, 0) to TetrominoType.O,
                        Position(2, 1) to TetrominoType.T,
                    ),
            )
        val currentPiece = Tetromino.create(TetrominoType.S, 1)
        val nextPiece = Tetromino.create(TetrominoType.Z, 2)
        val gameState =
            GameState(
                board = board,
                currentPiece = currentPiece,
                currentPosition = Position(5, 3),
                nextPiece = nextPiece,
                nextQueue = listOf(Tetromino.create(TetrominoType.I), Tetromino.create(TetrominoType.O)),
                holdPiece = Tetromino.create(TetrominoType.J),
                canHold = false,
                score = 2000,
                linesCleared = 20,
                level = 3,
                isGameOver = false,
                isPaused = true,
            )

        // When
        val entities = gameState.toEntities()

        // Then
        assertEquals(2000, entities.gameState.score)
        assertEquals(20, entities.gameState.linesCleared)
        assertEquals(3, entities.gameState.level)
        assertEquals(TetrominoType.S, entities.gameState.currentPieceType)
        assertEquals(1, entities.gameState.currentPieceRotation)
        assertEquals(5, entities.gameState.currentPositionX)
        assertEquals(3, entities.gameState.currentPositionY)
        assertEquals(TetrominoType.Z, entities.gameState.nextPieceType)
        assertEquals(2, entities.gameState.nextPieceRotation)
        assertEquals("I:0|O:0", entities.gameState.nextQueue)
        assertEquals(TetrominoType.J, entities.gameState.holdPieceType)
        assertEquals(false, entities.gameState.canHold)
        assertEquals(false, entities.gameState.isGameOver)
        assertEquals(true, entities.gameState.isPaused)
        assertEquals(10, entities.gameState.boardWidth)
        assertEquals(20, entities.gameState.boardHeight)
        assertEquals(3, entities.boardCells.size)
    }

    @Test
    fun toEntities_shouldMapGameStateWithoutCurrentPiece() {
        // Given
        val board = GameBoard(width = 10, height = 20, cells = emptyMap())
        val nextPiece = Tetromino.create(TetrominoType.I, 0)
        val gameState =
            GameState(
                board = board,
                currentPiece = null,
                currentPosition = Position(0, 0),
                nextPiece = nextPiece,
                nextQueue = emptyList(),
                holdPiece = null,
                canHold = true,
                score = 0,
                linesCleared = 0,
                level = 1,
                isGameOver = true,
                isPaused = false,
            )

        // When
        val entities = gameState.toEntities()

        // Then
        assertNull(entities.gameState.currentPieceType)
        assertEquals(1, entities.gameState.level)
        assertEquals(0, entities.gameState.currentPieceRotation)
        assertEquals(TetrominoType.I, entities.gameState.nextPieceType)
        assertEquals(0, entities.boardCells.size)
    }

    @Test
    fun roundTrip_shouldPreserveGameState() {
        // Given
        val originalBoard =
            GameBoard(
                width = 10,
                height = 20,
                cells =
                    mapOf(
                        Position(0, 0) to TetrominoType.I,
                        Position(1, 1) to TetrominoType.J,
                    ),
            )
        val originalState =
            GameState(
                board = originalBoard,
                currentPiece = Tetromino.create(TetrominoType.T, 1),
                currentPosition = Position(5, 5),
                nextPiece = Tetromino.create(TetrominoType.L, 2),
                nextQueue = listOf(Tetromino.create(TetrominoType.I), Tetromino.create(TetrominoType.O)),
                holdPiece = Tetromino.create(TetrominoType.S),
                canHold = false,
                score = 3000,
                linesCleared = 30,
                level = 4,
                isGameOver = false,
                isPaused = false,
            )

        // When
        val entities = originalState.toEntities()
        val dbState =
            CurrentGameState(
                id = 1,
                score = entities.gameState.score,
                linesCleared = entities.gameState.linesCleared,
                level = entities.gameState.level,
                piecesPlaced = entities.gameState.piecesPlaced,
                maxCombo = entities.gameState.maxCombo,
                tetrisesCleared = entities.gameState.tetrisesCleared,
                tSpinClears = entities.gameState.tSpinClears,
                perfectClears = entities.gameState.perfectClears,
                hardDrops = entities.gameState.hardDrops,
                hardDropCells = entities.gameState.hardDropCells,
                softDropCells = entities.gameState.softDropCells,
                backToBackChain = entities.gameState.backToBackChain,
                isTSpinEligible = entities.gameState.isTSpinEligible,
                currentPieceType = entities.gameState.currentPieceType,
                currentPieceRotation = entities.gameState.currentPieceRotation,
                currentPositionX = entities.gameState.currentPositionX,
                currentPositionY = entities.gameState.currentPositionY,
                nextPieceType = entities.gameState.nextPieceType,
                nextPieceRotation = entities.gameState.nextPieceRotation,
                nextQueue = entities.gameState.nextQueue,
                holdPieceType = entities.gameState.holdPieceType,
                holdPieceRotation = entities.gameState.holdPieceRotation,
                canHold = entities.gameState.canHold,
                isGameOver = entities.gameState.isGameOver,
                isPaused = entities.gameState.isPaused,
                boardWidth = entities.gameState.boardWidth,
                boardHeight = entities.gameState.boardHeight,
            )
        val result = dbState.toDomain(entities.boardCells)

        // Then
        assertEquals(originalState.score, result.score)
        assertEquals(originalState.linesCleared, result.linesCleared)
        assertEquals(originalState.level, result.level)
        assertEquals(originalState.currentPiece?.type, result.currentPiece?.type)
        assertEquals(originalState.currentPiece?.rotation, result.currentPiece?.rotation)
        assertEquals(originalState.currentPosition, result.currentPosition)
        assertEquals(originalState.nextPiece.type, result.nextPiece.type)
        assertEquals(originalState.nextPiece.rotation, result.nextPiece.rotation)
        assertEquals(originalState.nextQueue.map { it.type }, result.nextQueue.map { it.type })
        assertEquals(originalState.holdPiece?.type, result.holdPiece?.type)
        assertEquals(originalState.canHold, result.canHold)
        assertEquals(originalState.isGameOver, result.isGameOver)
        assertEquals(originalState.isPaused, result.isPaused)
        assertEquals(originalState.board.width, result.board.width)
        assertEquals(originalState.board.height, result.board.height)
        assertEquals(originalState.board.cells.size, result.board.cells.size)
    }
}
