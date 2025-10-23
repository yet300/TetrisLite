package com.yet.tetris.ui.view.game

import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.game.Position
import com.yet.tetris.domain.model.game.TetrominoType
import com.yet.tetris.domain.model.theme.PieceStyle
import com.yet.tetris.domain.model.theme.VisualTheme
import js.objects.unsafeJso
import mui.material.Box
import mui.system.sx
import react.FC
import react.Props
import react.dom.html.ReactHTML.canvas
import react.useEffect
import react.useRef
import web.canvas.CanvasRenderingContext2D
import web.canvas.ID
import web.cssom.AlignItems
import web.cssom.Border
import web.cssom.BoxShadow
import web.cssom.Color
import web.cssom.Display
import web.cssom.JustifyContent
import web.cssom.MaxHeight
import web.cssom.MaxWidth
import web.cssom.pct
import web.cssom.px
import web.html.HTMLCanvasElement

external interface GameBoardProps : Props {
    var gameState: GameState
    var settings: com.yet.tetris.domain.model.settings.GameSettings
    var ghostY: Int?
    var onDragStarted: (() -> Unit)?
    var onDragged: ((deltaX: Float, deltaY: Float) -> Unit)?
    var onDragEnded: (() -> Unit)?
    var onTap: (() -> Unit)?
}

@OptIn(ExperimentalWasmJsInterop::class)
val GameBoard = FC<GameBoardProps> { props ->
    val canvasRef = useRef<HTMLCanvasElement>()
    val lastPosRef = useRef<dynamic>()
    val didStartDraggingRef = useRef(false)
    val startTimeRef = useRef<Double>()
    val dragThresholdRef = useRef(5.0) // Pixels before considering it a drag
    val totalDragRef = useRef<dynamic>() // Track total drag distance

    // Setup gesture event listeners
    useEffect(Unit) {
        val canvas = canvasRef.current ?: return@useEffect

        val handleStart: (dynamic) -> Unit = { event ->
            event.preventDefault()

            val clientX = if (event.type == "touchstart" && event.touches.length > 0) {
                event.touches[0].clientX.unsafeCast<Double>()
            } else {
                event.clientX.unsafeCast<Double>()
            }

            val clientY = if (event.type == "touchstart" && event.touches.length > 0) {
                event.touches[0].clientY.unsafeCast<Double>()
            } else {
                event.clientY.unsafeCast<Double>()
            }

            startTimeRef.current = js("Date").now().unsafeCast<Double>()
            lastPosRef.current = unsafeJso {
                this.x = clientX
                this.y = clientY
            }
            totalDragRef.current = unsafeJso {
                this.x = 0.0
                this.y = 0.0
            }
            didStartDraggingRef.current = false
        }

        val handleMove: (dynamic) -> Unit = { event ->
            event.preventDefault()
            val lastPos = lastPosRef.current
            if (lastPos != null) {
                val clientX = if (event.type == "touchmove" && event.touches.length > 0) {
                    event.touches[0].clientX.unsafeCast<Double>()
                } else {
                    event.clientX.unsafeCast<Double>()
                }

                val clientY = if (event.type == "touchmove" && event.touches.length > 0) {
                    event.touches[0].clientY.unsafeCast<Double>()
                } else {
                    event.clientY.unsafeCast<Double>()
                }

                val deltaX = clientX - lastPos.x.unsafeCast<Double>()
                val deltaY = clientY - lastPos.y.unsafeCast<Double>()

                // Accumulate total drag
                val total = totalDragRef.current
                if (total != null) {
                    total.x = total.x.unsafeCast<Double>() + kotlin.math.abs(deltaX)
                    total.y = total.y.unsafeCast<Double>() + kotlin.math.abs(deltaY)

                    // Check if we've exceeded threshold
                    if (didStartDraggingRef.current != true) {
                        val totalDist = total.x.unsafeCast<Double>() + total.y.unsafeCast<Double>()
                        if (totalDist > 5.0) {
                            props.onDragStarted?.invoke()
                            didStartDraggingRef.current = true
                        }
                    }
                }

                if (didStartDraggingRef.current == true) {
                    // Increase sensitivity by multiplying deltas
                    props.onDragged?.invoke((deltaX * 1.5).toFloat(), (deltaY * 1.5).toFloat())
                }

                lastPosRef.current = unsafeJso {
                    this.x = clientX
                    this.y = clientY
                }
            }
        }

        val handleEnd: (dynamic) -> Unit = { event ->
            event.preventDefault()
            val lastPos = lastPosRef.current
            if (lastPos != null) {
                val elapsed = js("Date").now().unsafeCast<Double>() - (startTimeRef.current ?: 0.0)
                val total = totalDragRef.current
                val totalDist = if (total != null) {
                    total.x.unsafeCast<Double>() + total.y.unsafeCast<Double>()
                } else {
                    0.0
                }

                if (didStartDraggingRef.current != true && elapsed < 300 && totalDist < 10) {
                    // Quick tap with minimal movement - rotate
                    props.onTap?.invoke()
                } else if (didStartDraggingRef.current == true) {
                    props.onDragEnded?.invoke()
                }
            }
            lastPosRef.current = null
            totalDragRef.current = null
            didStartDraggingRef.current = false
        }

        val handleCancel: (dynamic) -> Unit = { _ ->
            if (didStartDraggingRef.current == true) {
                props.onDragEnded?.invoke()
            }
            lastPosRef.current = null
            totalDragRef.current = null
            didStartDraggingRef.current = false
        }

        // Add event listeners with passive: false
        val options = unsafeJso<dynamic> {
            this.passive = false
        }

        canvas.asDynamic().addEventListener("mousedown", handleStart, options)
        canvas.asDynamic().addEventListener("mousemove", handleMove, options)
        canvas.asDynamic().addEventListener("mouseup", handleEnd, options)
        canvas.asDynamic().addEventListener("mouseleave", handleCancel, options)
        canvas.asDynamic().addEventListener("touchstart", handleStart, options)
        canvas.asDynamic().addEventListener("touchmove", handleMove, options)
        canvas.asDynamic().addEventListener("touchend", handleEnd, options)
        canvas.asDynamic().addEventListener("touchcancel", handleCancel, options)

        val cleanup: () -> Unit = {
            canvas.asDynamic().removeEventListener("mousedown", handleStart)
            canvas.asDynamic().removeEventListener("mousemove", handleMove)
            canvas.asDynamic().removeEventListener("mouseup", handleEnd)
            canvas.asDynamic().removeEventListener("mouseleave", handleCancel)
            canvas.asDynamic().removeEventListener("touchstart", handleStart)
            canvas.asDynamic().removeEventListener("touchmove", handleMove)
            canvas.asDynamic().removeEventListener("touchend", handleEnd)
            canvas.asDynamic().removeEventListener("touchcancel", handleCancel)
        }

        cleanup
    }

    useEffect(props.gameState, props.ghostY) {
        val canvas = canvasRef.current ?: return@useEffect
        val ctx = canvas.getContext(CanvasRenderingContext2D.ID) ?: return@useEffect

        val board = props.gameState.board
        val rows = board.height
        val cols = board.width

        // Set canvas size
        val cellSize = canvas.width.toDouble() / cols
        canvas.height = (rows * cellSize).toInt()

        // Clear canvas with theme background color
        val bgColor = getBackgroundColor(props.settings)
        ctx.fillStyle = bgColor.toJsString()
        ctx.fillRect(0.0, 0.0, canvas.width.toDouble(), canvas.height.toDouble())

        // Draw locked blocks
        board.cells.forEach { (pos, type) ->
            if (pos.y >= 0) {
                drawBlock(
                    ctx,
                    pos.x,
                    pos.y,
                    type,
                    cellSize,
                    props.settings.themeConfig.pieceStyle,
                    1.0,
                    props.settings
                )
            }
        }

        // Draw ghost piece
        props.gameState.currentPiece?.let { piece ->
            props.ghostY?.let { landingY ->
                if (landingY > props.gameState.currentPosition.y) {
                    piece.blocks.forEach { blockPos ->
                        val absolutePos = Position(
                            props.gameState.currentPosition.x + blockPos.x,
                            landingY + blockPos.y
                        )
                        if (absolutePos.y >= 0 && absolutePos.y < rows) {
                            drawBlock(
                                ctx,
                                absolutePos.x,
                                absolutePos.y,
                                piece.type,
                                cellSize,
                                props.settings.themeConfig.pieceStyle,
                                0.3,
                                props.settings
                            )
                        }
                    }
                }
            }
        }

        // Draw current piece
        props.gameState.currentPiece?.let { piece ->
            piece.blocks.forEach { blockPos ->
                val absolutePos = Position(
                    props.gameState.currentPosition.x + blockPos.x,
                    props.gameState.currentPosition.y + blockPos.y
                )
                if (absolutePos.y >= 0 && absolutePos.y < rows) {
                    drawBlock(
                        ctx,
                        absolutePos.x,
                        absolutePos.y,
                        piece.type,
                        cellSize,
                        props.settings.themeConfig.pieceStyle,
                        1.0,
                        props.settings
                    )
                }
            }
        }

        // Draw grid lines with theme color
        val gridColor = getGridColor(props.settings)
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

    Box {
        sx {
            display = Display.flex
            alignItems = AlignItems.center
            justifyContent = JustifyContent.center
            width = 100.pct
            height = 100.pct
        }

        canvas {
            ref = canvasRef
            width = 400.0
            style = unsafeJso {
                maxWidth = "100%".unsafeCast<MaxWidth>()
                maxHeight = "70vh".unsafeCast<MaxHeight>()
                border = "2px solid rgba(255, 255, 255, 0.3)".unsafeCast<Border>()
                borderRadius = 8.px
                backgroundColor = Color("#000000")
                boxShadow = "0 10px 40px rgba(0, 0, 0, 0.5)".unsafeCast<BoxShadow>()
                touchAction = "none".unsafeCast<web.cssom.TouchAction>()
                cursor = "pointer".unsafeCast<web.cssom.Cursor>()
            }
        }
    }
}

@OptIn(ExperimentalWasmJsInterop::class)
private fun drawBlock(
    ctx: CanvasRenderingContext2D,
    x: Int,
    y: Int,
    type: TetrominoType,
    cellSize: Double,
    style: PieceStyle,
    alpha: Double,
    settings: com.yet.tetris.domain.model.settings.GameSettings
) {
    val color = getTetrominoColor(type, settings)
    val lightColor = getTetrominoLightColor(type, settings)
    val darkColor = getTetrominoDarkColor(type, settings)
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
            val gradient =
                ctx.createLinearGradient(offsetX, offsetY, offsetX + blockSize, offsetY + blockSize)
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

private fun getTetrominoColor(
    type: TetrominoType,
    settings: com.yet.tetris.domain.model.settings.GameSettings
): String {
    val color = when (settings.themeConfig.visualTheme) {
        VisualTheme.CLASSIC -> when (type) {
            TetrominoType.I -> 0x00F0F0
            TetrominoType.O -> 0xF0F000
            TetrominoType.T -> 0xA000F0
            TetrominoType.S -> 0x00F000
            TetrominoType.Z -> 0xF00000
            TetrominoType.J -> 0x0000F0
            TetrominoType.L -> 0xF0A000
        }

        VisualTheme.RETRO_GAMEBOY -> when (type) {
            TetrominoType.I -> 0x0F380F
            TetrominoType.O -> 0x306230
            TetrominoType.T -> 0x0F380F
            TetrominoType.S -> 0x306230
            TetrominoType.Z -> 0x0F380F
            TetrominoType.J -> 0x306230
            TetrominoType.L -> 0x0F380F
        }

        VisualTheme.RETRO_NES -> when (type) {
            TetrominoType.I -> 0x00D8F8
            TetrominoType.O -> 0xF8D800
            TetrominoType.T -> 0xB800F8
            TetrominoType.S -> 0x00F800
            TetrominoType.Z -> 0xF80000
            TetrominoType.J -> 0x0000F8
            TetrominoType.L -> 0xF87800
        }

        VisualTheme.NEON -> when (type) {
            TetrominoType.I -> 0x00FFFF
            TetrominoType.O -> 0xFFFF00
            TetrominoType.T -> 0xFF00FF
            TetrominoType.S -> 0x00FF00
            TetrominoType.Z -> 0xFF0066
            TetrominoType.J -> 0x0066FF
            TetrominoType.L -> 0xFF6600
        }

        VisualTheme.PASTEL -> when (type) {
            TetrominoType.I -> 0xB4E7F5
            TetrominoType.O -> 0xFFF4B4
            TetrominoType.T -> 0xE5B4F5
            TetrominoType.S -> 0xB4F5B4
            TetrominoType.Z -> 0xF5B4B4
            TetrominoType.J -> 0xB4B4F5
            TetrominoType.L -> 0xF5D4B4
        }

        VisualTheme.MONOCHROME -> when (type) {
            TetrominoType.I -> 0xFFFFFF
            TetrominoType.O -> 0xE0E0E0
            TetrominoType.T -> 0xC0C0C0
            TetrominoType.S -> 0xA0A0A0
            TetrominoType.Z -> 0x808080
            TetrominoType.J -> 0x606060
            TetrominoType.L -> 0x404040
        }

        VisualTheme.OCEAN -> when (type) {
            TetrominoType.I -> 0x00CED1
            TetrominoType.O -> 0x20B2AA
            TetrominoType.T -> 0x4682B4
            TetrominoType.S -> 0x5F9EA0
            TetrominoType.Z -> 0x1E90FF
            TetrominoType.J -> 0x0000CD
            TetrominoType.L -> 0x000080
        }

        VisualTheme.SUNSET -> when (type) {
            TetrominoType.I -> 0xFF6B6B
            TetrominoType.O -> 0xFFD93D
            TetrominoType.T -> 0xFF8C42
            TetrominoType.S -> 0xFFA07A
            TetrominoType.Z -> 0xFF69B4
            TetrominoType.J -> 0xFF4500
            TetrominoType.L -> 0xFF1493
        }

        VisualTheme.FOREST -> when (type) {
            TetrominoType.I -> 0x228B22
            TetrominoType.O -> 0x32CD32
            TetrominoType.T -> 0x006400
            TetrominoType.S -> 0x90EE90
            TetrominoType.Z -> 0x2E8B57
            TetrominoType.J -> 0x3CB371
            TetrominoType.L -> 0x8FBC8F
        }
    }
    return "#${color.toString(16).padStart(6, '0')}"
}

private fun getTetrominoLightColor(
    type: TetrominoType,
    settings: com.yet.tetris.domain.model.settings.GameSettings
): String {
    val baseColor = getTetrominoColor(type, settings)
    return lightenColorHex(baseColor, 0.3)
}

private fun getTetrominoDarkColor(
    type: TetrominoType,
    settings: com.yet.tetris.domain.model.settings.GameSettings
): String {
    val baseColor = getTetrominoColor(type, settings)
    return darkenColorHex(baseColor, 0.3)
}

private fun lightenColorHex(hex: String, factor: Double): String {
    val color = hex.removePrefix("#").toInt(16)
    val r = ((color shr 16) and 0xFF)
    val g = ((color shr 8) and 0xFF)
    val b = (color and 0xFF)

    val newR = (r + (255 - r) * factor).toInt().coerceIn(0, 255)
    val newG = (g + (255 - g) * factor).toInt().coerceIn(0, 255)
    val newB = (b + (255 - b) * factor).toInt().coerceIn(0, 255)

    return "#${((newR shl 16) or (newG shl 8) or newB).toString(16).padStart(6, '0')}"
}

private fun darkenColorHex(hex: String, factor: Double): String {
    val color = hex.removePrefix("#").toInt(16)
    val r = ((color shr 16) and 0xFF)
    val g = ((color shr 8) and 0xFF)
    val b = (color and 0xFF)

    val newR = (r * (1 - factor)).toInt().coerceIn(0, 255)
    val newG = (g * (1 - factor)).toInt().coerceIn(0, 255)
    val newB = (b * (1 - factor)).toInt().coerceIn(0, 255)

    return "#${((newR shl 16) or (newG shl 8) or newB).toString(16).padStart(6, '0')}"
}

private fun getBackgroundColor(settings: com.yet.tetris.domain.model.settings.GameSettings): String {
    val color = when (settings.themeConfig.visualTheme) {
        VisualTheme.CLASSIC -> 0x000000
        VisualTheme.RETRO_GAMEBOY -> 0x9BBC0F
        VisualTheme.RETRO_NES -> 0x000000
        VisualTheme.NEON -> 0x0A0A0A
        VisualTheme.PASTEL -> 0xF5F5DC
        VisualTheme.MONOCHROME -> 0x000000
        VisualTheme.OCEAN -> 0x001F3F
        VisualTheme.SUNSET -> 0x2C1810
        VisualTheme.FOREST -> 0x0D1F0D
    }
    return "#${color.toString(16).padStart(6, '0')}"
}

private fun getGridColor(settings: com.yet.tetris.domain.model.settings.GameSettings): String {
    val color = when (settings.themeConfig.visualTheme) {
        VisualTheme.CLASSIC -> 0x333333
        VisualTheme.RETRO_GAMEBOY -> 0x8BAC0F
        VisualTheme.RETRO_NES -> 0x404040
        VisualTheme.NEON -> 0x00FFFF
        VisualTheme.PASTEL -> 0xE0E0E0
        VisualTheme.MONOCHROME -> 0x404040
        VisualTheme.OCEAN -> 0x004080
        VisualTheme.SUNSET -> 0x804020
        VisualTheme.FOREST -> 0x1A3D1A
    }
    return "#${color.toString(16).padStart(6, '0')}"
}
