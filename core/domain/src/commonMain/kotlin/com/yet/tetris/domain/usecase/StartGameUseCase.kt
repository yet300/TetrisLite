package com.yet.tetris.domain.usecase

import com.yet.tetris.domain.model.game.GameBoard
import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.game.LevelProgression
import com.yet.tetris.domain.model.game.Position
import com.yet.tetris.domain.model.settings.GameSettings
import jakarta.inject.Singleton

/**
 * Use case for initializing a new game.
 * Sets up the initial game state with an empty board and first two pieces.
 */
@Singleton
class StartGameUseCase(
    private val generateTetromino: GenerateTetrominoUseCase,
) {
    companion object {
        // Standard spawn position for new pieces (top-center of board)
        private const val SPAWN_X = 3
        private const val SPAWN_Y = 0
    }

    /**
     * Initializes a new game with the provided settings.
     *
     * @param settings Game settings to apply (difficulty, colors, etc.)
     * @return Initial GameState ready to play
     */
    operator fun invoke(settings: GameSettings): GameState {
        // Reset the tetromino generator for a new game
        generateTetromino.reset()

        // Generate first two pieces
        val currentPiece = generateTetromino()
        val nextPiece = generateTetromino()

        return GameState(
            board = GameBoard(),
            currentPiece = currentPiece,
            currentPosition = Position(SPAWN_X, SPAWN_Y),
            nextPiece = nextPiece,
            score = 0,
            linesCleared = 0,
            level = LevelProgression.START_LEVEL,
            isGameOver = false,
            isPaused = false,
        )
    }

    /**
     * Creates a new game with default settings.
     */
    fun startWithDefaults(): GameState = invoke(GameSettings())
}
