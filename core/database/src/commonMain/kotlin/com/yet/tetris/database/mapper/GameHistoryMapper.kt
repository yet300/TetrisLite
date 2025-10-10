package com.yet.tetris.database.mapper

import com.yet.tetris.database.GameHistory
import com.yet.tetris.domain.model.history.GameRecord

// Database entity to Domain
fun GameHistory.toDomain(): GameRecord = GameRecord(
    id = id,
    score = score,
    linesCleared = linesCleared,
    difficulty = difficulty,
    timestamp = timestamp
)

// Domain to Database entity
fun GameRecord.toEntity(): GameHistory = GameHistory(
    id = id,
    score = score,
    linesCleared = linesCleared,
    difficulty = difficulty,
    timestamp = timestamp
)
