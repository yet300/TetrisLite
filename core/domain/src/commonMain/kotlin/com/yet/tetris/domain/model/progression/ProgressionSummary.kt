package com.yet.tetris.domain.model.progression

import com.yet.tetris.domain.model.game.Difficulty

data class ProgressionSummary(
    val totalGames: Int = 0,
    val totalLines: Long = 0,
    val totalPieces: Long = 0,
    val totalTetrises: Long = 0,
    val totalTSpins: Long = 0,
    val bestScore: Long = 0,
    val highestLevel: Int = 0,
    val totalAchievements: Int = ProgressAchievementId.entries.size,
    val unlockedAchievements: List<ProgressAchievementId> = emptyList(),
    val bestByDifficulty: List<DifficultyProgress> = emptyList(),
) {
    val hasProgress: Boolean
        get() = totalGames > 0

    fun bestFor(difficulty: Difficulty): DifficultyProgress =
        bestByDifficulty.firstOrNull { it.difficulty == difficulty } ?: DifficultyProgress(difficulty = difficulty)

    companion object {
        val EMPTY = ProgressionSummary()
    }
}
