package com.yet.tetris.domain.model.settings

import com.yet.tetris.domain.model.audio.AudioSettings
import com.yet.tetris.domain.model.game.Difficulty
import com.yet.tetris.domain.model.game.TetrominoType
import kotlinx.serialization.Serializable

@Serializable
data class GameSettings(
    val difficulty: Difficulty = Difficulty.NORMAL,
    val tetrominoColors: Map<TetrominoType, String> = defaultTetrominoColors,
    val backgroundColor: String = "#000000",
    val keyboardLayout: KeyboardLayout = KeyboardLayout.ARROWS,
    val swipeLayout: SwipeLayout = SwipeLayout.STANDARD,
    val swipeSensitivity: SwipeSensitivity = SwipeSensitivity(),
    val audioSettings: AudioSettings = AudioSettings()
) {
    companion object {
        val defaultTetrominoColors = mapOf(
            TetrominoType.I to "#00F0F0",  // Cyan
            TetrominoType.O to "#F0F000",  // Yellow
            TetrominoType.T to "#A000F0",  // Purple
            TetrominoType.S to "#00F000",  // Green
            TetrominoType.Z to "#F00000",  // Red
            TetrominoType.J to "#0000F0",  // Blue
            TetrominoType.L to "#F0A000"   // Orange
        )
    }
}
