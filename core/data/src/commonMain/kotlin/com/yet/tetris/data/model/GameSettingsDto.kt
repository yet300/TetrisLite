package com.yet.tetris.data.model

import kotlinx.serialization.Serializable

@Serializable
data class GameSettingsDto(
    val difficulty: DifficultyDto = DifficultyDto.NORMAL,
    val themeConfig: ThemeConfigDto = ThemeConfigDto(),
    val keyboardLayout: KeyboardLayoutDto = KeyboardLayoutDto.ARROWS,
    val swipeLayout: SwipeLayoutDto = SwipeLayoutDto.STANDARD,
    val swipeSensitivity: SwipeSensitivityDto = SwipeSensitivityDto(),
    val audioSettings: AudioSettingsDto = AudioSettingsDto()
)

@Serializable
data class ThemeConfigDto(
    val visualTheme: VisualThemeDto = VisualThemeDto.CLASSIC,
    val pieceStyle: PieceStyleDto = PieceStyleDto.SOLID
)

@Serializable
enum class VisualThemeDto {
    CLASSIC, RETRO_GAMEBOY, RETRO_NES, NEON, PASTEL, MONOCHROME, OCEAN, SUNSET, FOREST
}

@Serializable
enum class PieceStyleDto {
    SOLID, BORDERED, GRADIENT, RETRO_PIXEL, GLASS
}

@Serializable
enum class KeyboardLayoutDto {
    ARROWS, WASD, CUSTOM
}

@Serializable
enum class SwipeLayoutDto {
    STANDARD, INVERTED, CUSTOM
}

@Serializable
data class SwipeSensitivityDto(
    val softDropThreshold: Float = 0.5f,
    val horizontalSensitivity: Float = 1.0f,
    val verticalSensitivity: Float = 1.0f
)

@Serializable
data class AudioSettingsDto(
    val musicEnabled: Boolean = true,
    val soundEffectsEnabled: Boolean = true,
    val musicVolume: Float = 0.7f,
    val sfxVolume: Float = 0.8f,
    val selectedMusicTheme: MusicThemeDto = MusicThemeDto.CLASSIC
)

@Serializable
enum class MusicThemeDto {
    CLASSIC, MODERN, MINIMAL, NONE
}
