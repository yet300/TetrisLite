package com.yet.tetris.feature.game.integration

import com.yet.tetris.feature.game.GameComponent
import com.yet.tetris.feature.game.store.GameStore

internal val stateToModel: (GameStore.State) -> GameComponent.Model =
    { state ->
        GameComponent.Model(
            isLoading = state.isLoading || state.gameState == null,
            gameState = state.gameState,
            settings = state.settings,
            elapsedTime = state.elapsedTime,
            isGameOver = state.gameState?.isGameOver ?: false,
            finalScore = state.gameState?.score ?: 0,
            finalLinesCleared = state.gameState?.linesCleared ?: 0,
            ghostPieceY = state.ghostPieceY,
            comboStreak = state.comboStreak,
            visualEffectFeed = state.visualEffectFeed,
        )
    }
