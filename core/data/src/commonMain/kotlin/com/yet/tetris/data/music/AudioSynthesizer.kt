package com.yet.tetris.data.music

import com.yet.tetris.domain.model.audio.Envelope
import com.yet.tetris.domain.model.audio.MusicSequence
import com.yet.tetris.domain.model.audio.SoundEffectParams
import com.yet.tetris.domain.model.audio.WaveformType
import kotlin.math.*
import kotlin.random.Random


/**
 * A singleton object responsible for generating raw audio data (PCM) from descriptive parameters.
 * This synthesizer is platform-agnostic and operates entirely in the commonMain module.
 * It creates waveforms, applies envelopes, and constructs sound effects and music sequences.
 */
object AudioSynthesizer {

    /**
     * The sample rate in Hertz (samples per second). 44100 Hz is CD quality and standard for audio processing.
     */
    const val SAMPLE_RATE = 44100

    /**
     * Generates PCM audio data for a single sound effect.
     *
     * @param params The descriptive parameters for the sound effect to generate.
     * @return A FloatArray containing the raw PCM data, with values ranging from -1.0f to 1.0f.
     */
    fun synthesizeSoundEffect(params: SoundEffectParams): FloatArray {
        val numSamples = (params.duration * SAMPLE_RATE).toInt()
        if (numSamples <= 0) return FloatArray(0)

        val pcmData = FloatArray(numSamples)
        val envelope = params.envelope

        // Calculate the duration of each envelope phase in samples.
        val attackSamples = (envelope.attack * SAMPLE_RATE).toInt()
        val decaySamples = (envelope.decay * SAMPLE_RATE).toInt()
        val releaseSamples = (envelope.release * SAMPLE_RATE).toInt()

        // Ensure sustain phase is not negative if the other phases are too long.
        val sustainSamples = max(0, numSamples - attackSamples - decaySamples - releaseSamples)

        val sustainAmplitude = params.volume * envelope.sustain

        for (i in 0 until numSamples) {
            val time = i.toFloat() / SAMPLE_RATE
            val progress = time / params.duration

            // 1. Calculate the amplitude multiplier based on the ADSR envelope.
            val amplitude = when {
                // Attack phase: linear ramp from 0 to peak volume.
                i < attackSamples -> {
                    val attackProgress = i.toFloat() / attackSamples
                    attackProgress * params.volume
                }
                // Decay phase: linear ramp from peak volume to sustain level.
                i < attackSamples + decaySamples -> {
                    val decayProgress = (i - attackSamples).toFloat() / decaySamples
                    // Linear interpolation (lerp) between peak and sustain.
                    params.volume * (1.0f - decayProgress) + sustainAmplitude * decayProgress
                }
                // Sustain phase: constant sustain level.
                i < attackSamples + decaySamples + sustainSamples -> {
                    sustainAmplitude
                }
                // Release phase: linear ramp from sustain level to 0.
                else -> {
                    val releaseProgress = (i - (numSamples - releaseSamples)).toFloat() / releaseSamples
                    sustainAmplitude * (1.0f - releaseProgress)
                }
            }

            // 2. Calculate the current frequency, allowing for pitch slides.
            val currentFrequency = params.startFrequency + (params.endFrequency - params.startFrequency) * progress

            // 3. Generate the raw sample value based on the chosen waveform.
            // The 'phase' determines the position within a single wave cycle.
            val phase = 2 * PI * currentFrequency * time
            val rawValue = when (params.waveform) {
                WaveformType.SINE -> sin(phase).toFloat()
                WaveformType.SQUARE -> sign(sin(phase)).toFloat() // Creates a square wave from a sine wave.
                WaveformType.TRIANGLE -> (2.0 / PI * asin(sin(phase))).toFloat() // Creates a triangle wave.
                WaveformType.SAWTOOTH -> (2.0f / PI.toFloat()) * atan(tan(phase / 2.0)).toFloat() // Creates a sawtooth wave.
                WaveformType.NOISE -> Random.nextFloat() * 2.0f - 1.0f // White noise.
            }

            // 4. Combine the raw waveform value with the final calculated amplitude.
            pcmData[i] = rawValue * amplitude
        }
        return pcmData
    }

    /**
     * Generates PCM audio data for a complete musical sequence.
     * It synthesizes each note individually and concatenates them into a single audio buffer.
     *
     * @param sequence The music sequence to synthesize.
     * @param waveform The waveform to use for all notes in the sequence (e.g., SQUARE for chiptune).
     * @param noteEnvelope An optional ADSR envelope to apply to each note, creating a "plucking" or "fading" effect.
     * @return A FloatArray containing the raw PCM data for the entire sequence.
     */
    fun synthesizeMusicSequence(
        sequence: MusicSequence,
        waveform: WaveformType = WaveformType.SQUARE,
        noteEnvelope: Envelope = Envelope(attack = 0.01f, decay = 0.2f, sustain = 0.5f, release = 0.05f)
    ): FloatArray {
        val finalPcm = mutableListOf<Float>()

        // The Note durations are defined for 120 BPM. We need to scale them for the sequence's actual tempo.
        val tempoFactor = 120.0f / sequence.tempo

        for (note in sequence.notes) {
            val noteDuration = note.duration * tempoFactor

            // Generate audio for a single note using a simplified version of the SFX synthesizer.
            val numSamples = (noteDuration * SAMPLE_RATE).toInt()
            if (numSamples <= 0) continue

            val attackSamples = (noteEnvelope.attack * SAMPLE_RATE).toInt()
            val decaySamples = (noteEnvelope.decay * SAMPLE_RATE).toInt()
            val releaseSamples = (noteEnvelope.release * SAMPLE_RATE).toInt()
            val sustainSamples = max(0, numSamples - attackSamples - decaySamples - releaseSamples)

            val peakAmplitude = note.volume
            val sustainAmplitude = peakAmplitude * noteEnvelope.sustain

            for (i in 0 until numSamples) {
                val time = i.toFloat() / SAMPLE_RATE

                val amplitude = when {
                    i < attackSamples -> (i.toFloat() / attackSamples) * peakAmplitude
                    i < attackSamples + decaySamples -> {
                        val decayProgress = (i - attackSamples).toFloat() / decaySamples
                        peakAmplitude * (1.0f - decayProgress) + sustainAmplitude * decayProgress
                    }
                    i < attackSamples + decaySamples + sustainSamples -> sustainAmplitude
                    else -> {
                        val releaseProgress = (i - (numSamples - releaseSamples)).toFloat() / releaseSamples
                        sustainAmplitude * (1.0f - releaseProgress)
                    }
                }

                val phase = 2 * PI * note.frequency * time
                val rawValue = when (waveform) {
                    WaveformType.SINE -> sin(phase).toFloat()
                    WaveformType.SQUARE -> sign(sin(phase)).toFloat()
                    WaveformType.TRIANGLE -> (2.0 / PI * asin(sin(phase))).toFloat()
                    WaveformType.SAWTOOTH -> (2.0f / PI.toFloat()) * atan(tan(phase / 2.0)).toFloat()
                    WaveformType.NOISE -> 0.0f // Noise is not typically used for pitched notes.
                }

                finalPcm.add(rawValue * amplitude)
            }
        }

        return finalPcm.toFloatArray()
    }
}