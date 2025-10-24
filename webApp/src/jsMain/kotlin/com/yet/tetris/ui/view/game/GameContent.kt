package com.yet.tetris.ui.view.game

import com.yet.tetris.feature.game.GameComponent
import com.yet.tetris.ui.components.Scaffold
import com.yet.tetris.ui.strings.Strings
import com.yet.tetris.ui.view.game.components.NextPiecePreview
import com.yet.tetris.ui.view.game.components.StatItem
import com.yet.tetris.ui.view.game.dialog.ErrorDialog
import com.yet.tetris.ui.view.game.dialog.GameOverDialog
import com.yet.tetris.ui.view.game.dialog.PauseDialog
import com.yet.tetris.ui.view.settings.SettingsSheet
import com.yet.tetris.utils.RProps
import com.yet.tetris.utils.formatTime
import com.yet.tetris.utils.useAsState
import js.objects.unsafeJso
import kotlinx.browser.window
import mui.icons.material.Pause
import mui.material.Box
import mui.material.Container
import mui.material.Drawer
import mui.material.DrawerAnchor
import mui.material.IconButton
import mui.system.sx
import react.FC
import react.useEffectOnce
import web.cssom.AlignItems
import web.cssom.AutoLengthProperty
import web.cssom.BackdropFilter
import web.cssom.Border
import web.cssom.BoxSizing
import web.cssom.Color
import web.cssom.Display
import web.cssom.FlexDirection
import web.cssom.FlexWrap
import web.cssom.JustifyContent
import web.cssom.Overflow
import web.cssom.Padding
import web.cssom.integer
import web.cssom.number
import web.cssom.px
import web.cssom.rem
import web.cssom.vh

@OptIn(ExperimentalWasmJsInterop::class)
val GameContent = FC<RProps<GameComponent>> { props ->
    val model by props.component.model.useAsState()
    val dialogSlot by props.component.childSlot.useAsState()
    val sheetSlot by props.component.sheetSlot.useAsState()
    val activeSheet = sheetSlot.child?.instance

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
                    alignItems = AlignItems.center
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
                        maxWidth = 400.px
                        justifyContent = JustifyContent.spaceAround
                        minWidth = 0.px
                    }

                    StatItem {
                        label = Strings.score
                        value = model.gameState?.score?.toString() ?: "0"
                    }

                    StatItem {
                        label = Strings.lines
                        value = model.gameState?.linesCleared?.toString() ?: "0"
                    }

                    StatItem {
                        label = Strings.time
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
    Drawer {
        anchor = DrawerAnchor.bottom
        open = activeSheet != null
        onClose = { _, _ -> props.component.onDismissSheet() }

        ModalProps = unsafeJso {
            sx {
                zIndex = integer(1400)
            }
        }

        PaperProps = unsafeJso {
            sx {
                borderTopLeftRadius = 16.px
                borderTopRightRadius = 16.px

                maxHeight = 90.vh
                maxWidth = 600.px

                marginLeft = "auto".unsafeCast<AutoLengthProperty>()
                marginRight = "auto".unsafeCast<AutoLengthProperty>()
            }
        }

        activeSheet?.let { child ->
            when (child) {
                is GameComponent.SheetChild.Settings -> {
                    SettingsSheet {
                        component = child.component
                    }
                }
            }
        }
    }
}
