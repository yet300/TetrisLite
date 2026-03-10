package com.yet.tetris.data.model

import kotlinx.serialization.Serializable

@Serializable
data class GameStateDto(
    val board: GameBoardDto,
    val currentPiece: TetrominoDto?,
    val currentPosition: PositionDto,
    val nextPiece: TetrominoDto,
    val nextQueue: List<TetrominoDto> = emptyList(),
    val holdPiece: TetrominoDto? = null,
    val canHold: Boolean = true,
    val score: Long = 0,
    val linesCleared: Long = 0,
    val level: Int = 1,
    val piecesPlaced: Long = 0,
    val maxCombo: Int = 0,
    val tetrisesCleared: Long = 0,
    val tSpinClears: Long = 0,
    val perfectClears: Long = 0,
    val hardDrops: Long = 0,
    val hardDropCells: Long = 0,
    val softDropCells: Long = 0,
    val backToBackChain: Int = 0,
    val isTSpinEligible: Boolean = false,
    val isGameOver: Boolean = false,
    val isPaused: Boolean = false,
)

@Serializable
data class GameBoardDto(
    val width: Int = 10,
    val height: Int = 20,
    val cells: Map<PositionDto, TetrominoTypeDto> = emptyMap(),
)

@Serializable
data class TetrominoDto(
    val type: TetrominoTypeDto,
    val blocks: List<PositionDto>,
    val rotation: Int = 0,
)

@Serializable
data class PositionDto(
    val x: Int,
    val y: Int,
)

@Serializable
enum class TetrominoTypeDto {
    I,
    O,
    T,
    S,
    Z,
    J,
    L,
}

@Serializable
enum class DifficultyDto(
    val fallDelayMs: Long,
) {
    EASY(1000),
    NORMAL(600),
    HARD(300),
}
