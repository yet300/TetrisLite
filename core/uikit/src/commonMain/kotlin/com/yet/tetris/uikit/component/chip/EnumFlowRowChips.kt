package com.yet.tetris.uikit.component.chip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
inline fun <reified T : Enum<T>> EnumFlowRowChips(
    selectedValue: T,
    crossinline onValueChange: (T) -> Unit,
    modifier: Modifier = Modifier,
    crossinline getLabel: (T) -> String = { it.name.replace('_', ' ') }
) {
    val enumValues = enumValues<T>()

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier
    ) {
        enumValues.forEach { enumValue ->
            TetrisFilterChip(
                selected = selectedValue == enumValue,
                onClick = { onValueChange(enumValue) },
                label = getLabel(enumValue)
            )
        }
    }
}