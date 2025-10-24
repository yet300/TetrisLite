package com.yet.tetris.ui.view.home.components

import com.yet.tetris.ui.strings.Strings
import mui.icons.material.PlayArrow
import mui.icons.material.Replay
import mui.material.Box
import mui.material.Button
import mui.material.ButtonVariant
import mui.material.Size
import mui.system.sx
import react.FC
import react.Props
import react.create
import web.cssom.BackdropFilter
import web.cssom.Border
import web.cssom.BoxShadow
import web.cssom.Color
import web.cssom.Display
import web.cssom.FlexDirection
import web.cssom.TextTransform
import web.cssom.integer
import web.cssom.pct
import web.cssom.px
import web.cssom.rem

external interface ActionButtonsProps : Props {
    var hasSavedGame: Boolean
    var onStartNewGame: () -> Unit
    var onResumeGame: () -> Unit
}

@OptIn(ExperimentalWasmJsInterop::class)
val ActionButtons = FC<ActionButtonsProps> { props ->
    Box {
        sx {
            width = 100.pct
            maxWidth = 400.px
            display = Display.flex
            flexDirection = FlexDirection.column
            gap = 1.rem
            paddingBottom = 2.rem
        }

        // Start New Game button
        Button {
            variant = ButtonVariant.contained
            size = Size.large
            fullWidth = true
            onClick = { props.onStartNewGame() }
            startIcon = PlayArrow.create()
            sx {
                height = 56.px
                backgroundColor = Color("rgba(255, 255, 255, 0.15)")
                backdropFilter = "blur(10px)".unsafeCast<BackdropFilter>()
                border = "1px solid rgba(255, 255, 255, 0.3)".unsafeCast<Border>()
                borderRadius = 1.rem
                color = Color("white")
                fontWeight = integer(700)
                fontSize = 1.rem
                textTransform = "none".unsafeCast<TextTransform>()
                boxShadow = "0 8px 32px rgba(0, 0, 0, 0.1)".unsafeCast<BoxShadow>()

                hover {
                    backgroundColor = Color("rgba(255, 255, 255, 0.25)")
                    boxShadow = "0 12px 40px rgba(0, 0, 0, 0.15)".unsafeCast<BoxShadow>()
                }
            }
            +Strings.startNewGame
        }

        // Resume Game button (if available)
        if (props.hasSavedGame) {
            Button {
                variant = ButtonVariant.outlined
                size = Size.large
                fullWidth = true
                onClick = { props.onResumeGame() }
                startIcon = Replay.create()
                sx {
                    height = 56.px
                    backgroundColor = Color("rgba(255, 255, 255, 0.1)")
                    backdropFilter = "blur(10px)".unsafeCast<BackdropFilter>()
                    border = "2px solid rgba(255, 255, 255, 0.3)".unsafeCast<Border>()
                    borderRadius = 1.rem
                    color = Color("white")
                    fontWeight = integer(600)
                    fontSize = 1.rem
                    textTransform = "none".unsafeCast<TextTransform>()

                    hover {
                        backgroundColor = Color("rgba(255, 255, 255, 0.2)")
                        border = "2px solid rgba(255, 255, 255, 0.4)".unsafeCast<Border>()
                    }
                }
                +Strings.resumeGame
            }
        }
    }
}
