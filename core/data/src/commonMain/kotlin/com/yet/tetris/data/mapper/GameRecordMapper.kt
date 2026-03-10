package com.yet.tetris.data.mapper

import com.yet.tetris.data.model.GameRecordDto
import com.yet.tetris.domain.model.history.GameRecord

// Domain to DTO
fun GameRecord.toDto(): GameRecordDto =
    GameRecordDto(
        id = id,
        score = score,
        linesCleared = linesCleared,
        level = level,
        difficulty = difficulty.toDto(),
        timestamp = timestamp,
        durationMs = durationMs,
        piecesPlaced = piecesPlaced,
        maxCombo = maxCombo,
        tetrisesCleared = tetrisesCleared,
        tSpinClears = tSpinClears,
        perfectClears = perfectClears,
        hardDrops = hardDrops,
        hardDropCells = hardDropCells,
        softDropCells = softDropCells,
    )

// DTO to Domain
fun GameRecordDto.toDomain(): GameRecord =
    GameRecord(
        id = id,
        score = score,
        linesCleared = linesCleared,
        level = level,
        difficulty = difficulty.toDomain(),
        timestamp = timestamp,
        durationMs = durationMs,
        piecesPlaced = piecesPlaced,
        maxCombo = maxCombo,
        tetrisesCleared = tetrisesCleared,
        tSpinClears = tSpinClears,
        perfectClears = perfectClears,
        hardDrops = hardDrops,
        hardDropCells = hardDropCells,
        softDropCells = softDropCells,
    )
