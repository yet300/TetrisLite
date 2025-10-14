package com.yet.tetris.uikit.component.swipe

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SwipeableActionsBox(
    isRevealed: Boolean,
    onExpanded: () -> Unit,
    onCollapsed: () -> Unit,
    onFullSwipe: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit,
    content: @Composable () -> Unit
) {
    var actionsWidth by remember { mutableFloatStateOf(0f) }
    var componentWidth by remember { mutableFloatStateOf(0f) }
    val offset = remember { Animatable(initialValue = 0f) }
    val scope = rememberCoroutineScope()

    val density = LocalDensity.current

    val endPaddingPx = with(density) { 18.dp.toPx() }

    LaunchedEffect(isRevealed, actionsWidth) {
        val targetValue = if (isRevealed) -actionsWidth + endPaddingPx else 0f
        if (offset.targetValue != targetValue) {
            offset.animateTo(targetValue)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .onSizeChanged {
                componentWidth = it.width.toFloat()
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.CenterEnd)
                .onSizeChanged {
                    actionsWidth = it.width.toFloat()
                },
            horizontalArrangement = Arrangement.End,
        ) {
            actions()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(offset.value.roundToInt(), 0) }
                .pointerInput(actionsWidth, componentWidth) {
                    if (actionsWidth == 0f || componentWidth == 0f) return@pointerInput

                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount ->
                            scope.launch {
                                val newOffset = (offset.value + dragAmount)
                                    .coerceIn(-componentWidth, 0f)
                                offset.snapTo(newOffset)
                            }
                        },
                        onDragEnd = {
                            scope.launch {
                                val revealThreshold = -actionsWidth / 2f
                                val fullSwipeThreshold = -componentWidth * 0.75f

                                if (offset.value < fullSwipeThreshold) {
                                    offset.animateTo(-componentWidth)
                                    onFullSwipe()
                                } else if (offset.value < revealThreshold) {
                                    offset.animateTo(-actionsWidth + endPaddingPx)
                                    onExpanded()
                                } else {
                                    offset.animateTo(0f)
                                    onCollapsed()
                                }
                            }
                        }
                    )
                }
        ) {
            content()
        }
    }
}