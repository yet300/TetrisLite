package com.yet.tetris.ui.screens.game

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import com.yet.tetris.domain.model.game.RotationDirection

@Composable
expect fun Modifier.keyboardHandler(
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
): Modifier
