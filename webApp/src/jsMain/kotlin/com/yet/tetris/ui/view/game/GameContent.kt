package com.yet.tetris.ui.view.game

import com.yet.tetris.domain.model.game.Tetromino
import com.yet.tetris.domain.model.game.TetrominoType
import com.yet.tetris.feature.game.GameComponent
import com.yet.tetris.ui.components.Scaffold
import com.yet.tetris.ui.view.game.dialog.ErrorDialog
import com.yet.tetris.ui.view.game.dialog.GameOverDialog
import com.yet.tetris.ui.view.game.dialog.PauseDialog
import com.yet.tetris.ui.view.settings.SettingsSheet
import com.yet.tetris.utils.RProps
import com.yet.tetris.utils.useAsState
import js.objects.unsafeJso
import kotlinx.browser.window
import mui.icons.material.Pause
import mui.material.Box
import mui.material.Container
import mui.material.IconButton
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.sx
import react.FC
import react.Props
import react.dom.html.ReactHTML.canvas
import react.dom.html.ReactHTML.div
import react.useEffect
import react.useEffectOnce
import react.useRef
import web.canvas.CanvasRenderingContext2D
import web.canvas.ID
import web.cssom.AlignItems
import web.cssom.BackdropFilter
import web.cssom.Border
import web.cssom.Color
import web.cssom.Display
import web.cssom.FlexDirection
import web.cssom.JustifyContent
import web.cssom.MaxHeight
import web.cssom.Overflow
import web.cssom.Padding
import web.cssom.Position
import web.cssom.TextTransform
import web.cssom.Width
import web.cssom.integer
import web.cssom.number
import web.cssom.pct
import web.cssom.px
import web.cssom.rem
import web.html.HTMLCanvasElement

val GameContent = FC<RProps<GameComponent>> { props ->
    val model by props.component.model.useAsState()
    val dialogSlot by props.component.childSlot.useAsState()
    val sheetSlot by props.component.sheetSlot.useAsState()

    // Keyboard controls
    useEffectOnce {
        val handleKeyDown = { event: dynamic ->
            when (event.key.toString().lowercase()) {
                "arrowleft", "a" -> {
                    event.preventDefault()
                    props.component.onMoveLeft()
                }

                "arrowright", "d" -> {
                    event.preventDefault()
                    props.component.onMoveRight()
                }

                "arrowdown", "s" -> {
                    event.preventDefault()
                    props.component.onMoveDown()
                }

                "arrowup", "w", " " -> {
                    event.preventDefault()
                    props.component.onRotate()
                }

                "enter" -> {
                    event.preventDefault()
                    props.component.onHardDrop()
                }

                "escape", "p" -> {
                    event.preventDefault()
                    props.component.onPause()
                }

            }
        }

        window.addEventListener("keydown", handleKeyDown)

        val cleanup: () -> Unit = {
            window.removeEventListener("keydown", handleKeyDown)
        }

        cleanup
    }

    Scaffold {
        sx {
            backgroundColor = Color("#000000")
        }

        Container {
            maxWidth = "lg"
            sx {
                display = Display.flex
                flexDirection = FlexDirection.column
                height = 100.pct
                padding = 2.rem
            }

            // Top row: Pause button, Stats, Next piece
            Box {
                sx {
                    display = Display.flex
                    justifyContent = JustifyContent.spaceBetween
                    alignItems = AlignItems.center
                    marginBottom = 2.rem
                }

                // Pause button (frosted glass style)
                IconButton {
                    sx {
                        backgroundColor = Color("rgba(255, 255, 255, 0.1)")
                        backdropFilter = "blur(10px)".unsafeCast<BackdropFilter>()
                        border = "1px solid rgba(255, 255, 255, 0.2)".unsafeCast<Border>()
                        hover {
                            backgroundColor = Color("rgba(255, 255, 255, 0.2)")
                        }
                    }
                    onClick = { props.component.onPause() }
                    Pause()
                }

                // Stats (glass panel)
                Box {
                    sx {
                        display = Display.flex
                        gap = 2.rem
                        padding = Padding(1.rem, 2.rem)
                        backgroundColor = Color("rgba(255, 255, 255, 0.1)")
                        backdropFilter = "blur(10px)".unsafeCast<BackdropFilter>()
                        border = "1px solid rgba(255, 255, 255, 0.2)".unsafeCast<Border>()
                        borderRadius = 1.rem
                    }

                    StatItem {
                        label = "Score"
                        value = model.gameState?.score?.toString() ?: "0"
                    }

                    StatItem {
                        label = "Lines"
                        value = model.gameState?.linesCleared?.toString() ?: "0"
                    }

                    StatItem {
                        label = "Time"
                        value = formatTime(model.elapsedTime)
                    }
                }

                // Next piece preview
                model.gameState?.nextPiece?.let { nextPiece ->
                    NextPiecePreview {
                        piece = nextPiece
                    }
                }
            }

            // Game board - centered
            Box {
                sx {
                    flexGrow = number(1.0)
                    display = Display.flex
                    alignItems = AlignItems.center
                    justifyContent = JustifyContent.center
                }

                model.gameState?.let { gameState ->
                    GameBoard {
                        this.gameState = gameState
                        this.ghostY = model.ghostPieceY
                    }
                }
            }
        }
    }

    // Dialogs
    dialogSlot.child?.instance?.let { dialog ->
        when (dialog) {
            is GameComponent.DialogChild.Pause -> {
                PauseDialog {
                    component = props.component
                }
            }
            is GameComponent.DialogChild.GameOver -> {
                GameOverDialog {
                    component = props.component
                    score = model.finalScore
                    lines = model.finalLinesCleared
                }
            }
            is GameComponent.DialogChild.Error -> {
                ErrorDialog {
                    message = dialog.message
                    onDismiss = { props.component.onDismissDialog() }
                }
            }
        }
    }

    // Sheets
    sheetSlot.child?.instance?.let { sheet ->
        when (sheet) {
            is GameComponent.SheetChild.Settings -> {
                div {
                    style = unsafeJso {
                        position = "fixed".unsafeCast<Position>()
                        top = 0.px
                        left = 0.px
                        right = 0.px
                        bottom = 0.px
                        backgroundColor = "rgba(0, 0, 0, 0.5)".unsafeCast<Color>()
                        display = "flex".unsafeCast<Display>()
                        alignItems = "center".unsafeCast<AlignItems>()
                        justifyContent = "center".unsafeCast<JustifyContent>()
                        zIndex = integer(1300)
                    }
                    onClick = { props.component.onDismissSheet() }

                    div {
                        style = unsafeJso {
                            backgroundColor = Color("white")
                            borderRadius = 16.px
                            maxWidth = 600.px
                            width = "90%".unsafeCast<Width>()
                            maxHeight = "80vh".unsafeCast<MaxHeight>()
                            overflow = "auto".unsafeCast<Overflow>()
                        }
                        onClick = { it.stopPropagation() }

                        SettingsSheet {
                            component = sheet.component
                        }
                    }
                }
            }
        }
    }
}

external interface StatItemProps : Props {
    var label: String
    var value: String
}

val StatItem = FC<StatItemProps> { props ->
    Box {
        sx {
            display = Display.flex
            flexDirection = FlexDirection.column
            alignItems = AlignItems.center
        }

        Typography {
            variant = TypographyVariant.caption
            sx {
                color = Color("rgba(255, 255, 255, 0.7)")
                textTransform = TextTransform.uppercase
                fontSize = 0.75.rem
            }
            +props.label
        }

        Typography {
            variant = TypographyVariant.h6
            sx {
                fontWeight = integer(700)
                color = Color("#39FF14") // Terminal green
            }
            +props.value
        }
    }
}

external interface NextPiecePreviewProps : Props {
    var piece: Tetromino
}

@OptIn(ExperimentalWasmJsInterop::class)
val NextPiecePreview = FC<NextPiecePreviewProps> { props ->
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

        // Draw piece
        val color = getTetrominoColor(props.piece.type)
        ctx.fillStyle = color

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
            gap = 0.5.rem
            padding = 1.rem
            backgroundColor = Color("rgba(255, 255, 255, 0.1)")
            backdropFilter = "blur(10px)".unsafeCast<BackdropFilter>()
            border = "1px solid rgba(255, 255, 255, 0.2)".unsafeCast<Border>()
            borderRadius = 1.rem
        }

        Typography {
            variant = TypographyVariant.caption
            sx {
                color = Color("rgba(255, 255, 255, 0.7)")
                textTransform = TextTransform.uppercase
                fontSize = 0.75.rem
            }
            +"Next"
        }

        canvas {
            ref = canvasRef
            width = 80.toDouble()
            height = 80.toDouble()
            style = unsafeJso {
                display = "block".unsafeCast<Display>()
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    val seconds = (ms / 1000) % 60
    val minutes = ms / 1000 / 60
    return "$minutes:${seconds.toString().padStart(2, '0')}"
}

private fun getTetrominoColor(type: TetrominoType): String {
    return when (type) {
        TetrominoType.I -> "#00F0F0"
        TetrominoType.O -> "#F0F000"
        TetrominoType.T -> "#A000F0"
        TetrominoType.S -> "#00F000"
        TetrominoType.Z -> "#F00000"
        TetrominoType.J -> "#0000F0"
        TetrominoType.L -> "#F0A000"
    }
}
