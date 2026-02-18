package com.yet.tetris.wear.ui.settings

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Switch
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ToggleChip
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.yet.tetris.domain.model.audio.AudioSettings
import com.yet.tetris.domain.model.audio.MusicTheme
import com.yet.tetris.domain.model.theme.PieceStyle
import com.yet.tetris.domain.model.theme.VisualTheme
import com.yet.tetris.feature.settings.SettingsComponent
import com.yet.tetris.wear.R
import com.yet.tetris.wear.ui.components.EnumChipGroup
import com.yet.tetris.wear.ui.components.VolumeSlider
import com.yet.tetris.wear.ui.components.WearOverlaySurface

@Composable
fun WearSettingsOverlay(
    component: SettingsComponent,
    onDismissRequest: () -> Unit,
) {
    val model by component.model.subscribeAsState()
    val callbacks = rememberCallbacks(component)

    WearOverlaySurface(
        title = stringResource(R.string.settings),
        onDismiss = {
            component.onClose()
            onDismissRequest()
        },
    ) {
        WearSettingsContent(
            model = model,
            callbacks = callbacks,
        )
    }
}

@Composable
private fun WearSettingsContent(
    model: SettingsComponent.Model,
    callbacks: SettingsCallbacks,
) {
    VisualSettingsSection(
        currentTheme = model.settings.themeConfig.visualTheme,
        currentPieceStyle = model.settings.themeConfig.pieceStyle,
        onThemeChanged = callbacks.onVisualThemeChanged,
        onStyleChanged = callbacks.onPieceStyleChanged,
    )

    Spacer(modifier = Modifier.height(8.dp))

    AudioSettingsSection(
        audioSettings = model.settings.audioSettings,
        callbacks = callbacks,
    )
}

@Composable
private fun VisualSettingsSection(
    currentTheme: VisualTheme,
    currentPieceStyle: PieceStyle,
    onThemeChanged: (VisualTheme) -> Unit,
    onStyleChanged: (PieceStyle) -> Unit,
) {
    EnumChipGroup(
        label = stringResource(R.string.theme),
        values = VisualTheme.entries,
        selected = currentTheme,
        onSelected = onThemeChanged,
    )

    Spacer(modifier = Modifier.height(4.dp))

    EnumChipGroup(
        label = stringResource(R.string.piece_style),
        values = PieceStyle.entries,
        selected = currentPieceStyle,
        onSelected = onStyleChanged,
    )
}

@Composable
private fun AudioSettingsSection(
    audioSettings: AudioSettings,
    callbacks: SettingsCallbacks,
) {
    MusicControlGroup(
        audioSettings = audioSettings,
        callbacks = callbacks,
    )

    Spacer(modifier = Modifier.height(4.dp))

    SfxControlGroup(
        audioSettings = audioSettings,
        callbacks = callbacks,
    )
}

@Composable
private fun MusicControlGroup(
    audioSettings: AudioSettings,
    callbacks: SettingsCallbacks,
) {
    ToggleChip(
        modifier = Modifier.fillMaxWidth(),
        checked = audioSettings.musicEnabled,
        onCheckedChange = callbacks.onMusicToggled,
        label = { Text(stringResource(R.string.music)) },
        toggleControl = {
            Switch(
                checked = audioSettings.musicEnabled,
                onCheckedChange = null,
            )
        },
    )

    if (audioSettings.musicEnabled) {
        VolumeSlider(
            label = stringResource(R.string.music_volume),
            value = audioSettings.musicVolume,
            onValueChange = callbacks.onMusicVolumeChanged,
        )

        EnumChipGroup(
            label = stringResource(R.string.music_style),
            values = MusicTheme.entries,
            selected = audioSettings.selectedMusicTheme,
            onSelected = callbacks.onMusicThemeChanged,
        )
    }
}

@Composable
private fun SfxControlGroup(
    audioSettings: AudioSettings,
    callbacks: SettingsCallbacks,
) {
    ToggleChip(
        modifier = Modifier.fillMaxWidth(),
        checked = audioSettings.soundEffectsEnabled,
        onCheckedChange = callbacks.onSoundEffectsToggled,
        label = { Text(stringResource(R.string.sound_effects)) },
        toggleControl = {
            Switch(
                checked = audioSettings.soundEffectsEnabled,
                onCheckedChange = null,
            )
        },
    )

    if (audioSettings.soundEffectsEnabled) {
        VolumeSlider(
            label = stringResource(R.string.sfx_volume),
            value = audioSettings.sfxVolume,
            onValueChange = callbacks.onSFXVolumeChanged,
        )
    }
}

private data class SettingsCallbacks(
    val onVisualThemeChanged: (VisualTheme) -> Unit,
    val onPieceStyleChanged: (PieceStyle) -> Unit,
    val onMusicToggled: (Boolean) -> Unit,
    val onMusicVolumeChanged: (Float) -> Unit,
    val onMusicThemeChanged: (MusicTheme) -> Unit,
    val onSoundEffectsToggled: (Boolean) -> Unit,
    val onSFXVolumeChanged: (Float) -> Unit,
)

private fun rememberCallbacks(component: SettingsComponent): SettingsCallbacks = SettingsCallbacks(
    onVisualThemeChanged = component::onVisualThemeChanged,
    onPieceStyleChanged = component::onPieceStyleChanged,
    onMusicToggled = component::onMusicToggled,
    onMusicVolumeChanged = component::onMusicVolumeChanged,
    onMusicThemeChanged = component::onMusicThemeChanged,
    onSoundEffectsToggled = component::onSoundEffectsToggled,
    onSFXVolumeChanged = component::onSFXVolumeChanged,
)
