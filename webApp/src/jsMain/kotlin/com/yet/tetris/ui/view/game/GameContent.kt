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
import web.cssom.BoxSizing
import web.cssom.Color
import web.cssom.Display
import web.cssom.FlexDirection
import web.cssom.FlexWrap
import web.cssom.JustifyContent
import web.cssom.MaxHeight
import web.cssom.Overflow
import web.cssom.Padding
import web.cssom.Position
import web.cssom.TextTransform
import web.cssom.WhiteSpace
import web.cssom.Width
import web.cssom.integer
import web.cssom.number
import web.cssom.px
import web.cssom.rem
import web.cssom.vh
import web.html.HTMLCanvasElement

@OptIn(ExperimentalWasmJsInterop::class)
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
            overflow = Overflow.hidden
        }

        Container {
            maxWidth = "lg"
            sx {
                display = Display.flex
                flexDirection = FlexDirection.column
                height = 100.vh
                padding = 0.5.rem
                boxSizing = BoxSizing.borderBox
                overflow = Overflow.hidden
            }

            // Top row: Pause button, Stats, Next piece
            Box {
                sx {
                    display = Display.flex
                    justifyContent = JustifyContent.spaceBetween
                    alignItems = AlignItems.flexStart
                    marginBottom = 0.5.rem
                    gap = 0.5.rem
                    flexWrap = FlexWrap.wrap
                }

                // Pause button (frosted glass style)
                IconButton {
                    sx {
                        backgroundColor = Color("rgba(255, 255, 255, 0.1)")
                        backdropFilter = "blur(10px)".unsafeCast<BackdropFilter>()
                        border = "1px solid rgba(255, 255, 255, 0.2)".unsafeCast<Border>()
                        padding = 0.5.rem
                        minWidth = "auto".unsafeCast<web.cssom.MinWidth>()
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
                        gap = 1.rem
                        padding = Padding(0.5.rem, 1.rem)
                        backgroundColor = Color("rgba(255, 255, 255, 0.1)")
                        backdropFilter = "blur(10px)".unsafeCast<BackdropFilter>()
                        border = "1px solid rgba(255, 255, 255, 0.2)".unsafeCast<Border>()
                        borderRadius = 0.75.rem
                        flexGrow = number(1.0)
                        justifyContent = JustifyContent.spaceAround
                        minWidth = 0.px
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
                        settings = model.settings
                    }
                }
            }

            // Game board - centered and responsive
            Box {
                sx {
                    flexGrow = number(1.0)
                    display = Display.flex
                    alignItems = AlignItems.center
                    justifyContent = JustifyContent.center
                    minHeight = 0.px
                    overflow = Overflow.hidden
                }

                model.gameState?.let { gameState ->
                    GameBoard {
                        this.gameState = gameState
                        this.settings = model.settings
                        this.ghostY = model.ghostPieceY
                        this.onDragStarted = { props.component.onDragStarted() }
                        this.onDragged = { deltaX, deltaY ->
                            props.component.onDragged(deltaX, deltaY)
                        }
                        this.onDragEnded = { props.component.onDragEnded() }
                        this.onTap = { props.component.onRotate() }
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

@OptIn(ExperimentalWasmJsInterop::class)
val StatItem = FC<StatItemProps> { props ->
    Box {
        sx {
            display = Display.flex
            flexDirection = FlexDirection.column
            alignItems = AlignItems.center
            minWidth = 0.px
        }

        Typography {
            variant = TypographyVariant.caption
            sx {
                color = Color("rgba(255, 255, 255, 0.7)")
                textTransform = TextTransform.uppercase
                fontSize = 0.7.rem
                whiteSpace = WhiteSpace.nowrap
            }
            +props.label
        }

        Typography {
            variant = TypographyVariant.h6
            sx {
                fontWeight = integer(700)
                color = Color("#39FF14") // Terminal green
                fontSize = 1.1.rem
                whiteSpace = WhiteSpace.nowrap
            }
            +props.value
        }
    }
}

external interface NextPiecePreviewProps : Props {
    var piece: Tetromino
    var settings: com.yet.tetris.domain.model.settings.GameSettings
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
            +"Next"
        }

        canvas {
            ref = canvasRef
            width = 70.toDouble()
            height = 70.toDouble()
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

private fun getTetrominoColorForPreview(
    type: TetrominoType,
    settings: com.yet.tetris.domain.model.settings.GameSettings
): String {
    val color = when (settings.themeConfig.visualTheme) {
        com.yet.tetris.domain.model.theme.VisualTheme.CLASSIC -> when (type) {
            TetrominoType.I -> 0x00F0F0
            TetrominoType.O -> 0xF0F000
            TetrominoType.T -> 0xA000F0
            TetrominoType.S -> 0x00F000
            TetrominoType.Z -> 0xF00000
            TetrominoType.J -> 0x0000F0
            TetrominoType.L -> 0xF0A000
        }

        com.yet.tetris.domain.model.theme.VisualTheme.RETRO_GAMEBOY -> when (type) {
            TetrominoType.I -> 0x0F380F
            TetrominoType.O -> 0x306230
            TetrominoType.T -> 0x0F380F
            TetrominoType.S -> 0x306230
            TetrominoType.Z -> 0x0F380F
            TetrominoType.J -> 0x306230
            TetrominoType.L -> 0x0F380F
        }

        com.yet.tetris.domain.model.theme.VisualTheme.RETRO_NES -> when (type) {
            TetrominoType.I -> 0x00D8F8
            TetrominoType.O -> 0xF8D800
            TetrominoType.T -> 0xB800F8
            TetrominoType.S -> 0x00F800
            TetrominoType.Z -> 0xF80000
            TetrominoType.J -> 0x0000F8
            TetrominoType.L -> 0xF87800
        }

        com.yet.tetris.domain.model.theme.VisualTheme.NEON -> when (type) {
            TetrominoType.I -> 0x00FFFF
            TetrominoType.O -> 0xFFFF00
            TetrominoType.T -> 0xFF00FF
            TetrominoType.S -> 0x00FF00
            TetrominoType.Z -> 0xFF0066
            TetrominoType.J -> 0x0066FF
            TetrominoType.L -> 0xFF6600
        }

        com.yet.tetris.domain.model.theme.VisualTheme.PASTEL -> when (type) {
            TetrominoType.I -> 0xB4E7F5
            TetrominoType.O -> 0xFFF4B4
            TetrominoType.T -> 0xE5B4F5
            TetrominoType.S -> 0xB4F5B4
            TetrominoType.Z -> 0xF5B4B4
            TetrominoType.J -> 0xB4B4F5
            TetrominoType.L -> 0xF5D4B4
        }

        com.yet.tetris.domain.model.theme.VisualTheme.MONOCHROME -> when (type) {
            TetrominoType.I -> 0xFFFFFF
            TetrominoType.O -> 0xE0E0E0
            TetrominoType.T -> 0xC0C0C0
            TetrominoType.S -> 0xA0A0A0
            TetrominoType.Z -> 0x808080
            TetrominoType.J -> 0x606060
            TetrominoType.L -> 0x404040
        }

        com.yet.tetris.domain.model.theme.VisualTheme.OCEAN -> when (type) {
            TetrominoType.I -> 0x00CED1
            TetrominoType.O -> 0x20B2AA
            TetrominoType.T -> 0x4682B4
            TetrominoType.S -> 0x5F9EA0
            TetrominoType.Z -> 0x1E90FF
            TetrominoType.J -> 0x0000CD
            TetrominoType.L -> 0x000080
        }

        com.yet.tetris.domain.model.theme.VisualTheme.SUNSET -> when (type) {
            TetrominoType.I -> 0xFF6B6B
            TetrominoType.O -> 0xFFD93D
            TetrominoType.T -> 0xFF8C42
            TetrominoType.S -> 0xFFA07A
            TetrominoType.Z -> 0xFF69B4
            TetrominoType.J -> 0xFF4500
            TetrominoType.L -> 0xFF1493
        }

        com.yet.tetris.domain.model.theme.VisualTheme.FOREST -> when (type) {
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
