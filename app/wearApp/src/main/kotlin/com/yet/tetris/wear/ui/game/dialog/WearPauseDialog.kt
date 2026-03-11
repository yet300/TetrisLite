package com.yet.tetris.wear.ui.game.dialog

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.yet.tetris.wear.R
import com.yet.tetris.wear.ui.components.WearOverlaySurface

@Composable
fun WearPauseDialog(
    onResume: () -> Unit,
    onSettings: () -> Unit,
    onQuit: () -> Unit
) {
    WearOverlaySurface(
        title = stringResource(R.string.pause),
        showClose = false,
        onDismiss = onResume,
    ) {
        Text(
            text = stringResource(R.string.game_paused),
            style = MaterialTheme.typography.body2,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.size(4.dp))
        Chip(
            label = { Text(stringResource(R.string.resume_game)) },
            icon = { Icon(Icons.Default.PlayArrow, null) },
            onClick = onResume,
            colors = ChipDefaults.primaryChipColors()
        )
        Chip(
            label = { Text(stringResource(R.string.settings)) },
            icon = { Icon(Icons.Default.Settings, null) },
            onClick = onSettings,
            colors = ChipDefaults.secondaryChipColors()
        )
        Chip(
            label = { Text(stringResource(R.string.exit)) },
            icon = { Icon(Icons.Default.Close, null) },
            onClick = onQuit,
            colors = ChipDefaults.secondaryChipColors()
        )
    }
}

