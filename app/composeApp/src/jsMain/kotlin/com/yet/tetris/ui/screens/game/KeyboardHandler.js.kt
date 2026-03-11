package com.yet.tetris.ui.screens.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import com.yet.tetris.domain.model.game.RotationDirection
import kotlinx.browser.window
import org.w3c.dom.events.KeyboardEvent

/**
 * Keyboard handler for web platform
 * Listens to keyboard events and maps them to game actions
 *
 * Key mappings:
 * - Arrow Left / A: Move left
 * - Arrow Right / D: Move right
 * - Arrow Down / S: Move down (soft drop)
 * - Arrow Up / W / Space: Rotate
 * - Enter: Hard drop
 */
@Composable
actual fun Modifier.keyboardHandler(
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
    onMoveDown: () -> Unit,
    onRotate: () -> Unit,
    onRotateClockwise: () -> Unit,
    onRotateCounterClockwise: () -> Unit,
    onRotate180: () -> Unit,
    onHardDrop: () -> Unit,
    onHold: () -> Unit,
    onPause: () -> Unit,
    primaryRotateDirection: RotationDirection,
    enable180Rotation: Boolean,
): Modifier {
    DisposableEffect(Unit) {
        val handleKeyDown = { event: dynamic ->
            val keyboardEvent = event as KeyboardEvent
            val key = keyboardEvent.key.lowercase()

            when (key) {
                "arrowleft", "a" -> {
                    onMoveLeft()
                    keyboardEvent.preventDefault()
                }
                "arrowright", "d" -> {
                    onMoveRight()
                    keyboardEvent.preventDefault()
                }
                "arrowdown", "s" -> {
                    onMoveDown()
                    keyboardEvent.preventDefault()
                }
                "arrowup", "w", " " -> { // Space key
                    when (primaryRotateDirection) {
                        RotationDirection.CLOCKWISE -> onRotate()
                        RotationDirection.COUNTERCLOCKWISE -> onRotateCounterClockwise()
                        RotationDirection.ONE_EIGHTY -> onRotate180()
                    }
                    keyboardEvent.preventDefault()
                }
                "q", "z" -> {
                    onRotateCounterClockwise()
                    keyboardEvent.preventDefault()
                }
                "e", "x" -> {
                    onRotateClockwise()
                    keyboardEvent.preventDefault()
                }
                "r" -> {
                    if (enable180Rotation) {
                        onRotate180()
                    }
                    keyboardEvent.preventDefault()
                }
                "enter" -> {
                    onHardDrop()
                    keyboardEvent.preventDefault()
                }
                "c", "h" -> {
                    onHold()
                    keyboardEvent.preventDefault()
                }
                "escape" -> {
                    onPause()
                    keyboardEvent.preventDefault()
                }
            }
        }

        // Add event listener to window
        window.addEventListener("keydown", handleKeyDown)

        // Cleanup on dispose
        onDispose {
            window.removeEventListener("keydown", handleKeyDown)
        }
    }

    return this
}
