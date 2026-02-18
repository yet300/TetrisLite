package com.yet.tetris.uikit.component.game

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.yet.tetris.domain.model.game.Tetromino
import com.yet.tetris.domain.model.settings.GameSettings
import com.yet.tetris.ui.theme.getTetrominoComposeColor

@Composable
fun NextPieceCanvas(
    nextPiece: Tetromino,
    settings: GameSettings,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val cellSize = size.width / 4 // 4x4 grid max

        val minX = nextPiece.blocks.minOfOrNull { it.x } ?: 0
        val minY = nextPiece.blocks.minOfOrNull { it.y } ?: 0

        val blocksNormalized =
            nextPiece.blocks.map {
                it.copy(x = it.x - minX, y = it.y - minY)
            }

        val widthInBlocks = (blocksNormalized.maxOfOrNull { it.x } ?: 0) + 1
        val heightInBlocks = (blocksNormalized.maxOfOrNull { it.y } ?: 0) + 1

        val offsetX = (size.width - (widthInBlocks * cellSize)) / 2
        val offsetY = (size.height - (heightInBlocks * cellSize)) / 2

        val color = settings.themeConfig.getTetrominoComposeColor(nextPiece.type)

        blocksNormalized.forEach { block ->
            drawRect(
                color = color,
                topLeft =
                    Offset(
                        x = offsetX + block.x * cellSize,
                        y = offsetY + block.y * cellSize,
                    ),
                size = Size(cellSize - 1f, cellSize - 1f),
            )
        }
    }
}
