package com.yet.tetris.ui.screens.game.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.yet.tetris.feature.game.GameComponent
import org.jetbrains.compose.resources.stringResource
import tetrislite.composeapp.generated.resources.Res
import tetrislite.composeapp.generated.resources.game_paused
import tetrislite.composeapp.generated.resources.pause_message
import tetrislite.composeapp.generated.resources.quit
import tetrislite.composeapp.generated.resources.resume

@Composable
fun PauseDialog(component: GameComponent) {
    AlertDialog(
        onDismissRequest = component::onResume, // Dismissing by clicking outside resumes the game
        title = { Text(stringResource(Res.string.game_paused)) },
        text = { Text(stringResource(Res.string.pause_message)) },
        confirmButton = {
            TextButton(onClick = component::onResume) {
                Text(stringResource(Res.string.resume))
            }
        },
        dismissButton = {
            TextButton(onClick = component::onQuit) {
                Text(stringResource(Res.string.quit))
            }
        }
    )
}