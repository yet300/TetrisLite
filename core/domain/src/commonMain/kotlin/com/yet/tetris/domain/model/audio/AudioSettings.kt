package com.yet.tetris.domain.model.audio

/**
 * Audio settings for music and sound effects.
 */
data class AudioSettings(
    val musicEnabled: Boolean = true,
    val soundEffectsEnabled: Boolean = true,
    val musicVolume: Float = 0.7f,  // 0.0 to 1.0
    val sfxVolume: Float = 0.8f,     // 0.0 to 1.0
    val selectedMusicTheme: MusicTheme = MusicTheme.CLASSIC
)

/**
 * Available procedurally generated music themes.
 */
enum class MusicTheme {
    CLASSIC,    // 8-bit chiptune style, inspired by original Tetris
    MODERN,     // More contemporary electronic sound
    MINIMAL,    // Ambient, minimal background music
    NONE        // No music
}

/**
 * Sound effect types that can be triggered during gameplay.
 */
enum class SoundEffect {
    PIECE_MOVE,      // When piece moves left/right
    PIECE_ROTATE,    // When piece rotates
    PIECE_DROP,      // When piece is placed
    LINE_CLEAR,      // When lines are cleared
    TETRIS,          // When 4 lines cleared at once
    LEVEL_UP,        // When difficulty increases
    GAME_OVER        // When game ends
}
