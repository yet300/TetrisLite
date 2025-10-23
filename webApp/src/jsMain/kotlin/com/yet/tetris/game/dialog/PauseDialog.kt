package com.yet.tetris.game.dialog

import com.yet.tetris.feature.game.GameComponent
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
            +"Game Paused"
        }

        DialogContent {
            DialogContentText {
                +"The game is paused. Choose an action below."
            }
        }

        DialogActions {
            Button {
                onClick = { props.component.onResume() }
                +"Resume"
            }
            Button {
                onClick = { props.component.onSettings() }
                +"Settings"
            }
            Button {
                onClick = { props.component.onRetry() }
                +"Restart"
            }
            Button {
                onClick = { props.component.onQuit() }
                color = ButtonColor.error
                +"Quit"
            }
        }
    }
}
