package com.yet.tetris.domain.model.game

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DifficultyTest {
    @Test
    fun fallDelayForLevel_shouldUseBaseDelayAtLevelOne() {
        assertEquals(Difficulty.EASY.fallDelayMs, Difficulty.EASY.fallDelayForLevel(1))
        assertEquals(Difficulty.NORMAL.fallDelayMs, Difficulty.NORMAL.fallDelayForLevel(1))
        assertEquals(Difficulty.HARD.fallDelayMs, Difficulty.HARD.fallDelayForLevel(1))
    }

    @Test
    fun fallDelayForLevel_shouldDecreaseAsLevelIncreases() {
        val levelOne = Difficulty.NORMAL.fallDelayForLevel(1)
        val levelFive = Difficulty.NORMAL.fallDelayForLevel(5)
        assertTrue(levelFive < levelOne)
    }

    @Test
    fun fallDelayForLevel_shouldNotGoBelowMinimumDelay() {
        assertEquals(
            Difficulty.HARD.minFallDelayMs,
            Difficulty.HARD.fallDelayForLevel(99),
        )
    }
}
