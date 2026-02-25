package com.yet.tetris.data.repository

import com.yet.tetris.database.dao.GameStateDao
import com.yet.tetris.database.mapper.toDomain
import com.yet.tetris.database.mapper.toEntities
import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.repository.GameStateRepository
import jakarta.inject.Singleton

/**
 * Implementation of GameStateRepository using SQLDelight.
 * Used for pause/resume functionality.
 */
@Singleton
class GameStateRepositoryImpl(
    private val gameStateDao: GameStateDao,
) : GameStateRepository {
    override suspend fun saveGameState(state: GameState) {
        try {
            val entities = state.toEntities()
            val data = entities.gameState

            gameStateDao.saveGameState(
                score = data.score,
                linesCleared = data.linesCleared,
                level = data.level,
                currentPieceType = data.currentPieceType,
                currentPieceRotation = data.currentPieceRotation,
                currentPositionX = data.currentPositionX,
                currentPositionY = data.currentPositionY,
                nextPieceType = data.nextPieceType,
                nextPieceRotation = data.nextPieceRotation,
                isGameOver = data.isGameOver,
                isPaused = data.isPaused,
                boardWidth = data.boardWidth,
                boardHeight = data.boardHeight,
                boardCells = entities.boardCells,
            )
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun loadGameState(): GameState? =
        try {
            val gameState = gameStateDao.getGameState()
            val boardCells = gameStateDao.getBoardCells()

            gameState?.toDomain(boardCells)
        } catch (e: Exception) {
            null
        }

    override suspend fun clearGameState() {
        try {
            gameStateDao.clearGameState()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun hasSavedState(): Boolean =
        try {
            gameStateDao.hasSavedState()
        } catch (e: Exception) {
            false
        }
}
