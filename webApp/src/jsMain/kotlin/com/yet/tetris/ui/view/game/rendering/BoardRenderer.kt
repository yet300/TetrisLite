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
        settings: GameSettings
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
                    settings
                )
            }
        }

        // Draw ghost piece
        gameState.currentPiece?.let { piece ->
            ghostY?.let { landingY ->
                if (landingY > gameState.currentPosition.y) {
                    piece.blocks.forEach { blockPos ->
                        val absolutePos = Position(
                            gameState.currentPosition.x + blockPos.x,
                            landingY + blockPos.y
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
                                settings
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
                if (absolutePos.y >= 0 && absolutePos.y < rows) {
                    BlockRenderer.drawBlock(
                        ctx,
                        absolutePos.x,
                        absolutePos.y,
                        piece.type,
                        cellSize,
                        settings.themeConfig.pieceStyle,
                        1.0,
                        settings
                    )
                }
            }
        }

        // Draw grid lines
        drawGrid(ctx, canvas, rows, cols, cellSize, settings)
    }

    private fun drawGrid(
        ctx: CanvasRenderingContext2D,
        canvas: HTMLCanvasElement,
        rows: Int,
        cols: Int,
        cellSize: Double,
        settings: GameSettings
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
