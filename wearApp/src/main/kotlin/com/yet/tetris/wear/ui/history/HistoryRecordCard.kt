package com.yet.tetris.wear.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.SwipeToRevealCard
import androidx.wear.compose.material.SwipeToRevealPrimaryAction
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.rememberRevealState
import com.yet.tetris.domain.model.history.GameRecord
import com.yet.tetris.uikit.format.formatTimestamp
import com.yet.tetris.wear.R

private val CARD_CORNER_RADIUS = 20.dp
private val CARD_PADDING = 8.dp
private val CARD_ICON_SIZE = 14.dp
private val CARD_SPACING = 4.dp

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun HistoryRecordCard(
    record: GameRecord,
    onDelete: (String) -> Unit,
) {
    val revealState = rememberRevealState()

    SwipeToRevealCard(
        revealState = revealState,
        primaryAction = {
            DeleteAction(
                revealState = revealState,
                onDelete = { onDelete(record.id) })
        },
        onFullSwipe = { onDelete(record.id) },
    ) {
        HistoryRecordContent(record = record)
    }
}

@Composable
@OptIn(ExperimentalWearMaterialApi::class)
private fun DeleteAction(
    revealState: androidx.wear.compose.material.RevealState,
    onDelete: () -> Unit,
) {
    SwipeToRevealPrimaryAction(
        revealState = revealState,
        icon = {
            Icon(
                Icons.Default.Delete,
                contentDescription = stringResource(R.string.delete_record),
            )
        },
        label = { Text(stringResource(R.string.delete_record)) },
        onClick = onDelete,
    )
}

@Composable
private fun HistoryRecordContent(record: GameRecord) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colors.surface,
                shape = RoundedCornerShape(CARD_CORNER_RADIUS),
            )
            .padding(CARD_PADDING),
    ) {
        HistoryRecordHeader(record = record)
        Spacer(Modifier.height(CARD_SPACING))
        Text(
            text = stringResource(R.string.score_format, record.score),
            style = MaterialTheme.typography.title3,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colors.onSurface,
        )
        Text(
            text = stringResource(R.string.lines_format, record.linesCleared),
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onSurfaceVariant,
        )
    }
}

@Composable
private fun HistoryRecordHeader(record: GameRecord) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Default.SportsEsports,
            contentDescription = null,
            modifier = Modifier.size(CARD_ICON_SIZE),
            tint = MaterialTheme.colors.onSurfaceVariant,
        )
        Spacer(Modifier.width(CARD_SPACING))
        Text(
            text = record.difficulty.name,
            style = MaterialTheme.typography.caption2,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colors.onSurfaceVariant,
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = formatTimestamp(record.timestamp),
            style = MaterialTheme.typography.caption2,
            color = MaterialTheme.colors.onSurfaceVariant,
        )
    }
}
