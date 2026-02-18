package com.yet.tetris.wear.ui.game.dialog

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.yet.tetris.wear.R
import com.yet.tetris.wear.ui.components.WearOverlaySurface

@Composable
fun WearGameOverDialog(
    score: Long,
    lines: Long,
    onRetry: () -> Unit,
    onQuit: () -> Unit,
    onDismiss: () -> Unit
) {
    WearOverlaySurface(
        title = stringResource(R.string.game_over),
        showClose = false,
        onDismiss = onDismiss,
    ) {
        Text(
            text = stringResource(R.string.score_format, score),
            style = MaterialTheme.typography.body2,
        )
        Text(
            text = stringResource(R.string.lines_format, lines),
            style = MaterialTheme.typography.caption1,
            color = MaterialTheme.colors.secondary
        )
        Spacer(modifier = Modifier.size(8.dp))
        Chip(
            label = { Text(stringResource(R.string.play_again)) },
            icon = { Icon(Icons.Default.Refresh, null) },
            onClick = onRetry,
            colors = ChipDefaults.primaryChipColors()
        )
        Chip(
            label = { Text(stringResource(R.string.exit)) },
            icon = { Icon(Icons.Default.Close, null) },
            onClick = onQuit,
            colors = ChipDefaults.secondaryChipColors()
        )
    }
}
