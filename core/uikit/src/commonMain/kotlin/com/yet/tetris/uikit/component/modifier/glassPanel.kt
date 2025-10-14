package com.yet.tetris.uikit.component.modifier

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A custom modifier that replicates the glossy "glass panel" style from SwiftUI.
 * It applies a gradient background, a gradient border, a clip, and a shadow.
 *
 * This modifier is fully cross-platform and does not rely on blur effects.
 *
 * @param shape The shape of the panel.
 * @param shadowElevation The elevation for the shadow effect.
 */
fun Modifier.glassPanel(
    shape: Shape,
    shadowElevation: Dp = 10.dp
): Modifier = composed {
    val isDarkTheme = isSystemInDarkTheme()

    this
        .takeIf { isDarkTheme }
        ?.shadow(
            elevation = shadowElevation,
            shape = shape,
            ambientColor = Color.Black.copy(alpha = 0.2f),
            spotColor = Color.Black.copy(alpha = 0.2f)
        )
        ?.border(
            width = 1.5.dp,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.4f),
                    Color.White.copy(alpha = 0.2f)
                )
            ),
            shape = shape
        )
        ?.background(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.25f),
                    Color.White.copy(alpha = 0.05f)
                )
            ),
            shape = shape
        )
        ?.clip(shape) ?: Modifier
}


/**
 * A convenience overload of [glassPanel] for rounded rectangles.
 * This function is analogous to the SwiftUI version that takes a `cornerRadius`.
 *
 * @param cornerRadius The corner radius for the panel.
 * @param shadowElevation The elevation for the shadow effect.
 */
fun Modifier.glassPanel(
    cornerRadius: Dp,
    shadowElevation: Dp = 10.dp
): Modifier {
    val shape = RoundedCornerShape(cornerRadius)
    return this.glassPanel(shape = shape, shadowElevation = shadowElevation)
}