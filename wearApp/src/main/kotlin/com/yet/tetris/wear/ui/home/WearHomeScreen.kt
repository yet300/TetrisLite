package com.yet.tetris.wear.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.InlineSlider
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.yet.tetris.domain.model.game.Difficulty
import com.yet.tetris.feature.home.HomeComponent
import com.yet.tetris.wear.R
import com.yet.tetris.wear.ui.history.WearHistoryOverlay
import com.yet.tetris.wear.ui.settings.WearSettingsOverlay

@Composable
fun WearHomeScreen(component: HomeComponent) {
    val model by component.model.subscribeAsState()
    val listState = rememberScalingLazyListState()

    Scaffold(
        timeText = { TimeText() },
        vignette = { Vignette(VignettePosition.TopAndBottom) },
        positionIndicator = { PositionIndicator(scalingLazyListState = listState) }
    ) {
        when (val state = model) {
            is HomeComponent.Model.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is HomeComponent.Model.Content -> {
                WearHomeContent(
                    listState = listState,
                    hasSavedGame = state.hasSavedGame,
                    difficulty = state.settings.difficulty,
                    onOpenHistory = component::onOpenHistory,
                    onOpenSettings = component::onOpenSettings,
                    onStartNewGame = component::onStartNewGame,
                    onResumeGame = component::onResumeGame,
                    onDifficultyChanged = component::onDifficultyChanged
                )
            }
        }

        WearHomeSheet(component)
    }
}

@Composable
private fun WearHomeContent(
    listState: ScalingLazyListState,
    hasSavedGame: Boolean,
    difficulty: Difficulty,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    onStartNewGame: () -> Unit,
    onResumeGame: () -> Unit,
    onDifficultyChanged: (Difficulty) -> Unit
) {
    ScalingLazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        autoCentering = AutoCenteringParams(itemIndex = 1)
    ) {
        item {
            Text(
                text = stringResource(R.string.app_title),
                style = MaterialTheme.typography.title3,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
                color = MaterialTheme.colors.primary
            )
        }

        item {
            HomeActionsRow(
                hasSavedGame = hasSavedGame,
                onOpenHistory = onOpenHistory,
                onOpenSettings = onOpenSettings,
                onStartNewGame = onStartNewGame,
                onResumeGame = onResumeGame
            )
        }

        if (hasSavedGame) {
            item {
                Chip(
                    modifier = Modifier
                        .height(32.dp)
                        .width(140.dp),
                    label = {
                        Text(
                            text = stringResource(R.string.new_game),
                            style = MaterialTheme.typography.caption2,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    onClick = onStartNewGame,
                    colors = ChipDefaults.secondaryChipColors()
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(4.dp))
        }

        item {
            DifficultySelector(
                difficulty = difficulty,
                onDifficultyChanged = onDifficultyChanged
            )
        }
    }
}

@Composable
private fun HomeActionsRow(
    hasSavedGame: Boolean,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    onStartNewGame: () -> Unit,
    onResumeGame: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // History
        Button(
            onClick = onOpenHistory,
            colors = ButtonDefaults.secondaryButtonColors(),
            modifier = Modifier.size(ButtonDefaults.SmallButtonSize)
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = stringResource(R.string.history)
            )
        }

        val mainAction = if (hasSavedGame) onResumeGame else onStartNewGame
        val mainIcon = if (hasSavedGame) Icons.Default.PlayArrow else Icons.Default.PlayArrow
        val mainDesc = if (hasSavedGame) R.string.resume_game else R.string.new_game

        Button(
            onClick = mainAction,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary
            ),
            modifier = Modifier.size(ButtonDefaults.LargeButtonSize)
        ) {
            Icon(
                imageVector = mainIcon,
                contentDescription = stringResource(mainDesc),
                modifier = Modifier.size(32.dp)
            )
        }

        // Settings
        Button(
            onClick = onOpenSettings,
            colors = ButtonDefaults.secondaryButtonColors(),
            modifier = Modifier.size(ButtonDefaults.SmallButtonSize)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(R.string.settings)
            )
        }
    }
}


@Composable
private fun DifficultySelector(
    difficulty: Difficulty,
    onDifficultyChanged: (Difficulty) -> Unit
) {
    val sliderValue = difficulty.ordinal + 1f

    val difficultyName = when (difficulty) {
        Difficulty.EASY -> stringResource(R.string.difficulty_easy)
        Difficulty.NORMAL -> stringResource(R.string.difficulty_medium)
        Difficulty.HARD -> stringResource(R.string.difficulty_hard)
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = difficultyName,
            style = MaterialTheme.typography.caption1,
            color = MaterialTheme.colors.secondary
        )
        InlineSlider(
            value = sliderValue,
            onValueChange = { value: Float ->
                val index = value.toInt() - 1
                if (index in Difficulty.entries.indices) {
                    onDifficultyChanged(Difficulty.entries[index])
                }
            },
            valueRange = 1f..3f,
            steps = 1,
            segmented = true,
            decreaseIcon = { Icon(Icons.Default.Remove, "Decrease difficulty") },
            increaseIcon = { Icon(Icons.Default.Add, "Increase difficulty") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun WearHomeSheet(component: HomeComponent) {
    val sheetSlot by component.childBottomSheetNavigation.subscribeAsState()

    sheetSlot.child?.instance?.let { child ->
        when (child) {
            is HomeComponent.BottomSheetChild.SettingsChild ->
                WearSettingsOverlay(
                    component = child.component,
                    onDismissRequest = component::onDismissBottomSheet,
                )

            is HomeComponent.BottomSheetChild.HistoryChild ->
                WearHistoryOverlay(
                    component = child.component,
                    onDismissRequest = component::onDismissBottomSheet,
                )
        }
    }
}
