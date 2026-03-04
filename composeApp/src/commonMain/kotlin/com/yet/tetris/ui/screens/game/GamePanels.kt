package com.yet.tetris.ui.screens.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yet.tetris.domain.model.game.Tetromino
import com.yet.tetris.domain.model.settings.GameSettings
import com.yet.tetris.uikit.component.game.NextPieceCanvas
import com.yet.tetris.uikit.component.modifier.glassPanel
import org.jetbrains.compose.resources.stringResource
import tetrislite.composeapp.generated.resources.Res
import tetrislite.composeapp.generated.resources.hold
import tetrislite.composeapp.generated.resources.level
import tetrislite.composeapp.generated.resources.lines
import tetrislite.composeapp.generated.resources.next
import tetrislite.composeapp.generated.resources.score
import tetrislite.composeapp.generated.resources.time

@Composable
internal fun GameStatsPanel(
    score: Long,
    lines: Long,
    level: Int,
    time: Long,
    compact: Boolean,
    modifier: Modifier = Modifier,
) {
    val formattedTime = remember(time) { formatTime(time) }

    Row(
        modifier =
            modifier
                .glassPanel(shape = RoundedCornerShape(18.dp))
                .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StatItem(
            label = stringResource(Res.string.score),
            value = score.toString(),
            compact = compact,
            modifier = Modifier.weight(1f),
        )
        StatItem(
            label = stringResource(Res.string.lines),
            value = lines.toString(),
            compact = compact,
            modifier = Modifier.weight(1f),
        )
        StatItem(
            label = stringResource(Res.string.level),
            value = level.toString(),
            compact = compact,
            modifier = Modifier.weight(1f),
        )
        StatItem(
            label = stringResource(Res.string.time),
            value = formattedTime,
            compact = compact,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    compact: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = label,
            style = if (compact) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = value,
            style = if (compact) MaterialTheme.typography.titleSmall else MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun NextPiecePreview(
    title: String,
    piece: Tetromino?,
    settings: GameSettings,
    modifier: Modifier = Modifier,
    pieceSize: Dp = 60.dp,
) {
    Column(
        modifier =
            modifier
                .glassPanel(shape = RoundedCornerShape(16.dp))
                .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            modifier =
                Modifier.graphicsLayer {
                    shadowElevation = 2.dp.toPx()
                },
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )

        if (piece != null) {
            NextPieceCanvas(
                nextPiece = piece,
                settings = settings,
                modifier = Modifier.size(pieceSize),
            )
        } else {
            Text(
                text = "-",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
internal fun QueuePreview(
    holdPiece: Tetromino?,
    previewPieces: List<Tetromino>,
    settings: GameSettings,
    holdPieceSize: Dp,
    queuePieceSize: Dp,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        NextPiecePreview(
            modifier = Modifier.weight(0.9f),
            title = stringResource(Res.string.hold),
            piece = holdPiece,
            settings = settings,
            pieceSize = holdPieceSize,
        )

        Column(
            modifier =
                Modifier
                    .weight(1.1f)
                    .glassPanel(shape = RoundedCornerShape(16.dp))
                    .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = stringResource(Res.string.next),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            val count = minOf(previewPieces.size, 5)
            for (i in 0 until count) {
                NextPieceCanvas(
                    nextPiece = previewPieces[i],
                    settings = settings,
                    modifier = Modifier.size(queuePieceSize),
                )
            }
        }
    }
}

@Composable
internal fun QueuePreviewCompact(
    holdPiece: Tetromino?,
    previewPieces: List<Tetromino>,
    settings: GameSettings,
    holdPieceSize: Dp,
    queuePieceSize: Dp,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NextPiecePreview(
            modifier = Modifier.widthIn(min = 64.dp),
            title = stringResource(Res.string.hold),
            piece = holdPiece,
            settings = settings,
            pieceSize = holdPieceSize,
        )

        Row(
            modifier =
                Modifier
                    .weight(1f)
                    .glassPanel(shape = RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.next),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            val count = minOf(previewPieces.size, 3)
            for (i in 0 until count) {
                NextPieceCanvas(
                    nextPiece = previewPieces[i],
                    settings = settings,
                    modifier = Modifier.size(queuePieceSize),
                )
            }
        }
    }
}

private fun formatTime(milliseconds: Long): String {
    val seconds = (milliseconds / 1000) % 60
    val minutes = (milliseconds / 1000) / 60
    return "$minutes:${seconds.toString().padStart(2, '0')}"
}
