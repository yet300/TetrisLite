package com.yet.tetris.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.yet.tetris.domain.model.history.GameRecord
import com.yet.tetris.feature.history.DateFilter
import com.yet.tetris.feature.history.HistoryComponent
import com.yet.tetris.feature.history.PreviewHistoryComponent
import org.jetbrains.compose.resources.stringResource
import tetrislite.composeapp.generated.resources.Res
import tetrislite.composeapp.generated.resources.*
import com.yet.tetris.uikit.component.button.FrostedGlassButton
import com.yet.tetris.uikit.component.text.TitleText
import com.yet.tetris.uikit.theme.TetrisLiteAppTheme
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorySheet(component: HistoryComponent) {
    val model by component.model.subscribeAsState()
    var showFilterMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        when (val state = model) {
            is HistoryComponent.Model.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is HistoryComponent.Model.Content -> {
                if (state.games.isEmpty()) {
                    EmptyHistoryState(
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    HistoryList(
                        modifier = Modifier.fillMaxSize(),
                        games = state.games,
                        onDeleteGame = component::onDeleteGame
                    )
                }
            }
        }
        // TopBar
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .fillMaxWidth()
                .height(64.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            FrostedGlassButton(
                modifier = Modifier.align(Alignment.CenterEnd),
                onClick = { showFilterMenu = true },
                icon = Icons.Default.FilterList,
            )

            DropdownMenu(
                modifier = Modifier.align(Alignment.CenterEnd),
                expanded = showFilterMenu,
                onDismissRequest = { showFilterMenu = false }
            ) {
                DateFilter.entries.forEach { filter ->
                    DropdownMenuItem(
                        text = { Text(filter.name.replace('_', ' ')) },
                        onClick = {
                            component.onFilterChanged(filter)
                            showFilterMenu = false
                        }
                    )
                }
            }

        }
    }
}

@Composable
private fun EmptyHistoryState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(Res.string.no_games_yet),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(Res.string.start_game_prompt),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun HistoryList(
    modifier: Modifier = Modifier,
    games: List<GameRecord>,
    onDeleteGame: (String) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(top = 80.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item(key = "header") {
            TitleText(
                text = stringResource(Res.string.game_history),
            )
        }
        items(games, key = { it.id }) { game ->
            GameRecordCard(
                game = game,
                onDelete = { onDeleteGame(game.id) }
            )
        }
    }
}

@Composable
private fun GameRecordCard(
    game: GameRecord,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(Res.string.score_label, game.score),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(Res.string.lines_label, game.linesCleared),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(Res.string.difficulty_label, game.difficulty.name),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatTimestamp(game.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
private fun formatTimestamp(timestamp: Long): String {
    val instant = Instant.fromEpochMilliseconds(timestamp)
    val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${dateTime.date} ${dateTime.hour}:${dateTime.minute.toString().padStart(2, '0')}"
}


@Composable
@Preview
fun HistoryScreenPreview() {
    TetrisLiteAppTheme {
        HistorySheet(PreviewHistoryComponent())
    }
}