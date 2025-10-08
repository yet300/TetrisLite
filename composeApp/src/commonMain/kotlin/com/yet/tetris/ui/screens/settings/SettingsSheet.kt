package com.yet.tetris.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.yet.tetris.feature.settings.SettingsComponent
import com.yet.tetris.uikit.component.button.EnumSegmentedButtonRow
import com.yet.tetris.uikit.component.chip.EnumFlowRowChips
import com.yet.tetris.uikit.component.text.TitleText

@Composable
fun SettingsSheet(component: SettingsComponent) {
    val model by component.model.subscribeAsState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        SettingsScreenContent(model, component)

        // Action Buttons
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(64.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = component::onDiscard,
                    modifier = Modifier.weight(1f),
                    enabled = model.hasUnsavedChanges
                ) {
                    Text("Discard")
                }
                Button(
                    onClick = component::onSave,
                    modifier = Modifier.weight(1f),
                    enabled = model.hasUnsavedChanges && !model.isSaving
                ) {
                    if (model.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsScreenContent(
    model: SettingsComponent.Model,
    component: SettingsComponent
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 80.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item(key = "header") {
            TitleText(
                text = "Game Settings",
            )
        }

        item {
            // Visual Theme
            SettingsSection(title = "Visual Theme") {
                EnumFlowRowChips(
                    selectedValue = model.settings.themeConfig.visualTheme,
                    onValueChange = { component.onVisualThemeChanged(it) }
                )
            }
        }

        item {
            // Piece Style
            SettingsSection(title = "Piece Style") {
                EnumFlowRowChips(
                    selectedValue = model.settings.themeConfig.pieceStyle,
                    onValueChange = { component.onPieceStyleChanged(it) }
                )
            }
        }

        item {
            // Keyboard Layout (Desktop/Web)
            SettingsSection(title = "Keyboard Layout") {
                EnumSegmentedButtonRow(
                    selectedValue = model.settings.keyboardLayout,
                    onValueChange = { component.onKeyboardLayoutChanged(it) }
                )
            }
        }

        item {
            // Swipe Layout (Mobile)
            SettingsSection(title = "Swipe Layout") {
                EnumSegmentedButtonRow(
                    selectedValue = model.settings.swipeLayout,
                    onValueChange = { component.onSwipeLayoutChanged(it) }
                )
            }
        }

        item {
//                Swipe Sensitivity
//                        SettingsSection(title = "Swipe Sensitivity") {
//                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
//                                SwipeSensitivity.entries.forEach { sensitivity ->
//                                    FilterChip(
//                                        selected = model.settings.swipeSensitivity == sensitivity,
//                                        onClick = { component.onSwipeSensitivityChanged(sensitivity) },
//                                        label = { Text(sensitivity.name) },
//                                        modifier = Modifier.fillMaxWidth()
//                                    )
//                                }
//                            }
//                        }
        }

        item {
            // Audio Settings
            SettingsSection(title = "Audio") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Music")
                        Switch(
                            checked = model.settings.audioSettings.musicEnabled,
                            onCheckedChange = component::onMusicToggled
                        )
                    }

                    if (model.settings.audioSettings.musicEnabled) {
                        SliderRow(
                            label = "Music Volume",
                            value = model.settings.audioSettings.musicVolume,
                            onValueChange = component::onMusicVolumeChanged
                        )

                        Text("Music Theme", style = MaterialTheme.typography.bodyMedium)

                        EnumSegmentedButtonRow(
                            selectedValue = model.settings.audioSettings.selectedMusicTheme,
                            onValueChange = { newTheme -> component.onMusicThemeChanged(newTheme) }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Sound Effects")
                        Switch(
                            checked = model.settings.audioSettings.soundEffectsEnabled,
                            onCheckedChange = component::onSoundEffectsToggled
                        )
                    }

                    if (model.settings.audioSettings.soundEffectsEnabled) {
                        SliderRow(
                            label = "SFX Volume",
                            value = model.settings.audioSettings.sfxVolume,
                            onValueChange = component::onSFXVolumeChanged
                        )
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        content()
    }
}

@Composable
private fun SliderRow(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label)
            Text("${(value * 100).toInt()}%")
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..1f
        )
    }
}