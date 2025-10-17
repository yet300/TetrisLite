package com.yet.tetris.data.mapper

import com.yet.tetris.domain.model.audio.MusicSequence
import com.yet.tetris.domain.model.audio.MusicSequencePresets
import com.yet.tetris.domain.model.audio.MusicTheme
import com.yet.tetris.domain.model.audio.SoundEffect
import com.yet.tetris.domain.model.audio.SoundEffectParams
import com.yet.tetris.domain.model.audio.SoundEffectPresets

fun getParamsForEffect(effect: SoundEffect): SoundEffectParams? =
    when (effect) {
        SoundEffect.PIECE_MOVE -> SoundEffectPresets.PIECE_MOVE
        SoundEffect.PIECE_ROTATE -> SoundEffectPresets.PIECE_ROTATE
        SoundEffect.PIECE_DROP -> SoundEffectPresets.PIECE_DROP
        SoundEffect.LINE_CLEAR -> SoundEffectPresets.LINE_CLEAR
        SoundEffect.TETRIS -> SoundEffectPresets.TETRIS
        SoundEffect.GAME_OVER -> SoundEffectPresets.GAME_OVER
        SoundEffect.LEVEL_UP -> SoundEffectPresets.LEVEL_UP
    }

fun getSequenceForTheme(theme: MusicTheme): MusicSequence? =
    when (theme) {
        MusicTheme.CLASSIC -> MusicSequencePresets.CLASSIC_THEME
        MusicTheme.MODERN -> MusicSequencePresets.MODERN_THEME
        MusicTheme.MINIMAL -> MusicSequencePresets.MINIMAL_THEME
        MusicTheme.NONE -> null
    }
