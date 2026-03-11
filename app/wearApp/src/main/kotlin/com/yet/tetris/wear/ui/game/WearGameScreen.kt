package com.yet.tetris.wear.ui.game

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.yet.tetris.feature.game.GameComponent
import com.yet.tetris.wear.ui.game.dialog.WearErrorDialog
import com.yet.tetris.wear.ui.game.dialog.WearGameOverDialog
import com.yet.tetris.wear.ui.game.dialog.WearPauseDialog
import com.yet.tetris.wear.ui.settings.WearSettingsOverlay

@Composable
fun WearGameScreen(component: GameComponent) {
    val model by component.model.subscribeAsState()
    val juiceOverlayState = rememberWearJuiceOverlayState()

    LaunchedEffect(model.visualEffectFeed.sequence) {
        model.visualEffectFeed.latest?.let { burst ->
            juiceOverlayState.dispatchBurst(burst)
            component.onVisualEffectConsumed(model.visualEffectFeed.sequence)
        }
    }

    Scaffold(
        timeText = { TimeText() },
        vignette = { Vignette(VignettePosition.TopAndBottom) }
    ) {
        when {
            model.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            model.gameState != null -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    translationX = juiceOverlayState.shakeOffsetX
                                    translationY = juiceOverlayState.shakeOffsetY
                                    scaleX = juiceOverlayState.contentScale
                                    scaleY = juiceOverlayState.contentScale
                                },
                    ) {
                        WearGamePlayingContent(
                            model = model,
                            component = component,
                            onPauseClick = component::onPause
                        )
                    }

                    WearJuiceOverlay(
                        state = juiceOverlayState,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }

        WearGameDialog(component, model)

        WearGameSheet(component)
    }
}

@Composable
fun WearGameDialog(
    component: GameComponent,
    model: GameComponent.Model,
) {
    val dialogSlot by component.childSlot.subscribeAsState()

    dialogSlot.child?.instance?.let { child ->
        when (child) {
            is GameComponent.DialogChild.Pause ->
                WearPauseDialog(
                    onResume = component::onResume,
                    onSettings = component::onSettings,
                    onQuit = component::onQuit
                )

            is GameComponent.DialogChild.GameOver ->
                WearGameOverDialog(
                    score = model.finalScore,
                    lines = model.finalLinesCleared,
                    onRetry = component::onRetry,
                    onQuit = component::onQuit,
                    onDismiss = component::onDismissDialog
                )

            is GameComponent.DialogChild.Error ->
                WearErrorDialog(
                    message = child.message,
                    onDismiss = component::onDismissDialog
                )
        }
    }
}

@Composable
fun WearGameSheet(component: GameComponent) {
    val sheetSlot by component.sheetSlot.subscribeAsState()

    sheetSlot.child?.instance?.let { child ->
        when (child) {
            is GameComponent.SheetChild.Settings ->
                WearSettingsOverlay(
                    component = child.component,
                    onDismissRequest = component::onDismissSheet,
                )
        }
    }
}
