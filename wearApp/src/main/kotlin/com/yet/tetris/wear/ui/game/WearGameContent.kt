package com.yet.tetris.wear.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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

private const val BOARD_WEIGHT = 0.65f
private const val SIDEBAR_WEIGHT = 0.35f
private const val BOARD_ASPECT_RATIO = 0.5f
private val BOARD_CORNER_RADIUS = 4.dp
private const val BOARD_BACKGROUND_ALPHA = 0.1f
private val CONTENT_PADDING = 24.dp
private val CONTENT_SPACING = 4.dp
private val NEXT_PIECE_SPACING = 2.dp
private val NEXT_PIECE_SIZE = 20.dp
private val PAUSE_BUTTON_SIZE = 24.dp
private val PAUSE_ICON_SIZE = 14.dp
private val STATS_LABEL_FONT_SIZE = 8.sp
private val STATS_VALUE_FONT_SIZE = 11.sp

@Composable
fun WearGamePlayingContent(
    model: GameComponent.Model,
    component: GameComponent,
    onPauseClick: () -> Unit,
) {
    val state = model.gameState ?: return

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .wearGameInputHandlers(component, focusRequester),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(CONTENT_PADDING),
            horizontalArrangement = Arrangement.spacedBy(CONTENT_SPACING),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WearBoardSection(state = state, model = model)
            WearSidebarSection(state = state, model = model, onPauseClick = onPauseClick)
        }
    }
}

@Composable
private fun RowScope.WearBoardSection(
    state: com.yet.tetris.domain.model.game.GameState,
    model: GameComponent.Model,
) {
    Box(
        modifier = Modifier
            .weight(BOARD_WEIGHT)
            .fillMaxHeight(),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(BOARD_ASPECT_RATIO)
                .clip(RoundedCornerShape(BOARD_CORNER_RADIUS))
                .background(MaterialTheme.colors.surface.copy(alpha = BOARD_BACKGROUND_ALPHA)),
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
}

@Composable
private fun RowScope.WearSidebarSection(
    state: com.yet.tetris.domain.model.game.GameState,
    model: GameComponent.Model,
    onPauseClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .weight(SIDEBAR_WEIGHT)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        WearNextPiece(
            nextPiece = state.nextPiece,
            settings = model.settings,
        )
        WearGameStats(
            score = state.score,
            lines = state.linesCleared,
        )
        CompactPauseButton(onClick = onPauseClick)
    }
}

@Composable
private fun WearNextPiece(nextPiece: Tetromino, settings: GameSettings) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(R.string.next_label),
            style = MaterialTheme.typography.caption2,
            color = MaterialTheme.colors.primary,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(NEXT_PIECE_SPACING))
        NextPieceCanvas(
            nextPiece = nextPiece,
            settings = settings,
            modifier = Modifier.size(NEXT_PIECE_SIZE),
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
            fontSize = STATS_LABEL_FONT_SIZE,
            color = MaterialTheme.colors.secondary,
        )
        Text(
            text = value,
            fontSize = STATS_VALUE_FONT_SIZE,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun CompactPauseButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(PAUSE_BUTTON_SIZE),
        colors = ButtonDefaults.secondaryButtonColors(),
    ) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = stringResource(R.string.settings),
            modifier = Modifier.size(PAUSE_ICON_SIZE),
        )
    }
}

private fun Modifier.wearGameInputHandlers(
    component: GameComponent,
    focusRequester: FocusRequester,
): Modifier = this
    .onRotaryScrollEvent {
        if (it.verticalScrollPixels > 0) component.onMoveRight()
        else if (it.verticalScrollPixels < 0) component.onMoveLeft()
        true
    }
    .focusRequester(focusRequester)
    .focusable()
    .pointerInput(Unit) {
        detectTapGestures(
            onTap = { component.onRotate() },
            onLongPress = { component.onHardDrop() },
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
    }
