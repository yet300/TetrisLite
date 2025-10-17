package com.yet.tetris.data.repository

import app.cash.turbine.test
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.settings.observable.makeObservable
import com.yet.tetris.data.RobolectricTestRunner
import com.yet.tetris.domain.model.audio.AudioSettings
import com.yet.tetris.domain.model.audio.MusicTheme
import com.yet.tetris.domain.model.game.Difficulty
import com.yet.tetris.domain.model.settings.GameSettings
import com.yet.tetris.domain.model.settings.KeyboardLayout
import com.yet.tetris.domain.model.theme.PieceStyle
import com.yet.tetris.domain.model.theme.ThemeConfig
import com.yet.tetris.domain.model.theme.VisualTheme
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

class GameSettingsRepositoryImplTest : RobolectricTestRunner() {
    private lateinit var repository: GameSettingsRepositoryImpl
    private val settings = MapSettings()

    @OptIn(ExperimentalSettingsApi::class)
    @BeforeTest
    fun setup() {
        val flowSettings = settings.makeObservable().toFlowSettings()
        val json = Json { ignoreUnknownKeys = true }
        repository = GameSettingsRepositoryImpl(flowSettings, json)
    }

    @Test
    fun getSettings_shouldReturnDefaultWhenNoSettings() =
        runTest {
            // When
            val result = repository.getSettings()

            // Then
            assertNotNull(result)
            assertEquals(Difficulty.NORMAL, result.difficulty)
            assertEquals(VisualTheme.CLASSIC, result.themeConfig.visualTheme)
        }

    @Test
    fun saveSettings_shouldPersistSettings() =
        runTest {
            // Given
            val customSettings =
                GameSettings(
                    difficulty = Difficulty.HARD,
                    themeConfig =
                        ThemeConfig(
                            visualTheme = VisualTheme.NEON,
                            pieceStyle = PieceStyle.GRADIENT,
                        ),
                    keyboardLayout = KeyboardLayout.WASD,
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
            repository.saveSettings(customSettings)

            // Then
            val retrieved = repository.getSettings()
            assertEquals(Difficulty.HARD, retrieved.difficulty)
            assertEquals(VisualTheme.NEON, retrieved.themeConfig.visualTheme)
            assertEquals(PieceStyle.GRADIENT, retrieved.themeConfig.pieceStyle)
            assertEquals(KeyboardLayout.WASD, retrieved.keyboardLayout)
            assertFalse(retrieved.audioSettings.musicEnabled)
            assertEquals(MusicTheme.MODERN, retrieved.audioSettings.selectedMusicTheme)
        }

    @Test
    fun saveSettings_shouldOverwriteExistingSettings() =
        runTest {
            // Given
            repository.saveSettings(GameSettings(difficulty = Difficulty.EASY))

            // When
            repository.saveSettings(GameSettings(difficulty = Difficulty.HARD))

            // Then
            val retrieved = repository.getSettings()
            assertEquals(Difficulty.HARD, retrieved.difficulty)
        }

    @Test
    fun observeSettings_shouldEmitUpdates() =
        runTest {
            // When/Then
            repository.observeSettings().test {
                // Initial default settings
                val initial = awaitItem()
                assertEquals(Difficulty.NORMAL, initial.difficulty)

                // Update settings
                repository.saveSettings(GameSettings(difficulty = Difficulty.HARD))
                val updated = awaitItem()
                assertEquals(Difficulty.HARD, updated.difficulty)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun getSettings_shouldReturnDefaultOnCorruptedData() =
        runTest {
            // Given - Manually corrupt the settings
            settings.putString("game_settings", "invalid json {{{")

            // When
            val result = repository.getSettings()

            // Then - Should return default settings instead of crashing
            assertNotNull(result)
            assertEquals(Difficulty.NORMAL, result.difficulty)
        }

    @Test
    fun saveSettings_shouldPreserveAllFields() =
        runTest {
            // Given
            val settings =
                GameSettings(
                    difficulty = Difficulty.HARD,
                    themeConfig =
                        ThemeConfig(
                            visualTheme = VisualTheme.OCEAN,
                            pieceStyle = PieceStyle.GLASS,
                        ),
                    audioSettings =
                        AudioSettings(
                            musicEnabled = true,
                            soundEffectsEnabled = false,
                            musicVolume = 0.3f,
                            sfxVolume = 0.9f,
                            selectedMusicTheme = MusicTheme.MINIMAL,
                        ),
                )

            // When
            repository.saveSettings(settings)
            val retrieved = repository.getSettings()

            // Then - All fields should be preserved
            assertEquals(settings.difficulty, retrieved.difficulty)
            assertEquals(settings.themeConfig.visualTheme, retrieved.themeConfig.visualTheme)
            assertEquals(settings.themeConfig.pieceStyle, retrieved.themeConfig.pieceStyle)
            assertEquals(settings.audioSettings.musicEnabled, retrieved.audioSettings.musicEnabled)
            assertEquals(
                settings.audioSettings.soundEffectsEnabled,
                retrieved.audioSettings.soundEffectsEnabled,
            )
            assertEquals(settings.audioSettings.musicVolume, retrieved.audioSettings.musicVolume)
            assertEquals(settings.audioSettings.sfxVolume, retrieved.audioSettings.sfxVolume)
            assertEquals(
                settings.audioSettings.selectedMusicTheme,
                retrieved.audioSettings.selectedMusicTheme,
            )
        }
}
