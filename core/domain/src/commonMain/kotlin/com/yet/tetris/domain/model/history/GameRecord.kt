package com.yet.tetris.domain.model.history

import com.yet.tetris.domain.model.game.Difficulty
import kotlin.time.Instant
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

@Serializable
data class GameRecord(
    val id: String,
    val score: Int,
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
