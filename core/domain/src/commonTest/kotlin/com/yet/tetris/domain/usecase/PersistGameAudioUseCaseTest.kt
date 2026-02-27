package com.yet.tetris.domain.usecase

import com.yet.tetris.domain.model.audio.AudioSettings
import com.yet.tetris.domain.model.audio.MusicTheme
import com.yet.tetris.domain.model.audio.SoundEffect
import com.yet.tetris.domain.model.game.Difficulty
import com.yet.tetris.domain.model.game.GameBoard
import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.game.Position
import com.yet.tetris.domain.model.game.Tetromino
import com.yet.tetris.domain.model.game.TetrominoType
import com.yet.tetris.domain.model.history.GameRecord
import com.yet.tetris.domain.model.settings.GameSettings
import com.yet.tetris.domain.repository.AudioRepository
import com.yet.tetris.domain.repository.GameHistoryRepository
import com.yet.tetris.domain.repository.GameStateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PersistGameAudioUseCaseTest {
    @Test
    fun initializes_audio_repository() =
        runTest {
            val gameStateRepository = FakeGameStateRepository()
            val gameHistoryRepository = FakeGameHistoryRepository()
            val audioRepository = FakeAudioRepository()
            val useCase =
                PersistGameAudioUseCase(
                    gameStateRepository = gameStateRepository,
                    gameHistoryRepository = gameHistoryRepository,
                    audioRepository = audioRepository,
                )

            useCase.initializeAudio()

            assertEquals(1, audioRepository.initializeCallCount)
        }

    @Test
    fun applies_audio_settings_from_game_settings() {
        val gameStateRepository = FakeGameStateRepository()
        val gameHistoryRepository = FakeGameHistoryRepository()
        val audioRepository = FakeAudioRepository()
        val useCase =
            PersistGameAudioUseCase(
                gameStateRepository = gameStateRepository,
                gameHistoryRepository = gameHistoryRepository,
                audioRepository = audioRepository,
            )
        val settings =
            GameSettings(
                audioSettings =
                    AudioSettings(
                        musicEnabled = false,
                        selectedMusicTheme = MusicTheme.MINIMAL,
                    ),
            )

        useCase.applyAudioSettings(settings)

        assertEquals(settings.audioSettings, audioRepository.lastAppliedSettings)
    }

    @Test
    fun plays_music_only_WHEN_enabled() =
        runTest {
            val gameStateRepository = FakeGameStateRepository()
            val gameHistoryRepository = FakeGameHistoryRepository()
            val audioRepository = FakeAudioRepository()
            val useCase =
                PersistGameAudioUseCase(
                    gameStateRepository = gameStateRepository,
                    gameHistoryRepository = gameHistoryRepository,
                    audioRepository = audioRepository,
                )

            useCase.playMusicIfEnabled(
                GameSettings(
                    audioSettings =
                        AudioSettings(
                            musicEnabled = true,
                            selectedMusicTheme = MusicTheme.MODERN,
                        ),
                ),
            )
            useCase.playMusicIfEnabled(
                GameSettings(
                    audioSettings =
                        AudioSettings(
                            musicEnabled = false,
                            selectedMusicTheme = MusicTheme.CLASSIC,
                        ),
                ),
            )

            assertEquals(1, audioRepository.playMusicCallCount)
            assertEquals(MusicTheme.MODERN, audioRepository.lastPlayedTheme)
        }

    @Test
    fun plays_expected_lock_sounds() {
        val gameStateRepository = FakeGameStateRepository()
        val gameHistoryRepository = FakeGameHistoryRepository()
        val audioRepository = FakeAudioRepository()
        val useCase =
            PersistGameAudioUseCase(
                gameStateRepository = gameStateRepository,
                gameHistoryRepository = gameHistoryRepository,
                audioRepository = audioRepository,
            )

        useCase.playLockSounds(linesCleared = 0, levelIncreased = false)
        useCase.playLockSounds(linesCleared = 4, levelIncreased = false)
        useCase.playLockSounds(linesCleared = 2, levelIncreased = true)

        assertEquals(
            listOf(
                SoundEffect.PIECE_DROP,
                SoundEffect.TETRIS,
                SoundEffect.LINE_CLEAR,
                SoundEffect.LEVEL_UP,
            ),
            audioRepository.playedEffects,
        )
    }

    @Test
    fun saveCurrentState_saves_only_non_null_state() =
        runTest {
            val gameStateRepository = FakeGameStateRepository()
            val gameHistoryRepository = FakeGameHistoryRepository()
            val audioRepository = FakeAudioRepository()
            val useCase =
                PersistGameAudioUseCase(
                    gameStateRepository = gameStateRepository,
                    gameHistoryRepository = gameHistoryRepository,
                    audioRepository = audioRepository,
                )
            val gameState = createGameState(score = 123)

            useCase.saveCurrentState(null)
            useCase.saveCurrentState(gameState)

            assertEquals(1, gameStateRepository.saveGameStateCallCount)
            assertEquals(gameState, gameStateRepository.lastSavedState)
        }

    @Test
    fun saveCompletedGame_saves_record_and_clears_saved_state() =
        runTest {
            val gameStateRepository = FakeGameStateRepository()
            val gameHistoryRepository = FakeGameHistoryRepository()
            val audioRepository = FakeAudioRepository()
            val useCase =
                PersistGameAudioUseCase(
                    gameStateRepository = gameStateRepository,
                    gameHistoryRepository = gameHistoryRepository,
                    audioRepository = audioRepository,
                )
            val gameState = createGameState(score = 456).copy(linesCleared = 12)
            val settings = GameSettings(difficulty = Difficulty.HARD)

            useCase.saveCompletedGame(gameState, settings)

            assertEquals(1, gameHistoryRepository.savedRecords.size)
            val record = gameHistoryRepository.savedRecords.single()
            assertEquals(456, record.score)
            assertEquals(12, record.linesCleared)
            assertEquals(Difficulty.HARD, record.difficulty)
            assertTrue(record.timestamp > 0)
            assertTrue(record.id.isNotBlank())
            assertEquals(1, gameStateRepository.clearGameStateCallCount)
        }

    @Test
    fun playGameOverSound_stops_music_and_plays_game_over() {
        val gameStateRepository = FakeGameStateRepository()
        val gameHistoryRepository = FakeGameHistoryRepository()
        val audioRepository = FakeAudioRepository()
        val useCase =
            PersistGameAudioUseCase(
                gameStateRepository = gameStateRepository,
                gameHistoryRepository = gameHistoryRepository,
                audioRepository = audioRepository,
            )

        useCase.playGameOverSound()

        assertEquals(1, audioRepository.stopMusicCallCount)
        assertEquals(listOf(SoundEffect.GAME_OVER), audioRepository.playedEffects)
    }

    private class FakeGameStateRepository : GameStateRepository {
        var lastSavedState: GameState? = null
        var saveGameStateCallCount: Int = 0
        var clearGameStateCallCount: Int = 0

        override suspend fun saveGameState(state: GameState) {
            saveGameStateCallCount++
            lastSavedState = state
        }

        override suspend fun loadGameState(): GameState? = lastSavedState

        override suspend fun clearGameState() {
            clearGameStateCallCount++
            lastSavedState = null
        }

        override suspend fun hasSavedState(): Boolean = lastSavedState != null
    }

    private class FakeGameHistoryRepository : GameHistoryRepository {
        val savedRecords: MutableList<GameRecord> = mutableListOf()

        override suspend fun saveGame(record: GameRecord) {
            savedRecords += record
        }

        override suspend fun getAllGames(): List<GameRecord> = savedRecords.toList()

        override suspend fun getGameById(id: String): GameRecord? =
            savedRecords.firstOrNull { it.id == id }

        override fun observeGames(): Flow<List<GameRecord>> = flowOf(savedRecords.toList())

        override suspend fun deleteGame(id: String) {
            savedRecords.removeAll { it.id == id }
        }

        override suspend fun clearAllGames() {
            savedRecords.clear()
        }
    }

    private class FakeAudioRepository : AudioRepository {
        var initializeCallCount: Int = 0
        var playMusicCallCount: Int = 0
        var stopMusicCallCount: Int = 0
        var lastAppliedSettings: AudioSettings? = null
        var lastPlayedTheme: MusicTheme? = null
        val playedEffects: MutableList<SoundEffect> = mutableListOf()
        var releaseCallCount: Int = 0

        override suspend fun initialize() {
            initializeCallCount++
        }

        override suspend fun playMusic(theme: MusicTheme) {
            playMusicCallCount++
            lastPlayedTheme = theme
        }

        override fun playSoundEffect(effect: SoundEffect) {
            playedEffects += effect
        }

        override fun stopMusic() {
            stopMusicCallCount++
        }

        override fun applySettings(settings: AudioSettings) {
            lastAppliedSettings = settings
        }

        override suspend fun release() {
            releaseCallCount++
        }
    }

    private fun createGameState(score: Long): GameState =
        GameState(
            board = GameBoard(width = 10, height = 20),
            currentPiece = Tetromino.create(TetrominoType.I),
            currentPosition = Position(x = 3, y = 0),
            nextPiece = Tetromino.create(TetrominoType.T),
            score = score,
            linesCleared = 0,
            level = 1,
            isGameOver = false,
            isPaused = false,
        )
}
