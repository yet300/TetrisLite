package com.yet.tetris.data.mapper

import com.yet.tetris.data.model.AudioSettingsDto
import com.yet.tetris.data.model.GameSettingsDto
import com.yet.tetris.data.model.MusicThemeDto
import com.yet.tetris.data.model.PieceStyleDto
import com.yet.tetris.data.model.ThemeConfigDto
import com.yet.tetris.data.model.VisualThemeDto
import com.yet.tetris.domain.model.audio.AudioSettings
import com.yet.tetris.domain.model.audio.MusicTheme
import com.yet.tetris.domain.model.settings.GameSettings
import com.yet.tetris.domain.model.theme.PieceStyle
import com.yet.tetris.domain.model.theme.ThemeConfig
import com.yet.tetris.domain.model.theme.VisualTheme

// Domain to DTO
fun GameSettings.toDto(): GameSettingsDto =
    GameSettingsDto(
        difficulty = difficulty.toDto(),
        themeConfig = themeConfig.toDto(),
        audioSettings = audioSettings.toDto(),
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
        MusicTheme.NONE -> MusicThemeDto.NONE
    }

// DTO to Domain
fun GameSettingsDto.toDomain(): GameSettings =
    GameSettings(
        difficulty = difficulty.toDomain(),
        themeConfig = themeConfig.toDomain(),
        audioSettings = audioSettings.toDomain(),
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
        MusicThemeDto.NONE -> MusicTheme.NONE
    }
