package com.yet.tetris.data.mapper

import com.yet.tetris.data.model.AudioSettingsDto
import com.yet.tetris.data.model.ControlSettingsDto
import com.yet.tetris.data.model.GameSettingsDto
import com.yet.tetris.data.model.GestureSensitivityDto
import com.yet.tetris.data.model.MusicThemeDto
import com.yet.tetris.data.model.PieceStyleDto
import com.yet.tetris.data.model.RotationDirectionDto
import com.yet.tetris.data.model.ThemeConfigDto
import com.yet.tetris.data.model.VisualThemeDto
import com.yet.tetris.domain.model.audio.AudioSettings
import com.yet.tetris.domain.model.audio.MusicTheme
import com.yet.tetris.domain.model.game.RotationDirection
import com.yet.tetris.domain.model.settings.ControlSettings
import com.yet.tetris.domain.model.settings.GameSettings
import com.yet.tetris.domain.model.settings.GestureSensitivity
import com.yet.tetris.domain.model.theme.PieceStyle
import com.yet.tetris.domain.model.theme.ThemeConfig
import com.yet.tetris.domain.model.theme.VisualTheme

// Domain to DTO
fun GameSettings.toDto(): GameSettingsDto =
    GameSettingsDto(
        difficulty = difficulty.toDto(),
        themeConfig = themeConfig.toDto(),
        audioSettings = audioSettings.toDto(),
        controlSettings = controlSettings.toDto(),
    )

fun ThemeConfig.toDto(): ThemeConfigDto =
    ThemeConfigDto(
        visualTheme = visualTheme.toDto(),
        pieceStyle = pieceStyle.toDto(),
    )

fun VisualTheme.toDto(): VisualThemeDto =
    when (this) {
        VisualTheme.CLASSIC -> VisualThemeDto.CLASSIC
        VisualTheme.RETRO_GAMEBOY -> VisualThemeDto.RETRO_GAMEBOY
        VisualTheme.RETRO_NES -> VisualThemeDto.RETRO_NES
        VisualTheme.NEON -> VisualThemeDto.NEON
        VisualTheme.PASTEL -> VisualThemeDto.PASTEL
        VisualTheme.MONOCHROME -> VisualThemeDto.MONOCHROME
        VisualTheme.OCEAN -> VisualThemeDto.OCEAN
        VisualTheme.SUNSET -> VisualThemeDto.SUNSET
        VisualTheme.FOREST -> VisualThemeDto.FOREST
    }

fun PieceStyle.toDto(): PieceStyleDto =
    when (this) {
        PieceStyle.SOLID -> PieceStyleDto.SOLID
        PieceStyle.BORDERED -> PieceStyleDto.BORDERED
        PieceStyle.GRADIENT -> PieceStyleDto.GRADIENT
        PieceStyle.RETRO_PIXEL -> PieceStyleDto.RETRO_PIXEL
        PieceStyle.GLASS -> PieceStyleDto.GLASS
    }

fun AudioSettings.toDto(): AudioSettingsDto =
    AudioSettingsDto(
        musicEnabled = musicEnabled,
        soundEffectsEnabled = soundEffectsEnabled,
        musicVolume = musicVolume,
        sfxVolume = sfxVolume,
        selectedMusicTheme = selectedMusicTheme.toDto(),
    )

fun MusicTheme.toDto(): MusicThemeDto =
    when (this) {
        MusicTheme.CLASSIC -> MusicThemeDto.CLASSIC
        MusicTheme.MODERN -> MusicThemeDto.MODERN
        MusicTheme.MINIMAL -> MusicThemeDto.MINIMAL
        MusicTheme.ARCADE -> MusicThemeDto.ARCADE
        MusicTheme.DUSK -> MusicThemeDto.DUSK
        MusicTheme.BATTLE -> MusicThemeDto.BATTLE
        MusicTheme.NONE -> MusicThemeDto.NONE
    }

fun ControlSettings.toDto(): ControlSettingsDto =
    ControlSettingsDto(
        primaryRotateDirection = primaryRotateDirection.toDto(),
        enable180Rotation = enable180Rotation,
        gestureSensitivity = gestureSensitivity.toDto(),
    )

fun RotationDirection.toDto(): RotationDirectionDto =
    when (this) {
        RotationDirection.CLOCKWISE -> RotationDirectionDto.CLOCKWISE
        RotationDirection.COUNTERCLOCKWISE -> RotationDirectionDto.COUNTERCLOCKWISE
        RotationDirection.ONE_EIGHTY -> RotationDirectionDto.ONE_EIGHTY
    }

fun GestureSensitivity.toDto(): GestureSensitivityDto =
    when (this) {
        GestureSensitivity.RELAXED -> GestureSensitivityDto.RELAXED
        GestureSensitivity.NORMAL -> GestureSensitivityDto.NORMAL
        GestureSensitivity.COMPETITIVE -> GestureSensitivityDto.COMPETITIVE
    }

// DTO to Domain
fun GameSettingsDto.toDomain(): GameSettings =
    GameSettings(
        difficulty = difficulty.toDomain(),
        themeConfig = themeConfig.toDomain(),
        audioSettings = audioSettings.toDomain(),
        controlSettings = controlSettings.toDomain(),
    )

fun ThemeConfigDto.toDomain(): ThemeConfig =
    ThemeConfig(
        visualTheme = visualTheme.toDomain(),
        pieceStyle = pieceStyle.toDomain(),
    )

fun VisualThemeDto.toDomain(): VisualTheme =
    when (this) {
        VisualThemeDto.CLASSIC -> VisualTheme.CLASSIC
        VisualThemeDto.RETRO_GAMEBOY -> VisualTheme.RETRO_GAMEBOY
        VisualThemeDto.RETRO_NES -> VisualTheme.RETRO_NES
        VisualThemeDto.NEON -> VisualTheme.NEON
        VisualThemeDto.PASTEL -> VisualTheme.PASTEL
        VisualThemeDto.MONOCHROME -> VisualTheme.MONOCHROME
        VisualThemeDto.OCEAN -> VisualTheme.OCEAN
        VisualThemeDto.SUNSET -> VisualTheme.SUNSET
        VisualThemeDto.FOREST -> VisualTheme.FOREST
    }

fun PieceStyleDto.toDomain(): PieceStyle =
    when (this) {
        PieceStyleDto.SOLID -> PieceStyle.SOLID
        PieceStyleDto.BORDERED -> PieceStyle.BORDERED
        PieceStyleDto.GRADIENT -> PieceStyle.GRADIENT
        PieceStyleDto.RETRO_PIXEL -> PieceStyle.RETRO_PIXEL
        PieceStyleDto.GLASS -> PieceStyle.GLASS
    }

fun AudioSettingsDto.toDomain(): AudioSettings =
    AudioSettings(
        musicEnabled = musicEnabled,
        soundEffectsEnabled = soundEffectsEnabled,
        musicVolume = musicVolume,
        sfxVolume = sfxVolume,
        selectedMusicTheme = selectedMusicTheme.toDomain(),
    )

fun MusicThemeDto.toDomain(): MusicTheme =
    when (this) {
        MusicThemeDto.CLASSIC -> MusicTheme.CLASSIC
        MusicThemeDto.MODERN -> MusicTheme.MODERN
        MusicThemeDto.MINIMAL -> MusicTheme.MINIMAL
        MusicThemeDto.ARCADE -> MusicTheme.ARCADE
        MusicThemeDto.DUSK -> MusicTheme.DUSK
        MusicThemeDto.BATTLE -> MusicTheme.BATTLE
        MusicThemeDto.NONE -> MusicTheme.NONE
    }

fun ControlSettingsDto.toDomain(): ControlSettings =
    ControlSettings(
        primaryRotateDirection = primaryRotateDirection.toDomain(),
        enable180Rotation = enable180Rotation,
        gestureSensitivity = gestureSensitivity.toDomain(),
    )

fun RotationDirectionDto.toDomain(): RotationDirection =
    when (this) {
        RotationDirectionDto.CLOCKWISE -> RotationDirection.CLOCKWISE
        RotationDirectionDto.COUNTERCLOCKWISE -> RotationDirection.COUNTERCLOCKWISE
        RotationDirectionDto.ONE_EIGHTY -> RotationDirection.ONE_EIGHTY
    }

fun GestureSensitivityDto.toDomain(): GestureSensitivity =
    when (this) {
        GestureSensitivityDto.RELAXED -> GestureSensitivity.RELAXED
        GestureSensitivityDto.NORMAL -> GestureSensitivity.NORMAL
        GestureSensitivityDto.COMPETITIVE -> GestureSensitivity.COMPETITIVE
    }
