package com.yet.tetris.ui.view.game.rendering

import com.yet.tetris.domain.model.game.TetrominoType
import com.yet.tetris.domain.model.settings.GameSettings
import com.yet.tetris.domain.model.theme.PieceStyle
import web.canvas.CanvasRenderingContext2D

@OptIn(ExperimentalWasmJsInterop::class)
object BlockRenderer {
    fun drawBlock(
        ctx: CanvasRenderingContext2D,
        x: Int,
        y: Int,
        type: TetrominoType,
        cellSize: Double,
        style: PieceStyle,
        alpha: Double,
        settings: GameSettings
    ) {
        val color = ThemeColors.getTetrominoColor(type, settings)
        val lightColor = ThemeColors.getTetrominoLightColor(type, settings)
        val darkColor = ThemeColors.getTetrominoDarkColor(type, settings)
        val blockSize = cellSize - 2
        val offsetX = x * cellSize + 1
        val offsetY = y * cellSize + 1

        // Set alpha
        ctx.globalAlpha = alpha

        when (style) {
            PieceStyle.SOLID -> {
                ctx.fillStyle = color.toJsString()
                ctx.fillRect(offsetX, offsetY, blockSize, blockSize)
            }

            PieceStyle.BORDERED -> {
                // Fill
                ctx.fillStyle = color.toJsString()
                ctx.fillRect(offsetX, offsetY, blockSize, blockSize)

                // Light border (top-left)
                ctx.fillStyle = lightColor.toJsString()
                ctx.fillRect(offsetX, offsetY, blockSize, 2.0)
                ctx.fillRect(offsetX, offsetY, 2.0, blockSize)

                // Dark border (bottom-right)
                ctx.fillStyle = darkColor.toJsString()
                ctx.fillRect(offsetX, offsetY + blockSize - 2, blockSize, 2.0)
                ctx.fillRect(offsetX + blockSize - 2, offsetY, 2.0, blockSize)
            }

            PieceStyle.GRADIENT -> {
                // Create gradient
                val gradient = ctx.createLinearGradient(
                    offsetX,
                    offsetY,
                    offsetX + blockSize,
                    offsetY + blockSize
                )
                gradient.addColorStop(0.0, lightColor.toJsString())
                gradient.addColorStop(1.0, darkColor.toJsString())
                ctx.fillStyle = gradient
                ctx.fillRect(offsetX, offsetY, blockSize, blockSize)
            }

            else -> {
                // Default to solid
                ctx.fillStyle = color.toJsString()
                ctx.fillRect(offsetX, offsetY, blockSize, blockSize)
            }
        }

        // Reset alpha
        ctx.globalAlpha = 1.0
    }
}
