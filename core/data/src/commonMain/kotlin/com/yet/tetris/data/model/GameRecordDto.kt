package com.yet.tetris.data.model

import kotlinx.serialization.Serializable

@Serializable
data class GameRecordDto(
    val id: String,
    val score: Long,
    val linesCleared: Int,
    val difficulty: DifficultyDto,
    val timestamp: Long
)
