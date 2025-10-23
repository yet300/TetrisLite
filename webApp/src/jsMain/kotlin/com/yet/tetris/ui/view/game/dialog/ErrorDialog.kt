package com.yet.tetris.ui.view.game.dialog

import mui.material.Button
import mui.material.Dialog
import mui.material.DialogActions
import mui.material.DialogContent
import mui.material.DialogContentText
import mui.material.DialogTitle
import react.FC
import react.Props

external interface ErrorDialogProps : Props {
    var message: String
    var onDismiss: () -> Unit
}

val ErrorDialog = FC<ErrorDialogProps> { props ->
    Dialog {
        open = true
        onClose = { _, _ -> props.onDismiss() }

        DialogTitle {
            +"Error"
        }

        DialogContent {
            DialogContentText {
                +props.message
            }
        }

        DialogActions {
            Button {
                onClick = { props.onDismiss() }
                +"OK"
            }
        }
    }
}
