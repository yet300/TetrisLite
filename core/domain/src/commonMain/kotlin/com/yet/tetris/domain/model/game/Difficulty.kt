package com.yet.tetris.domain.model.game

enum class Difficulty(
    val fallDelayMs: Long,
) {
    EASY(1000),
    NORMAL(600),
    HARD(300),
}
