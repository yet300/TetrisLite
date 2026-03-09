package com.yet.tetris.ui.view.game.rendering

data class WebBoardCell(
    val x: Int,
    val y: Int,
)

data class WebLineSweepEffect(
    val id: String,
    val clearedRows: List<Int>,
    val createdAtMs: Double,
    val durationMs: Double,
    val primaryColor: String,
    val secondaryColor: String,
    val fillColor: String,
    val opacityBoost: Double,
)

data class WebLockGlowEffect(
    val id: String,
    val cells: List<WebBoardCell>,
    val createdAtMs: Double,
    val durationMs: Double,
    val primaryColor: String,
    val secondaryColor: String,
    val opacityBoost: Double,
    val cornerRadiusFactor: Double,
)
