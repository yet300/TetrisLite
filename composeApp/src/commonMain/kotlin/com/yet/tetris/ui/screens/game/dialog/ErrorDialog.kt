package com.yet.tetris.ui.screens.game.dialog

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource
import tetrislite.composeapp.generated.resources.Res
import tetrislite.composeapp.generated.resources.error_title
import tetrislite.composeapp.generated.resources.ok

@Composable
 fun ErrorDialog(message: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Warning, contentDescription = "Error", tint = MaterialTheme.colorScheme.error) },
        title = { Text(stringResource(Res.string.error_title)) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.ok))
            }
        }
    )
}