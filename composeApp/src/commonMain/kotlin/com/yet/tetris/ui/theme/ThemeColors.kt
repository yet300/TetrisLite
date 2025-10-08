package com.yet.tetris.ui.theme

import androidx.compose.ui.graphics.Color
import com.yet.tetris.domain.model.game.TetrominoType
import com.yet.tetris.domain.model.theme.ThemeConfig
import com.yet.tetris.domain.model.theme.VisualTheme

/**
 * Extension functions to get Compose Color objects for themes
 */

/**
 * Get Compose Color for a specific tetromino type based on the visual theme
 */
fun VisualTheme.getTetrominoColor(type: TetrominoType): Color {
    return when (this) {
        VisualTheme.CLASSIC -> when (type) {
            TetrominoType.I -> Color(0xFF00F0F0)  // Cyan
            TetrominoType.O -> Color(0xFFF0F000)  // Yellow
            TetrominoType.T -> Color(0xFFA000F0)  // Purple
            TetrominoType.S -> Color(0xFF00F000)  // Green
            TetrominoType.Z -> Color(0xFFF00000)  // Red
            TetrominoType.J -> Color(0xFF0000F0)  // Blue
            TetrominoType.L -> Color(0xFFF0A000)  // Orange
        }
        
        VisualTheme.RETRO_GAMEBOY -> when (type) {
            TetrominoType.I -> Color(0xFF0F380F)  // Dark green
            TetrominoType.O -> Color(0xFF306230)  // Medium green
            TetrominoType.T -> Color(0xFF0F380F)  // Dark green
            TetrominoType.S -> Color(0xFF306230)  // Medium green
            TetrominoType.Z -> Color(0xFF0F380F)  // Dark green
            TetrominoType.J -> Color(0xFF306230)  // Medium green
            TetrominoType.L -> Color(0xFF0F380F)  // Dark green
        }
        
        VisualTheme.RETRO_NES -> when (type) {
            TetrominoType.I -> Color(0xFF00D8F8)  // NES Cyan
            TetrominoType.O -> Color(0xFFF8D800)  // NES Yellow
            TetrominoType.T -> Color(0xFFB800F8)  // NES Purple
            TetrominoType.S -> Color(0xFF00F800)  // NES Green
            TetrominoType.Z -> Color(0xFFF80000)  // NES Red
            TetrominoType.J -> Color(0xFF0000F8)  // NES Blue
            TetrominoType.L -> Color(0xFFF87800)  // NES Orange
        }
        
        VisualTheme.NEON -> when (type) {
            TetrominoType.I -> Color(0xFF00FFFF)  // Neon cyan
            TetrominoType.O -> Color(0xFFFFFF00)  // Neon yellow
            TetrominoType.T -> Color(0xFFFF00FF)  // Neon magenta
            TetrominoType.S -> Color(0xFF00FF00)  // Neon green
            TetrominoType.Z -> Color(0xFFFF0066)  // Neon pink
            TetrominoType.J -> Color(0xFF0066FF)  // Neon blue
            TetrominoType.L -> Color(0xFFFF6600)  // Neon orange
        }
        
        VisualTheme.PASTEL -> when (type) {
            TetrominoType.I -> Color(0xFFB4E7F5)  // Pastel cyan
            TetrominoType.O -> Color(0xFFFFF4B4)  // Pastel yellow
            TetrominoType.T -> Color(0xFFE5B4F5)  // Pastel purple
            TetrominoType.S -> Color(0xFFB4F5B4)  // Pastel green
            TetrominoType.Z -> Color(0xFFF5B4B4)  // Pastel red
            TetrominoType.J -> Color(0xFFB4B4F5)  // Pastel blue
            TetrominoType.L -> Color(0xFFF5D4B4)  // Pastel orange
        }
        
        VisualTheme.MONOCHROME -> when (type) {
            TetrominoType.I -> Color(0xFFFFFFFF)  // White
            TetrominoType.O -> Color(0xFFE0E0E0)  // Light gray
            TetrominoType.T -> Color(0xFFC0C0C0)  // Gray
            TetrominoType.S -> Color(0xFFA0A0A0)  // Medium gray
            TetrominoType.Z -> Color(0xFF808080)  // Dark gray
            TetrominoType.J -> Color(0xFF606060)  // Darker gray
            TetrominoType.L -> Color(0xFF404040)  // Very dark gray
        }
        
        VisualTheme.OCEAN -> when (type) {
            TetrominoType.I -> Color(0xFF00CED1)  // Dark turquoise
            TetrominoType.O -> Color(0xFF20B2AA)  // Light sea green
            TetrominoType.T -> Color(0xFF4682B4)  // Steel blue
            TetrominoType.S -> Color(0xFF5F9EA0)  // Cadet blue
            TetrominoType.Z -> Color(0xFF1E90FF)  // Dodger blue
            TetrominoType.J -> Color(0xFF0000CD)  // Medium blue
            TetrominoType.L -> Color(0xFF000080)  // Navy
        }
        
        VisualTheme.SUNSET -> when (type) {
            TetrominoType.I -> Color(0xFFFF6B6B)  // Coral red
            TetrominoType.O -> Color(0xFFFFD93D)  // Golden yellow
            TetrominoType.T -> Color(0xFFFF8C42)  // Orange
            TetrominoType.S -> Color(0xFFFFA07A)  // Light salmon
            TetrominoType.Z -> Color(0xFFFF69B4)  // Hot pink
            TetrominoType.J -> Color(0xFFFF4500)  // Orange red
            TetrominoType.L -> Color(0xFFFF1493)  // Deep pink
        }
        
        VisualTheme.FOREST -> when (type) {
            TetrominoType.I -> Color(0xFF228B22)  // Forest green
            TetrominoType.O -> Color(0xFF32CD32)  // Lime green
            TetrominoType.T -> Color(0xFF006400)  // Dark green
            TetrominoType.S -> Color(0xFF90EE90)  // Light green
            TetrominoType.Z -> Color(0xFF2E8B57)  // Sea green
            TetrominoType.J -> Color(0xFF3CB371)  // Medium sea green
            TetrominoType.L -> Color(0xFF8FBC8F)  // Dark sea green
        }
    }
}

/**
 * Get background color based on the visual theme
 */
fun VisualTheme.getBackgroundColor(): Color {
    return when (this) {
        VisualTheme.CLASSIC -> Color(0xFF000000)          // Black
        VisualTheme.RETRO_GAMEBOY -> Color(0xFF9BBC0F)    // Game Boy light green
        VisualTheme.RETRO_NES -> Color(0xFF000000)        // Black
        VisualTheme.NEON -> Color(0xFF0A0A0A)             // Very dark gray
        VisualTheme.PASTEL -> Color(0xFFF5F5DC)           // Beige
        VisualTheme.MONOCHROME -> Color(0xFF000000)       // Black
        VisualTheme.OCEAN -> Color(0xFF001F3F)            // Deep ocean blue
        VisualTheme.SUNSET -> Color(0xFF2C1810)           // Dark brown
        VisualTheme.FOREST -> Color(0xFF0D1F0D)           // Very dark green
    }
}

/**
 * Get grid line color based on the visual theme
 */
fun VisualTheme.getGridColor(): Color {
    return when (this) {
        VisualTheme.CLASSIC -> Color(0xFF333333)          // Dark gray
        VisualTheme.RETRO_GAMEBOY -> Color(0xFF8BAC0F)    // Darker green
        VisualTheme.RETRO_NES -> Color(0xFF404040)        // Dark gray
        VisualTheme.NEON -> Color(0xFF00FFFF)             // Cyan
        VisualTheme.PASTEL -> Color(0xFFE0E0E0)           // Light gray
        VisualTheme.MONOCHROME -> Color(0xFF404040)       // Dark gray
        VisualTheme.OCEAN -> Color(0xFF004080)            // Medium blue
        VisualTheme.SUNSET -> Color(0xFF804020)           // Brown
        VisualTheme.FOREST -> Color(0xFF1A3D1A)           // Dark green
    }
}

/**
 * Get Compose Color for a specific tetromino type from ThemeConfig
 */
fun ThemeConfig.getTetrominoComposeColor(type: TetrominoType): Color {
    return visualTheme.getTetrominoColor(type)
}

/**
 * Get Compose Color for the background from ThemeConfig
 */
fun ThemeConfig.getBackgroundComposeColor(): Color {
    return visualTheme.getBackgroundColor()
}

/**
 * Get Compose Color for grid lines from ThemeConfig
 */
fun ThemeConfig.getGridComposeColor(): Color {
    return visualTheme.getGridColor()
}

/**
 * Get all tetromino colors for the current theme as a map
 */
fun ThemeConfig.getAllTetrominoColors(): Map<TetrominoType, Color> {
    return TetrominoType.entries.associateWith { type ->
        getTetrominoComposeColor(type)
    }
}

/**
 * Get a lighter variant of the tetromino color (for borders or highlights)
 */
fun ThemeConfig.getTetrominoLightColor(type: TetrominoType, factor: Float = 0.3f): Color {
    val baseColor = getTetrominoComposeColor(type)
    return Color(
        red = (baseColor.red + (1f - baseColor.red) * factor).coerceIn(0f, 1f),
        green = (baseColor.green + (1f - baseColor.green) * factor).coerceIn(0f, 1f),
        blue = (baseColor.blue + (1f - baseColor.blue) * factor).coerceIn(0f, 1f),
        alpha = baseColor.alpha
    )
}

/**
 * Get a darker variant of the tetromino color (for shadows or depth)
 */
fun ThemeConfig.getTetrominoDarkColor(type: TetrominoType, factor: Float = 0.3f): Color {
    val baseColor = getTetrominoComposeColor(type)
    return Color(
        red = (baseColor.red * (1f - factor)).coerceIn(0f, 1f),
        green = (baseColor.green * (1f - factor)).coerceIn(0f, 1f),
        blue = (baseColor.blue * (1f - factor)).coerceIn(0f, 1f),
        alpha = baseColor.alpha
    )
}

/**
 * Get ghost piece color (semi-transparent version)
 */
fun ThemeConfig.getGhostPieceColor(type: TetrominoType, alpha: Float = 0.3f): Color {
    val baseColor = getTetrominoComposeColor(type)
    return baseColor.copy(alpha = alpha)
}
