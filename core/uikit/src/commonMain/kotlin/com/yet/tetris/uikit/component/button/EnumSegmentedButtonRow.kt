package com.yet.tetris.uikit.component.button

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
inline fun <reified T : Enum<T>> EnumSegmentedButtonRow(
    modifier: Modifier = Modifier,
    selectedValue: T,
    crossinline onValueChange: (T) -> Unit,
    crossinline getLabel: (T) -> String = { it.name.replace('_', ' ') }
) {
    val enumValues = enumValues<T>()

    SingleChoiceSegmentedButtonRow(
        modifier = modifier,
        space = 8.dp
    ) {
        enumValues.forEachIndexed { index, enumValue ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = enumValues.size
                ),
                onClick = { onValueChange(enumValue) },
                selected = selectedValue == enumValue,
                label = {
                    Text(
                        text = getLabel(enumValue),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            )
        }
    }
}