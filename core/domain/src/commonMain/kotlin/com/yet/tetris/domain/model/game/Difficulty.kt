package com.yet.tetris.domain.model.game

import kotlinx.serialization.Serializable

@Serializable
enum class Difficulty(val fallDelayMs: Long) {
    EASY(1000),
    NORMAL(600),
    HARD(300)
}
