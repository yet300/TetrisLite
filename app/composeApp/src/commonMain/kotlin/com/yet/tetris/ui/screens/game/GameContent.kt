package com.yet.tetris.ui.screens.game

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirectiveWithTwoPanesOnMediumWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_EXPANDED_LOWER_BOUND
import com.yet.tetris.domain.model.game.RotationDirection
import com.yet.tetris.feature.game.GameComponent

internal data class GameInputActions(
    val onPause: () -> Unit,
    val onHold: () -> Unit,
    val onMoveLeft: () -> Unit,
    val onMoveRight: () -> Unit,
    val onMoveDown: () -> Unit,
    val onRotate: () -> Unit,
    val onRotateClockwise: () -> Unit,
    val onRotateCounterClockwise: () -> Unit,
    val onRotate180: () -> Unit,
    val onHardDrop: () -> Unit,
    val onBoardSizeChanged: (Float) -> Unit,
    val onDragStarted: () -> Unit,
    val onDragged: (Float, Float) -> Unit,
    val onDragEnded: () -> Unit,
    val primaryRotateDirection: RotationDirection,
    val enable180Rotation: Boolean,
)

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun GamePlayingContent(
    model: GameComponent.Model,
    actions: GameInputActions,
    juiceOverlayState: JuiceOverlayState,
) {
    val gameState = model.gameState ?: return
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val paneDirective =
        remember(adaptiveInfo) {
            calculatePaneScaffoldDirectiveWithTwoPanesOnMediumWidth(adaptiveInfo)
        }
    val singlePane = remember(paneDirective.maxHorizontalPartitions) { paneDirective.maxHorizontalPartitions <= 1 }
    val expandedWidth = remember(adaptiveInfo.windowSizeClass) { adaptiveInfo.windowSizeClass.minWidthDp >= WIDTH_DP_EXPANDED_LOWER_BOUND }

    BoxWithConstraints(
        modifier =
            Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .keyboardHandler(
                    onMoveLeft = actions.onMoveLeft,
                    onMoveRight = actions.onMoveRight,
                    onMoveDown = actions.onMoveDown,
                    onRotate = actions.onRotate,
                    onRotateClockwise = actions.onRotateClockwise,
                    onRotateCounterClockwise = actions.onRotateCounterClockwise,
                    onRotate180 = actions.onRotate180,
                    onHardDrop = actions.onHardDrop,
                    onHold = actions.onHold,
                    onPause = actions.onPause,
                    primaryRotateDirection = actions.primaryRotateDirection,
                    enable180Rotation = actions.enable180Rotation,
                ),
        contentAlignment = Alignment.TopCenter,
    ) {
        val metrics = remember(maxWidth, maxHeight, paneDirective) { resolveLayoutMetrics(maxWidth, maxHeight, paneDirective) }
        val contentModifier =
            Modifier
                .fillMaxSize()
                .padding(horizontal = metrics.horizontalPadding, vertical = metrics.verticalPadding)

        if (singlePane) {
            CompactGameLayout(
                modifier = contentModifier,
                gameState = gameState,
                model = model,
                actions = actions,
                metrics = metrics,
                juiceOverlayState = juiceOverlayState,
            )
        } else if (expandedWidth) {
            ExpandedGameLayout(
                modifier = contentModifier,
                gameState = gameState,
                model = model,
                actions = actions,
                metrics = metrics,
                juiceOverlayState = juiceOverlayState,
            )
        } else {
            CanonicalSupportingPaneGameLayout(
                modifier = contentModifier,
                gameState = gameState,
                model = model,
                actions = actions,
                metrics = metrics,
                paneDirective = paneDirective,
                juiceOverlayState = juiceOverlayState,
            )
        }
    }
}
