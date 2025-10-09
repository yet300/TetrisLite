package com.yet.tetris.domain.model.settings

import com.yet.tetris.domain.model.audio.AudioSettings
import com.yet.tetris.domain.model.game.Difficulty
import com.yet.tetris.domain.model.theme.ThemeConfig

data class GameSettings(
    val difficulty: Difficulty = Difficulty.NORMAL,
    val themeConfig: ThemeConfig = ThemeConfig(),
    val keyboardLayout: KeyboardLayout = KeyboardLayout.ARROWS,
    val swipeLayout: SwipeLayout = SwipeLayout.STANDARD,
    val swipeSensitivity: SwipeSensitivity = SwipeSensitivity(),
    val audioSettings: AudioSettings = AudioSettings()
)
