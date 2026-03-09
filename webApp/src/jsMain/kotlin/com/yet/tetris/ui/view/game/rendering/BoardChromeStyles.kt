package com.yet.tetris.ui.view.game.rendering

import com.yet.tetris.domain.model.settings.GameSettings
import com.yet.tetris.domain.model.theme.PieceStyle
import com.yet.tetris.domain.model.theme.VisualTheme

data class WebBoardChromeStyle(
    val borderColor: String,
    val backgroundColor: String,
    val boxShadow: String,
    val checkerPrimary: String,
    val checkerSecondary: String,
    val rimLight: String,
    val verticalLight: String,
    val verticalShadow: String,
    val horizontalLight: String,
    val horizontalShadow: String,
    val shimmerPrimary: String,
    val shimmerSecondary: String,
    val shimmerEnabled: Boolean,
)

fun webBoardChromeStyle(settings: GameSettings): WebBoardChromeStyle {
    val theme = settings.themeConfig.visualTheme
    val accent = boardAccentColor(settings)
    val grid = ThemeColors.getGridColor(settings)
    val background = ThemeColors.getBackgroundColor(settings)

    return when (theme) {
        VisualTheme.RETRO_GAMEBOY ->
            WebBoardChromeStyle(
                borderColor = colorWithAlpha(grid, 0.52),
                backgroundColor = background,
                boxShadow = "0 10px 28px rgba(44, 70, 14, 0.35), inset 0 1px 0 rgba(214, 244, 96, 0.12)",
                checkerPrimary = colorWithAlpha("#d6f460", 0.055),
                checkerSecondary = colorWithAlpha("#2b4610", 0.04),
                rimLight = colorWithAlpha("#d6f460", 0.12),
                verticalLight = colorWithAlpha("#d6f460", 0.08),
                verticalShadow = colorWithAlpha("#1f3310", 0.22),
                horizontalLight = colorWithAlpha("#c5e553", 0.07),
                horizontalShadow = colorWithAlpha("#18300b", 0.14),
                shimmerPrimary = colorWithAlpha("#d6f460", 0.0),
                shimmerSecondary = colorWithAlpha("#d6f460", 0.0),
                shimmerEnabled = false,
            )

        VisualTheme.RETRO_NES ->
            WebBoardChromeStyle(
                borderColor = colorWithAlpha(grid, 0.5),
                backgroundColor = background,
                boxShadow = "0 12px 32px rgba(0, 0, 0, 0.58), inset 0 1px 0 rgba(255, 255, 255, 0.08)",
                checkerPrimary = colorWithAlpha("#ffffff", 0.035),
                checkerSecondary = colorWithAlpha(accent, 0.03),
                rimLight = colorWithAlpha("#fff2b8", 0.1),
                verticalLight = colorWithAlpha("#ffffff", 0.065),
                verticalShadow = colorWithAlpha("#000000", 0.24),
                horizontalLight = colorWithAlpha(accent, 0.045),
                horizontalShadow = colorWithAlpha("#000000", 0.12),
                shimmerPrimary = colorWithAlpha("#ffffff", 0.0),
                shimmerSecondary = colorWithAlpha("#ffffff", 0.0),
                shimmerEnabled = false,
            )

        VisualTheme.NEON ->
            WebBoardChromeStyle(
                borderColor = colorWithAlpha(accent, 0.62),
                backgroundColor = background,
                boxShadow = "0 12px 34px rgba(0, 0, 0, 0.55), 0 0 28px rgba(0, 255, 255, 0.2), inset 0 1px 0 rgba(255, 255, 255, 0.08)",
                checkerPrimary = colorWithAlpha("#00ffff", 0.045),
                checkerSecondary = colorWithAlpha("#ff29e6", 0.03),
                rimLight = colorWithAlpha("#9efff9", 0.12),
                verticalLight = colorWithAlpha("#00ffff", 0.08),
                verticalShadow = colorWithAlpha("#000000", 0.24),
                horizontalLight = colorWithAlpha("#ffffff", 0.04),
                horizontalShadow = colorWithAlpha("#001c24", 0.16),
                shimmerPrimary = colorWithAlpha("#00fff5", 0.0),
                shimmerSecondary = colorWithAlpha("#ff35ea", 0.24),
                shimmerEnabled = true,
            )

        VisualTheme.PASTEL ->
            WebBoardChromeStyle(
                borderColor = colorWithAlpha(grid, 0.44),
                backgroundColor = background,
                boxShadow = "0 12px 28px rgba(137, 125, 157, 0.18), inset 0 1px 0 rgba(255, 255, 255, 0.2)",
                checkerPrimary = colorWithAlpha("#ffffff", 0.05),
                checkerSecondary = colorWithAlpha(accent, 0.032),
                rimLight = colorWithAlpha("#ffffff", 0.14),
                verticalLight = colorWithAlpha("#fff4ea", 0.08),
                verticalShadow = colorWithAlpha("#cbbcd9", 0.18),
                horizontalLight = colorWithAlpha("#ffffff", 0.05),
                horizontalShadow = colorWithAlpha("#c7b7d6", 0.1),
                shimmerPrimary = colorWithAlpha("#ffffff", 0.0),
                shimmerSecondary = colorWithAlpha("#ffffff", 0.0),
                shimmerEnabled = settings.themeConfig.pieceStyle == PieceStyle.GLASS,
            )

        VisualTheme.MONOCHROME ->
            WebBoardChromeStyle(
                borderColor = colorWithAlpha(grid, 0.46),
                backgroundColor = background,
                boxShadow = "0 12px 32px rgba(0, 0, 0, 0.62), inset 0 1px 0 rgba(255, 255, 255, 0.08)",
                checkerPrimary = colorWithAlpha("#ffffff", 0.035),
                checkerSecondary = colorWithAlpha("#7f7f7f", 0.025),
                rimLight = colorWithAlpha("#ffffff", 0.1),
                verticalLight = colorWithAlpha("#ffffff", 0.06),
                verticalShadow = colorWithAlpha("#000000", 0.25),
                horizontalLight = colorWithAlpha("#ffffff", 0.03),
                horizontalShadow = colorWithAlpha("#000000", 0.12),
                shimmerPrimary = colorWithAlpha("#ffffff", 0.0),
                shimmerSecondary = colorWithAlpha("#ffffff", 0.0),
                shimmerEnabled = false,
            )

        VisualTheme.OCEAN ->
            WebBoardChromeStyle(
                borderColor = colorWithAlpha(grid, 0.5),
                backgroundColor = background,
                boxShadow = "0 12px 32px rgba(0, 17, 38, 0.52), inset 0 1px 0 rgba(197, 244, 255, 0.08)",
                checkerPrimary = colorWithAlpha("#8beaff", 0.04),
                checkerSecondary = colorWithAlpha("#2b6cb0", 0.03),
                rimLight = colorWithAlpha("#c8f8ff", 0.12),
                verticalLight = colorWithAlpha("#5adff2", 0.08),
                verticalShadow = colorWithAlpha("#00162d", 0.22),
                horizontalLight = colorWithAlpha("#ffffff", 0.04),
                horizontalShadow = colorWithAlpha("#00162d", 0.13),
                shimmerPrimary = colorWithAlpha("#5ae6f2", 0.0),
                shimmerSecondary = colorWithAlpha("#a4f1ff", 0.18),
                shimmerEnabled = settings.themeConfig.pieceStyle == PieceStyle.GLASS,
            )

        VisualTheme.SUNSET ->
            WebBoardChromeStyle(
                borderColor = colorWithAlpha(grid, 0.5),
                backgroundColor = background,
                boxShadow = "0 12px 32px rgba(61, 21, 4, 0.42), inset 0 1px 0 rgba(255, 227, 186, 0.09)",
                checkerPrimary = colorWithAlpha("#ffb76a", 0.04),
                checkerSecondary = colorWithAlpha("#ff4f93", 0.03),
                rimLight = colorWithAlpha("#ffe0b0", 0.12),
                verticalLight = colorWithAlpha("#ff9b52", 0.08),
                verticalShadow = colorWithAlpha("#2a1006", 0.22),
                horizontalLight = colorWithAlpha("#ffd2aa", 0.04),
                horizontalShadow = colorWithAlpha("#2a1006", 0.13),
                shimmerPrimary = colorWithAlpha("#ff9d47", 0.0),
                shimmerSecondary = colorWithAlpha("#ff3d8a", 0.18),
                shimmerEnabled = settings.themeConfig.pieceStyle == PieceStyle.GLASS,
            )

        VisualTheme.FOREST ->
            WebBoardChromeStyle(
                borderColor = colorWithAlpha(grid, 0.5),
                backgroundColor = background,
                boxShadow = "0 12px 32px rgba(6, 31, 10, 0.48), inset 0 1px 0 rgba(219, 255, 213, 0.08)",
                checkerPrimary = colorWithAlpha("#8af08a", 0.04),
                checkerSecondary = colorWithAlpha("#347d49", 0.028),
                rimLight = colorWithAlpha("#deffd1", 0.12),
                verticalLight = colorWithAlpha("#61db7a", 0.08),
                verticalShadow = colorWithAlpha("#091e0b", 0.22),
                horizontalLight = colorWithAlpha("#deffd1", 0.04),
                horizontalShadow = colorWithAlpha("#091e0b", 0.13),
                shimmerPrimary = colorWithAlpha("#61db7a", 0.0),
                shimmerSecondary = colorWithAlpha("#d1fad1", 0.18),
                shimmerEnabled = settings.themeConfig.pieceStyle == PieceStyle.GLASS,
            )

        VisualTheme.CLASSIC ->
            WebBoardChromeStyle(
                borderColor = colorWithAlpha(grid, 0.48),
                backgroundColor = background,
                boxShadow = "0 12px 34px rgba(0, 0, 0, 0.56), inset 0 1px 0 rgba(255, 255, 255, 0.08)",
                checkerPrimary = colorWithAlpha("#ffffff", 0.035),
                checkerSecondary = colorWithAlpha(accent, 0.024),
                rimLight = colorWithAlpha("#ffffff", 0.1),
                verticalLight = colorWithAlpha("#ffffff", 0.08),
                verticalShadow = colorWithAlpha("#000000", 0.22),
                horizontalLight = colorWithAlpha("#ffffff", 0.035),
                horizontalShadow = colorWithAlpha("#000000", 0.12),
                shimmerPrimary = colorWithAlpha("#ffffff", 0.0),
                shimmerSecondary = colorWithAlpha("#ffffff", 0.0),
                shimmerEnabled = settings.themeConfig.pieceStyle == PieceStyle.GLASS,
            )
    }
}

private fun boardAccentColor(settings: GameSettings): String = ThemeColors.lightenHexColor(ThemeColors.getTetrominoColor(com.yet.tetris.domain.model.game.TetrominoType.I, settings), 0.22)
