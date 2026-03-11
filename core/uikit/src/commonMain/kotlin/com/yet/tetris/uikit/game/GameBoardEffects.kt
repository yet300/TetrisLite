package com.yet.tetris.uikit.game

import androidx.compose.ui.graphics.Color
import com.yet.tetris.domain.model.game.Position

data class BoardLineSweepEffect(
    val id: Long,
    val clearedRows: List<Int>,
    val createdAtMillis: Long,
    val durationMillis: Long,
    val primaryColor: Color,
    val secondaryColor: Color,
    val fillColor: Color,
    val opacityBoost: Float,
)

data class BoardLockGlowEffect(
    val id: Long,
    val cells: List<Position>,
    val createdAtMillis: Long,
    val durationMillis: Long,
    val primaryColor: Color,
    val secondaryColor: Color,
    val opacityBoost: Float,
    val cornerRadiusFactor: Float,
)
