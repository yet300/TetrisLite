package com.yet.tetris.ui.screens.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.yet.tetris.feature.game.GameComponent
import com.yet.tetris.ui.screens.game.dialog.ErrorDialog
import com.yet.tetris.ui.screens.game.dialog.GameOverDialog
import com.yet.tetris.ui.screens.game.dialog.PauseDialog
import com.yet.tetris.ui.screens.settings.SettingsSheet
import com.yet.tetris.uikit.component.sheet.ModalBottomSheet

@Composable
fun GameScreen(component: GameComponent) {
    val model by component.model.subscribeAsState()
    val juiceOverlayState = rememberJuiceOverlayState()

    val inputActions =
        remember(component) {
            GameInputActions(
                onPause = component::onPause,
                onHold = component::onHold,
                onMoveLeft = component::onMoveLeft,
                onMoveRight = component::onMoveRight,
                onMoveDown = component::onMoveDown,
                onRotate = component::onRotate,
                onHardDrop = component::onHardDrop,
                onBoardSizeChanged = component::onBoardSizeChanged,
                onDragStarted = component::onDragStarted,
                onDragged = component::onDragged,
                onDragEnded = component::onDragEnded,
            )
        }

    LaunchedEffect(model.visualEffectFeed.sequence) {
        model.visualEffectFeed.latest?.let {
            juiceOverlayState.dispatchBurst(it)
            component.onVisualEffectConsumed(model.visualEffectFeed.sequence)
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
    ) {
        when {
            model.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
            }
            model.gameState != null -> {
                Box(
                    modifier =
                        Modifier.graphicsLayer {
                            translationX = juiceOverlayState.shakeOffsetX
                            translationY = juiceOverlayState.shakeOffsetY
                            scaleX = juiceOverlayState.contentScale
                            scaleY = juiceOverlayState.contentScale
                        },
                ) {
                    GamePlayingContent(
                        model = model,
                        actions = inputActions,
                    )

                    GameDialog(component, model)
                    GameSheet(component)
                }

                JuiceOverlay(
                    state = juiceOverlayState,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
fun GameDialog(
    component: GameComponent,
    model: GameComponent.Model,
) {
    val dialogSheetSlot by component.childSlot.subscribeAsState()

    dialogSheetSlot.child?.instance?.let { child ->
        when (child) {
            is GameComponent.DialogChild.Pause -> {
                PauseDialog(component = component)
            }

            is GameComponent.DialogChild.GameOver -> {
                GameOverDialog(component = component, model = model)
            }

            is GameComponent.DialogChild.Error -> {
                ErrorDialog(
                    message = child.message,
                    onDismiss = component::onDismissDialog,
                )
            }
        }
    }
}

@Composable
fun GameSheet(component: GameComponent) {
    val dialogSheetSlot by component.sheetSlot.subscribeAsState()

    dialogSheetSlot.child?.instance?.let { child ->
        ModalBottomSheet(
            onDismiss = component::onDismissSheet,
        ) {
            when (child) {
                is GameComponent.SheetChild.Settings -> SettingsSheet(component = child.component)
            }
        }
    }
}
