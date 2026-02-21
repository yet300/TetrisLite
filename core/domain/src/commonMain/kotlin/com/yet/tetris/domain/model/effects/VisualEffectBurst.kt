package com.yet.tetris.domain.model.effects

data class VisualEffectBurst(
    val id: Long,
    val linesCleared: Int,
    val comboStreak: Int,
    val intensity: IntensityLevel,
    val power: Float,
    val events: List<VisualEffectEvent>,
)
