@file:OptIn(ExperimentalMaterial3Api::class)

package com.yet.tetris.uikit.component.scaffold

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp

@Composable
fun BasicScrollableScaffold(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    BasicScaffold(
        modifier = modifier,
        floatingActionButton = floatingActionButton,
        topBar = {
            TopAppBar(
                title = title,
                navigationIcon = navigationIcon,
                actions = {
                    Row(modifier = Modifier.padding(horizontal = 12.dp)) {
                        actions()
                    }
                },
            )
        },
        bottomBar = bottomBar,
        snackbarHost = snackbarHost,
        scrollBehavior = scrollBehavior,
        content = content,
    )
}

@Composable
fun BasicScaffold(
    modifier: Modifier = Modifier,
    floatingActionButton: @Composable () -> Unit = {},
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier =
            scrollBehavior?.nestedScrollConnection?.run(modifier::nestedScroll)
                ?: modifier,
        topBar = topBar,
        snackbarHost = snackbarHost,
        floatingActionButton = floatingActionButton,
        bottomBar = bottomBar,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        content = content,
    )
}
