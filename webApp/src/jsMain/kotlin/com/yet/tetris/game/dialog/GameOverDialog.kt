package com.yet.tetris.game.dialog

import com.yet.tetris.feature.game.GameComponent
import mui.material.Box
import mui.material.Button
import mui.material.ButtonVariant
import mui.material.Dialog
import mui.material.DialogActions
import mui.material.DialogContent
import mui.material.DialogTitle
import mui.material.Typography
import mui.material.styles.TypographyVariant
import mui.system.sx
import react.FC
import react.Props
import web.cssom.AlignItems
import web.cssom.Color
import web.cssom.Display
import web.cssom.FlexDirection
import web.cssom.TextAlign
import web.cssom.integer
import web.cssom.rem

external interface GameOverDialogProps : Props {
    var component: GameComponent
    var score: Long
    var lines: Long
}

val GameOverDialog = FC<GameOverDialogProps> { props ->
    Dialog {
        open = true
        onClose = { _, _ -> props.component.onQuit() }

        DialogTitle {
            sx {
                textAlign = TextAlign.center
            }
            +"Game Over!"
        }

        DialogContent {
            Box {
                sx {
                    display = Display.flex
                    flexDirection = FlexDirection.column
                    alignItems = AlignItems.center
                    gap = 1.rem
                    padding = 1.rem
                }

                Typography {
                    variant = TypographyVariant.h4
                    sx {
                        color = Color("#667eea")
                        fontWeight = integer(700)
                    }
                    +"${props.score}"
                }

                Typography {
                    variant = TypographyVariant.body1
                    +"Lines Cleared: ${props.lines}"
                }
            }
        }

        DialogActions {
            Button {
                onClick = { props.component.onRetry() }
                variant = ButtonVariant.contained
                +"Play Again"
            }
            Button {
                onClick = { props.component.onQuit() }
                +"Quit"
            }
        }
    }
}
