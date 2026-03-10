package com.yet.tetris.domain.model.history

import com.yet.tetris.domain.model.game.Difficulty
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class GameRecord(
    val id: String,
    val score: Long,
    val linesCleared: Long,
    val level: Int = 1,
    val difficulty: Difficulty,
    val timestamp: Long,
    val durationMs: Long = 0,
    val piecesPlaced: Long = 0,
    val maxCombo: Int = 0,
    val tetrisesCleared: Long = 0,
    val tSpinClears: Long = 0,
    val perfectClears: Long = 0,
    val hardDrops: Long = 0,
    val hardDropCells: Long = 0,
    val softDropCells: Long = 0,
) {
    @OptIn(ExperimentalTime::class)
    fun getFormattedDate(): String {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        return instant.toString()
    }
}
