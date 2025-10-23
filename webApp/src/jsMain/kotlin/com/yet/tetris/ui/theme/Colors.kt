package com.yet.tetris.ui.theme

import web.cssom.Color

/**
 * Color palette matching the Compose theme
 * Based on core/uikit/src/commonMain/kotlin/com/yet/tetris/uikit/theme/Theme.kt
 */
object AppColors {
    // Dark theme colors (terminal-like green on black)
    object Dark {
        val primary = Color("#39FF14") // Bright green (terminal-like)
        val onPrimary = Color("#000000")
        val secondary = Color("#2ECB10") // Darker green
        val onSecondary = Color("#000000")
        val background = Color("#000000")
        val onBackground = Color("#39FF14") // Green on black
        val surface = Color("#111111") // Very dark gray
        val onSurface = Color("#39FF14") // Green text
        val error = Color("#FF5555") // Red for errors
        val onError = Color("#000000")
    }

    // Light theme colors
    object Light {
        val primary = Color("#008000") // Dark green
        val onPrimary = Color("#FFFFFF")
        val secondary = Color("#006600") // Even darker green
        val onSecondary = Color("#FFFFFF")
        val background = Color("#FFFFFF")
        val onBackground = Color("#008000") // Dark green on white
        val surface = Color("#F8F8F8") // Very light gray
        val onSurface = Color("#008000") // Dark green text
        val error = Color("#CC0000") // Dark red for errors
        val onError = Color("#FFFFFF")
    }

    // Gradient colors for backgrounds (keeping the purple gradient for now)
    val gradientStart = Color("rgb(102, 126, 234)")
    val gradientEnd = Color("rgb(118, 75, 162)")

    // Helper function to create gradient background
    fun gradientBackground() =
        "linear-gradient(135deg, rgb(102, 126, 234) 0%, rgb(118, 75, 162) 100%)"
}
