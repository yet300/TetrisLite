package com.yet.tetris.ui.view.game.components

import com.yet.tetris.domain.model.game.Tetromino
import com.yet.tetris.domain.model.settings.GameSettings
import com.yet.tetris.ui.strings.Strings
import com.yet.tetris.ui.view.game.rendering.BlockRenderer
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
    var piece: Tetromino?
    var settings: GameSettings
    var title: String?
    var canvasSize: Double?
    var showTitle: Boolean?
    var compact: Boolean?
    var chrome: Boolean?
}

@OptIn(ExperimentalWasmJsInterop::class)
val NextPiecePreview =
    FC<NextPiecePreviewProps> { props ->
        val canvasRef = useRef<HTMLCanvasElement>()
        val canvasSize = props.canvasSize ?: 70.0
        val title = props.title ?: Strings.NEXT
        val showTitle = props.showTitle ?: true
        val compact = props.compact ?: false
        val chrome = props.chrome ?: true

        useEffect(props.piece, props.canvasSize, props.settings) {
            val canvas = canvasRef.current ?: return@useEffect
            val ctx = canvas.getContext(CanvasRenderingContext2D.ID) ?: return@useEffect

            // Clear canvas
            ctx.clearRect(0.0, 0.0, canvas.width.toDouble(), canvas.height.toDouble())
            val piece = props.piece ?: return@useEffect
            val cellSize = canvasSize / 4.0

            // Calculate piece bounds
            val minX = piece.blocks.minOfOrNull { it.x } ?: 0
            val maxX = piece.blocks.maxOfOrNull { it.x } ?: 0
            val minY = piece.blocks.minOfOrNull { it.y } ?: 0
            val maxY = piece.blocks.maxOfOrNull { it.y } ?: 0

            val pieceWidth = (maxX - minX + 1) * cellSize
            val pieceHeight = (maxY - minY + 1) * cellSize

            val offsetX = (canvas.width - pieceWidth) / 2 - (minX * cellSize)
            val offsetY = (canvas.height - pieceHeight) / 2 - (minY * cellSize)

            piece.blocks.forEach { block ->
                val x = block.x * cellSize + offsetX
                val y = block.y * cellSize + offsetY
                BlockRenderer.drawBlockAt(
                    ctx = ctx,
                    offsetX = x,
                    offsetY = y,
                    type = piece.type,
                    cellSize = cellSize,
                    style = props.settings.themeConfig.pieceStyle,
                    alpha = 1.0,
                    settings = props.settings,
                )
            }
        }

        Box {
            sx {
                display = Display.flex
                flexDirection = FlexDirection.column
                alignItems = AlignItems.center
                gap = (if (compact) 0.2 else 0.4).rem
                padding = (if (compact) 0.45 else 0.75).rem
                if (chrome) {
                    backgroundColor = Color("rgba(255, 255, 255, 0.1)")
                    backdropFilter = "blur(10px)".unsafeCast<BackdropFilter>()
                    border = "1px solid rgba(255, 255, 255, 0.2)".unsafeCast<Border>()
                    borderRadius = 0.75.rem
                }
            }

            if (showTitle) {
                Typography {
                    variant = TypographyVariant.caption
                    sx {
                        color = Color("rgba(255, 255, 255, 0.7)")
                        textTransform = TextTransform.uppercase
                        fontSize = (if (compact) 0.62 else 0.7).rem
                        whiteSpace = WhiteSpace.nowrap
                    }
                    +title
                }
            }

            if (props.piece != null) {
                canvas {
                    ref = canvasRef
                    width = canvasSize
                    height = canvasSize
                    style =
                        unsafeJso {
                            display = "block".unsafeCast<Display>()
                        }
                }
            } else {
                Typography {
                    variant = TypographyVariant.body2
                    sx {
                        color = Color("rgba(255, 255, 255, 0.7)")
                        fontSize = (if (compact) 0.95 else 1.0).rem
                    }
                    +"—"
                }
            }
        }
    }
