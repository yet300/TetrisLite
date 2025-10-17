package com.yet.tetris.domain.usecase

import jakarta.inject.Singleton

/**
 * Use case for calculating score based on the number of lines cleared simultaneously.
 * Implements standard Tetris scoring rules.
 */
@Singleton
class CalculateScoreUseCase {
    /**
     * Calculates the score increment based on lines cleared.
     *
     * Scoring rules:
     * - 1 line (Single): 100 points
     * - 2 lines (Double): 300 points
     * - 3 lines (Triple): 500 points
     * - 4 lines (Tetris): 800 points
     *
     * @param linesCleared Number of lines cleared simultaneously (0-4)
     * @return Score increment
     */
    operator fun invoke(linesCleared: Int): Int =
        when (linesCleared) {
            1 -> 100
            2 -> 300
            3 -> 500
            4 -> 800
            else -> 0
        }

    /**
     * Calculates score with difficulty multiplier.
     * Higher difficulties can award bonus points.
     */
    fun calculateWithMultiplier(
        linesCleared: Int,
        multiplier: Float,
    ): Int = (invoke(linesCleared) * multiplier).toInt()
}
