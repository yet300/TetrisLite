package com.yet.tetris.ui.theme

import web.cssom.BackgroundImage

/**
 * Helper extensions for applying theme colors
 */

fun gradientBackground(): BackgroundImage =
    AppColors.gradientBackground().unsafeCast<BackgroundImage>()

// Alternative: Green gradient matching the theme
fun greenGradientBackground(): BackgroundImage =
    "linear-gradient(135deg, #006600 0%, #003300 100%)".unsafeCast<BackgroundImage>()

// Terminal-style green gradient
fun terminalGradientBackground(): BackgroundImage =
    "linear-gradient(135deg, #001100 0%, #000000 100%)".unsafeCast<BackgroundImage>()
