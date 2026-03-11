package com.yet.tetris.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.yet.tetris.domain.model.game.Difficulty
import com.yet.tetris.domain.model.progression.ProgressAchievementId
import com.yet.tetris.domain.model.progression.ProgressionSummary
import com.yet.tetris.feature.home.HomeComponent
import com.yet.tetris.feature.home.PreviewHomeComponent
import com.yet.tetris.ui.screens.history.HistorySheet
import com.yet.tetris.ui.screens.settings.SettingsSheet
import com.yet.tetris.uikit.component.button.EnumSegmentedButtonRow
import com.yet.tetris.uikit.component.button.FrostedGlassButton
import com.yet.tetris.uikit.component.button.GlassButton
import com.yet.tetris.uikit.component.sheet.ModalBottomSheet
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
import tetrislite.composeapp.generated.resources.app_title
import tetrislite.composeapp.generated.resources.best_score_value
import tetrislite.composeapp.generated.resources.difficulty_best_level_value
import tetrislite.composeapp.generated.resources.difficulty_best_score_value
import tetrislite.composeapp.generated.resources.games_played_value
import tetrislite.composeapp.generated.resources.highest_level_value
import tetrislite.composeapp.generated.resources.progress_title
import tetrislite.composeapp.generated.resources.resume_game
import tetrislite.composeapp.generated.resources.start_new_game

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(component: HomeComponent) {
    val model by component.model.subscribeAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(Res.string.app_title),
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    FrostedGlassButton(
                        onClick = component::onOpenHistory,
                        icon = Icons.Default.History,
                    )
                },
                actions = {
                    FrostedGlassButton(
                        onClick = component::onOpenSettings,
                        icon = Icons.Default.Settings,
                    )
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                    ),
            )
        },
    ) { paddingValues ->
        when (val state = model) {
            is HomeComponent.Model.Loading -> {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            is HomeComponent.Model.Content -> {
                HomeContent(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                    hasSavedGame = state.hasSavedGame,
                    currentDifficulty = state.settings.difficulty,
                    progression = state.progression,
                    onStartNewGame = component::onStartNewGame,
                    onResumeGame = component::onResumeGame,
                    onDifficultyChanged = component::onDifficultyChanged,
                )
            }
        }
        HomeSheet(component)
    }
}

@Composable
fun HomeSheet(component: HomeComponent) {
    val bottomSheetSlot by component.childBottomSheetNavigation.subscribeAsState()

    bottomSheetSlot.child?.instance?.let { child ->
        ModalBottomSheet(
            onDismiss = component::onDismissBottomSheet,
        ) {
            when (child) {
                is HomeComponent.BottomSheetChild.SettingsChild -> {
                    SettingsSheet(child.component)
                }

                is HomeComponent.BottomSheetChild.HistoryChild -> HistorySheet(child.component)
            }
        }
    }
}

@Composable
private fun HomeContent(
    modifier: Modifier = Modifier,
    hasSavedGame: Boolean,
    currentDifficulty: Difficulty,
    progression: ProgressionSummary,
    onStartNewGame: () -> Unit,
    onResumeGame: () -> Unit,
    onDifficultyChanged: (Difficulty) -> Unit,
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        EnumSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth(0.8f),
            selectedValue = currentDifficulty,
            onValueChange = onDifficultyChanged,
        )

        if (progression.hasProgress) {
            ProgressionOverviewCard(
                progression = progression,
                currentDifficulty = currentDifficulty,
                modifier =
                    Modifier
                        .widthIn(max = 520.dp)
                .fillMaxWidth(),
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier.widthIn(max = 400.dp).padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            GlassButton(
                title = stringResource(Res.string.start_new_game),
                icon = Icons.Default.PlayArrow,
                onClick = onStartNewGame,
                modifier = Modifier.fillMaxWidth(),
            )

            if (hasSavedGame) {
                GlassButton(
                    title = stringResource(Res.string.resume_game),
                    icon = Icons.Default.Refresh,
                    onClick = onResumeGame,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProgressionOverviewCard(
    progression: ProgressionSummary,
    currentDifficulty: Difficulty,
    modifier: Modifier = Modifier,
) {
    val difficultyBest = progression.bestFor(currentDifficulty)

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.12f)),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = stringResource(Res.string.progress_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(Res.string.best_score_value, progression.bestScore),
            )
            Text(
                text = stringResource(Res.string.highest_level_value, progression.highestLevel),
            )
            Text(
                text = stringResource(Res.string.games_played_value, progression.totalGames),
            )
            Text(
                text =
                    stringResource(
                        Res.string.achievements_unlocked_value,
                        progression.unlockedAchievements.size,
                        progression.totalAchievements,
                    ),
            )
            if (difficultyBest.gamesPlayed > 0) {
                Text(
                    text =
                        stringResource(
                            Res.string.difficulty_best_score_value,
                            currentDifficulty.displayName(),
                            difficultyBest.bestScore,
                        ),
                )
                Text(
                    text =
                        stringResource(
                            Res.string.difficulty_best_level_value,
                            currentDifficulty.displayName(),
                            difficultyBest.highestLevel,
                        ),
                )
            }
            if (progression.unlockedAchievements.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    for (achievement in progression.unlockedAchievements.takeLast(3)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier =
                                Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.background,
                                        shape = androidx.compose.foundation.shape.RoundedCornerShape(999.dp),
                                    ).padding(horizontal = 10.dp, vertical = 6.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.MilitaryTech,
                                contentDescription = null,
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

private fun Difficulty.displayName(): String = name.lowercase().replaceFirstChar { it.uppercase() }

@Composable
@Preview
fun HomeScreenPreview() {
    TetrisLiteAppTheme {
        HomeScreen(PreviewHomeComponent())
    }
}
