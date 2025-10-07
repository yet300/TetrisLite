package com.yet.tetris.di

import com.app.common.di.CommonModule
import com.yet.tetris.data.di.DataModule
import com.yet.tetris.domain.di.DomainModule
import com.yet.tetris.feature.game.di.GameModule
import com.yet.tetris.feature.settings.di.SettingsModule
import com.yet.tetris.feature.tab.history.di.HistoryModule
import com.yet.tetris.feature.tab.home.di.HomeModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import org.koin.ksp.generated.module
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        modules(
            DomainModule().module, DataModule().module, CommonModule().module,
            GameModule().module, SettingsModule().module, HomeModule().module, HistoryModule().module
        )
        config?.invoke(this)
    }
}