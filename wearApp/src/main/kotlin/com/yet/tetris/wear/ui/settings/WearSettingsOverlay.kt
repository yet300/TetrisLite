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

    WearOverlaySurface(
        title = stringResource(R.string.settings),
        onDismiss = {
            component.onClose()
            onDismissRequest()
        },
    ) {
        WearSettingsContent(
            model = model,
            onVisualThemeChanged = component::onVisualThemeChanged,
            onPieceStyleChanged = component::onPieceStyleChanged,
            onMusicToggled = component::onMusicToggled,
            onMusicVolumeChanged = component::onMusicVolumeChanged,
            onMusicThemeChanged = component::onMusicThemeChanged,
            onSoundEffectsToggled = component::onSoundEffectsToggled,
            onSFXVolumeChanged = component::onSFXVolumeChanged
        )
    }
}


@Composable
private fun WearSettingsContent(
    model: SettingsComponent.Model,
    onVisualThemeChanged: (VisualTheme) -> Unit,
    onPieceStyleChanged: (PieceStyle) -> Unit,
    onMusicToggled: (Boolean) -> Unit,
    onMusicVolumeChanged: (Float) -> Unit,
    onMusicThemeChanged: (MusicTheme) -> Unit,
    onSoundEffectsToggled: (Boolean) -> Unit,
    onSFXVolumeChanged: (Float) -> Unit
) {
    VisualSettingsSection(
        currentTheme = model.settings.themeConfig.visualTheme,
        currentPieceStyle = model.settings.themeConfig.pieceStyle,
        onThemeChanged = onVisualThemeChanged,
        onStyleChanged = onPieceStyleChanged
    )

    Spacer(modifier = Modifier.height(8.dp))

    AudioSettingsSection(
        audioSettings = model.settings.audioSettings,
        onMusicToggled = onMusicToggled,
        onMusicVolumeChanged = onMusicVolumeChanged,
        onMusicThemeChanged = onMusicThemeChanged,
        onSoundEffectsToggled = onSoundEffectsToggled,
        onSFXVolumeChanged = onSFXVolumeChanged
    )
}


@Composable
private fun VisualSettingsSection(
    currentTheme: VisualTheme,
    currentPieceStyle: PieceStyle,
    onThemeChanged: (VisualTheme) -> Unit,
    onStyleChanged: (PieceStyle) -> Unit
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
    onMusicToggled: (Boolean) -> Unit,
    onMusicVolumeChanged: (Float) -> Unit,
    onMusicThemeChanged: (MusicTheme) -> Unit,
    onSoundEffectsToggled: (Boolean) -> Unit,
    onSFXVolumeChanged: (Float) -> Unit
) {
    // Music Control
    MusicControlGroup(
        isEnabled = audioSettings.musicEnabled,
        volume = audioSettings.musicVolume,
        selectedTheme = audioSettings.selectedMusicTheme,
        onToggle = onMusicToggled,
        onVolumeChange = onMusicVolumeChanged,
        onThemeChange = onMusicThemeChanged
    )

    Spacer(modifier = Modifier.height(4.dp))

    // SFX Control
    SfxControlGroup(
        isEnabled = audioSettings.soundEffectsEnabled,
        volume = audioSettings.sfxVolume,
        onToggle = onSoundEffectsToggled,
        onVolumeChange = onSFXVolumeChanged
    )
}

@Composable
private fun MusicControlGroup(
    isEnabled: Boolean,
    volume: Float,
    selectedTheme: MusicTheme,
    onToggle: (Boolean) -> Unit,
    onVolumeChange: (Float) -> Unit,
    onThemeChange: (MusicTheme) -> Unit
) {
    ToggleChip(
        modifier = Modifier.fillMaxWidth(),
        checked = isEnabled,
        onCheckedChange = onToggle,
        label = { Text(stringResource(R.string.music)) },
        toggleControl = {
            Switch(
                checked = isEnabled,
                onCheckedChange = null,
            )
        },
    )

    if (isEnabled) {
        VolumeSlider(
            label = stringResource(R.string.music_volume),
            value = volume,
            onValueChange = onVolumeChange,
        )

        EnumChipGroup(
            label = stringResource(R.string.music_style),
            values = MusicTheme.entries,
            selected = selectedTheme,
            onSelected = onThemeChange,
        )
    }
}

@Composable
private fun SfxControlGroup(
    isEnabled: Boolean,
    volume: Float,
    onToggle: (Boolean) -> Unit,
    onVolumeChange: (Float) -> Unit
) {
    ToggleChip(
        modifier = Modifier.fillMaxWidth(),
        checked = isEnabled,
        onCheckedChange = onToggle,
        label = { Text(stringResource(R.string.sound_effects)) },
        toggleControl = {
            Switch(
                checked = isEnabled,
                onCheckedChange = null,
            )
        },
    )

    if (isEnabled) {
        VolumeSlider(
            label = stringResource(R.string.sfx_volume),
            value = volume,
            onValueChange = onVolumeChange,
        )
    }
}