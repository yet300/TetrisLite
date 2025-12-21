package com.yet.tetris.wear.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.yet.tetris.domain.model.game.Tetromino
import com.yet.tetris.domain.model.settings.GameSettings
import com.yet.tetris.feature.game.GameComponent
import com.yet.tetris.uikit.component.game.NextPieceCanvas
import com.yet.tetris.uikit.game.TetrisBoard
import com.yet.tetris.wear.R

@Composable
fun WearGamePlayingContent(
    model: GameComponent.Model,
    component: GameComponent,
    onPauseClick: () -> Unit,
) {
    val state = model.gameState ?: return

    // Logic for Rotary Input and Focus
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            // Rotary Input (Crown rotation)
            .onRotaryScrollEvent {
                if (it.verticalScrollPixels > 0) component.onMoveRight()
                else if (it.verticalScrollPixels < 0) component.onMoveLeft()
                true
            }
            .focusRequester(focusRequester)
            .focusable()
            // Touch Gestures
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { component.onRotate() },
                    onLongPress = { component.onHardDrop() }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { component.onDragStarted() },
                    onDragEnd = component::onDragEnded,
                    onDrag = { change, dragAmount ->
                        change.consume()
                        component.onDragged(dragAmount.x, dragAmount.y)
                    },
                )
            },
    ) {
        // Layout: Left (Board) | Right (Info)
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // LEFT: Game Board
            Box(
                modifier = Modifier
                    .weight(0.65f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                // Board Container with Aspect Ratio
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(10f / 20f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colors.surface.copy(alpha = 0.1f))
                ) {
                    TetrisBoard(
                        modifier = Modifier.fillMaxSize(),
                        gameState = state,
                        settings = model.settings,
                        ghostPieceY = model.ghostPieceY,
                        borderWidth = 0.dp,
                    )
                }
            }

            // RIGHT: Sidebar (Next, Stats, Pause)
            Column(
                modifier = Modifier
                    .weight(0.35f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Next Piece
                WearNextPiece(
                    nextPiece = state.nextPiece,
                    settings = model.settings
                )

                // Stats
                WearGameStats(
                    score = state.score,
                    lines = state.linesCleared
                )

                // Pause Button
                CompactPauseButton(onClick = onPauseClick)
            }
        }
    }
}

@Composable
private fun WearNextPiece(
    nextPiece: Tetromino,
    settings: GameSettings
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(R.string.next_label),
            style = MaterialTheme.typography.caption2,
            color = MaterialTheme.colors.primary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(2.dp))
        NextPieceCanvas(
            nextPiece = nextPiece,
            settings = settings,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun WearGameStats(score: Long, lines: Long) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StatItem(stringResource(R.string.score_label), score.toString())
        StatItem(stringResource(R.string.lines_label), lines.toString())
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 8.sp,
            color = MaterialTheme.colors.secondary,
        )
        Text(
            text = value,
            fontSize = 11.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CompactPauseButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(24.dp),
        colors = ButtonDefaults.secondaryButtonColors()
    ) {
        Icon(
            imageVector = Icons.Default.Settings, // Или Pause/Menu
            contentDescription = stringResource(R.string.settings),
            modifier = Modifier.size(14.dp)
        )
    }
}
