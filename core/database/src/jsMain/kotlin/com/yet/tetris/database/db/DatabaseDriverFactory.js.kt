package com.yet.tetris.database.db

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import kotlinx.browser.window
import org.w3c.dom.Worker

actual class DatabaseDriverFactory {
    actual suspend fun provideDbDriver(schema: SqlSchema<QueryResult.AsyncValue<Unit>>): SqlDriver =
        WebWorkerDriver(
            Worker(
                scriptURL = resolveWorkerScriptUrl(),
            ),
        ).also { schema.create(it).await() }

    private fun resolveWorkerScriptUrl(): String {
        val pathname = window.location.pathname
        val basePath =
            when {
                pathname == "/" -> ""
                pathname.endsWith("/") -> pathname.removeSuffix("/")
                pathname.indexOf('/', startIndex = 1) == -1 -> pathname
                else -> pathname.substringBeforeLast("/")
            }

        return "${window.location.origin}$basePath/sqlite.worker.js"
    }
}
