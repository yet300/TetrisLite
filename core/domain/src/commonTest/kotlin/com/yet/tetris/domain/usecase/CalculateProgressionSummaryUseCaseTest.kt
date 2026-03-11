package com.yet.tetris.domain.usecase

import com.yet.tetris.domain.model.game.Difficulty
import com.yet.tetris.domain.model.history.GameRecord
import com.yet.tetris.domain.model.progression.ProgressAchievementId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CalculateProgressionSummaryUseCaseTest {
    private val useCase = CalculateProgressionSummaryUseCase()

    @Test
    fun returns_empty_summary_when_no_games_exist() {
        val summary = useCase(emptyList())

        assertFalse(summary.hasProgress)
        assertEquals(0, summary.totalGames)
        assertTrue(summary.unlockedAchievements.isEmpty())
    }

    @Test
    fun aggregates_totals_and_difficulty_bests() {
        val summary =
            useCase(
                listOf(
                    gameRecord(
                        id = "easy-1",
                        difficulty = Difficulty.EASY,
                        score = 1_500,
                        level = 3,
                        lines = 12,
                        pieces = 40,
                    ),
                    gameRecord(
                        id = "hard-1",
                        difficulty = Difficulty.HARD,
                        score = 8_400,
                        level = 7,
                        lines = 32,
                        pieces = 88,
                        tetrises = 2,
                        tspins = 1,
                    ),
                ),
            )

        assertTrue(summary.hasProgress)
        assertEquals(2, summary.totalGames)
        assertEquals(44, summary.totalLines)
        assertEquals(128, summary.totalPieces)
        assertEquals(8_400, summary.bestScore)
        assertEquals(7, summary.highestLevel)
        assertEquals(1_500, summary.bestFor(Difficulty.EASY).bestScore)
        assertEquals(8_400, summary.bestFor(Difficulty.HARD).bestScore)
    }

    @Test
    fun unlocks_achievements_when_thresholds_are_met() {
        val games =
            (1..10).map { index ->
                gameRecord(
                    id = "game-$index",
                    difficulty = if (index % 2 == 0) Difficulty.NORMAL else Difficulty.HARD,
                    score = if (index == 10) 22_500 else 5_200,
                    level = 6,
                    lines = 20,
                    maxCombo = if (index == 1) 5 else 2,
                    tetrises = if (index == 2) 1 else 0,
                    tspins = if (index == 3) 1 else 0,
                    perfectClears = if (index == 4) 1 else 0,
                )
            }

        val summary = useCase(games)

        assertEquals(
            listOf(
                ProgressAchievementId.FIRST_GAME,
                ProgressAchievementId.SCORE_5000,
                ProgressAchievementId.SCORE_20000,
                ProgressAchievementId.FIRST_TETRIS,
                ProgressAchievementId.FIRST_TSPIN,
                ProgressAchievementId.COMBO_5,
                ProgressAchievementId.PERFECT_CLEAR,
                ProgressAchievementId.TEN_GAMES,
            ),
            summary.unlockedAchievements,
        )
    }

    private fun gameRecord(
        id: String,
        difficulty: Difficulty,
        score: Long,
        level: Int,
        lines: Long,
        pieces: Long = 0,
        maxCombo: Int = 0,
        tetrises: Long = 0,
        tspins: Long = 0,
        perfectClears: Long = 0,
    ): GameRecord =
        GameRecord(
            id = id,
            score = score,
            linesCleared = lines,
            level = level,
            difficulty = difficulty,
            timestamp = 0L,
            piecesPlaced = pieces,
            maxCombo = maxCombo,
            tetrisesCleared = tetrises,
            tSpinClears = tspins,
            perfectClears = perfectClears,
        )
}
