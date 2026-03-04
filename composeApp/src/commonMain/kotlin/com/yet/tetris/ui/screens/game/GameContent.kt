package com.yet.tetris.ui.screens.game

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.calculatePaneScaffoldDirectiveWithTwoPanesOnMediumWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.yet.tetris.feature.game.GameComponent

internal data class GameInputActions(
    val onPause: () -> Unit,
    val onHold: () -> Unit,
    val onMoveLeft: () -> Unit,
    val onMoveRight: () -> Unit,
    val onMoveDown: () -> Unit,
    val onRotate: () -> Unit,
    val onHardDrop: () -> Unit,
    val onBoardSizeChanged: (Float) -> Unit,
    val onDragStarted: () -> Unit,
    val onDragged: (Float, Float) -> Unit,
    val onDragEnded: () -> Unit,
)

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun GamePlayingContent(
    model: GameComponent.Model,
    actions: GameInputActions,
) {
    val gameState = model.gameState ?: return
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val paneDirective =
        remember(adaptiveInfo) {
            calculatePaneScaffoldDirectiveWithTwoPanesOnMediumWidth(adaptiveInfo)
        }
    val singlePane = remember(paneDirective.maxHorizontalPartitions) { paneDirective.maxHorizontalPartitions <= 1 }

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
                    onHardDrop = actions.onHardDrop,
                    onHold = actions.onHold,
                    onPause = actions.onPause,
                ),
        contentAlignment = Alignment.TopCenter,
    ) {
        val metrics = remember(maxWidth, maxHeight, paneDirective) { resolveLayoutMetrics(maxWidth, maxHeight, paneDirective) }
        val contentMaxWidth =
            remember(paneDirective) {
                paneDirective.defaultPanePreferredWidth * (paneDirective.maxHorizontalPartitions + 2)
            }

        val boundedContentModifier =
            Modifier
                .fillMaxSize()
                .padding(horizontal = metrics.horizontalPadding, vertical = metrics.verticalPadding)
                .widthIn(max = contentMaxWidth)

        if (singlePane) {
            CompactGameLayout(
                modifier = boundedContentModifier,
                gameState = gameState,
                model = model,
                actions = actions,
                metrics = metrics,
            )
        } else {
            CanonicalSupportingPaneGameLayout(
                modifier = boundedContentModifier,
                gameState = gameState,
                model = model,
                actions = actions,
                metrics = metrics,
                paneDirective = paneDirective,
            )
        }
    }
}
