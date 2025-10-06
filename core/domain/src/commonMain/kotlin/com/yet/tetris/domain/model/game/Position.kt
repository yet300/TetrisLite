package com.yet.tetris.domain.model.game

import kotlinx.serialization.Serializable

@Serializable
data class Position(
    val x: Int,
    val y: Int
) {
    operator fun plus(other: Position) = Position(x + other.x, y + other.y)
    operator fun minus(other: Position) = Position(x - other.x, y - other.y)
}
