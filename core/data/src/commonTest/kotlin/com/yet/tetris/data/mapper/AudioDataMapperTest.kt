package com.yet.tetris.data.mapper

import com.yet.tetris.domain.model.audio.MusicTheme
import com.yet.tetris.domain.model.audio.WaveformType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AudioDataMapperTest {
    @Test
    fun getSequenceForTheme_returnsSequenceForEachPlayableTheme() {
        MusicTheme.entries
            .filterNot { it == MusicTheme.NONE }
            .forEach { theme ->
                assertNotNull(getSequenceForTheme(theme), "Expected a sequence for $theme")
            }
    }

    @Test
    fun getSequenceForTheme_returnsNullForNone() {
        assertNull(getSequenceForTheme(MusicTheme.NONE))
    }

    @Test
    fun getWaveformForTheme_returnsExpectedWaveforms() {
        assertEquals(WaveformType.SQUARE, getWaveformForTheme(MusicTheme.CLASSIC))
        assertEquals(WaveformType.SAWTOOTH, getWaveformForTheme(MusicTheme.MODERN))
        assertEquals(WaveformType.TRIANGLE, getWaveformForTheme(MusicTheme.MINIMAL))
        assertEquals(WaveformType.SQUARE, getWaveformForTheme(MusicTheme.ARCADE))
        assertEquals(WaveformType.TRIANGLE, getWaveformForTheme(MusicTheme.DUSK))
        assertEquals(WaveformType.SAWTOOTH, getWaveformForTheme(MusicTheme.BATTLE))
    }
}
