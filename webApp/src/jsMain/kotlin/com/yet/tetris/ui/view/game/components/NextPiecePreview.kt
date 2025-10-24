package com.yet.tetris.ui.view.game.components

import com.yet.tetris.domain.model.game.Tetromino
import com.yet.tetris.domain.model.game.TetrominoType
import com.yet.tetris.domain.model.settings.GameSettings
import com.yet.tetris.ui.strings.Strings
import js.objects.unsafeJso
import mui.material.Box
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.sx
import react.FC
import react.Props
import react.dom.html.ReactHTML.canvas
import react.useEffect
import react.useRef
import web.canvas.CanvasRenderingContext2D
import web.canvas.ID
import web.cssom.AlignItems
import web.cssom.BackdropFilter
import web.cssom.Border
import web.cssom.Color
import web.cssom.Display
import web.cssom.FlexDirection
import web.cssom.TextTransform
import web.cssom.WhiteSpace
import web.cssom.rem
import web.html.HTMLCanvasElement

external interface NextPiecePreviewProps : Props {
    var piece: Tetromino
    var settings: GameSettings
}

@OptIn(ExperimentalWasmJsInterop::class)
val NextPiecePreview =
    FC<NextPiecePreviewProps> { props ->
        val canvasRef = useRef<HTMLCanvasElement>()

        useEffect(props.piece) {
            val canvas = canvasRef.current ?: return@useEffect
            val ctx = canvas.getContext(CanvasRenderingContext2D.ID) ?: return@useEffect

            val cellSize = 15.0

            // Clear canvas
            ctx.clearRect(0.0, 0.0, canvas.width.toDouble(), canvas.height.toDouble())

            // Calculate piece bounds
            val minX = props.piece.blocks.minOfOrNull { it.x } ?: 0
            val maxX = props.piece.blocks.maxOfOrNull { it.x } ?: 0
            val minY = props.piece.blocks.minOfOrNull { it.y } ?: 0
            val maxY = props.piece.blocks.maxOfOrNull { it.y } ?: 0

            val pieceWidth = (maxX - minX + 1) * cellSize
            val pieceHeight = (maxY - minY + 1) * cellSize

            val offsetX = (canvas.width - pieceWidth) / 2 - (minX * cellSize)
            val offsetY = (canvas.height - pieceHeight) / 2 - (minY * cellSize)

            // Draw piece with theme color
            val color = getTetrominoColorForPreview(props.piece.type, props.settings)
            ctx.fillStyle = color.toJsString()

            props.piece.blocks.forEach { block ->
                val x = block.x * cellSize + offsetX
                val y = block.y * cellSize + offsetY
                ctx.fillRect(x, y, cellSize - 1, cellSize - 1)
            }
        }

        Box {
            sx {
                display = Display.flex
                flexDirection = FlexDirection.column
                alignItems = AlignItems.center
                gap = 0.4.rem
                padding = 0.75.rem
                backgroundColor = Color("rgba(255, 255, 255, 0.1)")
                backdropFilter = "blur(10px)".unsafeCast<BackdropFilter>()
                border = "1px solid rgba(255, 255, 255, 0.2)".unsafeCast<Border>()
                borderRadius = 0.75.rem
            }

            Typography {
                variant = TypographyVariant.caption
                sx {
                    color = Color("rgba(255, 255, 255, 0.7)")
                    textTransform = TextTransform.uppercase
                    fontSize = 0.7.rem
                    whiteSpace = WhiteSpace.nowrap
                }
                +Strings.NEXT
            }

            canvas {
                ref = canvasRef
                width = 70.toDouble()
                height = 70.toDouble()
                style =
                    unsafeJso {
                        display = "block".unsafeCast<Display>()
                    }
            }
        }
    }

private fun getTetrominoColorForPreview(
    type: TetrominoType,
    settings: GameSettings,
): String {
    val color =
        when (settings.themeConfig.visualTheme) {
            com.yet.tetris.domain.model.theme.VisualTheme.CLASSIC ->
                when (type) {
                    TetrominoType.I -> 0x00F0F0
                    TetrominoType.O -> 0xF0F000
                    TetrominoType.T -> 0xA000F0
                    TetrominoType.S -> 0x00F000
                    TetrominoType.Z -> 0xF00000
                    TetrominoType.J -> 0x0000F0
                    TetrominoType.L -> 0xF0A000
                }

            com.yet.tetris.domain.model.theme.VisualTheme.RETRO_GAMEBOY ->
                when (type) {
                    TetrominoType.I -> 0x0F380F
                    TetrominoType.O -> 0x306230
                    TetrominoType.T -> 0x0F380F
                    TetrominoType.S -> 0x306230
                    TetrominoType.Z -> 0x0F380F
                    TetrominoType.J -> 0x306230
                    TetrominoType.L -> 0x0F380F
                }

            com.yet.tetris.domain.model.theme.VisualTheme.RETRO_NES ->
                when (type) {
                    TetrominoType.I -> 0x00D8F8
                    TetrominoType.O -> 0xF8D800
                    TetrominoType.T -> 0xB800F8
                    TetrominoType.S -> 0x00F800
                    TetrominoType.Z -> 0xF80000
                    TetrominoType.J -> 0x0000F8
                    TetrominoType.L -> 0xF87800
                }

            com.yet.tetris.domain.model.theme.VisualTheme.NEON ->
                when (type) {
                    TetrominoType.I -> 0x00FFFF
                    TetrominoType.O -> 0xFFFF00
                    TetrominoType.T -> 0xFF00FF
                    TetrominoType.S -> 0x00FF00
                    TetrominoType.Z -> 0xFF0066
                    TetrominoType.J -> 0x0066FF
                    TetrominoType.L -> 0xFF6600
                }

            com.yet.tetris.domain.model.theme.VisualTheme.PASTEL ->
                when (type) {
                    TetrominoType.I -> 0xB4E7F5
                    TetrominoType.O -> 0xFFF4B4
                    TetrominoType.T -> 0xE5B4F5
                    TetrominoType.S -> 0xB4F5B4
                    TetrominoType.Z -> 0xF5B4B4
                    TetrominoType.J -> 0xB4B4F5
                    TetrominoType.L -> 0xF5D4B4
                }

            com.yet.tetris.domain.model.theme.VisualTheme.MONOCHROME ->
                when (type) {
                    TetrominoType.I -> 0xFFFFFF
                    TetrominoType.O -> 0xE0E0E0
                    TetrominoType.T -> 0xC0C0C0
                    TetrominoType.S -> 0xA0A0A0
                    TetrominoType.Z -> 0x808080
                    TetrominoType.J -> 0x606060
                    TetrominoType.L -> 0x404040
                }

            com.yet.tetris.domain.model.theme.VisualTheme.OCEAN ->
                when (type) {
                    TetrominoType.I -> 0x00CED1
                    TetrominoType.O -> 0x20B2AA
                    TetrominoType.T -> 0x4682B4
                    TetrominoType.S -> 0x5F9EA0
                    TetrominoType.Z -> 0x1E90FF
                    TetrominoType.J -> 0x0000CD
                    TetrominoType.L -> 0x000080
                }

            com.yet.tetris.domain.model.theme.VisualTheme.SUNSET ->
                when (type) {
                    TetrominoType.I -> 0xFF6B6B
                    TetrominoType.O -> 0xFFD93D
                    TetrominoType.T -> 0xFF8C42
                    TetrominoType.S -> 0xFFA07A
                    TetrominoType.Z -> 0xFF69B4
                    TetrominoType.J -> 0xFF4500
                    TetrominoType.L -> 0xFF1493
                }

            com.yet.tetris.domain.model.theme.VisualTheme.FOREST ->
                when (type) {
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
