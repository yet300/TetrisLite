package com.yet.tetris.domain.usecase

import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.settings.GameSettings
import com.yet.tetris.domain.repository.GameSettingsRepository
import com.yet.tetris.domain.repository.GameStateRepository


class InitializeGameSessionUseCase(
    private val gameSettingsRepository: GameSettingsRepository,
    private val gameStateRepository: GameStateRepository,
    private val startGameUseCase: StartGameUseCase,
) {
    data class Result(
        val gameState: GameState,
        val settings: GameSettings,
    )

    suspend operator fun invoke(forceNewGame: Boolean): Result {
        val settings = gameSettingsRepository.getSettings()
        val gameState =
            if (forceNewGame) {
                gameStateRepository.clearGameState()
                startGameUseCase(settings)
            } else {
                gameStateRepository.loadGameState() ?: startGameUseCase(settings)
            }
        return Result(
            gameState = gameState,
            settings = settings,
        )
    }
}
