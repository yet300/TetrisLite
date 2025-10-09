package com.yet.tetris.data.mapper

import com.yet.tetris.data.model.GameRecordDto
import com.yet.tetris.domain.model.history.GameRecord

// Domain to DTO
fun GameRecord.toDto(): GameRecordDto = GameRecordDto(
    id = id,
    score = score,
    linesCleared = linesCleared,
    difficulty = difficulty.toDto(),
    timestamp = timestamp
)

// DTO to Domain
fun GameRecordDto.toDomain(): GameRecord = GameRecord(
    id = id,
    score = score,
    linesCleared = linesCleared,
    difficulty = difficulty.toDomain(),
    timestamp = timestamp
)
