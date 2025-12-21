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
            color = MaterialTheme.colors.secondary
        )
        InlineSlider(
            value = value * 10, // Scale 0.0-1.0 to 0-10
            onValueChange = { onValueChange(it / 10f) },
            valueRange = 0f..10f,
            steps = 9, // 10 intervals = 9 steps in between
            segmented = true,
            decreaseIcon = { Icon(Icons.Default.Remove, "Decrease Volume") }, // Standard Icon
            increaseIcon = { Icon(Icons.Default.Add, "Increase Volume") },   // Standard Icon
            modifier = Modifier.fillMaxWidth()
        )
    }
}
