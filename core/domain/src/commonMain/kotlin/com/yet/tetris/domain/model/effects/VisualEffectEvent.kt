package com.yet.tetris.domain.model.effects

sealed interface VisualEffectEvent {
    val intensity: IntensityLevel
    val power: Float

    data class ScreenShake(
        override val intensity: IntensityLevel,
        override val power: Float,
    ) : VisualEffectEvent

    data class FloatingText(
        override val intensity: IntensityLevel,
        override val power: Float,
        val textKey: VisualTextKey,
    ) : VisualEffectEvent

    data class ScreenFlash(
        override val intensity: IntensityLevel,
        override val power: Float,
    ) : VisualEffectEvent

    data class Explosion(
        override val intensity: IntensityLevel,
        override val power: Float,
        val particleCount: Int,
    ) : VisualEffectEvent
}
