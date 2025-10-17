package com.yet.tetris.domain.model.audio

/**
 * Represents a musical note with frequency and duration.
 * Used for procedural music generation.
 */
data class Note(
    val frequency: Float, // Hz
    val duration: Float, // seconds
    val volume: Float = 1.0f,
) {
    companion object {
        // Musical note frequencies (A4 = 440Hz standard)
        const val C4 = 261.63f
        const val D4 = 293.66f
        const val E4 = 329.63f
        const val F4 = 349.23f
        const val G4 = 392.00f
        const val A4 = 440.00f
        const val B4 = 493.88f
        const val C5 = 523.25f
        const val D5 = 587.33f
        const val E5 = 659.25f
        const val F5 = 698.46f
        const val G5 = 783.99f
        const val A5 = 880.00f
        const val B5 = 987.77f

        // Common note durations (at 120 BPM)
        const val WHOLE = 2.0f
        const val HALF = 1.0f
        const val QUARTER = 0.5f
        const val EIGHTH = 0.25f
        const val SIXTEENTH = 0.125f
    }
}

/**
 * Waveform types for sound synthesis.
 */
enum class WaveformType {
    SINE, // Pure tone, smooth
    SQUARE, // Retro, harsh
    TRIANGLE, // Softer than square
    SAWTOOTH, // Bright, buzzy
    NOISE, // White noise for percussion
}

/**
 * ADSR envelope for shaping sound over time.
 * Attack, Decay, Sustain, Release
 */
data class Envelope(
    val attack: Float = 0.01f, // seconds
    val decay: Float = 0.1f, // seconds
    val sustain: Float = 0.7f, // level (0-1)
    val release: Float = 0.2f, // seconds
)

/**
 * Parameters for procedural sound effect generation.
 */
data class SoundEffectParams(
    val waveform: WaveformType,
    val startFrequency: Float,
    val endFrequency: Float = startFrequency,
    val duration: Float,
    val envelope: Envelope = Envelope(),
    val volume: Float = 1.0f,
)

/**
 * Music sequence definition for procedural generation.
 */
data class MusicSequence(
    val notes: List<Note>,
    val tempo: Int = 120, // BPM
    val loop: Boolean = true,
)

/**
 * Predefined sound effect parameters for common game events.
 */
object SoundEffectPresets {
    val PIECE_MOVE =
        SoundEffectParams(
            waveform = WaveformType.SQUARE,
            startFrequency = 200f,
            duration = 0.05f,
            envelope = Envelope(attack = 0.01f, decay = 0.02f, sustain = 0.5f, release = 0.02f),
            volume = 0.3f,
        )

    val PIECE_ROTATE =
        SoundEffectParams(
            waveform = WaveformType.TRIANGLE,
            startFrequency = 400f,
            endFrequency = 600f,
            duration = 0.08f,
            envelope = Envelope(attack = 0.01f, decay = 0.03f, sustain = 0.6f, release = 0.04f),
            volume = 0.4f,
        )

    val PIECE_DROP =
        SoundEffectParams(
            waveform = WaveformType.SQUARE,
            startFrequency = 150f,
            endFrequency = 80f,
            duration = 0.15f,
            envelope = Envelope(attack = 0.01f, decay = 0.05f, sustain = 0.4f, release = 0.09f),
            volume = 0.5f,
        )

    val LINE_CLEAR =
        SoundEffectParams(
            waveform = WaveformType.SAWTOOTH,
            startFrequency = 300f,
            endFrequency = 800f,
            duration = 0.3f,
            envelope = Envelope(attack = 0.02f, decay = 0.1f, sustain = 0.7f, release = 0.18f),
            volume = 0.6f,
        )

    val TETRIS =
        SoundEffectParams(
            waveform = WaveformType.SAWTOOTH,
            startFrequency = 400f,
            endFrequency = 1200f,
            duration = 0.5f,
            envelope = Envelope(attack = 0.02f, decay = 0.15f, sustain = 0.8f, release = 0.33f),
            volume = 0.7f,
        )

    val GAME_OVER =
        SoundEffectParams(
            waveform = WaveformType.TRIANGLE,
            startFrequency = 440f,
            endFrequency = 110f,
            duration = 1.0f,
            envelope = Envelope(attack = 0.05f, decay = 0.3f, sustain = 0.5f, release = 0.65f),
            volume = 0.6f,
        )

    val LEVEL_UP =
        SoundEffectParams(
            waveform = WaveformType.SAWTOOTH,
            startFrequency = 500f,
            endFrequency = 1000f,
            duration = 0.5f,
            envelope = Envelope(attack = 0.05f, decay = 0.15f, sustain = 0.7f, release = 0.25f),
            volume = 0.7f,
        )
}

/**
 * Predefined music sequences inspired by classic Tetris (but original compositions).
 */
object MusicSequencePresets {
    /**
     * Classic 8-bit chiptune style melody.
     * Original composition inspired by Russian folk music style.
     */
    val CLASSIC_THEME =
        MusicSequence(
            notes =
                listOf(
                    // Phrase 1
                    Note(Note.E5, Note.QUARTER),
                    Note(Note.B4, Note.EIGHTH),
                    Note(Note.C5, Note.EIGHTH),
                    Note(Note.D5, Note.QUARTER),
                    Note(Note.C5, Note.EIGHTH),
                    Note(Note.B4, Note.EIGHTH),
                    Note(Note.A4, Note.QUARTER),
                    Note(Note.A4, Note.EIGHTH),
                    Note(Note.C5, Note.EIGHTH),
                    Note(Note.E5, Note.QUARTER),
                    Note(Note.D5, Note.EIGHTH),
                    Note(Note.C5, Note.EIGHTH),
                    Note(Note.B4, Note.QUARTER + Note.EIGHTH),
                    Note(Note.C5, Note.EIGHTH),
                    Note(Note.D5, Note.QUARTER),
                    Note(Note.E5, Note.QUARTER),
                    Note(Note.C5, Note.QUARTER),
                    Note(Note.A4, Note.QUARTER),
                    Note(Note.A4, Note.HALF),
                    // Phrase 2
                    Note(Note.D5, Note.QUARTER + Note.EIGHTH),
                    Note(Note.F5, Note.EIGHTH),
                    Note(Note.A5, Note.QUARTER),
                    Note(Note.G5, Note.EIGHTH),
                    Note(Note.F5, Note.EIGHTH),
                    Note(Note.E5, Note.QUARTER + Note.EIGHTH),
                    Note(Note.C5, Note.EIGHTH),
                    Note(Note.E5, Note.QUARTER),
                    Note(Note.D5, Note.EIGHTH),
                    Note(Note.C5, Note.EIGHTH),
                    Note(Note.B4, Note.QUARTER),
                    Note(Note.B4, Note.EIGHTH),
                    Note(Note.C5, Note.EIGHTH),
                    Note(Note.D5, Note.QUARTER),
                    Note(Note.E5, Note.QUARTER),
                    Note(Note.C5, Note.QUARTER),
                    Note(Note.A4, Note.QUARTER),
                    Note(Note.A4, Note.HALF),
                ),
            tempo = 144,
            loop = true,
        )

    /**
     * Modern electronic style melody.
     */
    val MODERN_THEME =
        MusicSequence(
            notes =
                listOf(
                    Note(Note.C5, Note.EIGHTH),
                    Note(Note.E5, Note.EIGHTH),
                    Note(Note.G5, Note.EIGHTH),
                    Note(Note.E5, Note.EIGHTH),
                    Note(Note.C5, Note.EIGHTH),
                    Note(Note.E5, Note.EIGHTH),
                    Note(Note.G5, Note.QUARTER),
                    Note(Note.A4, Note.EIGHTH),
                    Note(Note.C5, Note.EIGHTH),
                    Note(Note.E5, Note.EIGHTH),
                    Note(Note.C5, Note.EIGHTH),
                    Note(Note.A4, Note.EIGHTH),
                    Note(Note.C5, Note.EIGHTH),
                    Note(Note.E5, Note.QUARTER),
                ),
            tempo = 128,
            loop = true,
        )

    val MINIMAL_THEME =
        MusicSequence(
            notes =
                listOf(
                    Note(Note.C4, Note.QUARTER),
                    Note(Note.E4, Note.QUARTER),
                    Note(Note.G4, Note.HALF),
                    Note(Note.A4, Note.QUARTER),
                    Note(Note.G4, Note.QUARTER),
                    Note(Note.E4, Note.HALF),
                ),
            tempo = 100,
            loop = true,
        )
}
