package com.yet.tetris.ui.view.game

import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.game.Position
import com.yet.tetris.domain.model.game.TetrominoType
import com.yet.tetris.domain.model.theme.PieceStyle
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

        // Clear canvas
        ctx.fillStyle = "#000000".toJsString()
        ctx.fillRect(0.0, 0.0, canvas.width.toDouble(), canvas.height.toDouble())

        // Draw locked blocks
        board.cells.forEach { (pos, type) ->
            if (pos.y >= 0) {
                drawBlock(ctx, pos.x, pos.y, type, cellSize, PieceStyle.SOLID, 1.0)
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
                                PieceStyle.SOLID,
                                0.3
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
                        PieceStyle.SOLID,
                        1.0
                    )
                }
            }
        }

        // Draw grid lines
        ctx.strokeStyle = "rgba(128, 128, 128, 0.2)".toJsString()
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
    alpha: Double
) {
    val color = getTetrominoColor(type)
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
            ctx.fillStyle = lightenColor(color).toJsString()
            ctx.fillRect(offsetX, offsetY, blockSize, 2.0)
            ctx.fillRect(offsetX, offsetY, 2.0, blockSize)

            // Dark border (bottom-right)
            ctx.fillStyle = darkenColor(color).toJsString()
            ctx.fillRect(offsetX, offsetY + blockSize - 2, blockSize, 2.0)
            ctx.fillRect(offsetX + blockSize - 2, offsetY, 2.0, blockSize)
        }

        PieceStyle.GRADIENT -> {
            // Create gradient
            val gradient =
                ctx.createLinearGradient(offsetX, offsetY, offsetX + blockSize, offsetY + blockSize)
            gradient.addColorStop(0.0, lightenColor(color).toJsString())
            gradient.addColorStop(1.0, darkenColor(color).toJsString())
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

private fun getTetrominoColor(type: TetrominoType): String {
    return when (type) {
        TetrominoType.I -> "#00F0F0" // Cyan
        TetrominoType.O -> "#F0F000" // Yellow
        TetrominoType.T -> "#A000F0" // Purple
        TetrominoType.S -> "#00F000" // Green
        TetrominoType.Z -> "#F00000" // Red
        TetrominoType.J -> "#0000F0" // Blue
        TetrominoType.L -> "#F0A000" // Orange
    }
}

private fun lightenColor(color: String): String {
    // Simple lightening by adding white
    return when (color) {
        "#00F0F0" -> "#80F8F8"
        "#F0F000" -> "#F8F880"
        "#A000F0" -> "#D080F8"
        "#00F000" -> "#80F880"
        "#F00000" -> "#F88080"
        "#0000F0" -> "#8080F8"
        "#F0A000" -> "#F8D080"
        else -> color
    }
}

private fun darkenColor(color: String): String {
    // Simple darkening
    return when (color) {
        "#00F0F0" -> "#007878"
        "#F0F000" -> "#787800"
        "#A000F0" -> "#500078"
        "#00F000" -> "#007800"
        "#F00000" -> "#780000"
        "#0000F0" -> "#000078"
        "#F0A000" -> "#785000"
        else -> color
    }
}
