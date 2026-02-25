package com.yet.tetris.data.mapper

import com.yet.tetris.data.model.DifficultyDto
import com.yet.tetris.data.model.GameBoardDto
import com.yet.tetris.data.model.GameStateDto
import com.yet.tetris.data.model.PositionDto
import com.yet.tetris.data.model.TetrominoDto
import com.yet.tetris.data.model.TetrominoTypeDto
import com.yet.tetris.domain.model.game.Difficulty
import com.yet.tetris.domain.model.game.GameBoard
import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.game.Position
import com.yet.tetris.domain.model.game.Tetromino
import com.yet.tetris.domain.model.game.TetrominoType

// Domain to DTO
fun GameState.toDto(): GameStateDto =
    GameStateDto(
        board = board.toDto(),
        currentPiece = currentPiece?.toDto(),
        currentPosition = currentPosition.toDto(),
        nextPiece = nextPiece.toDto(),
        score = score,
        linesCleared = linesCleared,
        level = level,
        isGameOver = isGameOver,
        isPaused = isPaused,
    )

fun GameBoard.toDto(): GameBoardDto =
    GameBoardDto(
        width = width,
        height = height,
        cells = cells.mapKeys { it.key.toDto() }.mapValues { it.value.toDto() },
    )

fun Tetromino.toDto(): TetrominoDto =
    TetrominoDto(
        type = type.toDto(),
        blocks = blocks.map { it.toDto() },
        rotation = rotation,
    )

fun Position.toDto(): PositionDto = PositionDto(x = x, y = y)

fun TetrominoType.toDto(): TetrominoTypeDto =
    when (this) {
        TetrominoType.I -> TetrominoTypeDto.I
        TetrominoType.O -> TetrominoTypeDto.O
        TetrominoType.T -> TetrominoTypeDto.T
        TetrominoType.S -> TetrominoTypeDto.S
        TetrominoType.Z -> TetrominoTypeDto.Z
        TetrominoType.J -> TetrominoTypeDto.J
        TetrominoType.L -> TetrominoTypeDto.L
    }

fun Difficulty.toDto(): DifficultyDto =
    when (this) {
        Difficulty.EASY -> DifficultyDto.EASY
        Difficulty.NORMAL -> DifficultyDto.NORMAL
        Difficulty.HARD -> DifficultyDto.HARD
    }

// DTO to Domain
fun GameStateDto.toDomain(): GameState =
    GameState(
        board = board.toDomain(),
        currentPiece = currentPiece?.toDomain(),
        currentPosition = currentPosition.toDomain(),
        nextPiece = nextPiece.toDomain(),
        score = score,
        linesCleared = linesCleared,
        level = level,
        isGameOver = isGameOver,
        isPaused = isPaused,
    )

fun GameBoardDto.toDomain(): GameBoard =
    GameBoard(
        width = width,
        height = height,
        cells = cells.mapKeys { it.key.toDomain() }.mapValues { it.value.toDomain() },
    )

fun TetrominoDto.toDomain(): Tetromino =
    Tetromino(
        type = type.toDomain(),
        blocks = blocks.map { it.toDomain() },
        rotation = rotation,
    )

fun PositionDto.toDomain(): Position = Position(x = x, y = y)

fun TetrominoTypeDto.toDomain(): TetrominoType =
    when (this) {
        TetrominoTypeDto.I -> TetrominoType.I
        TetrominoTypeDto.O -> TetrominoType.O
        TetrominoTypeDto.T -> TetrominoType.T
        TetrominoTypeDto.S -> TetrominoType.S
        TetrominoTypeDto.Z -> TetrominoType.Z
        TetrominoTypeDto.J -> TetrominoType.J
        TetrominoTypeDto.L -> TetrominoType.L
    }

fun DifficultyDto.toDomain(): Difficulty =
    when (this) {
        DifficultyDto.EASY -> Difficulty.EASY
        DifficultyDto.NORMAL -> Difficulty.NORMAL
        DifficultyDto.HARD -> Difficulty.HARD
    }
