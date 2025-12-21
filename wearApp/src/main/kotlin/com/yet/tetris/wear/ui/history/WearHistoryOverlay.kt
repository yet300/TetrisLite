package com.yet.tetris.wear.ui.history

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.yet.tetris.feature.history.HistoryComponent
import com.yet.tetris.wear.R
import com.yet.tetris.wear.ui.components.WearOverlaySurface

@Composable
fun WearHistoryOverlay(
    component: HistoryComponent,
    onDismissRequest: () -> Unit,
) {
    val model by component.model.subscribeAsState()

    WearOverlaySurface(
        title = stringResource(R.string.history_title),
        onDismiss = {
            component.onDismiss()
            onDismissRequest()
        },
    ) {
        when (val state = model) {
            HistoryComponent.Model.Loading -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            is HistoryComponent.Model.Content -> {
                if (state.games.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_games_played),
                        style = MaterialTheme.typography.body2,
                        textAlign = TextAlign.Center,
                    )
                } else {
                    state.games.take(10).forEach { record ->
                        HistoryRecordCard(
                            record = record,
                            onDelete = component::onDeleteGame,
                        )
                    }
                }

            }
        }
    }
}
