package com.yet.tetris.database.mapper

import com.yet.tetris.database.GameHistory
import com.yet.tetris.domain.model.history.GameRecord

// Database entity to Domain
fun GameHistory.toDomain(): GameRecord =
    GameRecord(
        id = id,
        score = score,
        linesCleared = linesCleared,
        level = level.toInt(),
        difficulty = difficulty,
        timestamp = timestamp,
        durationMs = durationMs,
        piecesPlaced = piecesPlaced,
        maxCombo = maxCombo.toInt(),
        tetrisesCleared = tetrisesCleared,
        tSpinClears = tSpinClears,
        perfectClears = perfectClears,
        hardDrops = hardDrops,
        hardDropCells = hardDropCells,
        softDropCells = softDropCells,
    )

// Domain to Database entity
fun GameRecord.toEntity(): GameHistory =
    GameHistory(
        id = id,
        score = score,
        linesCleared = linesCleared,
        level = level.toLong(),
        difficulty = difficulty,
        timestamp = timestamp,
        durationMs = durationMs,
        piecesPlaced = piecesPlaced,
        maxCombo = maxCombo.toLong(),
        tetrisesCleared = tetrisesCleared,
        tSpinClears = tSpinClears,
        perfectClears = perfectClears,
        hardDrops = hardDrops,
        hardDropCells = hardDropCells,
        softDropCells = softDropCells,
    )
