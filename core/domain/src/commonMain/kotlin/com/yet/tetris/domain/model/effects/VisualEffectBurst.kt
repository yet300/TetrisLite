package com.yet.tetris.domain.model.effects

import com.yet.tetris.domain.model.game.Position

data class VisualEffectBurst(
    val id: Long,
    val linesCleared: Int,
    val comboStreak: Int,
    val intensity: IntensityLevel,
    val power: Float,
    val events: List<VisualEffectEvent>,
    val clearedRows: List<Int> = emptyList(),
    val lockCells: List<Position> = emptyList(),
)
