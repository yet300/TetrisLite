package com.yet.tetris.data.music

import com.yet.tetris.domain.model.audio.Envelope
import com.yet.tetris.domain.model.audio.MusicSequence
import com.yet.tetris.domain.model.audio.Note
import com.yet.tetris.domain.model.audio.SoundEffectParams
import com.yet.tetris.domain.model.audio.WaveformType
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AudioSynthesizerTest {
    @Test
    fun synthesizeSoundEffect_keepsSamplesWithinExpectedRange() {
        val params =
            SoundEffectParams(
                waveform = WaveformType.SAWTOOTH,
                startFrequency = 220f,
                endFrequency = 880f,
                duration = 0.2f,
                envelope = Envelope(attack = 0f, decay = 0.02f, sustain = 0.8f, release = 0.04f),
                volume = 1.0f,
            )

        val pcm = AudioSynthesizer.synthesizeSoundEffect(params)

        assertTrue(pcm.isNotEmpty())
        assertTrue(pcm.all { abs(it) <= 1.0f })
        assertEquals(0.0f, pcm.first(), absoluteTolerance = 0.001f)
        assertEquals(0.0f, pcm.last(), absoluteTolerance = 0.05f)
    }

    @Test
    fun synthesizeMusicSequence_returnsBoundedAudiblePcm() {
        val sequence =
            MusicSequence(
                notes =
                    listOf(
                        Note(Note.C4, Note.EIGHTH, volume = 0.7f),
                        Note(Note.E4, Note.EIGHTH, volume = 0.75f),
                        Note(Note.G4, Note.QUARTER, volume = 0.8f),
                    ),
                tempo = 128,
            )

        val pcm =
            AudioSynthesizer.synthesizeMusicSequence(
                sequence = sequence,
                waveform = WaveformType.SQUARE,
                noteEnvelope = Envelope(attack = 0.005f, decay = 0.04f, sustain = 0.65f, release = 0.03f),
            )

        assertTrue(pcm.isNotEmpty())
        assertTrue(pcm.all { abs(it) <= 1.0f })
        assertTrue(pcm.any { abs(it) > 0.05f })
    }
}
