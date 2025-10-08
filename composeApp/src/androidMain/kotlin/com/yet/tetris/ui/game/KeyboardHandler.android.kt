package com.yet.tetris.ui.game

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.input.key.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun Modifier.keyboardHandler(
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
    onMoveDown: () -> Unit,
    onRotate: () -> Unit,
    onHardDrop: () -> Unit
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
                        onRotate()
                        true
                    }
                    Key.Enter -> {
                        onHardDrop()
                        true
                    }
                    else -> false
                }
            } else {
                false
            }
        }
}
