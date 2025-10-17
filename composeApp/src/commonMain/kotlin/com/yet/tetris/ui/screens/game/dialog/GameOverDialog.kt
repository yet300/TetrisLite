package com.yet.tetris.ui.screens.game.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.yet.tetris.feature.game.GameComponent
import org.jetbrains.compose.resources.stringResource
import tetrislite.composeapp.generated.resources.Res
import tetrislite.composeapp.generated.resources.back_to_home
import tetrislite.composeapp.generated.resources.final_score
import tetrislite.composeapp.generated.resources.game_over
import tetrislite.composeapp.generated.resources.lines_cleared
import tetrislite.composeapp.generated.resources.retry

@Composable
fun GameOverDialog(
    component: GameComponent,
    model: GameComponent.Model,
) {
    AlertDialog(
        onDismissRequest = component::onQuit,
        icon = {
            Icon(
                Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
            )
        },
        title = {
            Text(
                text = stringResource(Res.string.game_over),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            // Use a Column to display multiple pieces of information
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(Res.string.final_score, model.finalScore),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = stringResource(Res.string.lines_cleared, model.finalLinesCleared),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
            }
        },
        confirmButton = {
            Button(onClick = component::onRetry) {
                Text(stringResource(Res.string.retry))
            }
        },
        dismissButton = {
            OutlinedButton(onClick = component::onQuit) {
                Text(stringResource(Res.string.back_to_home))
            }
        },
    )
}
