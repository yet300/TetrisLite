package com.yet.tetris.domain.usecase

import com.yet.tetris.domain.model.game.Difficulty
import com.yet.tetris.domain.model.game.GameBoard
import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.game.Position
import com.yet.tetris.domain.model.game.Tetromino
import com.yet.tetris.domain.model.game.TetrominoType
import com.yet.tetris.domain.model.settings.GameSettings
import com.yet.tetris.domain.repository.GameSettingsRepository
import com.yet.tetris.domain.repository.GameStateRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

class InitializeGameSessionUseCaseTest {
    @Test
    fun uses_saved_state_WHEN_forceNewGame_is_false_and_saved_exists() =
        runTest {
            val settingsRepository = FakeGameSettingsRepository()
            val stateRepository = FakeGameStateRepository()
            val savedState = createGameState(score = 777)
            stateRepository.savedState = savedState

            val useCase =
                InitializeGameSessionUseCase(
                    gameSettingsRepository = settingsRepository,
                    gameStateRepository = stateRepository,
                    startGameUseCase = StartGameUseCase(GenerateTetrominoUseCase()),
                )

            val result = useCase(forceNewGame = false)

            assertEquals(savedState, result.gameState)
            assertEquals(settingsRepository.settings, result.settings)
            assertEquals(1, stateRepository.loadGameStateCallCount)
            assertEquals(0, stateRepository.clearGameStateCallCount)
        }

    @Test
    fun starts_new_game_WHEN_forceNewGame_is_true() =
        runTest {
            val settingsRepository =
                FakeGameSettingsRepository(
                    settings = GameSettings(difficulty = Difficulty.HARD),
                )
            val stateRepository = FakeGameStateRepository(savedState = createGameState(score = 999))

            val useCase =
                InitializeGameSessionUseCase(
                    gameSettingsRepository = settingsRepository,
                    gameStateRepository = stateRepository,
                    startGameUseCase = StartGameUseCase(GenerateTetrominoUseCase()),
                )

            val result = useCase(forceNewGame = true)

            assertEquals(settingsRepository.settings, result.settings)
            assertEquals(0, stateRepository.loadGameStateCallCount)
            assertEquals(1, stateRepository.clearGameStateCallCount)
            assertEquals(0, result.gameState.score)
            assertNotNull(result.gameState.currentPiece)
            assertFalse(result.gameState.isGameOver)
        }

    @Test
    fun starts_new_game_WHEN_no_saved_state_exists() =
        runTest {
            val settingsRepository = FakeGameSettingsRepository()
            val stateRepository = FakeGameStateRepository(savedState = null)

            val useCase =
                InitializeGameSessionUseCase(
                    gameSettingsRepository = settingsRepository,
                    gameStateRepository = stateRepository,
                    startGameUseCase = StartGameUseCase(GenerateTetrominoUseCase()),
                )

            val result = useCase(forceNewGame = false)

            assertEquals(1, stateRepository.loadGameStateCallCount)
            assertEquals(0, stateRepository.clearGameStateCallCount)
            assertEquals(0, result.gameState.score)
            assertNotNull(result.gameState.currentPiece)
            assertEquals(settingsRepository.settings, result.settings)
        }

    private class FakeGameSettingsRepository(
        val settings: GameSettings = GameSettings(),
    ) : GameSettingsRepository {
        override suspend fun getSettings(): GameSettings = settings

        override suspend fun saveSettings(settings: GameSettings) = Unit

        override fun observeSettings(): Flow<GameSettings> = flowOf(settings)
    }

    private class FakeGameStateRepository(
        var savedState: GameState? = null,
    ) : GameStateRepository {
        var loadGameStateCallCount: Int = 0
        var clearGameStateCallCount: Int = 0

        override suspend fun saveGameState(state: GameState) {
            savedState = state
        }

        override suspend fun loadGameState(): GameState? {
            loadGameStateCallCount++
            return savedState
        }

        override suspend fun clearGameState() {
            clearGameStateCallCount++
            savedState = null
        }

        override suspend fun hasSavedState(): Boolean = savedState != null
    }

    private fun createGameState(score: Long): GameState =
        GameState(
            board = GameBoard(width = 10, height = 20),
            currentPiece = Tetromino.create(TetrominoType.I),
            currentPosition = Position(x = 4, y = 0),
            nextPiece = Tetromino.create(TetrominoType.T),
            score = score,
            linesCleared = 0,
            level = 1,
            isGameOver = false,
            isPaused = false,
        )
}
