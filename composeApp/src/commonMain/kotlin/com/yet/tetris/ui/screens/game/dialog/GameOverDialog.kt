package com.yet.tetris.ui.screens.game.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
                val gameState = model.gameState
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
                if (gameState != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Level ${gameState.level} • ${formatDuration(model.elapsedTime)}",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = "Pieces ${gameState.piecesPlaced} • Max combo ${gameState.maxCombo}",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = "Tetrises ${gameState.tetrisesCleared} • T-Spins ${gameState.tSpinClears} • Perfect clears ${gameState.perfectClears}",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                    )
                }
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

private fun formatDuration(milliseconds: Long): String {
    val seconds = (milliseconds / 1000) % 60
    val minutes = (milliseconds / 1000) / 60
    return "$minutes:${seconds.toString().padStart(2, '0')}"
}
