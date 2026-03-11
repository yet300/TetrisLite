package com.yet.tetris.ui.view.game.dialog

import com.yet.tetris.feature.game.GameComponent
import com.yet.tetris.ui.strings.Strings
import com.yet.tetris.utils.RProps
import mui.material.Button
import mui.material.ButtonColor
import mui.material.Dialog
import mui.material.DialogActions
import mui.material.DialogContent
import mui.material.DialogContentText
import mui.material.DialogTitle
import react.FC

val PauseDialog =
    FC<RProps<GameComponent>> { props ->
        Dialog {
            open = true
            onClose = { _, _ -> props.component.onResume() }

            DialogTitle {
                +Strings.GAME_PAUSED
            }

            DialogContent {
                DialogContentText {
                    +Strings.PAUSE_MESSAGE
                }
            }

            DialogActions {
                Button {
                    onClick = { props.component.onQuit() }
                    color = ButtonColor.error
                    +Strings.QUIT
                }
                Button {
                    onClick = { props.component.onSettings() }
                    +Strings.GAME_SETTINGS
                }
                Button {
                    onClick = { props.component.onResume() }
                    +Strings.RESUME
                }
            }
        }
    }
