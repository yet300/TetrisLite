package com.yet.tetris.ui.screens.game

import androidx.compose.ui.graphics.Color
import com.yet.tetris.domain.model.game.TetrominoType
import com.yet.tetris.domain.model.theme.VisualTheme
import com.yet.tetris.ui.theme.getTetrominoColor

internal data class ComposeThemeEffectStyle(
    val flashColor: Color,
    val flashBoost: Float,
    val textHigh: Color,
    val textLow: Color,
    val textStrokeHigh: Color,
    val textStrokeLow: Color,
    val particlePrimary: Color,
    val particleSecondary: Color,
    val particleOpacityBoost: Float,
    val particleUsesSquares: Boolean,
    val sweepPrimary: Color,
    val sweepSecondary: Color,
    val sweepFill: Color,
    val sweepOpacityBoost: Float,
    val lockGlowPrimary: Color,
    val lockGlowSecondary: Color,
    val lockGlowOpacityBoost: Float,
    val lockGlowCornerRadiusFactor: Float,
)

internal data class ComposeThemeMotionStyle(
    val flashFadeDurationMillis: Int,
    val shakeDurationHighMillis: Long,
    val shakeDurationLowMillis: Long,
    val floatingDurationHighMultiplier: Float,
    val floatingDurationLowMultiplier: Float,
    val particleDurationMultiplier: Float,
    val sweepDurationMultiplier: Float,
    val lockGlowDurationMultiplier: Float,
)

internal fun composeThemeEffectStyle(theme: VisualTheme): ComposeThemeEffectStyle {
    val accent = theme.effectAccentColor()
    return when (theme) {
        VisualTheme.RETRO_GAMEBOY ->
            ComposeThemeEffectStyle(
                flashColor = Color(0xFFC7E64C),
                flashBoost = 0.72f,
                textHigh = Color(0xFF1F3D12),
                textLow = Color(0xFF334F19),
                textStrokeHigh = Color(0xFFC7E64C),
                textStrokeLow = Color(0xFFA0BC3B),
                particlePrimary = Color(0xFF1F3D12),
                particleSecondary = Color(0xFF5C8524),
                particleOpacityBoost = 0.85f,
                particleUsesSquares = true,
                sweepPrimary = Color(0xFF2E4D14),
                sweepSecondary = Color(0xFFC2E547),
                sweepFill = Color(0xFF84A82E),
                sweepOpacityBoost = 0.82f,
                lockGlowPrimary = Color(0xFF2E4D14),
                lockGlowSecondary = Color(0xFFBDE046),
                lockGlowOpacityBoost = 0.8f,
                lockGlowCornerRadiusFactor = 0f,
            )

        VisualTheme.RETRO_NES ->
            ComposeThemeEffectStyle(
                flashColor = Color(0xFFFFF0B2),
                flashBoost = 0.84f,
                textHigh = Color(0xFFFFF0B8),
                textLow = Color.White,
                textStrokeHigh = Color(0xFF5C1414),
                textStrokeLow = Color.Black.copy(alpha = 0.95f),
                particlePrimary = Color.White,
                particleSecondary = accent,
                particleOpacityBoost = 0.9f,
                particleUsesSquares = true,
                sweepPrimary = accent,
                sweepSecondary = Color.White,
                sweepFill = Color(0xFFFFDB80),
                sweepOpacityBoost = 0.92f,
                lockGlowPrimary = accent,
                lockGlowSecondary = Color.White,
                lockGlowOpacityBoost = 0.88f,
                lockGlowCornerRadiusFactor = 0f,
            )

        VisualTheme.NEON ->
            ComposeThemeEffectStyle(
                flashColor = Color(0xFF9EFFF9),
                flashBoost = 0.95f,
                textHigh = Color(0xFFFAF5FF),
                textLow = Color(0xFFCCFFFA),
                textStrokeHigh = Color(0xFF330847),
                textStrokeLow = Color(0xFF082433),
                particlePrimary = Color(0xFF19FFF5),
                particleSecondary = Color(0xFFFF29E6),
                particleOpacityBoost = 1.2f,
                particleUsesSquares = false,
                sweepPrimary = Color(0xFF1CFFF9),
                sweepSecondary = Color(0xFFFF2EE6),
                sweepFill = Color.White,
                sweepOpacityBoost = 1.15f,
                lockGlowPrimary = Color(0xFF14FFF5),
                lockGlowSecondary = Color(0xFFFF47EB),
                lockGlowOpacityBoost = 1.18f,
                lockGlowCornerRadiusFactor = 0.36f,
            )

        VisualTheme.PASTEL ->
            ComposeThemeEffectStyle(
                flashColor = Color(0xFFFFF7E6),
                flashBoost = 0.82f,
                textHigh = Color(0xFFEB866B),
                textLow = Color(0xFF8C78A3),
                textStrokeHigh = Color.White.copy(alpha = 0.95f),
                textStrokeLow = Color(0xFFD1BFE6),
                particlePrimary = Color(0xFFFFDBC2),
                particleSecondary = accent,
                particleOpacityBoost = 0.8f,
                particleUsesSquares = false,
                sweepPrimary = accent,
                sweepSecondary = Color.White,
                sweepFill = Color(0xFFFFE8CC),
                sweepOpacityBoost = 0.78f,
                lockGlowPrimary = accent,
                lockGlowSecondary = Color.White,
                lockGlowOpacityBoost = 0.72f,
                lockGlowCornerRadiusFactor = 0.42f,
            )

        VisualTheme.MONOCHROME ->
            ComposeThemeEffectStyle(
                flashColor = Color.White,
                flashBoost = 0.9f,
                textHigh = Color.White,
                textLow = Color(0xFFE0E0E0),
                textStrokeHigh = Color.Black,
                textStrokeLow = Color.Black.copy(alpha = 0.92f),
                particlePrimary = Color.White,
                particleSecondary = Color(0xFFA8A8A8),
                particleOpacityBoost = 0.88f,
                particleUsesSquares = false,
                sweepPrimary = Color.White,
                sweepSecondary = Color(0xFFB8B8B8),
                sweepFill = Color(0xFFE6E6E6),
                sweepOpacityBoost = 0.88f,
                lockGlowPrimary = Color.White,
                lockGlowSecondary = Color(0xFFB8B8B8),
                lockGlowOpacityBoost = 0.84f,
                lockGlowCornerRadiusFactor = 0.16f,
            )

        VisualTheme.OCEAN ->
            ComposeThemeEffectStyle(
                flashColor = Color(0xFFCCF2FF),
                flashBoost = 0.86f,
                textHigh = Color(0xFFD6F7FF),
                textLow = Color(0xFFADE0FA),
                textStrokeHigh = Color(0xFF002E5C),
                textStrokeLow = Color(0xFF002447),
                particlePrimary = Color(0xFF52E5F2),
                particleSecondary = Color(0xFF2E73E6),
                particleOpacityBoost = 0.95f,
                particleUsesSquares = false,
                sweepPrimary = Color(0xFF42E5F0),
                sweepSecondary = Color(0xFFA6EDFF),
                sweepFill = accent,
                sweepOpacityBoost = 0.96f,
                lockGlowPrimary = Color(0xFF33E0F0),
                lockGlowSecondary = Color(0xFF94EBFF),
                lockGlowOpacityBoost = 0.98f,
                lockGlowCornerRadiusFactor = 0.36f,
            )

        VisualTheme.SUNSET ->
            ComposeThemeEffectStyle(
                flashColor = Color(0xFFFFDBB8),
                flashBoost = 0.88f,
                textHigh = Color(0xFFFFDBAD),
                textLow = Color(0xFFFFB88F),
                textStrokeHigh = Color(0xFF571703),
                textStrokeLow = Color(0xFF47141F),
                particlePrimary = Color(0xFFFF8F47),
                particleSecondary = Color(0xFFFF478F),
                particleOpacityBoost = 1f,
                particleUsesSquares = false,
                sweepPrimary = Color(0xFFFF8A38),
                sweepSecondary = Color(0xFFFF3D8A),
                sweepFill = Color(0xFFFFD19E),
                sweepOpacityBoost = 1.02f,
                lockGlowPrimary = Color(0xFFFF7A2E),
                lockGlowSecondary = Color(0xFFFF4280),
                lockGlowOpacityBoost = 1f,
                lockGlowCornerRadiusFactor = 0.32f,
            )

        VisualTheme.FOREST ->
            ComposeThemeEffectStyle(
                flashColor = Color(0xFFE0FFE0),
                flashBoost = 0.8f,
                textHigh = Color(0xFFE0FFD1),
                textLow = Color(0xFFADEBA8),
                textStrokeHigh = Color(0xFF123312),
                textStrokeLow = Color(0xFF0D290D),
                particlePrimary = Color(0xFF57DB6B),
                particleSecondary = Color(0xFFADF29E),
                particleOpacityBoost = 0.92f,
                particleUsesSquares = false,
                sweepPrimary = Color(0xFF57DB6B),
                sweepSecondary = Color(0xFFD1FAD1),
                sweepFill = accent,
                sweepOpacityBoost = 0.92f,
                lockGlowPrimary = Color(0xFF4DD161),
                lockGlowSecondary = Color(0xFFC2F7B8),
                lockGlowOpacityBoost = 0.9f,
                lockGlowCornerRadiusFactor = 0.32f,
            )

        VisualTheme.CLASSIC ->
            ComposeThemeEffectStyle(
                flashColor = Color.White,
                flashBoost = 0.88f,
                textHigh = Color(0xFFFFED9E),
                textLow = Color.White,
                textStrokeHigh = Color(0xFF3B1C00),
                textStrokeLow = Color.Black.copy(alpha = 0.95f),
                particlePrimary = accent,
                particleSecondary = Color(0xFFFFED99),
                particleOpacityBoost = 1f,
                particleUsesSquares = false,
                sweepPrimary = accent,
                sweepSecondary = Color.White,
                sweepFill = Color.White,
                sweepOpacityBoost = 1f,
                lockGlowPrimary = accent,
                lockGlowSecondary = Color.White,
                lockGlowOpacityBoost = 1f,
                lockGlowCornerRadiusFactor = 0.35f,
            )
    }
}

internal fun composeThemeMotionStyle(theme: VisualTheme): ComposeThemeMotionStyle =
    when (theme) {
        VisualTheme.RETRO_GAMEBOY ->
            ComposeThemeMotionStyle(120, 200L, 140L, 0.82f, 0.8f, 0.84f, 0.82f, 0.8f)
        VisualTheme.RETRO_NES ->
            ComposeThemeMotionStyle(140, 220L, 150L, 0.86f, 0.84f, 0.88f, 0.86f, 0.84f)
        VisualTheme.NEON ->
            ComposeThemeMotionStyle(240, 340L, 240L, 1.14f, 1.08f, 1.16f, 1.18f, 1.22f)
        VisualTheme.PASTEL ->
            ComposeThemeMotionStyle(220, 280L, 200L, 1.12f, 1.1f, 1.08f, 1.08f, 1.1f)
        VisualTheme.MONOCHROME ->
            ComposeThemeMotionStyle(120, 180L, 130L, 0.84f, 0.82f, 0.86f, 0.84f, 0.8f)
        VisualTheme.OCEAN ->
            ComposeThemeMotionStyle(200, 300L, 220L, 1.1f, 1.06f, 1.08f, 1.12f, 1.14f)
        VisualTheme.SUNSET ->
            ComposeThemeMotionStyle(220, 320L, 220L, 1.06f, 1.04f, 1.1f, 1.12f, 1.14f)
        VisualTheme.FOREST ->
            ComposeThemeMotionStyle(180, 260L, 190L, 1f, 0.98f, 1f, 1.02f, 1.04f)
        VisualTheme.CLASSIC ->
            ComposeThemeMotionStyle(180, 300L, 200L, 1f, 1f, 1f, 1f, 1f)
    }

private fun VisualTheme.effectAccentColor(): Color = getTetrominoColor(TetrominoType.I).lighten(0.22f)

private fun Color.lighten(factor: Float): Color =
    Color(
        red = red + ((1f - red) * factor),
        green = green + ((1f - green) * factor),
        blue = blue + ((1f - blue) * factor),
        alpha = alpha,
    )
