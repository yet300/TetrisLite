package com.yet.tetris.wear.ui.game.dialog

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
fun WearErrorDialog(
    message: String,
    onDismiss: () -> Unit
) {
    WearOverlaySurface(
        title = stringResource(R.string.error),
        showClose = true,
        onDismiss = onDismiss,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.body2,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Chip(
            label = { Text(stringResource(R.string.understood)) },
            onClick = onDismiss,
            colors = ChipDefaults.secondaryChipColors()
        )
    }
}
