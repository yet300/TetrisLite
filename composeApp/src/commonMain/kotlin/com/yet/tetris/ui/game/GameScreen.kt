package com.yet.tetris.ui.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.domain.model.game.Position
import com.yet.tetris.domain.model.game.TetrominoType
import com.yet.tetris.domain.model.settings.GameSettings
import com.yet.tetris.feature.game.GameComponent
import com.yet.tetris.uikit.component.button.FrostedGlassButton
import kotlin.math.abs

@Composable
fun GameScreen(component: GameComponent) {
    val model by component.model.subscribeAsState()
    var showPauseDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            model.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            model.isGameOver -> {
                GameOverContent(
                    finalScore = model.finalScore,
                    linesCleared = model.finalLinesCleared,
                    onQuit = component::onQuit
                )
            }

            model.gameState != null -> {
                GamePlayingContent(
                    model = model,
                    component = component,
                    onPauseClick = {
                        component.onPause()
                        showPauseDialog = true
                    }
                )

                if (showPauseDialog && model.isPaused) {
                    PauseDialog(
                        onResume = {
                            showPauseDialog = false
                            component.onResume()
                        },
                        onQuit = component::onQuit
                    )
                }
            }
        }
    }
}

@Composable
private fun GamePlayingContent(
    model: GameComponent.Model,
    component: GameComponent,
    onPauseClick: () -> Unit
) {
    val gameState = model.gameState ?: return
    val swipeThreshold = 50f
    var accumulatedDragX by remember { mutableStateOf(0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .navigationBarsPadding()
            .keyboardHandler(
                onMoveLeft = component::onMoveLeft,
                onMoveRight = component::onMoveRight,
                onMoveDown = component::onMoveDown,
                onRotate = component::onRotate,
                onHardDrop = component::onHardDrop
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top section: Stats and Next Piece
        Row(
            modifier = Modifier.fillMaxWidth().statusBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pause button
            FrostedGlassButton(
                onClick = onPauseClick,
                icon = Icons.Default.Pause
            )

            // Game stats
            GameStatsRow(
                score = gameState.score,
                lines = gameState.linesCleared,
                time = model.elapsedTime
            )

            // Next piece
            NextPiecePreview(
                nextPiece = gameState.nextPiece,
                settings = model.settings
            )
        }

        // Game board - centered with border
        GameBoardWithBorder(
            modifier = Modifier
                .weight(1f)
                .widthIn(max = 400.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { component.onRotate() }
                    )
                }
                .pointerInput(Unit) {
                    var dragStartTime = 0L
                    var totalDragDistanceY = 0f
                    var isHorizontalSwipe = false

                    detectDragGestures(
                        onDragStart = { _: Offset ->
                            accumulatedDragX = 0f
                            totalDragDistanceY = 0f
                            dragStartTime = System.currentTimeMillis()
                            isHorizontalSwipe = false
                        },
                        onDragEnd = {
                            val dragDuration = System.currentTimeMillis() - dragStartTime

                            if (!isHorizontalSwipe && totalDragDistanceY > size.height * 0.25f && dragDuration < 500) {
                                component.onHardDrop()
                            }

                            accumulatedDragX = 0f
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()

                            if (!isHorizontalSwipe && abs(dragAmount.x) > abs(dragAmount.y) * 1.5f) {
                                isHorizontalSwipe = true
                            }

                            if (isHorizontalSwipe || abs(accumulatedDragX) > abs(totalDragDistanceY)) {
                                accumulatedDragX += dragAmount.x
                                if (abs(accumulatedDragX) > swipeThreshold) {
                                    if (accumulatedDragX > 0) {
                                        component.onMoveRight()
                                    } else {
                                        component.onMoveLeft()
                                    }
                                    accumulatedDragX = 0f
                                }
                            }

                            if (dragAmount.y > 0) {
                                totalDragDistanceY += dragAmount.y
                            }
                        }
                    )
                },
            gameState = gameState,
            settings = model.settings
        )
    }
}

@Composable
private fun GameStatsRow(
    score: Int,
    lines: Int,
    time: Long
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem("Score", score.toString())
            StatItem("Lines", lines.toString())
            StatItem("Time", formatTime(time))
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun NextPiecePreview(
    nextPiece: com.yet.tetris.domain.model.game.Tetromino,
    settings: GameSettings
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Next",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Canvas(
            modifier = Modifier.size(60.dp)
        ) {
            val cellSize = size.width / 4
            nextPiece.blocks.forEach { blockPos ->
                drawRect(
                    color = getTetrominoColor(nextPiece.type, settings),
                    topLeft = Offset(
                        (blockPos.x + 1) * cellSize,
                        (blockPos.y + 1) * cellSize
                    ),
                    size = Size(cellSize - 1, cellSize - 1)
                )
            }
        }
    }
}

@Composable
private fun GameBoardWithBorder(
    modifier: Modifier = Modifier,
    gameState: GameState,
    settings: GameSettings
) {
    Canvas(
        modifier = modifier
            .aspectRatio(gameState.board.width.toFloat() / gameState.board.height.toFloat())
            .fillMaxWidth()
            .background(com.yet.tetris.ui.utils.parseColor(settings.backgroundColor))
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.outline,
            )
    ) {
        val cellSize = size.width / gameState.board.width

        // Calculate ghost piece position
        val ghostY = gameState.currentPiece?.let { piece ->
            calculateGhostPosition(gameState, piece)
        }

        // Draw locked blocks
        gameState.board.cells.forEach { (pos, type) ->
            if (pos.y >= 0) {
                drawRect(
                    color = getTetrominoColor(type, settings),
                    topLeft = Offset(pos.x * cellSize, pos.y * cellSize),
                    size = Size(cellSize - 1, cellSize - 1)
                )
            }
        }

        // Draw ghost piece
        gameState.currentPiece?.let { piece ->
            ghostY?.let { landingY ->
                if (landingY > gameState.currentPosition.y) {
                    piece.blocks.forEach { blockPos ->
                        val absolutePos = Position(
                            gameState.currentPosition.x + blockPos.x,
                            landingY + blockPos.y
                        )
                        if (absolutePos.y >= 0 && absolutePos.y < gameState.board.height) {
                            drawRect(
                                color = getTetrominoColor(
                                    piece.type,
                                    settings
                                ).copy(alpha = 0.3f),
                                topLeft = Offset(
                                    absolutePos.x * cellSize,
                                    absolutePos.y * cellSize
                                ),
                                size = Size(cellSize - 1, cellSize - 1)
                            )
                        }
                    }
                }
            }
        }

        // Draw current piece
        gameState.currentPiece?.let { piece ->
            piece.blocks.forEach { blockPos ->
                val absolutePos = Position(
                    gameState.currentPosition.x + blockPos.x,
                    gameState.currentPosition.y + blockPos.y
                )
                if (absolutePos.y >= 0 && absolutePos.y < gameState.board.height) {
                    drawRect(
                        color = getTetrominoColor(piece.type, settings),
                        topLeft = Offset(
                            absolutePos.x * cellSize,
                            absolutePos.y * cellSize
                        ),
                        size = Size(cellSize - 1, cellSize - 1)
                    )
                }
            }
        }

        // Draw grid lines
        for (x in 0..gameState.board.width) {
            drawLine(
                color = Color.Gray.copy(alpha = 0.2f),
                start = Offset(x * cellSize, 0f),
                end = Offset(x * cellSize, size.height),
                strokeWidth = 1f
            )
        }
        for (y in 0..gameState.board.height) {
            drawLine(
                color = Color.Gray.copy(alpha = 0.2f),
                start = Offset(0f, y * cellSize),
                end = Offset(size.width, y * cellSize),
                strokeWidth = 1f
            )
        }
    }
}


@Composable
private fun GameOverContent(
    finalScore: Int,
    linesCleared: Int,
    onQuit: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(32.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Game Over",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )

                Text(
                    text = "Final Score: $finalScore",
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = "Lines Cleared: $linesCleared",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = onQuit,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Back to Home")
                }
            }
        }
    }
}

@Composable
private fun PauseDialog(
    onResume: () -> Unit,
    onQuit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onResume,
        title = { Text("Game Paused") },
        text = { Text("What would you like to do?") },
        confirmButton = {
            TextButton(onClick = onResume) {
                Text("Resume")
            }
        },
        dismissButton = {
            TextButton(onClick = onQuit) {
                Text("Quit")
            }
        }
    )
}

// Calculate ghost piece position
private fun calculateGhostPosition(
    gameState: GameState,
    piece: com.yet.tetris.domain.model.game.Tetromino
): Int {
    var testY = gameState.currentPosition.y

    while (testY < gameState.board.height) {
        val wouldCollide = piece.blocks.any { blockPos ->
            val absolutePos = Position(
                gameState.currentPosition.x + blockPos.x,
                testY + blockPos.y + 1
            )

            absolutePos.y >= gameState.board.height ||
                    absolutePos.x < 0 ||
                    absolutePos.x >= gameState.board.width ||
                    gameState.board.cells.containsKey(absolutePos)
        }

        if (wouldCollide) {
            return testY
        }
        testY++
    }

    return testY
}

private fun getTetrominoColor(type: TetrominoType, settings: GameSettings): Color {
    val hexColor = settings.tetrominoColors[type] ?: "#FFFFFF"
    return com.yet.tetris.ui.utils.parseColor(hexColor)
}

private fun formatTime(milliseconds: Long): String {
    val seconds = (milliseconds / 1000) % 60
    val minutes = (milliseconds / 1000) / 60
    return "${minutes}:${seconds.toString().padStart(2, '0')}"
}
