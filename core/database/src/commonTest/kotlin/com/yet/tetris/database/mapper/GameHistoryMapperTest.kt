package com.yet.tetris.database.mapper

import com.yet.tetris.database.GameHistory
import com.yet.tetris.domain.model.game.Difficulty
import com.yet.tetris.domain.model.history.GameRecord
import kotlin.test.Test
import kotlin.test.assertEquals

class GameHistoryMapperTest {
    @Test
    fun toDomain_shouldMapAllFieldsCorrectly() {
        // Given
        val entity =
            GameHistory(
                id = "test-id",
                score = 5000,
                linesCleared = 50,
                difficulty = Difficulty.HARD,
                timestamp = 1234567890,
            )

        // When
        val domain = entity.toDomain()

        // Then
        assertEquals(entity.id, domain.id)
        assertEquals(entity.score, domain.score)
        assertEquals(entity.linesCleared, domain.linesCleared)
        assertEquals(entity.difficulty, domain.difficulty)
        assertEquals(entity.timestamp, domain.timestamp)
    }

    @Test
    fun toEntity_shouldMapAllFieldsCorrectly() {
        // Given
        val domain =
            GameRecord(
                id = "test-id",
                score = 5000,
                linesCleared = 50,
                difficulty = Difficulty.EASY,
                timestamp = 1234567890,
            )

        // When
        val entity = domain.toEntity()

        // Then
        assertEquals(domain.id, entity.id)
        assertEquals(domain.score, entity.score)
        assertEquals(domain.linesCleared, entity.linesCleared)
        assertEquals(domain.difficulty, entity.difficulty)
        assertEquals(domain.timestamp, entity.timestamp)
    }

    @Test
    fun roundTrip_shouldPreserveData() {
        // Given
        val original =
            GameRecord(
                id = "test-id",
                score = 10000,
                linesCleared = 100,
                difficulty = Difficulty.NORMAL,
                timestamp = 9876543210,
            )

        // When
        val entity = original.toEntity()
        val result = entity.toDomain()

        // Then
        assertEquals(original.id, result.id)
        assertEquals(original.score, result.score)
        assertEquals(original.linesCleared, result.linesCleared)
        assertEquals(original.difficulty, result.difficulty)
        assertEquals(original.timestamp, result.timestamp)
    }
}
