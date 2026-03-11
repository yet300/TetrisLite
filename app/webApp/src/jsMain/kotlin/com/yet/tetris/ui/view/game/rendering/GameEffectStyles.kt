package com.yet.tetris.ui.view.game.rendering

import com.yet.tetris.domain.model.game.TetrominoType
import com.yet.tetris.domain.model.settings.GameSettings
import com.yet.tetris.domain.model.theme.VisualTheme

data class WebThemeEffectStyle(
    val flashColor: String,
    val flashBoost: Double,
    val textHigh: String,
    val textLow: String,
    val textStrokeHigh: String,
    val textStrokeLow: String,
    val particlePrimary: String,
    val particleSecondary: String,
    val particleOpacityBoost: Double,
    val particleUsesSquares: Boolean,
    val sweepPrimary: String,
    val sweepSecondary: String,
    val sweepFill: String,
    val sweepOpacityBoost: Double,
    val lockGlowPrimary: String,
    val lockGlowSecondary: String,
    val lockGlowOpacityBoost: Double,
    val lockGlowCornerRadiusFactor: Double,
)

data class WebThemeMotionStyle(
    val flashFadeDurationMs: Int,
    val shakeDurationHighMs: Int,
    val shakeDurationLowMs: Int,
    val scaleResetDelayMs: Int,
    val floatingDurationHighMultiplier: Double,
    val floatingDurationLowMultiplier: Double,
    val particleDurationMultiplier: Double,
    val sweepDurationMultiplier: Double,
    val lockGlowDurationMultiplier: Double,
    val pulseDurationMs: Int,
)

fun webThemeEffectStyle(settings: GameSettings): WebThemeEffectStyle {
    val theme = settings.themeConfig.visualTheme
    val accent = themeAccentColor(settings)
    return when (theme) {
        VisualTheme.RETRO_GAMEBOY ->
            WebThemeEffectStyle(
                flashColor = "#c7e64c",
                flashBoost = 0.72,
                textHigh = "#1f3d12",
                textLow = "#334f19",
                textStrokeHigh = "#c7e64c",
                textStrokeLow = "#a0bc3b",
                particlePrimary = "#1f3d12",
                particleSecondary = "#5c8524",
                particleOpacityBoost = 0.85,
                particleUsesSquares = true,
                sweepPrimary = "#2e4d14",
                sweepSecondary = "#c2e547",
                sweepFill = "#84a82e",
                sweepOpacityBoost = 0.82,
                lockGlowPrimary = "#2e4d14",
                lockGlowSecondary = "#bde046",
                lockGlowOpacityBoost = 0.8,
                lockGlowCornerRadiusFactor = 0.0,
            )

        VisualTheme.RETRO_NES ->
            WebThemeEffectStyle(
                flashColor = "#fff0b2",
                flashBoost = 0.84,
                textHigh = "#fff0b8",
                textLow = "#ffffff",
                textStrokeHigh = "#5c1414",
                textStrokeLow = "#000000",
                particlePrimary = "#ffffff",
                particleSecondary = accent,
                particleOpacityBoost = 0.9,
                particleUsesSquares = true,
                sweepPrimary = accent,
                sweepSecondary = "#ffffff",
                sweepFill = "#ffdb80",
                sweepOpacityBoost = 0.92,
                lockGlowPrimary = accent,
                lockGlowSecondary = "#ffffff",
                lockGlowOpacityBoost = 0.88,
                lockGlowCornerRadiusFactor = 0.0,
            )

        VisualTheme.NEON ->
            WebThemeEffectStyle(
                flashColor = "#9efff9",
                flashBoost = 0.95,
                textHigh = "#faf5ff",
                textLow = "#ccfffa",
                textStrokeHigh = "#330847",
                textStrokeLow = "#082433",
                particlePrimary = "#19fff5",
                particleSecondary = "#ff29e6",
                particleOpacityBoost = 1.2,
                particleUsesSquares = false,
                sweepPrimary = "#1cfff9",
                sweepSecondary = "#ff2ee6",
                sweepFill = "#ffffff",
                sweepOpacityBoost = 1.15,
                lockGlowPrimary = "#14fff5",
                lockGlowSecondary = "#ff47eb",
                lockGlowOpacityBoost = 1.18,
                lockGlowCornerRadiusFactor = 0.36,
            )

        VisualTheme.PASTEL ->
            WebThemeEffectStyle(
                flashColor = "#fff7e6",
                flashBoost = 0.82,
                textHigh = "#eb866b",
                textLow = "#8c78a3",
                textStrokeHigh = "#ffffff",
                textStrokeLow = "#d1bfe6",
                particlePrimary = "#ffdbc2",
                particleSecondary = accent,
                particleOpacityBoost = 0.8,
                particleUsesSquares = false,
                sweepPrimary = accent,
                sweepSecondary = "#ffffff",
                sweepFill = "#ffe8cc",
                sweepOpacityBoost = 0.78,
                lockGlowPrimary = accent,
                lockGlowSecondary = "#ffffff",
                lockGlowOpacityBoost = 0.72,
                lockGlowCornerRadiusFactor = 0.42,
            )

        VisualTheme.MONOCHROME ->
            WebThemeEffectStyle(
                flashColor = "#ffffff",
                flashBoost = 0.9,
                textHigh = "#ffffff",
                textLow = "#e0e0e0",
                textStrokeHigh = "#000000",
                textStrokeLow = "#000000",
                particlePrimary = "#ffffff",
                particleSecondary = "#a8a8a8",
                particleOpacityBoost = 0.88,
                particleUsesSquares = false,
                sweepPrimary = "#ffffff",
                sweepSecondary = "#b8b8b8",
                sweepFill = "#e6e6e6",
                sweepOpacityBoost = 0.88,
                lockGlowPrimary = "#ffffff",
                lockGlowSecondary = "#b8b8b8",
                lockGlowOpacityBoost = 0.84,
                lockGlowCornerRadiusFactor = 0.16,
            )

        VisualTheme.OCEAN ->
            WebThemeEffectStyle(
                flashColor = "#ccf2ff",
                flashBoost = 0.86,
                textHigh = "#d6f7ff",
                textLow = "#ade0fa",
                textStrokeHigh = "#002e5c",
                textStrokeLow = "#002447",
                particlePrimary = "#52e5f2",
                particleSecondary = "#2e73e6",
                particleOpacityBoost = 0.95,
                particleUsesSquares = false,
                sweepPrimary = "#42e5f0",
                sweepSecondary = "#a6edff",
                sweepFill = accent,
                sweepOpacityBoost = 0.96,
                lockGlowPrimary = "#33e0f0",
                lockGlowSecondary = "#94ebff",
                lockGlowOpacityBoost = 0.98,
                lockGlowCornerRadiusFactor = 0.36,
            )

        VisualTheme.SUNSET ->
            WebThemeEffectStyle(
                flashColor = "#ffdbb8",
                flashBoost = 0.88,
                textHigh = "#ffdbad",
                textLow = "#ffb88f",
                textStrokeHigh = "#571703",
                textStrokeLow = "#47141f",
                particlePrimary = "#ff8f47",
                particleSecondary = "#ff478f",
                particleOpacityBoost = 1.0,
                particleUsesSquares = false,
                sweepPrimary = "#ff8a38",
                sweepSecondary = "#ff3d8a",
                sweepFill = "#ffd19e",
                sweepOpacityBoost = 1.02,
                lockGlowPrimary = "#ff7a2e",
                lockGlowSecondary = "#ff4280",
                lockGlowOpacityBoost = 1.0,
                lockGlowCornerRadiusFactor = 0.32,
            )

        VisualTheme.FOREST ->
            WebThemeEffectStyle(
                flashColor = "#e0ffe0",
                flashBoost = 0.8,
                textHigh = "#e0ffd1",
                textLow = "#adeba8",
                textStrokeHigh = "#123312",
                textStrokeLow = "#0d290d",
                particlePrimary = "#57db6b",
                particleSecondary = "#adf29e",
                particleOpacityBoost = 0.92,
                particleUsesSquares = false,
                sweepPrimary = "#57db6b",
                sweepSecondary = "#d1fad1",
                sweepFill = accent,
                sweepOpacityBoost = 0.92,
                lockGlowPrimary = "#4dd161",
                lockGlowSecondary = "#c2f7b8",
                lockGlowOpacityBoost = 0.9,
                lockGlowCornerRadiusFactor = 0.32,
            )

        VisualTheme.CLASSIC ->
            WebThemeEffectStyle(
                flashColor = "#ffffff",
                flashBoost = 0.88,
                textHigh = "#ffed9e",
                textLow = "#ffffff",
                textStrokeHigh = "#3b1c00",
                textStrokeLow = "#000000",
                particlePrimary = accent,
                particleSecondary = "#ffed99",
                particleOpacityBoost = 1.0,
                particleUsesSquares = false,
                sweepPrimary = accent,
                sweepSecondary = "#ffffff",
                sweepFill = "#ffffff",
                sweepOpacityBoost = 1.0,
                lockGlowPrimary = accent,
                lockGlowSecondary = "#ffffff",
                lockGlowOpacityBoost = 1.0,
                lockGlowCornerRadiusFactor = 0.35,
            )
    }
}

fun webThemeMotionStyle(theme: VisualTheme): WebThemeMotionStyle =
    when (theme) {
        VisualTheme.RETRO_GAMEBOY -> WebThemeMotionStyle(120, 200, 140, 130, 0.82, 0.8, 0.84, 0.82, 0.8, 120)
        VisualTheme.RETRO_NES -> WebThemeMotionStyle(140, 220, 150, 150, 0.86, 0.84, 0.88, 0.86, 0.84, 140)
        VisualTheme.NEON -> WebThemeMotionStyle(240, 340, 240, 260, 1.14, 1.08, 1.16, 1.18, 1.22, 220)
        VisualTheme.PASTEL -> WebThemeMotionStyle(220, 280, 200, 240, 1.12, 1.1, 1.08, 1.08, 1.1, 200)
        VisualTheme.MONOCHROME -> WebThemeMotionStyle(120, 180, 130, 120, 0.84, 0.82, 0.86, 0.84, 0.8, 120)
        VisualTheme.OCEAN -> WebThemeMotionStyle(200, 300, 220, 240, 1.1, 1.06, 1.08, 1.12, 1.14, 190)
        VisualTheme.SUNSET -> WebThemeMotionStyle(220, 320, 220, 240, 1.06, 1.04, 1.1, 1.12, 1.14, 200)
        VisualTheme.FOREST -> WebThemeMotionStyle(180, 260, 190, 200, 1.0, 0.98, 1.0, 1.02, 1.04, 170)
        VisualTheme.CLASSIC -> WebThemeMotionStyle(180, 300, 200, 220, 1.0, 1.0, 1.0, 1.0, 1.0, 180)
    }

fun colorWithAlpha(
    hex: String,
    alpha: Double,
): String {
    val normalized = hex.removePrefix("#")
    val r = normalized.substring(0, 2).toInt(16)
    val g = normalized.substring(2, 4).toInt(16)
    val b = normalized.substring(4, 6).toInt(16)
    return "rgba($r, $g, $b, ${alpha.coerceIn(0.0, 1.0)})"
}

private fun themeAccentColor(settings: GameSettings): String =
    lightenHexColor(
        ThemeColors.getTetrominoColor(TetrominoType.I, settings),
        0.22,
    )

private fun lightenHexColor(
    hex: String,
    factor: Double,
): String {
    val normalized = hex.removePrefix("#")
    val r = normalized.substring(0, 2).toInt(16)
    val g = normalized.substring(2, 4).toInt(16)
    val b = normalized.substring(4, 6).toInt(16)

    val nextR = (r + ((255 - r) * factor)).toInt().coerceIn(0, 255)
    val nextG = (g + ((255 - g) * factor)).toInt().coerceIn(0, 255)
    val nextB = (b + ((255 - b) * factor)).toInt().coerceIn(0, 255)

    return "#${((nextR shl 16) or (nextG shl 8) or nextB).toString(16).padStart(6, '0')}"
}
