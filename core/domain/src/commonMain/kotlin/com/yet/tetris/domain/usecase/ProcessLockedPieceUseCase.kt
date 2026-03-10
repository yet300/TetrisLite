package com.yet.tetris.domain.usecase

import com.yet.tetris.domain.model.effects.VisualEffectBurst
import com.yet.tetris.domain.model.effects.VisualEffectFeed
import com.yet.tetris.domain.model.game.GameState

class ProcessLockedPieceUseCase(
    private val lockPieceUseCase: LockPieceUseCase,
    private val planVisualFeedbackUseCase: PlanVisualFeedbackUseCase,
    private val advanceGameTickUseCase: AdvanceGameTickUseCase,
) {
    data class Result(
        val gameState: GameState,
        val ghostPieceY: Int?,
        val linesCleared: Int,
        val nextComboStreak: Int,
        val visualEffectFeed: VisualEffectFeed?,
        val levelIncreased: Boolean,
    )

    operator fun invoke(
        gameState: GameState,
        currentComboStreak: Int,
        currentVisualSequence: Long,
    ): Result {
        val lockResult = lockPieceUseCase.invokeDetailed(gameState)
        val updatedState = lockResult.gameState

        val feedback =
            planVisualFeedbackUseCase(
                currentComboStreak = currentComboStreak,
                linesClearedThisLock = lockResult.linesCleared,
                clearedRowsThisLock = lockResult.clearedRows,
                lockCellsThisLock = lockResult.lockCells,
            )

        val finalState =
            updatedState.copy(
                maxCombo = maxOf(updatedState.maxCombo, feedback.nextComboStreak),
            )

        val visualEffectFeed =
            feedback.burst?.let { burstSpec ->
                val nextSequence = currentVisualSequence + 1
                VisualEffectFeed(
                    sequence = nextSequence,
                    latest =
                        VisualEffectBurst(
                            id = nextSequence,
                            linesCleared = burstSpec.linesCleared,
                            comboStreak = burstSpec.comboStreak,
                            intensity = burstSpec.intensity,
                            power = burstSpec.power,
                            events = burstSpec.events,
                            clearedRows = burstSpec.clearedRows,
                            lockCells = burstSpec.lockCells,
                        ),
                )
            }

        return Result(
            gameState = finalState,
            ghostPieceY = advanceGameTickUseCase.calculateGhostY(finalState),
            linesCleared = lockResult.linesCleared,
            nextComboStreak = feedback.nextComboStreak,
            visualEffectFeed = visualEffectFeed,
            levelIncreased = finalState.level > gameState.level,
        )
    }
}
