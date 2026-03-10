package com.yet.tetris.data.model

import kotlinx.serialization.Serializable

@Serializable
data class GameRecordDto(
    val id: String,
    val score: Long,
    val linesCleared: Long,
    val level: Int,
    val difficulty: DifficultyDto,
    val timestamp: Long,
    val durationMs: Long,
    val piecesPlaced: Long,
    val maxCombo: Int,
    val tetrisesCleared: Long,
    val tSpinClears: Long,
    val perfectClears: Long,
    val hardDrops: Long,
    val hardDropCells: Long,
    val softDropCells: Long,
)
