package com.yet.tetris.domain.usecase

import com.yet.tetris.domain.model.game.Difficulty
import com.yet.tetris.domain.model.history.GameRecord
import com.yet.tetris.domain.model.progression.DifficultyProgress
import com.yet.tetris.domain.model.progression.ProgressAchievementId
import com.yet.tetris.domain.model.progression.ProgressionSummary

class CalculateProgressionSummaryUseCase {
    operator fun invoke(games: List<GameRecord>): ProgressionSummary {
        if (games.isEmpty()) {
            return ProgressionSummary.EMPTY
        }

        val unlockedAchievements = buildUnlockedAchievements(games)
        val difficultyProgress =
            Difficulty.entries.map { difficulty ->
                val gamesForDifficulty = games.filter { it.difficulty == difficulty }
                DifficultyProgress(
                    difficulty = difficulty,
                    gamesPlayed = gamesForDifficulty.size,
                    bestScore = gamesForDifficulty.maxOfOrNull { it.score } ?: 0,
                    highestLevel = gamesForDifficulty.maxOfOrNull { it.level } ?: 0,
                )
            }

        return ProgressionSummary(
            totalGames = games.size,
            totalLines = games.sumOf { it.linesCleared },
            totalPieces = games.sumOf { it.piecesPlaced },
            totalTetrises = games.sumOf { it.tetrisesCleared },
            totalTSpins = games.sumOf { it.tSpinClears },
            bestScore = games.maxOf { it.score },
            highestLevel = games.maxOf { it.level },
            unlockedAchievements = unlockedAchievements,
            bestByDifficulty = difficultyProgress,
        )
    }

    private fun buildUnlockedAchievements(games: List<GameRecord>): List<ProgressAchievementId> =
        buildList {
            if (games.isNotEmpty()) add(ProgressAchievementId.FIRST_GAME)
            if (games.any { it.score >= 5_000 }) add(ProgressAchievementId.SCORE_5000)
            if (games.any { it.score >= 20_000 }) add(ProgressAchievementId.SCORE_20000)
            if (games.any { it.tetrisesCleared > 0 }) add(ProgressAchievementId.FIRST_TETRIS)
            if (games.any { it.tSpinClears > 0 }) add(ProgressAchievementId.FIRST_TSPIN)
            if (games.any { it.maxCombo >= 5 }) add(ProgressAchievementId.COMBO_5)
            if (games.any { it.perfectClears > 0 }) add(ProgressAchievementId.PERFECT_CLEAR)
            if (games.size >= 10) add(ProgressAchievementId.TEN_GAMES)
        }
}
