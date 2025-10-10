package com.yet.tetris.domain.model.history

import com.yet.tetris.domain.model.game.Difficulty
import kotlin.time.Instant
import kotlin.time.ExperimentalTime

data class GameRecord(
    val id: String,
    val score: Long,
    val linesCleared: Int,
    val difficulty: Difficulty,
    val timestamp: Long
) {
    @OptIn(ExperimentalTime::class)
    fun getFormattedDate(): String {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        return instant.toString()
    }
}
