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
        settings: GameSettings,
    ) {
        drawBlockAt(
            ctx = ctx,
            offsetX = x * cellSize,
            offsetY = y * cellSize,
            type = type,
            cellSize = cellSize,
            style = style,
            alpha = alpha,
            settings = settings,
        )
    }

    fun drawBlockAt(
        ctx: CanvasRenderingContext2D,
        offsetX: Double,
        offsetY: Double,
        type: TetrominoType,
        cellSize: Double,
        style: PieceStyle,
        alpha: Double,
        settings: GameSettings,
    ) {
        val color = ThemeColors.getTetrominoColor(type, settings)
        val lightColor = ThemeColors.getTetrominoLightColor(type, settings)
        val darkColor = ThemeColors.getTetrominoDarkColor(type, settings)
        val inset = kotlin.math.max(1.0, cellSize * 0.06)
        val blockSize = cellSize - inset
        val shadowOffsetY = kotlin.math.max(1.0, cellSize * 0.08)
        val highlightInset = inset

        ctx.save()
        ctx.globalAlpha = alpha

        ctx.fillStyle = "rgba(0, 0, 0, ${0.14 * alpha})".toJsString()
        ctx.fillRect(offsetX, offsetY + shadowOffsetY, blockSize, blockSize)

        when (style) {
            PieceStyle.SOLID -> {
                ctx.fillStyle = color.toJsString()
                ctx.fillRect(offsetX, offsetY, blockSize, blockSize)

                ctx.fillStyle = colorWithAlpha(lightColor, 0.18 * alpha).toJsString()
                ctx.fillRect(offsetX, offsetY, blockSize, blockSize * 0.22)
            }

            PieceStyle.BORDERED -> {
                ctx.fillStyle = color.toJsString()
                ctx.fillRect(offsetX, offsetY, blockSize, blockSize)

                ctx.fillStyle = lightColor.toJsString()
                ctx.fillRect(offsetX, offsetY, blockSize, 2.0)
                ctx.fillRect(offsetX, offsetY, 2.0, blockSize)

                ctx.fillStyle = darkColor.toJsString()
                ctx.fillRect(offsetX, offsetY + blockSize - 2, blockSize, 2.0)
                ctx.fillRect(offsetX + blockSize - 2, offsetY, 2.0, blockSize)

                ctx.fillStyle = "rgba(255, 255, 255, ${0.1 * alpha})".toJsString()
                ctx.fillRect(offsetX + 2.0, offsetY + 2.0, blockSize - 4.0, blockSize * 0.18)
            }

            PieceStyle.GRADIENT -> {
                ctx.fillStyle = color.toJsString()
                ctx.fillRect(offsetX, offsetY, blockSize, blockSize)

                val topGradient =
                    ctx.createLinearGradient(
                        offsetX,
                        offsetY,
                        offsetX + (blockSize * 0.5),
                        offsetY + (blockSize * 0.5),
                    )
                topGradient.addColorStop(0.0, colorWithAlpha(lightColor, 0.5 * alpha))
                topGradient.addColorStop(1.0, colorWithAlpha(lightColor, 0.0))
                ctx.fillStyle = topGradient
                ctx.fillRect(offsetX, offsetY, blockSize * 0.5, blockSize * 0.5)

                val bottomGradient =
                    ctx.createLinearGradient(
                        offsetX + (blockSize * 0.5),
                        offsetY + (blockSize * 0.5),
                        offsetX + blockSize,
                        offsetY + blockSize,
                    )
                bottomGradient.addColorStop(0.0, colorWithAlpha(darkColor, 0.3 * alpha))
                bottomGradient.addColorStop(1.0, colorWithAlpha(darkColor, 0.0))
                ctx.fillStyle = bottomGradient
                ctx.fillRect(offsetX + (blockSize * 0.5), offsetY + (blockSize * 0.5), blockSize * 0.5, blockSize * 0.5)

                ctx.fillStyle = "rgba(255, 255, 255, ${0.12 * alpha})".toJsString()
                ctx.fillRect(
                    offsetX + highlightInset,
                    offsetY + highlightInset,
                    blockSize - (highlightInset * 2.0),
                    blockSize * 0.14,
                )
            }

            PieceStyle.RETRO_PIXEL -> {
                val pixelSize = cellSize / 4.0
                for (py in 0..3) {
                    for (px in 0..3) {
                        val isLight = (px + py) % 2 == 0
                        ctx.fillStyle = if (isLight) color.toJsString() else darkColor.toJsString()
                        ctx.fillRect(
                            offsetX + (px * pixelSize),
                            offsetY + (py * pixelSize),
                            pixelSize - 0.5,
                            pixelSize - 0.5,
                        )
                    }
                }
            }

            PieceStyle.GLASS -> {
                ctx.fillStyle = colorWithAlpha(color, 0.6 * alpha).toJsString()
                ctx.fillRect(offsetX, offsetY, blockSize, blockSize)

                val glassGradient =
                    ctx.createLinearGradient(
                        offsetX,
                        offsetY,
                        offsetX,
                        offsetY + (blockSize * 0.3),
                    )
                glassGradient.addColorStop(0.0, colorWithAlpha("#ffffff", 0.3 * alpha))
                glassGradient.addColorStop(1.0, colorWithAlpha("#ffffff", 0.0))
                ctx.fillStyle = glassGradient
                ctx.fillRect(offsetX, offsetY, blockSize, blockSize * 0.3)

                ctx.fillStyle = colorWithAlpha(lightColor, 0.8 * alpha).toJsString()
                ctx.fillRect(offsetX, offsetY, blockSize, 1.0)
                ctx.fillRect(offsetX, offsetY, 1.0, blockSize)

                ctx.fillStyle = "rgba(255, 255, 255, ${0.12 * alpha})".toJsString()
                ctx.fillRect(
                    offsetX + highlightInset,
                    offsetY + highlightInset,
                    blockSize - (highlightInset * 2.0),
                    blockSize * 0.12,
                )
            }
        }

        ctx.restore()
    }
}
