package com.yet.tetris.ui.screens.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.game.Position
import com.yet.tetris.domain.model.game.Tetromino
import com.yet.tetris.domain.model.game.TetrominoType
import com.yet.tetris.domain.model.settings.GameSettings
import com.yet.tetris.domain.model.theme.PieceStyle
import com.yet.tetris.feature.game.GameComponent
import com.yet.tetris.feature.game.PreviewGameComponent
import com.yet.tetris.ui.screens.game.dialog.ErrorDialog
import com.yet.tetris.ui.screens.game.dialog.GameOverDialog
import com.yet.tetris.ui.screens.game.dialog.PauseDialog
import com.yet.tetris.ui.theme.*
import com.yet.tetris.uikit.component.button.FrostedGlassButton
import com.yet.tetris.uikit.theme.TetrisLiteAppTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import tetrislite.composeapp.generated.resources.*

@Composable
fun GameScreen(component: GameComponent) {
    val model by component.model.subscribeAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            model.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            model.gameState != null -> {
                GamePlayingContent(
                    model = model,
                    component = component,
                    onPauseClick = {
                        component.onPause()
                    }
                )

                GameSheet(component, model)
            }
        }
    }
}

@Composable
fun GameSheet(
    component: GameComponent,
    model: GameComponent.Model
) {
    val bottomSheetSlot by component.childSlot.subscribeAsState()

    bottomSheetSlot.child?.instance?.let { child ->
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
                    onDismiss = component::onDismissSheet
                )
            }
        }
    }
}

@Composable
private fun GamePlayingContent(
    model: GameComponent.Model,
    component: GameComponent,
    onPauseClick: () -> Unit
) {
    val gameState = model.gameState ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .navigationBarsPadding()
            .keyboardHandler(
                onMoveLeft = component::onMoveLeft,
                onMoveRight = component::onMoveRight,
                onMoveDown = component::onMoveDown,
                onRotate = component::onRotate,
                onHardDrop = component::onHardDrop
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top section: Stats and Next Piece
        Row(
            modifier = Modifier.fillMaxWidth().statusBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pause button
            FrostedGlassButton(
                onClick = onPauseClick,
                icon = Icons.Default.Pause
            )

            // Game stats
            GameStatsRow(
                score = gameState.score,
                lines = gameState.linesCleared,
                time = model.elapsedTime
            )

            // Next piece
            NextPiecePreview(
                nextPiece = gameState.nextPiece,
                settings = model.settings
            )
        }

        // Game board - centered with border
        GameBoardWithBorder(
            modifier = Modifier
                .weight(1f)
                .widthIn(max = 400.dp)
                .onSizeChanged { size ->
                    component.onBoardSizeChanged(size.height.toFloat())
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { component.onRotate() }
                    )
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { component.onRotate() }
                    )
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { component.onDragStarted() },
                        onDragEnd = component::onDragEnded,
                        onDrag = { change, dragAmount ->
                            change.consume()
                            component.onDragged(dragAmount.x, dragAmount.y)
                        }
                    )
                },
            gameState = gameState,
            settings = model.settings,
            ghostPieceY = model.ghostPieceY
        )
    }
}

@Composable

private fun GameStatsRow(
    score: Long,
    lines: Int,
    time: Long
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(stringResource(Res.string.score), score.toString())
            StatItem(stringResource(Res.string.lines), lines.toString())
            StatItem(stringResource(Res.string.time), formatTime(time))
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable

private fun NextPiecePreview(
    nextPiece: Tetromino,
    settings: GameSettings
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = stringResource(Res.string.next),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Canvas(
            modifier = Modifier.size(60.dp)
        ) {
            val cellSize = size.width / 4
            nextPiece.blocks.forEach { blockPos ->
                drawRect(
                    color = getTetrominoColor(nextPiece.type, settings),
                    topLeft = Offset(
                        (blockPos.x + 1) * cellSize,
                        (blockPos.y + 1) * cellSize
                    ),
                    size = Size(cellSize - 1, cellSize - 1)
                )
            }
        }
    }
}

@Composable
private fun GameBoardWithBorder(
    modifier: Modifier = Modifier,
    gameState: GameState,
    settings: GameSettings,
    ghostPieceY: Int?
) {
    Canvas(
        modifier = modifier
            .aspectRatio(gameState.board.width.toFloat() / gameState.board.height.toFloat())
            .fillMaxWidth()
            .background(settings.themeConfig.getBackgroundComposeColor())
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.outline,
            )
    ) {
        val cellSize = size.width / gameState.board.width

        // Draw locked blocks
        gameState.board.cells.forEach { (pos, type) ->
            if (pos.y >= 0) {
                drawStyledBlock(
                    type = type,
                    settings = settings,
                    topLeft = Offset(pos.x * cellSize, pos.y * cellSize),
                    cellSize = cellSize
                )
            }
        }

        // Draw ghost piece
        gameState.currentPiece?.let { piece ->
            ghostPieceY?.let { landingY ->
                if (landingY > gameState.currentPosition.y) {
                    piece.blocks.forEach { blockPos ->
                        val absolutePos = Position(
                            gameState.currentPosition.x + blockPos.x,
                            landingY + blockPos.y
                        )
                        if (absolutePos.y >= 0 && absolutePos.y < gameState.board.height) {
                            drawStyledBlock(
                                type = piece.type,
                                settings = settings,
                                topLeft = Offset(
                                    absolutePos.x * cellSize,
                                    absolutePos.y * cellSize
                                ),
                                cellSize = cellSize,
                                alpha = 0.3f
                            )
                        }
                    }
                }
            }
        }

        // Draw current piece
        gameState.currentPiece?.let { piece ->
            piece.blocks.forEach { blockPos ->
                val absolutePos = Position(
                    gameState.currentPosition.x + blockPos.x,
                    gameState.currentPosition.y + blockPos.y
                )
                if (absolutePos.y >= 0 && absolutePos.y < gameState.board.height) {
                    drawStyledBlock(
                        type = piece.type,
                        settings = settings,
                        topLeft = Offset(
                            absolutePos.x * cellSize,
                            absolutePos.y * cellSize
                        ),
                        cellSize = cellSize
                    )
                }
            }
        }

        // Draw grid lines
        for (x in 0..gameState.board.width) {
            drawLine(
                color = Color.Gray.copy(alpha = 0.2f),
                start = Offset(x * cellSize, 0f),
                end = Offset(x * cellSize, size.height),
                strokeWidth = 1f
            )
        }
        for (y in 0..gameState.board.height) {
            drawLine(
                color = Color.Gray.copy(alpha = 0.2f),
                start = Offset(0f, y * cellSize),
                end = Offset(size.width, y * cellSize),
                strokeWidth = 1f
            )
        }
    }
}

private fun getTetrominoColor(type: TetrominoType, settings: GameSettings): Color {
    return settings.themeConfig.getTetrominoComposeColor(type)
}

private fun DrawScope.drawStyledBlock(
    type: TetrominoType,
    settings: GameSettings,
    topLeft: Offset,
    cellSize: Float,
    alpha: Float = 1f
) {
    val baseColor = getTetrominoColor(type, settings).copy(alpha = alpha)
    val lightColor = settings.themeConfig.getTetrominoLightColor(type).copy(alpha = alpha)
    val darkColor = settings.themeConfig.getTetrominoDarkColor(type).copy(alpha = alpha)
    val blockSize = Size(cellSize - 1, cellSize - 1)

    when (settings.themeConfig.pieceStyle) {
        PieceStyle.SOLID -> {
            // Simple solid block
            drawRect(
                color = baseColor,
                topLeft = topLeft,
                size = blockSize
            )
        }

        PieceStyle.BORDERED -> {
            // Solid block with border
            drawRect(
                color = baseColor,
                topLeft = topLeft,
                size = blockSize
            )
            // Draw border
            drawRect(
                color = lightColor,
                topLeft = topLeft,
                size = Size(blockSize.width, 2f)
            )
            drawRect(
                color = lightColor,
                topLeft = topLeft,
                size = Size(2f, blockSize.height)
            )
            drawRect(
                color = darkColor,
                topLeft = Offset(topLeft.x, topLeft.y + blockSize.height - 2f),
                size = Size(blockSize.width, 2f)
            )
            drawRect(
                color = darkColor,
                topLeft = Offset(topLeft.x + blockSize.width - 2f, topLeft.y),
                size = Size(2f, blockSize.height)
            )
        }

        PieceStyle.GRADIENT -> {
            // Gradient effect using multiple rectangles
            drawRect(
                color = baseColor,
                topLeft = topLeft,
                size = blockSize
            )
            // Top-left highlight
            drawRect(
                color = lightColor.copy(alpha = alpha * 0.5f),
                topLeft = topLeft,
                size = Size(blockSize.width * 0.5f, blockSize.height * 0.5f)
            )
            // Bottom-right shadow
            drawRect(
                color = darkColor.copy(alpha = alpha * 0.3f),
                topLeft = Offset(
                    topLeft.x + blockSize.width * 0.5f,
                    topLeft.y + blockSize.height * 0.5f
                ),
                size = Size(blockSize.width * 0.5f, blockSize.height * 0.5f)
            )
        }

        PieceStyle.RETRO_PIXEL -> {
            // Pixelated retro style with smaller blocks
            val pixelSize = cellSize / 4f
            for (py in 0..3) {
                for (px in 0..3) {
                    // Create checkerboard pattern
                    val isLight = (px + py) % 2 == 0
                    drawRect(
                        color = if (isLight) baseColor else darkColor,
                        topLeft = Offset(
                            topLeft.x + px * pixelSize,
                            topLeft.y + py * pixelSize
                        ),
                        size = Size(pixelSize - 0.5f, pixelSize - 0.5f)
                    )
                }
            }
        }

        PieceStyle.GLASS -> {
            // Translucent glass effect
            drawRect(
                color = baseColor.copy(alpha = alpha * 0.6f),
                topLeft = topLeft,
                size = blockSize
            )
            // Shine effect on top
            drawRect(
                color = Color.White.copy(alpha = alpha * 0.3f),
                topLeft = topLeft,
                size = Size(blockSize.width, blockSize.height * 0.3f)
            )
            // Border
            drawRect(
                color = lightColor.copy(alpha = alpha * 0.8f),
                topLeft = topLeft,
                size = Size(blockSize.width, 1f)
            )
            drawRect(
                color = lightColor.copy(alpha = alpha * 0.8f),
                topLeft = topLeft,
                size = Size(1f, blockSize.height)
            )
        }
    }
}

private fun formatTime(milliseconds: Long): String {
    val seconds = (milliseconds / 1000) % 60
    val minutes = (milliseconds / 1000) / 60
    return "${minutes}:${seconds.toString().padStart(2, '0')}"
}


@Composable

@Preview
fun GameScreenPreview() {
    TetrisLiteAppTheme {
        GameScreen(PreviewGameComponent())
    }
}
