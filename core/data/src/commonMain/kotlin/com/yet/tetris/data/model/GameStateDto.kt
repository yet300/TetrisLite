package com.yet.tetris.data.model

import kotlinx.serialization.Serializable

@Serializable
data class GameStateDto(
    val board: GameBoardDto,
    val currentPiece: TetrominoDto?,
    val currentPosition: PositionDto,
    val nextPiece: TetrominoDto,
    val score: Long = 0,
    val linesCleared: Long = 0,
    val level: Int = 1,
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
