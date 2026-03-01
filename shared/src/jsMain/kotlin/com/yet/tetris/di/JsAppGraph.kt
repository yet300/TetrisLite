package com.yet.tetris.di

import com.app.common.di.CommonBindings
import com.yet.tetris.data.di.DataBindings
import com.yet.tetris.database.di.DatabaseBindings
import com.yet.tetris.database.di.JsDatabaseBindings
import com.yet.tetris.feature.game.di.GameBindings
import com.yet.tetris.feature.history.di.HistoryBindings
import com.yet.tetris.feature.home.di.HomeBindings
import com.yet.tetris.feature.root.di.RootBindings
import com.yet.tetris.feature.settings.di.SettingsBindings
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.createGraph

@DependencyGraph(
    scope = AppScope::class,
    bindingContainers = [
        CommonBindings::class,
        DatabaseBindings::class,
        JsDatabaseBindings::class,
        DataBindings::class,
        DomainBindings::class,
        RootBindings::class,
        SettingsBindings::class,
        HistoryBindings::class,
        HomeBindings::class,
        GameBindings::class,
    ],
)
internal interface JsAppGraph : AppGraph

fun createJsAppGraph(): AppGraph = createGraph<JsAppGraph>()
