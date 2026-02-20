package com.yet.tetris.domain.usecase

import com.yet.tetris.domain.model.effects.IntensityLevel
import com.yet.tetris.domain.model.effects.VisualEffectEvent
import com.yet.tetris.domain.model.effects.VisualTextKey
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PlanVisualFeedbackUseCaseTest {
    private val useCase = PlanVisualFeedbackUseCase()

    @Test
    fun returns_null_burst_WHEN_lines_cleared_is_zero() {
        val result = useCase(currentComboStreak = 0, linesClearedThisLock = 0)

        assertEquals(0, result.nextComboStreak)
        assertNull(result.burst)
    }

    @Test
    fun increments_combo_streak_WHEN_lines_cleared() {
        val result = useCase(currentComboStreak = 1, linesClearedThisLock = 2)

        assertEquals(2, result.nextComboStreak)
    }

    @Test
    fun resets_combo_streak_WHEN_no_lines_cleared() {
        val result = useCase(currentComboStreak = 4, linesClearedThisLock = 0)

        assertEquals(0, result.nextComboStreak)
    }

    @Test
    fun creates_low_intensity_burst_for_single_line() {
        val result = useCase(currentComboStreak = 0, linesClearedThisLock = 1)
        val burst = result.burst
        assertNotNull(burst)

        assertEquals(IntensityLevel.LOW, burst.intensity)
        assertClose(expected = 0.30f, actual = burst.power)
        assertEquals(2, burst.events.size)
        assertTrue(burst.events.any { it is VisualEffectEvent.ScreenShake })
        assertTrue(burst.events.any { it is VisualEffectEvent.FloatingText })
        assertTrue(burst.events.none { it is VisualEffectEvent.ScreenFlash })
        assertTrue(burst.events.none { it is VisualEffectEvent.Explosion })
    }

    @Test
    fun boosts_to_high_intensity_WHEN_combo_is_two_or_higher() {
        val result = useCase(currentComboStreak = 2, linesClearedThisLock = 2)
        val burst = result.burst
        assertNotNull(burst)

        assertEquals(IntensityLevel.HIGH, burst.intensity)
        assertClose(expected = 0.65f, actual = burst.power)

        val text = burst.events.filterIsInstance<VisualEffectEvent.FloatingText>().single()
        assertEquals(VisualTextKey.DOUBLE, text.textKey)

        assertTrue(burst.events.any { it is VisualEffectEvent.ScreenFlash })
        assertTrue(burst.events.any { it is VisualEffectEvent.Explosion })
    }

    @Test
    fun clamps_power_and_particles_for_tetris_combo() {
        val result = useCase(currentComboStreak = 5, linesClearedThisLock = 4)
        val burst = result.burst
        assertNotNull(burst)

        assertEquals(IntensityLevel.HIGH, burst.intensity)
        assertClose(expected = 1f, actual = burst.power)

        val explosion = burst.events.filterIsInstance<VisualEffectEvent.Explosion>().single()
        assertEquals(72, explosion.particleCount)

        val text = burst.events.filterIsInstance<VisualEffectEvent.FloatingText>().single()
        assertEquals(VisualTextKey.TETRIS, text.textKey)
    }

    private fun assertClose(
        expected: Float,
        actual: Float,
        epsilon: Float = 0.0001f,
    ) {
        assertTrue(abs(expected - actual) <= epsilon, "Expected $expected, got $actual")
    }
}
