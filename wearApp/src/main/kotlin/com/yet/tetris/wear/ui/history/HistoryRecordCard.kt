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
            SwipeToRevealPrimaryAction(
                revealState = revealState,
                icon = {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete_record)
                    )
                },
                label = { Text(stringResource(R.string.delete_record)) },
                onClick = { onDelete(record.id) }
            )
        },
        onFullSwipe = { onDelete(record.id) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colors.surface,
                    shape = RoundedCornerShape(20.dp),
                )
                .padding(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.SportsEsports,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colors.onSurfaceVariant,
                )
                Spacer(Modifier.width(4.dp))
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
            Spacer(Modifier.height(4.dp))

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
}
