package com.yet.tetris.domain.model.history

import com.yet.tetris.domain.model.game.Difficulty
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class GameRecord(
    val id: String,
    val score: Int,
    val linesCleared: Int,
    val difficulty: Difficulty,
    val timestamp: Long
) {
    fun getFormattedDate(): String {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        return instant.toString()
    }
}
