package com.yet.tetris.domain.usecase

import com.yet.tetris.domain.model.audio.SoundEffect
import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.history.GameRecord
import com.yet.tetris.domain.model.settings.GameSettings
import com.yet.tetris.domain.repository.AudioRepository
import com.yet.tetris.domain.repository.GameHistoryRepository
import com.yet.tetris.domain.repository.GameStateRepository
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class PersistGameAudioUseCase(
    private val gameStateRepository: GameStateRepository,
    private val gameHistoryRepository: GameHistoryRepository,
    private val audioRepository: AudioRepository,
) {
    suspend fun initializeAudio() {
        audioRepository.initialize()
    }

    fun applyAudioSettings(settings: GameSettings) {
        audioRepository.applySettings(settings.audioSettings)
    }

    suspend fun playMusicIfEnabled(settings: GameSettings) {
        if (settings.audioSettings.musicEnabled) {
            audioRepository.playMusic(settings.audioSettings.selectedMusicTheme)
        }
    }

    fun stopMusic() {
        audioRepository.stopMusic()
    }

    fun playMoveSound() {
        audioRepository.playSoundEffect(SoundEffect.PIECE_MOVE)
    }

    fun playLockSounds(
        linesCleared: Int,
        levelIncreased: Boolean,
    ) {
        when (linesCleared) {
            0 -> audioRepository.playSoundEffect(SoundEffect.PIECE_DROP)
            4 -> audioRepository.playSoundEffect(SoundEffect.TETRIS)
            else -> audioRepository.playSoundEffect(SoundEffect.LINE_CLEAR)
        }

        if (levelIncreased) {
            audioRepository.playSoundEffect(SoundEffect.LEVEL_UP)
        }
    }

    fun playGameOverSound() {
        audioRepository.stopMusic()
        audioRepository.playSoundEffect(SoundEffect.GAME_OVER)
    }

    suspend fun saveCurrentState(gameState: GameState?) {
        gameState?.let { gameStateRepository.saveGameState(it) }
    }

    @OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)
    suspend fun saveCompletedGame(
        gameState: GameState,
        settings: GameSettings,
    ) {
        val record =
            GameRecord(
                id = Uuid.random().toString(),
                score = gameState.score,
                linesCleared = gameState.linesCleared,
                difficulty = settings.difficulty,
                timestamp = Clock.System.now().toEpochMilliseconds(),
            )
        gameHistoryRepository.saveGame(record)
        gameStateRepository.clearGameState()
    }
}
