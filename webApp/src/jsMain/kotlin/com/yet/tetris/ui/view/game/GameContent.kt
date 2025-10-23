package com.yet.tetris.ui.view.game

import com.yet.tetris.feature.game.GameComponent
import com.yet.tetris.ui.components.AppBarConfig
import com.yet.tetris.ui.components.Scaffold
import com.yet.tetris.ui.view.game.dialog.ErrorDialog
import com.yet.tetris.ui.view.game.dialog.GameOverDialog
import com.yet.tetris.ui.view.game.dialog.PauseDialog
import com.yet.tetris.ui.view.settings.SettingsSheet
import com.yet.tetris.utils.RProps
import com.yet.tetris.utils.useAsState
import kotlinx.browser.window
import mui.icons.material.Pause
import mui.icons.material.Settings
import mui.material.Box
import mui.material.Container
import mui.material.Grid
import mui.material.IconButton
import mui.material.IconButtonColor
import mui.material.Paper
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.responsive
import mui.system.sx
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.useEffectOnce
import web.cssom.AlignItems
import web.cssom.BackgroundImage
import web.cssom.Color
import web.cssom.Display
import web.cssom.FlexDirection
import web.cssom.JustifyContent
import web.cssom.TextTransform
import web.cssom.integer
import web.cssom.number
import web.cssom.pct
import web.cssom.rem

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

                "c" -> {
                    event.preventDefault()
                    // props.component.onHold() // If hold is implemented
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
        appBar = AppBarConfig(
            title = "Game",
            onBackClick = { props.component.onBackClick() },
            actions = {
                IconButton {
                    color = IconButtonColor.inherit
                    onClick = { props.component.onSettings() }
                    Settings()
                }
                IconButton {
                    color = IconButtonColor.inherit
                    onClick = { props.component.onPause() }
                    Pause()
                }
            }
        )

        sx {
            backgroundImage =
                "linear-gradient(135deg, rgb(102, 126, 234) 0%, rgb(118, 75, 162) 100%)".unsafeCast<BackgroundImage>()
        }

        Container {
            maxWidth = "lg"
            sx {
                display = Display.flex
                flexDirection = FlexDirection.column
                height = 100.pct
                padding = 2.rem
            }

            // Stats
            Paper {
                elevation = 2
                sx {
                    padding = 2.rem
                    marginBottom = 2.rem
                    backgroundColor = Color("rgba(255, 255, 255, 0.95)")
                    borderRadius = 1.rem
                }

                Grid {
                    container = true
                    spacing = responsive(2)

                    Grid {
                        item = true
//                        xs = 4.unsafeCast<GridSize>()

                        StatCard {
                            label = "Score"
                            value = model.gameState?.score?.toString() ?: "0"
                        }
                    }

                    Grid {
                        item = true
//                        xs = 4.unsafeCast<GridSize>()

                        StatCard {
                            label = "Lines"
                            value = model.gameState?.linesCleared?.toString() ?: "0"
                        }
                    }

                    Grid {
                        item = true
//                        xs = 4.unsafeCast<GridSize>()

                        StatCard {
                            label = "Time"
                            value = formatTime(model.elapsedTime)
                        }
                    }
                }
            }

            // Game board
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
                    onClick = { props.component.onDismissSheet() }

                    div {
//                        style = jso {
//                            backgroundColor = "white"
//                            borderRadius = 16.px
//                            maxWidth = 600.px
//                            width = "90%".unsafeCast<Width>()
//                            maxHeight = "80vh".unsafeCast<MaxHeight>()
//                            overflow = "auto".unsafeCast<Overflow>()
//                        }
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

external interface StatCardProps : Props {
    var label: String
    var value: String
}

val StatCard = FC<StatCardProps> { props ->
    Box {
        sx {
            display = Display.flex
            flexDirection = FlexDirection.column
            alignItems = AlignItems.center
        }

        Typography {
            variant = TypographyVariant.caption
            sx {
                color = Color("rgba(0, 0, 0, 0.6)")
                textTransform = TextTransform.uppercase
            }
            +props.label
        }

        Typography {
            variant = TypographyVariant.h4
            sx {
                fontWeight = integer(700)
                color = Color("#667eea")
            }
            +props.value
        }
    }
}

private fun formatTime(ms: Long): String {
    val seconds = (ms / 1000) % 60
    val minutes = ms / 1000 / 60
    return "$minutes:${seconds.toString().padStart(2, '0')}"
}
