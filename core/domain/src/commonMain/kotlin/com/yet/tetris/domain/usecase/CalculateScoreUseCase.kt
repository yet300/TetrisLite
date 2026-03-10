package com.yet.tetris.domain.usecase

import com.yet.tetris.domain.model.game.ClearType
import kotlin.math.roundToInt

/**
 * Use case for calculating score based on the number of lines cleared simultaneously.
 * Implements standard Tetris scoring rules.
 */
class CalculateScoreUseCase {
    data class LockScoreResult(
        val points: Int,
        val clearType: ClearType,
        val nextBackToBackChain: Int,
        val didBackToBackBonus: Boolean,
        val perfectClearBonus: Int,
    )

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

    fun resolveClearType(
        linesCleared: Int,
        isTSpin: Boolean,
    ): ClearType =
        when {
            isTSpin && linesCleared == 0 -> ClearType.T_SPIN
            isTSpin && linesCleared == 1 -> ClearType.T_SPIN_SINGLE
            isTSpin && linesCleared == 2 -> ClearType.T_SPIN_DOUBLE
            isTSpin && linesCleared == 3 -> ClearType.T_SPIN_TRIPLE
            linesCleared == 1 -> ClearType.SINGLE
            linesCleared == 2 -> ClearType.DOUBLE
            linesCleared == 3 -> ClearType.TRIPLE
            linesCleared == 4 -> ClearType.TETRIS
            else -> ClearType.NONE
        }

    fun calculateLockScore(
        level: Int,
        clearType: ClearType,
        previousBackToBackChain: Int,
        perfectClear: Boolean,
    ): LockScoreResult {
        val normalizedLevel = level.coerceAtLeast(1)
        val basePoints =
            when (clearType) {
                ClearType.NONE -> 0
                ClearType.SINGLE -> 100
                ClearType.DOUBLE -> 300
                ClearType.TRIPLE -> 500
                ClearType.TETRIS -> 800
                ClearType.T_SPIN -> 400
                ClearType.T_SPIN_SINGLE -> 800
                ClearType.T_SPIN_DOUBLE -> 1200
                ClearType.T_SPIN_TRIPLE -> 1600
            } * normalizedLevel

        val didBackToBackBonus = clearType.isBackToBackEligible && previousBackToBackChain > 0
        val lineClearPoints =
            if (didBackToBackBonus) {
                (basePoints * 1.5f).roundToInt()
            } else {
                basePoints
            }

        val perfectClearBonus =
            if (perfectClear) {
                2000 * normalizedLevel
            } else {
                0
            }

        val nextBackToBackChain =
            when {
                clearType.isBackToBackEligible -> previousBackToBackChain + 1
                clearType == ClearType.NONE -> previousBackToBackChain
                else -> 0
            }

        return LockScoreResult(
            points = lineClearPoints + perfectClearBonus,
            clearType = clearType,
            nextBackToBackChain = nextBackToBackChain,
            didBackToBackBonus = didBackToBackBonus,
            perfectClearBonus = perfectClearBonus,
        )
    }

    fun softDropPoints(cells: Int): Int = cells.coerceAtLeast(0)

    fun hardDropPoints(cells: Int): Int = cells.coerceAtLeast(0) * 2
}
