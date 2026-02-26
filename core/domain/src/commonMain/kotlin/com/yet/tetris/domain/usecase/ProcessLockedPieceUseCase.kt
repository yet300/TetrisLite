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
        val oldLinesCleared = gameState.linesCleared
        val updatedState = lockPieceUseCase(gameState)
        val linesClearedThisLock = (updatedState.linesCleared - oldLinesCleared).toInt()

        val feedback =
            planVisualFeedbackUseCase(
                currentComboStreak = currentComboStreak,
                linesClearedThisLock = linesClearedThisLock,
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
                        ),
                )
            }

        return Result(
            gameState = updatedState,
            ghostPieceY = advanceGameTickUseCase.calculateGhostY(updatedState),
            linesCleared = linesClearedThisLock,
            nextComboStreak = feedback.nextComboStreak,
            visualEffectFeed = visualEffectFeed,
            levelIncreased = updatedState.level > gameState.level,
        )
    }
}
