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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
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
import com.yet.tetris.feature.settings.PreviewSettingsComponent
import com.yet.tetris.feature.settings.SettingsComponent
import com.yet.tetris.uikit.component.button.EnumSegmentedButtonRow
import com.yet.tetris.uikit.component.button.FrostedGlassButton
import com.yet.tetris.uikit.component.chip.EnumFlowRowChips
import com.yet.tetris.uikit.component.text.TitleText
import com.yet.tetris.uikit.theme.TetrisLiteAppTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import tetrislite.composeapp.generated.resources.Res
import tetrislite.composeapp.generated.resources.audio
import tetrislite.composeapp.generated.resources.game_settings
import tetrislite.composeapp.generated.resources.music
import tetrislite.composeapp.generated.resources.music_theme
import tetrislite.composeapp.generated.resources.music_volume
import tetrislite.composeapp.generated.resources.piece_style
import tetrislite.composeapp.generated.resources.sfx_volume
import tetrislite.composeapp.generated.resources.sound_effects
import tetrislite.composeapp.generated.resources.visual_theme

@Composable
fun SettingsSheet(component: SettingsComponent) {
    val model by component.model.subscribeAsState()

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
    ) {
        SettingsScreenContent(model, component)

        // Close Button
        Box(
            modifier =
                Modifier
                    .align(Alignment.TopEnd)
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(MaterialTheme.colorScheme.background),
        ) {
            FrostedGlassButton(
                modifier = Modifier.align(Alignment.CenterEnd),
                onClick = component::onClose,
                icon = Icons.Default.Close,
            )
        }
    }
}

@Composable
private fun SettingsScreenContent(
    model: SettingsComponent.Model,
    component: SettingsComponent,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 80.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item(key = "header") {
            TitleText(
                text = stringResource(Res.string.game_settings),
            )
        }

        item {
            // Visual Theme
            SettingsSection(title = stringResource(Res.string.visual_theme)) {
                EnumFlowRowChips(
                    selectedValue = model.settings.themeConfig.visualTheme,
                    onValueChange = { component.onVisualThemeChanged(it) },
                )
            }
        }

        item {
            // Piece Style
            SettingsSection(title = stringResource(Res.string.piece_style)) {
                EnumFlowRowChips(
                    selectedValue = model.settings.themeConfig.pieceStyle,
                    onValueChange = { component.onPieceStyleChanged(it) },
                )
            }
        }

        item {
            // Audio Settings
            SettingsSection(title = stringResource(Res.string.audio)) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(stringResource(Res.string.music))
                        Switch(
                            checked = model.settings.audioSettings.musicEnabled,
                            onCheckedChange = component::onMusicToggled,
                        )
                    }

                    if (model.settings.audioSettings.musicEnabled) {
                        SliderRow(
                            label = stringResource(Res.string.music_volume),
                            value = model.settings.audioSettings.musicVolume,
                            onValueChange = component::onMusicVolumeChanged,
                        )

                        Text(stringResource(Res.string.music_theme), style = MaterialTheme.typography.bodyMedium)

                        EnumSegmentedButtonRow(
                            selectedValue = model.settings.audioSettings.selectedMusicTheme,
                            onValueChange = { newTheme -> component.onMusicThemeChanged(newTheme) },
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(stringResource(Res.string.sound_effects))
                        Switch(
                            checked = model.settings.audioSettings.soundEffectsEnabled,
                            onCheckedChange = component::onSoundEffectsToggled,
                        )
                    }

                    if (model.settings.audioSettings.soundEffectsEnabled) {
                        SliderRow(
                            label = stringResource(Res.string.sfx_volume),
                            value = model.settings.audioSettings.sfxVolume,
                            onValueChange = component::onSFXVolumeChanged,
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
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        content()
    }
}

@Composable
private fun SliderRow(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(label)
            Text("${(value * 100).toInt()}%")
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..1f,
        )
    }
}

@Composable
@Preview
fun SettingsScreenPreview() {
    TetrisLiteAppTheme {
        SettingsSheet(PreviewSettingsComponent())
    }
}
