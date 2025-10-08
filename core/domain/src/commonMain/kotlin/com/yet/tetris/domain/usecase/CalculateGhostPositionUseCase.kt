package com.yet.tetris.domain.usecase

import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.game.Position
import com.yet.tetris.domain.model.game.Tetromino
import jakarta.inject.Singleton
import org.koin.core.annotation.Single

/**
 * Calculates the landing position (Y coordinate) for the ghost piece preview.
 * The ghost piece shows where the current piece will land if dropped.
 */
@Singleton
class CalculateGhostPositionUseCase {
    
    /**
     * Calculate the Y position where the piece would land if hard dropped
     * 
     * @param gameState Current game state
     * @param piece The piece to calculate landing position for
     * @param currentPosition Current position of the piece
     * @return Y coordinate where the piece would land, or null if piece is invalid
     */
    operator fun invoke(
        gameState: GameState,
        piece: Tetromino,
        currentPosition: Position
    ): Int? {
        var testY = currentPosition.y

        while (testY < gameState.board.height) {
            val wouldCollide = piece.blocks.any { blockPos ->
                val absolutePos = Position(
                    currentPosition.x + blockPos.x,
                    testY + blockPos.y + 1
                )

                absolutePos.y >= gameState.board.height ||
                        absolutePos.x < 0 ||
                        absolutePos.x >= gameState.board.width ||
                        gameState.board.cells.containsKey(absolutePos)
            }

            if (wouldCollide) {
                return testY
            }
            testY++
        }

        return testY
    }
}
