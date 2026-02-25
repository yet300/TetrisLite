package com.yet.tetris.domain.model.game

enum class Difficulty(
    val fallDelayMs: Long,
    val minFallDelayMs: Long,
    val fallDelayStepMs: Long,
) {
    EASY(1000, 180, 60),
    NORMAL(600, 100, 45),
    HARD(300, 80, 25),
    ;

    fun fallDelayForLevel(level: Int): Long {
        val levelOffset =
            (level.coerceAtLeast(LevelProgression.START_LEVEL) - LevelProgression.START_LEVEL).toLong()
        val adjusted = fallDelayMs - (levelOffset * fallDelayStepMs)
        return adjusted.coerceAtLeast(minFallDelayMs)
    }
}
