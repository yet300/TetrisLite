package com.yet.tetris.domain.model.settings

import com.yet.tetris.domain.model.game.RotationDirection

data class ControlSettings(
    val primaryRotateDirection: RotationDirection = RotationDirection.CLOCKWISE,
    val enable180Rotation: Boolean = true,
    val gestureSensitivity: GestureSensitivity = GestureSensitivity.NORMAL,
)

enum class GestureSensitivity(
    val distanceMultiplier: Float,
    val velocityMultiplier: Float,
) {
    RELAXED(distanceMultiplier = 1.2f, velocityMultiplier = 1.2f),
    NORMAL(distanceMultiplier = 1.0f, velocityMultiplier = 1.0f),
    COMPETITIVE(distanceMultiplier = 0.8f, velocityMultiplier = 0.8f),
}
