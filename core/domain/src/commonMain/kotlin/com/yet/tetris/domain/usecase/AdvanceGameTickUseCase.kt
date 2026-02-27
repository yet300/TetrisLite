package com.yet.tetris.domain.usecase

import com.yet.tetris.domain.model.game.GameState

class AdvanceGameTickUseCase(
    private val movePieceUseCase: MovePieceUseCase,
    private val calculateGhostPositionUseCase: CalculateGhostPositionUseCase,
) {
    sealed interface Result {
        data class Moved(
            val gameState: GameState,
            val ghostPieceY: Int?,
        ) : Result

        data class RequiresLock(
            val gameState: GameState,
        ) : Result
    }

    operator fun invoke(gameState: GameState): Result {
        val movedState = movePieceUseCase.moveDown(gameState)
        return if (movedState != null) {
            Result.Moved(
                gameState = movedState,
                ghostPieceY = calculateGhostY(movedState),
            )
        } else {
            Result.RequiresLock(gameState)
        }
    }

    fun calculateGhostY(gameState: GameState): Int? =
        gameState.currentPiece?.let { piece ->
            calculateGhostPositionUseCase(
                gameState = gameState,
                piece = piece,
                currentPosition = gameState.currentPosition,
            )
        }
}
