package com.yet.tetris.domain.model.progression

import com.yet.tetris.domain.model.game.Difficulty

data class DifficultyProgress(
    val difficulty: Difficulty,
    val gamesPlayed: Int = 0,
    val bestScore: Long = 0,
    val highestLevel: Int = 0,
)
