package com.yet.tetris.domain.model.theme

/**
 * Visual theme for the game including colors and piece styles
 */
enum class VisualTheme {
    CLASSIC,        // Modern colorful Tetris
    RETRO_GAMEBOY,  // Game Boy green monochrome
    RETRO_NES,      // NES/Famicom style
    NEON,           // Neon/cyberpunk colors
    PASTEL,         // Soft pastel colors
    MONOCHROME,     // Black and white
    OCEAN,          // Blue ocean theme
    SUNSET,         // Warm sunset colors
    FOREST          // Green forest theme
}

/**
 * Piece rendering style
 */
enum class PieceStyle {
    SOLID,          // Solid filled blocks
    BORDERED,       // Blocks with borders
    GRADIENT,       // Gradient fill
    RETRO_PIXEL,    // Pixelated retro style
    GLASS           // Translucent glass effect
}

/**
 * Theme configuration with colors and style
 */
data class ThemeConfig(
    val visualTheme: VisualTheme = VisualTheme.CLASSIC,
    val pieceStyle: PieceStyle = PieceStyle.SOLID
)
