package com.yet.tetris.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import tetrislite.composeapp.generated.resources.app_title
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
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
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
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
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
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        EnumSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth(0.8f),
            selectedValue = currentDifficulty,
            onValueChange = onDifficultyChanged,
        )

        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier.widthIn(max = 400.dp).padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GlassButton(
                title = stringResource(Res.string.start_new_game),
                icon = Icons.Default.PlayArrow,
                onClick = onStartNewGame,
                modifier = Modifier.fillMaxWidth()
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

@Composable
@Preview
fun HomeScreenPreview() {
    TetrisLiteAppTheme {
        HomeScreen(PreviewHomeComponent())
    }
}