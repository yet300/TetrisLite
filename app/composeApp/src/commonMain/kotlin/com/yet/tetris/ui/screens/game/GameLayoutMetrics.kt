package com.yet.tetris.ui.screens.game

import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp

internal data class GameLayoutMetrics(
    val horizontalPadding: Dp,
    val verticalPadding: Dp,
    val paneSpacing: Dp,
    val boardMaxWidth: Dp,
    val holdPieceSize: Dp,
    val queuePieceSize: Dp,
    val buttonSize: Dp,
)

internal fun resolveLayoutMetrics(
    availableWidth: Dp,
    availableHeight: Dp,
    paneDirective: PaneScaffoldDirective,
): GameLayoutMetrics {
    if (paneDirective.maxHorizontalPartitions <= 1) {
        val compactHeight =
            paneDirective.maxVerticalPartitions <= 1 ||
                availableHeight <= paneDirective.defaultPanePreferredHeight

        return GameLayoutMetrics(
            horizontalPadding = if (compactHeight) 2.dp else 4.dp,
            verticalPadding = if (compactHeight) 2.dp else 4.dp,
            paneSpacing = if (compactHeight) 4.dp else 5.dp,
            boardMaxWidth = availableWidth,
            holdPieceSize = 24.dp,
            queuePieceSize = 18.dp,
            buttonSize = 40.dp,
        )
    }

    val widthPerPane = availableWidth / paneDirective.maxHorizontalPartitions.coerceAtLeast(1).toFloat()
    val preferredPaneWidth = paneDirective.defaultPanePreferredWidth.value.coerceAtLeast(1f)
    val widthRatio = (widthPerPane.value / preferredPaneWidth).coerceIn(1f, 1.5f)
    val wideFactor = ((widthRatio - 1f) / 0.5f).coerceIn(0f, 1f)

    return GameLayoutMetrics(
        horizontalPadding = lerp(4.dp, 6.dp, wideFactor),
        verticalPadding = lerp(4.dp, 6.dp, wideFactor),
        paneSpacing = lerp(6.dp, 10.dp, wideFactor),
        boardMaxWidth = availableWidth,
        holdPieceSize = lerp(46.dp, 68.dp, wideFactor),
        queuePieceSize = lerp(34.dp, 44.dp, wideFactor),
        buttonSize = lerp(42.dp, 48.dp, wideFactor),
    )
}
