package com.yet.tetris.ui.view.game.rendering

import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.game.Position
import com.yet.tetris.domain.model.settings.GameSettings
import web.canvas.CanvasRenderingContext2D
import web.html.HTMLCanvasElement

@OptIn(ExperimentalWasmJsInterop::class)
object BoardRenderer {
    fun render(
        canvas: HTMLCanvasElement,
        ctx: CanvasRenderingContext2D,
        gameState: GameState,
        ghostY: Int?,
        settings: GameSettings,
        lineSweeps: List<WebLineSweepEffect> = emptyList(),
        lockGlows: List<WebLockGlowEffect> = emptyList(),
        effectTimeMs: Double = 0.0,
    ) {
        val board = gameState.board
        val rows = board.height
        val cols = board.width

        // Set canvas size
        val cellSize = canvas.width.toDouble() / cols
        canvas.height = (rows * cellSize).toInt()

        // Clear canvas with theme background color
        val bgColor = ThemeColors.getBackgroundColor(settings)
        ctx.fillStyle = bgColor.toJsString()
        ctx.fillRect(0.0, 0.0, canvas.width.toDouble(), canvas.height.toDouble())

        // Draw locked blocks
        board.cells.forEach { (pos, type) ->
            if (pos.y >= 0) {
                BlockRenderer.drawBlock(
                    ctx,
                    pos.x,
                    pos.y,
                    type,
                    cellSize,
                    settings.themeConfig.pieceStyle,
                    1.0,
                    settings,
                )
            }
        }

        // Draw ghost piece
        gameState.currentPiece?.let { piece ->
            ghostY?.let { landingY ->
                if (landingY > gameState.currentPosition.y) {
                    piece.blocks.forEach { blockPos ->
                        val absolutePos =
                            Position(
                                gameState.currentPosition.x + blockPos.x,
                                landingY + blockPos.y,
                            )
                        if (absolutePos.y >= 0 && absolutePos.y < rows) {
                            BlockRenderer.drawBlock(
                                ctx,
                                absolutePos.x,
                                absolutePos.y,
                                piece.type,
                                cellSize,
                                settings.themeConfig.pieceStyle,
                                0.3,
                                settings,
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
                if (absolutePos.y >= 0 && absolutePos.y < rows) {
                    BlockRenderer.drawBlock(
                        ctx,
                        absolutePos.x,
                        absolutePos.y,
                        piece.type,
                        cellSize,
                        settings.themeConfig.pieceStyle,
                        1.0,
                        settings,
                    )
                }
            }
        }

        drawLockGlows(
            ctx = ctx,
            lockGlows = lockGlows,
            effectTimeMs = effectTimeMs,
            cellSize = cellSize,
        )
        drawLineSweeps(
            ctx = ctx,
            lineSweeps = lineSweeps,
            effectTimeMs = effectTimeMs,
            cellSize = cellSize,
            boardWidthPx = cols * cellSize,
        )

        // Draw grid lines
        drawGrid(ctx, canvas, rows, cols, cellSize, settings)
    }

    private fun drawLineSweeps(
        ctx: CanvasRenderingContext2D,
        lineSweeps: List<WebLineSweepEffect>,
        effectTimeMs: Double,
        cellSize: Double,
        boardWidthPx: Double,
    ) {
        if (lineSweeps.isEmpty()) return

        val sweepWidth = kotlin.math.max(boardWidthPx * 0.42, cellSize * 3.2)

        lineSweeps.forEach { effect ->
            val progress = ((effectTimeMs - effect.createdAtMs) / effect.durationMs).coerceIn(0.0, 1.0)
            if (progress >= 1.0) return@forEach

            val fillAlpha = ((1.0 - progress) * 0.14 * effect.opacityBoost).coerceIn(0.0, 0.4)
            val sweepAlpha = ((1.0 - progress) * 0.7 * effect.opacityBoost).coerceIn(0.0, 0.95)
            val sweepLeft = (-sweepWidth) + ((boardWidthPx + sweepWidth) * progress)
            val sweepHeight = kotlin.math.max(cellSize * 0.88, cellSize * 0.72)

            effect.clearedRows.forEach { row ->
                val top = row * cellSize
                ctx.fillStyle = colorWithAlpha(effect.fillColor, fillAlpha)
                ctx.fillRect(0.0, top, boardWidthPx, cellSize)

                val gradient =
                    ctx.createLinearGradient(
                        sweepLeft,
                        top,
                        sweepLeft + sweepWidth,
                        top,
                    )
                gradient.addColorStop(0.0, colorWithAlpha(effect.primaryColor, 0.0))
                gradient.addColorStop(0.32, colorWithAlpha(effect.primaryColor, sweepAlpha * 0.55))
                gradient.addColorStop(0.55, colorWithAlpha(effect.secondaryColor, sweepAlpha))
                gradient.addColorStop(1.0, colorWithAlpha(effect.secondaryColor, 0.0))
                ctx.fillStyle = gradient
                ctx.fillRect(sweepLeft, top + ((cellSize - sweepHeight) * 0.5), sweepWidth, sweepHeight)
            }
        }
    }

    private fun drawLockGlows(
        ctx: CanvasRenderingContext2D,
        lockGlows: List<WebLockGlowEffect>,
        effectTimeMs: Double,
        cellSize: Double,
    ) {
        if (lockGlows.isEmpty()) return

        lockGlows.forEach { effect ->
            if (effect.cells.isEmpty()) return@forEach
            val progress = ((effectTimeMs - effect.createdAtMs) / effect.durationMs).coerceIn(0.0, 1.0)
            if (progress >= 1.0) return@forEach

            val minX = effect.cells.minOf { it.x }
            val maxX = effect.cells.maxOf { it.x }
            val minY = effect.cells.minOf { it.y }
            val maxY = effect.cells.maxOf { it.y }
            val inset = cellSize * 0.18
            val x = (minX * cellSize) - inset
            val y = (minY * cellSize) - inset
            val width = ((maxX - minX + 1) * cellSize) + (inset * 2.0)
            val height = ((maxY - minY + 1) * cellSize) + (inset * 2.0)
            val centerX = x + (width * 0.5)
            val centerY = y + (height * 0.5)
            val radius = kotlin.math.max(width, height) * (0.75 + ((1.0 - progress) * 0.18))
            val alpha = ((1.0 - progress) * 0.34 * effect.opacityBoost).coerceIn(0.0, 0.6)
            val cornerRadius = cellSize * effect.cornerRadiusFactor

            val gradient = ctx.createRadialGradient(centerX, centerY, 0.0, centerX, centerY, radius)
            gradient.addColorStop(0.0, colorWithAlpha(effect.secondaryColor, alpha))
            gradient.addColorStop(0.45, colorWithAlpha(effect.primaryColor, alpha * 0.52))
            gradient.addColorStop(1.0, colorWithAlpha(effect.primaryColor, 0.0))

            ctx.save()
            ctx.fillStyle = gradient
            if (cornerRadius <= 0.0) {
                ctx.fillRect(x, y, width, height)
            } else {
                roundRect(
                    ctx = ctx,
                    x = x,
                    y = y,
                    width = width,
                    height = height,
                    radius = cornerRadius,
                )
                ctx.fill()
            }
            ctx.restore()
        }
    }

    private fun roundRect(
        ctx: CanvasRenderingContext2D,
        x: Double,
        y: Double,
        width: Double,
        height: Double,
        radius: Double,
    ) {
        val clampedRadius = radius.coerceAtMost(kotlin.math.min(width, height) * 0.5)
        ctx.beginPath()
        ctx.moveTo(x + clampedRadius, y)
        ctx.lineTo(x + width - clampedRadius, y)
        ctx.quadraticCurveTo(x + width, y, x + width, y + clampedRadius)
        ctx.lineTo(x + width, y + height - clampedRadius)
        ctx.quadraticCurveTo(x + width, y + height, x + width - clampedRadius, y + height)
        ctx.lineTo(x + clampedRadius, y + height)
        ctx.quadraticCurveTo(x, y + height, x, y + height - clampedRadius)
        ctx.lineTo(x, y + clampedRadius)
        ctx.quadraticCurveTo(x, y, x + clampedRadius, y)
        ctx.closePath()
    }

    private fun drawGrid(
        ctx: CanvasRenderingContext2D,
        canvas: HTMLCanvasElement,
        rows: Int,
        cols: Int,
        cellSize: Double,
        settings: GameSettings,
    ) {
        val gridColor = ThemeColors.getGridColor(settings)
        ctx.strokeStyle = gridColor.toJsString()
        ctx.lineWidth = 1.0

        // Vertical lines
        for (x in 0..cols) {
            ctx.beginPath()
            ctx.moveTo(x * cellSize, 0.0)
            ctx.lineTo(x * cellSize, canvas.height.toDouble())
            ctx.stroke()
        }

        // Horizontal lines
        for (y in 0..rows) {
            ctx.beginPath()
            ctx.moveTo(0.0, y * cellSize)
            ctx.lineTo(canvas.width.toDouble(), y * cellSize)
            ctx.stroke()
        }
    }
}
