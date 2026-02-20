package com.yet.tetris.ui.screens.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.yet.tetris.domain.model.game.Tetromino
import com.yet.tetris.domain.model.game.TetrominoType
import com.yet.tetris.domain.model.settings.GameSettings
import com.yet.tetris.feature.game.GameComponent
import com.yet.tetris.feature.game.PreviewGameComponent
import com.yet.tetris.ui.screens.game.dialog.ErrorDialog
import com.yet.tetris.ui.screens.game.dialog.GameOverDialog
import com.yet.tetris.ui.screens.game.dialog.PauseDialog
import com.yet.tetris.ui.screens.settings.SettingsSheet
import com.yet.tetris.ui.theme.getTetrominoComposeColor
import com.yet.tetris.uikit.component.button.FrostedGlassButton
import com.yet.tetris.uikit.component.modifier.glassPanel
import com.yet.tetris.uikit.component.sheet.ModalBottomSheet
import com.yet.tetris.uikit.game.TetrisBoard
import com.yet.tetris.uikit.theme.TetrisLiteAppTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import tetrislite.composeapp.generated.resources.Res
import tetrislite.composeapp.generated.resources.lines
import tetrislite.composeapp.generated.resources.next
import tetrislite.composeapp.generated.resources.score
import tetrislite.composeapp.generated.resources.time

@Composable
fun GameScreen(component: GameComponent) {
    val model by component.model.subscribeAsState()
    val juiceOverlayState = rememberJuiceOverlayState()

    LaunchedEffect(model.visualEffectFeed.sequence) {
        model.visualEffectFeed.latest?.let {
            juiceOverlayState.dispatchBurst(it)
            component.onVisualEffectConsumed(model.visualEffectFeed.sequence)
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
    ) {
        when {
            model.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
            }
            model.gameState != null -> {
                Box(
                    modifier =
                        Modifier.graphicsLayer {
                            translationX = juiceOverlayState.shakeOffsetX
                            translationY = juiceOverlayState.shakeOffsetY
                            scaleX = juiceOverlayState.contentScale
                            scaleY = juiceOverlayState.contentScale
                        },
                ) {
                    GamePlayingContent(
                        model = model,
                        component = component,
                        onPauseClick = {
                            component.onPause()
                        },
                    )

                    GameDialog(component, model)
                    GameSheet(component)
                }

                JuiceOverlay(
                    state = juiceOverlayState,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
fun GameDialog(
    component: GameComponent,
    model: GameComponent.Model,
) {
    val dialogSheetSlot by component.childSlot.subscribeAsState()

    dialogSheetSlot.child?.instance?.let { child ->
        when (child) {
            is GameComponent.DialogChild.Pause -> {
                PauseDialog(component = component)
            }

            is GameComponent.DialogChild.GameOver -> {
                GameOverDialog(component = component, model = model)
            }

            is GameComponent.DialogChild.Error -> {
                ErrorDialog(
                    message = child.message,
                    onDismiss = component::onDismissDialog,
                )
            }
        }
    }
}

@Composable
fun GameSheet(component: GameComponent) {
    val dialogSheetSlot by component.sheetSlot.subscribeAsState()

    dialogSheetSlot.child?.instance?.let { child ->
        ModalBottomSheet(
            onDismiss = component::onDismissSheet,
        ) {
            when (child) {
                is GameComponent.SheetChild.Settings -> SettingsSheet(component = child.component)
            }
        }
    }
}

@Composable
private fun GamePlayingContent(
    model: GameComponent.Model,
    component: GameComponent,
    onPauseClick: () -> Unit,
) {
    val gameState = model.gameState ?: return

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .navigationBarsPadding()
                .keyboardHandler(
                    onMoveLeft = component::onMoveLeft,
                    onMoveRight = component::onMoveRight,
                    onMoveDown = component::onMoveDown,
                    onRotate = component::onRotate,
                    onHardDrop = component::onHardDrop,
                    onPause = component::onPause,
                ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Top section: Stats and Next Piece
        Row(
            modifier = Modifier.fillMaxWidth().statusBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Pause button
            FrostedGlassButton(
                onClick = onPauseClick,
                icon = Icons.Default.Pause,
            )

            // Game stats
            GameStatsRow(
                score = gameState.score,
                lines = gameState.linesCleared,
                time = model.elapsedTime,
            )

            // Next piece
            NextPiecePreview(
                nextPiece = gameState.nextPiece,
                settings = model.settings,
            )
        }

        // Game board - centered with border
        TetrisBoard(
            modifier =
                Modifier
                    .weight(1f)
                    .widthIn(max = 400.dp)
                    .aspectRatio(gameState.board.width.toFloat() / gameState.board.height.toFloat())
                    .fillMaxWidth()
                    .onSizeChanged { size ->
                        component.onBoardSizeChanged(size.height.toFloat())
                    }.pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { component.onRotate() },
                        )
                    }.pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { component.onRotate() },
                        )
                    }.pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { component.onDragStarted() },
                            onDragEnd = component::onDragEnded,
                            onDrag = { change, dragAmount ->
                                change.consume()
                                component.onDragged(dragAmount.x, dragAmount.y)
                            },
                        )
                    },
            gameState = gameState,
            settings = model.settings,
            ghostPieceY = model.ghostPieceY,
        )
    }
}

@Composable
private fun GameStatsRow(
    score: Long,
    lines: Long,
    time: Long,
) {
    Row(
        modifier =
            Modifier
                .glassPanel(
                    shape = RoundedCornerShape(16.dp),
                ).padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StatItem(stringResource(Res.string.score), score.toString())
        StatItem(stringResource(Res.string.lines), lines.toString())
        StatItem(stringResource(Res.string.time), formatTime(time))
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun NextPiecePreview(
    nextPiece: Tetromino,
    settings: GameSettings,
) {
    Column(
        modifier =
            Modifier
                .glassPanel(shape = RoundedCornerShape(16.dp))
                .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            modifier =
                Modifier.graphicsLayer {
                    shadowElevation = 2.dp.toPx()
                },
            text = stringResource(Res.string.next),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Canvas(
            modifier = Modifier.size(60.dp),
        ) {
            val cellSize = 12.dp.toPx()

            val minX = nextPiece.blocks.minOfOrNull { it.x } ?: 0
            val maxX = nextPiece.blocks.maxOfOrNull { it.x } ?: 0
            val minY = nextPiece.blocks.minOfOrNull { it.y } ?: 0
            val maxY = nextPiece.blocks.maxOfOrNull { it.y } ?: 0

            val pieceWidth = (maxX - minX + 1) * cellSize
            val pieceHeight = (maxY - minY + 1) * cellSize

            val offsetX = (size.width - pieceWidth) / 2f - (minX * cellSize)
            val offsetY = (size.height - pieceHeight) / 2f - (minY * cellSize)

            nextPiece.blocks.forEach { blockPos ->
                drawRect(
                    color = getTetrominoColor(nextPiece.type, settings),
                    topLeft =
                        Offset(
                            blockPos.x * cellSize + offsetX,
                            blockPos.y * cellSize + offsetY,
                        ),
                    size = Size(cellSize - 1, cellSize - 1),
                )
            }
        }
    }
}

private fun getTetrominoColor(
    type: TetrominoType,
    settings: GameSettings,
): Color = settings.themeConfig.getTetrominoComposeColor(type)

private fun formatTime(milliseconds: Long): String {
    val seconds = (milliseconds / 1000) % 60
    val minutes = (milliseconds / 1000) / 60
    return "$minutes:${seconds.toString().padStart(2, '0')}"
}

@Composable
@Preview
fun GameScreenPreview() {
    TetrisLiteAppTheme {
        GameScreen(PreviewGameComponent())
    }
}
