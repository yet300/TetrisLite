package com.yet.tetris.database.dao

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.yet.tetris.database.BoardCells
import com.yet.tetris.database.CurrentGameState
import com.yet.tetris.database.db.DatabaseManager
import com.yet.tetris.domain.model.game.TetrominoType
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

@Singleton
class GameStateDao(private val databaseManager: DatabaseManager) {

    suspend fun saveGameState(
        score: Long,
        linesCleared: Long,
        currentPieceType: TetrominoType?,
        currentPieceRotation: Long,
        currentPositionX: Long,
        currentPositionY: Long,
        nextPieceType: TetrominoType,
        nextPieceRotation: Long,
        isGameOver: Boolean,
        isPaused: Boolean,
        boardWidth: Long,
        boardHeight: Long,
        boardCells: List<BoardCells>
    ) = withContext(Dispatchers.Default) {
        databaseManager.getDb().transaction {
            // Save game state
            databaseManager.getDb().currentGameStateQueries.insertOrReplaceGameState(
                score = score,
                linesCleared = linesCleared,
                currentPieceType = currentPieceType,
                currentPieceRotation = currentPieceRotation,
                currentPositionX = currentPositionX,
                currentPositionY = currentPositionY,
                nextPieceType = nextPieceType,
                nextPieceRotation = nextPieceRotation,
                isGameOver = isGameOver,
                isPaused = isPaused,
                boardWidth = boardWidth,
                boardHeight = boardHeight
            )
            
            // Clear old board cells
            databaseManager.getDb().boardCellsQueries.clearAllCells()
            
            // Insert new board cells
            boardCells.forEach { cell ->
                databaseManager.getDb().boardCellsQueries.insertCell(
                    positionX = cell.positionX,
                    positionY = cell.positionY,
                    pieceType = cell.pieceType
                )
            }
        }
    }
    
    suspend fun getGameState(): CurrentGameState? = withContext(Dispatchers.Default) {
        databaseManager.getDb().currentGameStateQueries.getGameState().awaitAsOneOrNull()
    }
    
    suspend fun getBoardCells(): List<BoardCells> = withContext(Dispatchers.Default) {
        databaseManager.getDb().boardCellsQueries.getAllCells().awaitAsList()
    }
    
    suspend fun clearGameState() = withContext(Dispatchers.Default) {
        databaseManager.getDb().transaction {
            databaseManager.getDb().currentGameStateQueries.clearGameState()
            databaseManager.getDb().boardCellsQueries.clearAllCells()
        }
    }
    
    suspend fun hasSavedState(): Boolean = withContext(Dispatchers.Default) {
        databaseManager.getDb().currentGameStateQueries.hasSavedState().awaitAsOne()
    }
    
    fun observeGameState(): Flow<CurrentGameState?> {
        return flow {
            val db = databaseManager.getDb()
            emitAll(
                db.currentGameStateQueries.getGameState()
                    .asFlow()
                    .mapToOneOrNull(Dispatchers.Default)
            )
        }
    }
}
