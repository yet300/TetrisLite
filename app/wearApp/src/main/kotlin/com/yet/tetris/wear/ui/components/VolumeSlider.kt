package com.yet.tetris.wear.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.InlineSlider
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import kotlin.math.roundToInt

private const val SLIDER_SCALE = 10f
private const val SLIDER_STEPS = 9

@Composable
fun VolumeSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = "$label ${(value * 100).roundToInt()}%",
            style = MaterialTheme.typography.caption2,
            color = MaterialTheme.colors.secondary,
        )
        InlineSlider(
            value = value * SLIDER_SCALE,
            onValueChange = { onValueChange(it / SLIDER_SCALE) },
            valueRange = 0f..SLIDER_SCALE,
            steps = SLIDER_STEPS,
            segmented = true,
            decreaseIcon = { Icon(Icons.Default.Remove, "Decrease Volume") },
            increaseIcon = { Icon(Icons.Default.Add, "Increase Volume") },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
