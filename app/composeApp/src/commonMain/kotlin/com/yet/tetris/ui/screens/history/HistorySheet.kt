package com.yet.tetris.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import com.yet.tetris.domain.model.progression.ProgressAchievementId
import com.yet.tetris.domain.model.progression.ProgressionSummary
import com.yet.tetris.feature.history.DateFilter
import com.yet.tetris.feature.history.HistoryComponent
import com.yet.tetris.feature.history.PreviewHistoryComponent
import com.yet.tetris.uikit.component.button.FrostedGlassButton
import com.yet.tetris.uikit.component.swipe.SwipeContent
import com.yet.tetris.uikit.component.swipe.SwipeableActionsBox
import com.yet.tetris.uikit.component.text.TitleText
import com.yet.tetris.uikit.format.formatTimestamp
import com.yet.tetris.uikit.theme.TetrisLiteAppTheme
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import tetrislite.composeapp.generated.resources.Res
import tetrislite.composeapp.generated.resources.achievement_combo_5
import tetrislite.composeapp.generated.resources.achievement_first_game
import tetrislite.composeapp.generated.resources.achievement_first_tetris
import tetrislite.composeapp.generated.resources.achievement_first_tspin
import tetrislite.composeapp.generated.resources.achievement_perfect_clear
import tetrislite.composeapp.generated.resources.achievement_score_20000
import tetrislite.composeapp.generated.resources.achievement_score_5000
import tetrislite.composeapp.generated.resources.achievement_ten_games
import tetrislite.composeapp.generated.resources.achievements_unlocked_value
import tetrislite.composeapp.generated.resources.best_score_value
import tetrislite.composeapp.generated.resources.career_summary_title
import tetrislite.composeapp.generated.resources.game_history
import tetrislite.composeapp.generated.resources.games_played_value
import tetrislite.composeapp.generated.resources.highest_level_value
import tetrislite.composeapp.generated.resources.history_empty_filter
import tetrislite.composeapp.generated.resources.lines_label
import tetrislite.composeapp.generated.resources.no_games_yet
import tetrislite.composeapp.generated.resources.score_label
import tetrislite.composeapp.generated.resources.start_game_prompt
import tetrislite.composeapp.generated.resources.total_lines_value
import tetrislite.composeapp.generated.resources.total_tetrises_value
import tetrislite.composeapp.generated.resources.total_tspins_value

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorySheet(component: HistoryComponent) {
    val model by component.model.subscribeAsState()
    var showFilterMenu by remember { mutableStateOf(false) }

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
    ) {
        when (val state = model) {
            is HistoryComponent.Model.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            is HistoryComponent.Model.Content -> {
                if (state.games.isEmpty() && state.totalGamesCount == 0) {
                    EmptyHistoryState(
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    HistoryList(
                        modifier = Modifier.fillMaxSize(),
                        games = state.games,
                        totalGamesCount = state.totalGamesCount,
                        progression = state.progression,
                        onDeleteGame = component::onDeleteGame,
                    )
                }
            }
        }
        // TopBar
        Box(
            modifier =
                Modifier
                    .align(Alignment.TopEnd)
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(MaterialTheme.colorScheme.background),
        ) {
            FrostedGlassButton(
                modifier = Modifier.align(Alignment.CenterStart),
                onClick = { showFilterMenu = true },
                icon = Icons.Default.FilterList,
            )

            DropdownMenu(
                modifier = Modifier.align(Alignment.CenterStart),
                expanded = showFilterMenu,
                onDismissRequest = { showFilterMenu = false },
            ) {
                DateFilter.entries.forEach { filter ->
                    DropdownMenuItem(
                        text = { Text(filter.name.replace('_', ' ')) },
                        onClick = {
                            component.onFilterChanged(filter)
                            showFilterMenu = false
                        },
                    )
                }
            }

            FrostedGlassButton(
                modifier = Modifier.align(Alignment.CenterEnd),
                onClick = component::onDismiss,
                icon = Icons.Default.Close,
            )
        }
    }
}

@Composable
private fun EmptyHistoryState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(Res.string.no_games_yet),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = stringResource(Res.string.start_game_prompt),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun HistoryList(
    modifier: Modifier = Modifier,
    games: List<GameRecord>,
    totalGamesCount: Int,
    progression: ProgressionSummary,
    onDeleteGame: (String) -> Unit,
) {
    var revealedId by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(top = 80.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item(key = "header") {
            TitleText(
                text = stringResource(Res.string.game_history),
            )
        }
        item(key = "summary") {
            HistorySummaryCard(progression = progression)
        }
        if (games.isEmpty()) {
            item(key = "filtered-empty") {
                FilteredHistoryState(totalGamesCount = totalGamesCount)
            }
        }
        items(games, key = { it.id }) { game ->
            GameRecordSwipeableItem(
                game = game,
                isRevealed = revealedId == game.id,
                onExpand = { revealedId = game.id },
                onCollapse = { revealedId = null },
                onDelete = { onDeleteGame(game.id) },
            )
        }
    }
}

@Composable
private fun FilteredHistoryState(
    totalGamesCount: Int,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = stringResource(Res.string.history_empty_filter),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = stringResource(Res.string.games_played_value, totalGamesCount),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HistorySummaryCard(
    progression: ProgressionSummary,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(Res.string.career_summary_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(text = stringResource(Res.string.best_score_value, progression.bestScore))
            Text(text = stringResource(Res.string.highest_level_value, progression.highestLevel))
            Text(text = stringResource(Res.string.total_lines_value, progression.totalLines))
            Text(text = stringResource(Res.string.total_tetrises_value, progression.totalTetrises))
            Text(text = stringResource(Res.string.total_tspins_value, progression.totalTSpins))
            Text(
                text =
                    stringResource(
                        Res.string.achievements_unlocked_value,
                        progression.unlockedAchievements.size,
                        progression.totalAchievements,
                    ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (progression.unlockedAchievements.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    for (achievement in progression.unlockedAchievements) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier =
                                Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.background,
                                        shape = RoundedCornerShape(999.dp),
                                    ).padding(horizontal = 10.dp, vertical = 6.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.MilitaryTech,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.primary,
                            )
                            Text(
                                text = achievement.title(),
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GameRecordSwipeableItem(
    game: GameRecord,
    isRevealed: Boolean,
    onExpand: () -> Unit,
    onCollapse: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SwipeableActionsBox(
        modifier =
            modifier
                .fillMaxSize(),
        isRevealed = isRevealed,
        onExpanded = onExpand,
        onCollapsed = onCollapse,
        onFullSwipe = onDelete,
        actions = {
            SwipeContent(
                modifier = Modifier.padding(start = 30.dp),
                icon = Icons.Default.Delete,
                backgroundColor = MaterialTheme.colorScheme.error,
                onClick = {
                    onDelete()
                    onCollapse()
                },
            )
        },
        content = {
            GameRecordCard(
                game = game,
            )
        },
    )
}

@Composable
private fun GameRecordCard(
    game: GameRecord,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.SportsEsports,
                    contentDescription = "Game icon",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = game.difficulty.name,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = formatTimestamp(game.timestamp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.height(8.dp))

            Text(
                text = stringResource(Res.string.score_label, game.score),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Text(
                text = stringResource(Res.string.lines_label, game.linesCleared),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Text(
                text = "Level ${game.level} • ${formatDuration(game.durationMs)} • ${game.piecesPlaced} pieces",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Text(
                text = "Max combo ${game.maxCombo} • Tetrises ${game.tetrisesCleared} • T-Spins ${game.tSpinClears}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun formatDuration(milliseconds: Long): String {
    val seconds = (milliseconds / 1000) % 60
    val minutes = (milliseconds / 1000) / 60
    return "$minutes:${seconds.toString().padStart(2, '0')}"
}

@Composable
@Preview
fun HistoryScreenPreview() {
    TetrisLiteAppTheme {
        HistorySheet(PreviewHistoryComponent())
    }
}

@Composable
private fun ProgressAchievementId.title(): String =
    when (this) {
        ProgressAchievementId.FIRST_GAME -> stringResource(Res.string.achievement_first_game)
        ProgressAchievementId.SCORE_5000 -> stringResource(Res.string.achievement_score_5000)
        ProgressAchievementId.SCORE_20000 -> stringResource(Res.string.achievement_score_20000)
        ProgressAchievementId.FIRST_TETRIS -> stringResource(Res.string.achievement_first_tetris)
        ProgressAchievementId.FIRST_TSPIN -> stringResource(Res.string.achievement_first_tspin)
        ProgressAchievementId.COMBO_5 -> stringResource(Res.string.achievement_combo_5)
        ProgressAchievementId.PERFECT_CLEAR -> stringResource(Res.string.achievement_perfect_clear)
        ProgressAchievementId.TEN_GAMES -> stringResource(Res.string.achievement_ten_games)
    }
