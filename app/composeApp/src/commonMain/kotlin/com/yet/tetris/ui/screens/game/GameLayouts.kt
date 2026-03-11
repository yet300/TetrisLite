package com.yet.tetris.ui.screens.game

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldDefaults
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldDestinationItem
import androidx.compose.material3.adaptive.layout.calculateThreePaneScaffoldValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.settings.GameSettings
import com.yet.tetris.feature.game.GameComponent
import com.yet.tetris.uikit.component.button.FrostedGlassButton
import com.yet.tetris.uikit.component.modifier.glassPanel
import com.yet.tetris.uikit.game.TetrisBoard

@Composable
internal fun CompactGameLayout(
    modifier: Modifier,
    gameState: GameState,
    model: GameComponent.Model,
    actions: GameInputActions,
    metrics: GameLayoutMetrics,
    juiceOverlayState: JuiceOverlayState,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(metrics.paneSpacing),
    ) {
        CompactHeaderPane(
            gameState = gameState,
            elapsedTime = model.elapsedTime,
            onPause = actions.onPause,
            onHold = actions.onHold,
            buttonSize = metrics.buttonSize,
        )

        GameBoardPane(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            gameState = gameState,
            settings = model.settings,
            ghostPieceY = model.ghostPieceY,
            boardMaxWidth = metrics.boardMaxWidth,
            actions = actions,
            juiceOverlayState = juiceOverlayState,
        )

        CompactBottomPane(
            gameState = gameState,
            elapsedTime = model.elapsedTime,
            settings = model.settings,
            holdPieceSize = metrics.holdPieceSize,
            queuePieceSize = metrics.queuePieceSize,
        )
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun CanonicalSupportingPaneGameLayout(
    modifier: Modifier,
    gameState: GameState,
    model: GameComponent.Model,
    actions: GameInputActions,
    metrics: GameLayoutMetrics,
    paneDirective: PaneScaffoldDirective,
    juiceOverlayState: JuiceOverlayState,
) {
    val paneValue =
        remember(paneDirective.maxHorizontalPartitions, paneDirective.maxVerticalPartitions) {
            calculateThreePaneScaffoldValue(
                maxHorizontalPartitions = paneDirective.maxHorizontalPartitions,
                adaptStrategies = SupportingPaneScaffoldDefaults.adaptStrategies(),
                currentDestination = null as ThreePaneScaffoldDestinationItem<*>?,
                maxVerticalPartitions = paneDirective.maxVerticalPartitions,
            )
        }

    SupportingPaneScaffold(
        modifier = modifier.fillMaxSize(),
        directive = paneDirective,
        value = paneValue,
        mainPane = {
            GameBoardPane(
                modifier = Modifier.fillMaxSize(),
                gameState = gameState,
                settings = model.settings,
                ghostPieceY = model.ghostPieceY,
                boardMaxWidth = metrics.boardMaxWidth,
                actions = actions,
                juiceOverlayState = juiceOverlayState,
                fitHeightFirst = true,
                paneAlignment = Alignment.Center,
            )
        },
        supportingPane = {
            MediumSupportingPane(
                gameState = gameState,
                elapsedTime = model.elapsedTime,
                settings = model.settings,
                onPause = actions.onPause,
                onHold = actions.onHold,
                holdPieceSize = metrics.holdPieceSize,
                queuePieceSize = metrics.queuePieceSize,
                buttonSize = metrics.buttonSize,
                modifier = Modifier.fillMaxSize(),
            )
        },
    )
}

@Composable
internal fun ExpandedGameLayout(
    modifier: Modifier,
    gameState: GameState,
    model: GameComponent.Model,
    actions: GameInputActions,
    metrics: GameLayoutMetrics,
    juiceOverlayState: JuiceOverlayState,
) {
    Row(
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(metrics.paneSpacing),
    ) {
        Column(
            modifier =
                Modifier
                    .weight(0.92f)
                    .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(metrics.paneSpacing),
        ) {
            HoldPreviewPanel(
                holdPiece = gameState.holdPiece,
                settings = model.settings,
                pieceSize = metrics.holdPieceSize,
                modifier = Modifier.fillMaxWidth(),
            )

            GameStatsPanel(
                score = gameState.score,
                lines = gameState.linesCleared,
                level = gameState.level,
                time = model.elapsedTime,
                compact = false,
                includeTime = false,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.weight(1f))

            GameActionButtons(
                onPause = actions.onPause,
                onHold = actions.onHold,
                buttonSize = metrics.buttonSize,
            )
        }

        GameBoardPane(
            modifier =
                Modifier
                    .weight(1.42f)
                    .fillMaxHeight(),
            gameState = gameState,
            settings = model.settings,
            ghostPieceY = model.ghostPieceY,
            boardMaxWidth = metrics.boardMaxWidth,
            actions = actions,
            juiceOverlayState = juiceOverlayState,
            fitHeightFirst = true,
            paneAlignment = Alignment.Center,
        )

        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(metrics.paneSpacing),
        ) {
            NextQueuePanel(
                previewPieces = gameState.previewPieces,
                settings = model.settings,
                pieceSize = metrics.queuePieceSize,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.weight(1f))

            TimeStatPanel(
                time = model.elapsedTime,
                compact = false,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun CompactHeaderPane(
    gameState: GameState,
    elapsedTime: Long,
    onPause: () -> Unit,
    onHold: () -> Unit,
    buttonSize: Dp,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        GameActionButtons(
            onPause = onPause,
            onHold = onHold,
            buttonSize = buttonSize,
        )

        GameStatsPanel(
            score = gameState.score,
            lines = gameState.linesCleared,
            level = gameState.level,
            time = elapsedTime,
            compact = true,
            includeTime = false,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun CompactBottomPane(
    gameState: GameState,
    elapsedTime: Long,
    settings: GameSettings,
    holdPieceSize: Dp,
    queuePieceSize: Dp,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        QueuePreviewCompact(
            holdPiece = gameState.holdPiece,
            previewPieces = gameState.previewPieces,
            settings = settings,
            holdPieceSize = holdPieceSize,
            queuePieceSize = queuePieceSize,
            modifier = Modifier.weight(1f),
        )

        TimeStatPanel(
            time = elapsedTime,
            compact = true,
            modifier = Modifier.width(92.dp),
        )
    }
}

@Composable
private fun MediumSupportingPane(
    gameState: GameState,
    elapsedTime: Long,
    settings: GameSettings,
    onPause: () -> Unit,
    onHold: () -> Unit,
    holdPieceSize: Dp,
    queuePieceSize: Dp,
    buttonSize: Dp,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        GameActionButtons(
            onPause = onPause,
            onHold = onHold,
            buttonSize = buttonSize,
        )

        GameStatsPanel(
            score = gameState.score,
            lines = gameState.linesCleared,
            level = gameState.level,
            time = elapsedTime,
            compact = false,
            includeTime = false,
            modifier = Modifier.fillMaxWidth(),
        )

        QueuePreview(
            holdPiece = gameState.holdPiece,
            previewPieces = gameState.previewPieces,
            settings = settings,
            holdPieceSize = holdPieceSize,
            queuePieceSize = queuePieceSize,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.weight(1f))

        TimeStatPanel(
            time = elapsedTime,
            compact = false,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun GameActionButtons(
    onPause: () -> Unit,
    onHold: () -> Unit,
    buttonSize: Dp,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FrostedGlassButton(
            onClick = onPause,
            modifier = Modifier.size(buttonSize),
            icon = Icons.Default.Pause,
        )
        FrostedGlassButton(
            onClick = onHold,
            modifier = Modifier.size(buttonSize),
            icon = Icons.Default.SwapHoriz,
        )
    }
}

@Composable
private fun GameBoardPane(
    modifier: Modifier,
    gameState: GameState,
    settings: GameSettings,
    ghostPieceY: Int?,
    boardMaxWidth: Dp,
    actions: GameInputActions,
    juiceOverlayState: JuiceOverlayState,
    fitHeightFirst: Boolean = false,
    paneAlignment: Alignment = Alignment.Center,
) {
    val boardAspectRatio =
        remember(gameState.board.width, gameState.board.height) {
            gameState.board.width.toFloat() / gameState.board.height.toFloat()
        }

    val panelModifier =
        if (fitHeightFirst) {
            modifier.aspectRatio(boardAspectRatio, matchHeightConstraintsFirst = true)
        } else {
            modifier
        }

    BoxWithConstraints(
        modifier =
            panelModifier
                .glassPanel(shape = RoundedCornerShape(20.dp))
                .padding(4.dp),
        contentAlignment = paneAlignment,
    ) {
        val widthBoundByHeight = maxHeight * boardAspectRatio
        val boardWidth = minOf(maxWidth, widthBoundByHeight, boardMaxWidth)

        TetrisBoard(
            modifier =
                Modifier
                    .width(boardWidth)
                    .aspectRatio(boardAspectRatio)
                    .onSizeChanged { size ->
                        actions.onBoardSizeChanged(size.height.toFloat())
                    }.pointerInput(actions) {
                        detectTapGestures(
                            onTap = { actions.onRotate() },
                        )
                    }.pointerInput(actions) {
                        detectDragGestures(
                            onDragStart = { actions.onDragStarted() },
                            onDragEnd = actions.onDragEnded,
                            onDragCancel = actions.onDragEnded,
                            onDrag = { change, dragAmount ->
                                change.consume()
                                actions.onDragged(dragAmount.x, dragAmount.y)
                            },
                        )
                    },
            gameState = gameState,
            settings = settings,
            ghostPieceY = ghostPieceY,
            lineSweeps = juiceOverlayState.activeLineSweeps,
            lockGlows = juiceOverlayState.activeLockGlows,
            effectTimeMillis = juiceOverlayState.frameTimeMillis,
        )
    }
}
