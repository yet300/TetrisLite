package com.yet.tetris.domain.model.game

object LevelProgression {
    const val START_LEVEL: Int = 1
    const val LINES_PER_LEVEL: Long = 10L

    fun levelForLines(linesCleared: Long): Int {
        val safeLines = linesCleared.coerceAtLeast(0L)
        return (safeLines / LINES_PER_LEVEL).toInt() + START_LEVEL
    }
}
