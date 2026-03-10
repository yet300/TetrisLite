package com.yet.tetris.ui.screens.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import com.yet.tetris.domain.model.game.RotationDirection

@OptIn(ExperimentalComposeUiApi::class)
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
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    return this
        .focusRequester(focusRequester)
        .focusTarget()
        .onKeyEvent { event ->
            if (event.type == KeyEventType.KeyDown) {
                when (event.key) {
                    Key.DirectionLeft, Key.A -> {
                        onMoveLeft()
                        true
                    }
                    Key.DirectionRight, Key.D -> {
                        onMoveRight()
                        true
                    }
                    Key.DirectionDown, Key.S -> {
                        onMoveDown()
                        true
                    }
                    Key.DirectionUp, Key.W, Key.Spacebar -> {
                        when (primaryRotateDirection) {
                            RotationDirection.CLOCKWISE -> onRotate()
                            RotationDirection.COUNTERCLOCKWISE -> onRotateCounterClockwise()
                            RotationDirection.ONE_EIGHTY -> onRotate180()
                        }
                        true
                    }
                    Key.Q, Key.Z -> {
                        onRotateCounterClockwise()
                        true
                    }
                    Key.E, Key.X -> {
                        onRotateClockwise()
                        true
                    }
                    Key.R -> {
                        if (enable180Rotation) {
                            onRotate180()
                            true
                        } else {
                            false
                        }
                    }
                    Key.Enter -> {
                        onHardDrop()
                        true
                    }
                    Key.C, Key.H -> {
                        onHold()
                        true
                    }
                    Key.Escape -> {
                        onPause()
                        true
                    }
                    else -> false
                }
            } else {
                false
            }
        }
}
