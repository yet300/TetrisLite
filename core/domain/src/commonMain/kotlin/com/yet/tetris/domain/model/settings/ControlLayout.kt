package com.yet.tetris.domain.model.settings

/**
 * Keyboard layout options for desktop and web platforms.
 */
enum class KeyboardLayout {
    ARROWS,      // Arrow keys for movement, Space for hard drop, Up for rotate
    WASD,        // WASD for movement, Space for hard drop, W for rotate
    CUSTOM       // User-defined key bindings
}

/**
 * Swipe/gesture layout options for mobile platforms.
 */
enum class SwipeLayout {
    STANDARD,    // Swipe left/right/down, tap to rotate
    INVERTED,    // Inverted swipe directions
    CUSTOM       // User-defined gesture mappings
}

/**
 * Swipe sensitivity settings for controlling how swipe velocity affects movement.
 */
data class SwipeSensitivity(
    val softDropThreshold: Float = 0.5f,  // Velocity threshold for soft drop vs hard drop
    val horizontalSensitivity: Float = 1.0f,
    val verticalSensitivity: Float = 1.0f
)
