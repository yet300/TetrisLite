package com.yet.tetris.domain.model.game

import kotlin.test.Test
import kotlin.test.assertEquals

class LevelProgressionTest {
    @Test
    fun levelForLines_shouldStartAtOne() {
        assertEquals(1, LevelProgression.levelForLines(0))
    }

    @Test
    fun levelForLines_shouldIncreaseEveryTenLines() {
        assertEquals(1, LevelProgression.levelForLines(9))
        assertEquals(2, LevelProgression.levelForLines(10))
        assertEquals(3, LevelProgression.levelForLines(20))
    }
}
