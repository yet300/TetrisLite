package com.yet.tetris.ui.theme

import web.cssom.BackgroundImage

/**
 * Helper extensions for applying theme colors
 */

// For now, keeping the purple gradient for visual appeal
// Can be changed to green theme if desired
fun gradientBackground(): BackgroundImage {
    return AppColors.gradientBackground().unsafeCast<BackgroundImage>()
}

// Alternative: Green gradient matching the theme
fun greenGradientBackground(): BackgroundImage {
    return "linear-gradient(135deg, #006600 0%, #003300 100%)".unsafeCast<BackgroundImage>()
}

// Terminal-style green gradient
fun terminalGradientBackground(): BackgroundImage {
    return "linear-gradient(135deg, #001100 0%, #000000 100%)".unsafeCast<BackgroundImage>()
}
