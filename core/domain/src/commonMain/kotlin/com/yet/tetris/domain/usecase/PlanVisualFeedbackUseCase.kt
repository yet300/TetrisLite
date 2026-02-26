package com.yet.tetris.domain.usecase

import com.yet.tetris.domain.model.effects.IntensityLevel
import com.yet.tetris.domain.model.effects.VisualEffectEvent
import com.yet.tetris.domain.model.effects.VisualTextKey
import kotlin.math.floor

class PlanVisualFeedbackUseCase {
    data class BurstSpec(
        val linesCleared: Int,
        val comboStreak: Int,
        val intensity: IntensityLevel,
        val power: Float,
        val events: List<VisualEffectEvent>,
    )

    data class Result(
        val nextComboStreak: Int,
        val burst: BurstSpec?,
    )

    operator fun invoke(
        currentComboStreak: Int,
        linesClearedThisLock: Int,
    ): Result {
        val nextComboStreak =
            if (linesClearedThisLock > 0) {
                currentComboStreak + 1
            } else {
                0
            }

        val burst =
            if (linesClearedThisLock > 0) {
                createBurst(
                    linesCleared = linesClearedThisLock,
                    comboStreak = nextComboStreak,
                )
            } else {
                null
            }

        return Result(
            nextComboStreak = nextComboStreak,
            burst = burst,
        )
    }

    private fun createBurst(
        linesCleared: Int,
        comboStreak: Int,
    ): BurstSpec {
        val baseIntensity = if (linesCleared >= 3) IntensityLevel.HIGH else IntensityLevel.LOW
        val intensity =
            if (baseIntensity == IntensityLevel.LOW && comboStreak >= 2) {
                IntensityLevel.HIGH
            } else {
                baseIntensity
            }

        val basePower =
            when (linesCleared) {
                1 -> 0.30f
                2 -> 0.45f
                3 -> 0.75f
                4 -> 1.00f
                else -> 1.00f
            }
        val comboBonus =
            if (comboStreak < 2) {
                0f
            } else {
                minOf(0.25f, 0.15f + (comboStreak - 2) * 0.05f)
            }
        val power = minOf(1.0f, basePower + comboBonus)

        val textKey = toTextKey(linesCleared)

        val events =
            buildList {
                add(VisualEffectEvent.ScreenShake(intensity = intensity, power = power))
                add(
                    VisualEffectEvent.FloatingText(
                        intensity = intensity,
                        power = power,
                        textKey = textKey,
                    ),
                )

                if (intensity == IntensityLevel.HIGH) {
                    add(
                        VisualEffectEvent.ScreenFlash(
                            intensity = IntensityLevel.HIGH,
                            power = power,
                        ),
                    )
                    add(
                        VisualEffectEvent.Explosion(
                            intensity = IntensityLevel.HIGH,
                            power = power,
                            particleCount = 24 + floor(48 * power).toInt(),
                        ),
                    )
                }
            }

        return BurstSpec(
            linesCleared = linesCleared,
            comboStreak = comboStreak,
            intensity = intensity,
            power = power,
            events = events,
        )
    }

    private fun toTextKey(linesCleared: Int): VisualTextKey =
        when (linesCleared) {
            1 -> VisualTextKey.SINGLE
            2 -> VisualTextKey.DOUBLE
            3 -> VisualTextKey.TRIPLE
            4 -> VisualTextKey.TETRIS
            else -> VisualTextKey.CLEAR
        }
}
