package com.yet.tetris.uikit.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.game.Position
import com.yet.tetris.domain.model.game.TetrominoType
import com.yet.tetris.domain.model.settings.GameSettings
import com.yet.tetris.domain.model.theme.PieceStyle
import com.yet.tetris.ui.theme.getBackgroundComposeColor
import com.yet.tetris.ui.theme.getGridComposeColor
import com.yet.tetris.ui.theme.getTetrominoComposeColor
import com.yet.tetris.ui.theme.getTetrominoDarkColor
import com.yet.tetris.ui.theme.getTetrominoLightColor
import kotlin.math.max

@Composable
fun TetrisBoard(
    modifier: Modifier = Modifier,
    gameState: GameState,
    settings: GameSettings,
    ghostPieceY: Int?,
    borderWidth: Dp = 2.dp,
) {
    Canvas(
        modifier =
            modifier
                .border(
                    width = borderWidth,
                    color = settings.themeConfig.getGridComposeColor().copy(alpha = 0.45f),
                ),
    ) {
        val cellSize = size.width / gameState.board.width
        drawBoardSurface(settings = settings, cellSize = cellSize)

        // Draw locked blocks
        gameState.board.cells.forEach { (pos, type) ->
            if (pos.y >= 0) {
                drawStyledBlock(
                    type = type,
                    settings = settings,
                    topLeft = Offset(pos.x * cellSize, pos.y * cellSize),
                    cellSize = cellSize,
                )
            }
        }

        // Draw ghost piece
        gameState.currentPiece?.let { piece ->
            ghostPieceY?.let { landingY ->
                if (landingY > gameState.currentPosition.y) {
                    piece.blocks.forEach { blockPos ->
                        val absolutePos =
                            Position(
                                gameState.currentPosition.x + blockPos.x,
                                landingY + blockPos.y,
                            )
                        if (absolutePos.y >= 0 && absolutePos.y < gameState.board.height) {
                            drawStyledBlock(
                                type = piece.type,
                                settings = settings,
                                topLeft =
                                    Offset(
                                        absolutePos.x * cellSize,
                                        absolutePos.y * cellSize,
                                    ),
                                cellSize = cellSize,
                                alpha = 0.3f,
                            )
                        }
                    }
                }
            }
        }

        // Draw current piece
        gameState.currentPiece?.let { piece ->
            piece.blocks.forEach { blockPos ->
                val absolutePos =
                    Position(
                        gameState.currentPosition.x + blockPos.x,
                        gameState.currentPosition.y + blockPos.y,
                    )
                if (absolutePos.y >= 0 && absolutePos.y < gameState.board.height) {
                    drawStyledBlock(
                        type = piece.type,
                        settings = settings,
                        topLeft =
                            Offset(
                                absolutePos.x * cellSize,
                                absolutePos.y * cellSize,
                            ),
                        cellSize = cellSize,
                    )
                }
            }
        }

        drawBoardGrid(
            width = gameState.board.width,
            height = gameState.board.height,
            cellSize = cellSize,
            settings = settings,
        )
    }
}

private fun DrawScope.drawBoardSurface(
    settings: GameSettings,
    cellSize: Float,
) {
    val background = settings.themeConfig.getBackgroundComposeColor()
    val rimLight = Color.White.copy(alpha = 0.08f)
    val shadow = Color.Black.copy(alpha = 0.22f)

    drawRect(color = background)

    for (row in 0 until max(1, (size.height / cellSize).toInt())) {
        for (column in 0 until max(1, (size.width / cellSize).toInt())) {
            val alpha =
                if ((row + column) % 2 == 0) {
                    0.035f
                } else {
                    0.018f
                }
            drawRect(
                color = Color.White.copy(alpha = alpha),
                topLeft = Offset(column * cellSize, row * cellSize),
                size = Size(cellSize, cellSize),
            )
        }
    }

    drawRect(
        brush =
            Brush.verticalGradient(
                colors =
                    listOf(
                        rimLight,
                        Color.Transparent,
                        shadow,
                    ),
            ),
        size = size,
    )

    drawRect(
        brush =
            Brush.horizontalGradient(
                colors =
                    listOf(
                        Color.White.copy(alpha = 0.035f),
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.12f),
                    ),
            ),
        size = size,
    )

    drawRect(
        color = Color.White.copy(alpha = 0.06f),
        size = Size(size.width, max(2f, cellSize * 0.2f)),
    )
}

private fun DrawScope.drawBoardGrid(
    width: Int,
    height: Int,
    cellSize: Float,
    settings: GameSettings,
) {
    val gridColor = settings.themeConfig.getGridComposeColor()

    for (x in 0..width) {
        drawLine(
            color = gridColor.copy(alpha = if (x == 0 || x == width) 0.28f else 0.16f),
            start = Offset(x * cellSize, 0f),
            end = Offset(x * cellSize, size.height),
            strokeWidth = 1f,
        )
    }
    for (y in 0..height) {
        drawLine(
            color = gridColor.copy(alpha = if (y == 0 || y == height) 0.28f else 0.16f),
            start = Offset(0f, y * cellSize),
            end = Offset(size.width, y * cellSize),
            strokeWidth = 1f,
        )
    }
}

fun DrawScope.drawStyledBlock(
    type: TetrominoType,
    settings: GameSettings,
    topLeft: Offset,
    cellSize: Float,
    alpha: Float = 1f,
) {
    val baseColor = settings.themeConfig.getTetrominoComposeColor(type).copy(alpha = alpha)
    val lightColor = settings.themeConfig.getTetrominoLightColor(type).copy(alpha = alpha)
    val darkColor = settings.themeConfig.getTetrominoDarkColor(type).copy(alpha = alpha)
    val inset = max(1f, cellSize * 0.06f)
    val blockSize = Size(cellSize - inset, cellSize - inset)
    val outerShadowTopLeft = Offset(topLeft.x, topLeft.y + max(1f, cellSize * 0.08f))

    drawRect(
        color = Color.Black.copy(alpha = alpha * 0.14f),
        topLeft = outerShadowTopLeft,
        size = blockSize,
    )

    when (settings.themeConfig.pieceStyle) {
        PieceStyle.SOLID -> {
            drawRect(
                color = baseColor,
                topLeft = topLeft,
                size = blockSize,
            )
            drawRect(
                color = lightColor.copy(alpha = alpha * 0.18f),
                topLeft = topLeft,
                size = Size(blockSize.width, blockSize.height * 0.22f),
            )
        }

        PieceStyle.BORDERED -> {
            drawRect(
                color = baseColor,
                topLeft = topLeft,
                size = blockSize,
            )
            drawRect(
                color = lightColor,
                topLeft = topLeft,
                size = Size(blockSize.width, 2f),
            )
            drawRect(
                color = lightColor,
                topLeft = topLeft,
                size = Size(2f, blockSize.height),
            )
            drawRect(
                color = darkColor,
                topLeft = Offset(topLeft.x, topLeft.y + blockSize.height - 2f),
                size = Size(blockSize.width, 2f),
            )
            drawRect(
                color = darkColor,
                topLeft = Offset(topLeft.x + blockSize.width - 2f, topLeft.y),
                size = Size(2f, blockSize.height),
            )
            drawRect(
                color = Color.White.copy(alpha = alpha * 0.1f),
                topLeft = Offset(topLeft.x + 2f, topLeft.y + 2f),
                size = Size(blockSize.width - 4f, blockSize.height * 0.18f),
            )
        }

        PieceStyle.GRADIENT -> {
            drawRect(
                color = baseColor,
                topLeft = topLeft,
                size = blockSize,
            )
            drawRect(
                color = lightColor.copy(alpha = alpha * 0.5f),
                topLeft = topLeft,
                size = Size(blockSize.width * 0.5f, blockSize.height * 0.5f),
            )
            drawRect(
                color = darkColor.copy(alpha = alpha * 0.3f),
                topLeft =
                    Offset(
                        topLeft.x + blockSize.width * 0.5f,
                        topLeft.y + blockSize.height * 0.5f,
                    ),
                size = Size(blockSize.width * 0.5f, blockSize.height * 0.5f),
            )
            drawRect(
                color = Color.White.copy(alpha = alpha * 0.12f),
                topLeft = Offset(topLeft.x + inset, topLeft.y + inset),
                size = Size(blockSize.width - (inset * 2), blockSize.height * 0.14f),
            )
        }

        PieceStyle.RETRO_PIXEL -> {
            val pixelSize = cellSize / 4f
            for (py in 0..3) {
                for (px in 0..3) {
                    val isLight = (px + py) % 2 == 0
                    drawRect(
                        color = if (isLight) baseColor else darkColor,
                        topLeft =
                            Offset(
                                topLeft.x + px * pixelSize,
                                topLeft.y + py * pixelSize,
                            ),
                        size = Size(pixelSize - 0.5f, pixelSize - 0.5f),
                    )
                }
            }
        }

        PieceStyle.GLASS -> {
            drawRect(
                color = baseColor.copy(alpha = alpha * 0.6f),
                topLeft = topLeft,
                size = blockSize,
            )
            drawRect(
                color = Color.White.copy(alpha = alpha * 0.3f),
                topLeft = topLeft,
                size = Size(blockSize.width, blockSize.height * 0.3f),
            )
            drawRect(
                color = lightColor.copy(alpha = alpha * 0.8f),
                topLeft = topLeft,
                size = Size(blockSize.width, 1f),
            )
            drawRect(
                color = lightColor.copy(alpha = alpha * 0.8f),
                topLeft = topLeft,
                size = Size(1f, blockSize.height),
            )
            drawRect(
                color = Color.White.copy(alpha = alpha * 0.12f),
                topLeft = Offset(topLeft.x + inset, topLeft.y + inset),
                size = Size(blockSize.width - (inset * 2), blockSize.height * 0.12f),
            )
        }
    }
}
