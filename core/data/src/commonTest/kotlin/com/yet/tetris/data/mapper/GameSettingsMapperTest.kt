package com.yet.tetris.data.mapper

import com.yet.tetris.domain.model.audio.AudioSettings
import com.yet.tetris.domain.model.audio.MusicTheme
import com.yet.tetris.domain.model.game.Difficulty
import com.yet.tetris.domain.model.settings.GameSettings
import com.yet.tetris.domain.model.theme.PieceStyle
import com.yet.tetris.domain.model.theme.ThemeConfig
import com.yet.tetris.domain.model.theme.VisualTheme
import kotlin.test.Test
import kotlin.test.assertEquals

class GameSettingsMapperTest {
    @Test
    fun gameSettings_toDtoAndBack_shouldPreserveData() {
        // Given
        val original =
            GameSettings(
                difficulty = Difficulty.HARD,
                themeConfig =
                    ThemeConfig(
                        visualTheme = VisualTheme.NEON,
                        pieceStyle = PieceStyle.GRADIENT,
                    ),
                audioSettings =
                    AudioSettings(
                        musicEnabled = false,
                        soundEffectsEnabled = true,
                        musicVolume = 0.5f,
                        sfxVolume = 0.8f,
                        selectedMusicTheme = MusicTheme.MODERN,
                    ),
            )

        // When
        val dto = original.toDto()
        val result = dto.toDomain()

        // Then
        assertEquals(original.difficulty, result.difficulty)
        assertEquals(original.themeConfig.visualTheme, result.themeConfig.visualTheme)
        assertEquals(original.themeConfig.pieceStyle, result.themeConfig.pieceStyle)
        assertEquals(original.audioSettings.musicEnabled, result.audioSettings.musicEnabled)
        assertEquals(original.audioSettings.soundEffectsEnabled, result.audioSettings.soundEffectsEnabled)
        assertEquals(original.audioSettings.musicVolume, result.audioSettings.musicVolume)
        assertEquals(original.audioSettings.sfxVolume, result.audioSettings.sfxVolume)
        assertEquals(original.audioSettings.selectedMusicTheme, result.audioSettings.selectedMusicTheme)
    }

    @Test
    fun visualTheme_allValues_shouldMapCorrectly() {
        VisualTheme.entries.forEach { theme ->
            val dto = theme.toDto()
            val result = dto.toDomain()
            assertEquals(theme, result, "Failed for $theme")
        }
    }

    @Test
    fun pieceStyle_allValues_shouldMapCorrectly() {
        PieceStyle.entries.forEach { style ->
            val dto = style.toDto()
            val result = dto.toDomain()
            assertEquals(style, result, "Failed for $style")
        }
    }

    @Test
    fun musicTheme_allValues_shouldMapCorrectly() {
        MusicTheme.entries.forEach { theme ->
            val dto = theme.toDto()
            val result = dto.toDomain()
            assertEquals(theme, result, "Failed for $theme")
        }
    }

    @Test
    fun audioSettings_shouldMapAllFields() {
        // Given
        val original =
            AudioSettings(
                musicEnabled = true,
                soundEffectsEnabled = false,
                musicVolume = 0.3f,
                sfxVolume = 0.9f,
                selectedMusicTheme = MusicTheme.MINIMAL,
            )

        // When
        val dto = original.toDto()
        val result = dto.toDomain()

        // Then
        assertEquals(original.musicEnabled, result.musicEnabled)
        assertEquals(original.soundEffectsEnabled, result.soundEffectsEnabled)
        assertEquals(original.musicVolume, result.musicVolume)
        assertEquals(original.sfxVolume, result.sfxVolume)
        assertEquals(original.selectedMusicTheme, result.selectedMusicTheme)
    }

    @Test
    fun themeConfig_shouldMapAllFields() {
        // Given
        val original =
            ThemeConfig(
                visualTheme = VisualTheme.SUNSET,
                pieceStyle = PieceStyle.RETRO_PIXEL,
            )

        // When
        val dto = original.toDto()
        val result = dto.toDomain()

        // Then
        assertEquals(original.visualTheme, result.visualTheme)
        assertEquals(original.pieceStyle, result.pieceStyle)
    }
}
