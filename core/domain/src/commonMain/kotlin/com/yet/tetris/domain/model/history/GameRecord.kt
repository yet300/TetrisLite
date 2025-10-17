package com.yet.tetris.domain.model.history

import com.yet.tetris.domain.model.game.Difficulty
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class GameRecord(
    val id: String,
    val score: Long,
    val linesCleared: Long,
    val difficulty: Difficulty,
    val timestamp: Long,
) {
    @OptIn(ExperimentalTime::class)
    fun getFormattedDate(): String {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        return instant.toString()
    }
}
