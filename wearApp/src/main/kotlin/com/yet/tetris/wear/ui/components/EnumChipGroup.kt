package com.yet.tetris.wear.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.RadioButton
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ToggleChip

@Composable
fun <T : Enum<T>> EnumChipGroup(
    label: String,
    values: Iterable<T>,
    selected: T,
    onSelected: (T) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Chip(
            label = { Text("$label: ${formatEnumName(selected)}") },
            onClick = { expanded = !expanded },
            icon = {
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Expand $label"
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        if (expanded) {
            values.forEach { value ->
                ToggleChip(
                    checked = value == selected,
                    onCheckedChange = {
                        onSelected(value)
                        expanded =
                            false // Auto-collapse on selection? usually good for single select
                    },
                    label = { Text(formatEnumName(value)) },
                    toggleControl = {
                        RadioButton(
                            selected = value == selected,
                            onClick = null
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

private fun formatEnumName(value: Enum<*>): String =
    value.name
        .lowercase()
        .replaceFirstChar { it.uppercase() }
        .replace('_', ' ')
