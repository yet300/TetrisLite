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

val PauseDialog = FC<RProps<GameComponent>> { props ->
    Dialog {
        open = true
        onClose = { _, _ -> props.component.onResume() }

        DialogTitle {
            +Strings.gamePaused
        }

        DialogContent {
            DialogContentText {
                +Strings.pauseMessage
            }
        }

        DialogActions {
            Button {
                onClick = { props.component.onQuit() }
                color = ButtonColor.error
                +Strings.quit
            }
            Button {
                onClick = { props.component.onSettings() }
                +Strings.gameSettings
            }
            Button {
                onClick = { props.component.onResume() }
                +Strings.resume
            }
        }
    }
}
