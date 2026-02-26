package com.yet.tetris.di

import com.app.common.di.CommonModule
import com.yet.tetris.data.di.DataModule
import com.yet.tetris.feature.game.di.GameFeatureModule
import com.yet.tetris.feature.history.di.HistoryFeatureModule
import com.yet.tetris.feature.home.di.HomeFeatureModule
import com.yet.tetris.feature.root.di.RootFeatureModule
import com.yet.tetris.feature.settings.di.SettingsFeatureModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.ksp.generated.module
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
fun InitKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        modules(
            DomainModule().module,
            DataModule().module,
            CommonModule().module,

            GameFeatureModule().module,
            HomeFeatureModule().module,
            HistoryFeatureModule().module,
            RootFeatureModule().module,
            SettingsFeatureModule().module,
        )
        config?.invoke(this)
    }
}
