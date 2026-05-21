package com.yet.tetris.di

import com.app.common.di.CommonModule
import com.yet.tetris.data.di.DataModule
import com.yet.tetris.feature.game.di.GameFeatureModule
import com.yet.tetris.feature.history.di.HistoryFeatureModule
import com.yet.tetris.feature.home.di.HomeFeatureModule
import com.yet.tetris.feature.settings.di.SettingsFeatureModule
import org.koin.core.annotation.KoinApplication
import org.koin.dsl.KoinAppDeclaration
import org.koin.plugin.module.dsl.startKoin
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@KoinApplication(
    modules = [
        DomainModule::class,
        DataModule::class,
        CommonModule::class,
        GameFeatureModule::class,
        HomeFeatureModule::class,
        HistoryFeatureModule::class,
        SettingsFeatureModule::class,
    ]
)
object TetrisApp

@OptIn(ExperimentalJsExport::class)
@JsExport
fun InitKoin(config: KoinAppDeclaration? = null) {
    startKoin<TetrisApp> {
        config?.invoke(this)
    }
}
