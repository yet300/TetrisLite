package com.yet.tetris.di

import com.app.common.di.CommonModule
import com.yet.tetris.data.di.DataModule
import com.yet.tetris.domain.di.DomainModule
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
        )
        config?.invoke(this)
    }
}