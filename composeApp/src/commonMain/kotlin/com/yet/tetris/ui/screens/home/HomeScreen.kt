package com.yet.tetris.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.yet.tetris.domain.model.game.Difficulty
import com.yet.tetris.feature.home.HomeComponent
import com.yet.tetris.feature.home.PreviewHomeComponent
import com.yet.tetris.ui.screens.settings.SettingsSheet
import com.yet.tetris.ui.screens.history.HistorySheet
import com.yet.tetris.uikit.component.button.FrostedGlassButton
import com.yet.tetris.uikit.component.sheet.ModalBottomSheet
import com.yet.tetris.uikit.theme.TetrisLiteAppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(component: HomeComponent) {
    val model by component.model.subscribeAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    FrostedGlassButton(
                        onClick = component::onOpenHistory,
                        icon = Icons.Default.History
                    )
                },
                actions = {
                    FrostedGlassButton(
                        onClick = component::onOpenSettings,
                        icon = Icons.Default.Settings,
                    )
                }
            )
        }
    ) { paddingValues ->
        when (val state = model) {
            is HomeComponent.Model.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is HomeComponent.Model.Content -> {
                HomeContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    hasSavedGame = state.hasSavedGame,
                    currentDifficulty = state.settings.difficulty,
                    onStartNewGame = component::onStartNewGame,
                    onResumeGame = component::onResumeGame,
                    onDifficultyChanged = component::onDifficultyChanged
                )
            }
        }
        HomeSheet(component)
    }
}

@Composable
fun HomeSheet(
    component: HomeComponent
) {
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
    onStartNewGame: () -> Unit,
    onResumeGame: () -> Unit,
    onDifficultyChanged: (Difficulty) -> Unit
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Text(
            text = "Tetris Lite",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))


        DifficultySelector(
            currentDifficulty = currentDifficulty,
            onDifficultyChanged = onDifficultyChanged
        )
        Button(
            onClick = onStartNewGame,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Start New Game", style = MaterialTheme.typography.titleMedium)
        }

        if (hasSavedGame) {
            OutlinedButton(
                onClick = onResumeGame,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Resume Game", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun DifficultySelector(
    currentDifficulty: Difficulty,
    onDifficultyChanged: (Difficulty) -> Unit
) {
    val options = Difficulty.entries

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Difficulty",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth(),
                space = 8.dp
            ) {
                options.forEachIndexed { index, difficulty ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = options.size
                        ),
                        onClick = { onDifficultyChanged(difficulty) },
                        selected = currentDifficulty == difficulty,
                        label = { Text(difficulty.name) }
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun HomeScreenPreview() {
    TetrisLiteAppTheme {
        HomeScreen(PreviewHomeComponent())
    }
}