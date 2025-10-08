package com.yet.tetris.ui.screens.game

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun Modifier.keyboardHandler(
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
    onMoveDown: () -> Unit,
    onRotate: () -> Unit,
    onHardDrop: () -> Unit
): Modifier
