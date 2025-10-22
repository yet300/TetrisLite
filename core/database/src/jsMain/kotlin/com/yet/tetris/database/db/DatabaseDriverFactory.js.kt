package com.yet.tetris.database.db

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import org.w3c.dom.Worker

actual class DatabaseDriverFactory {
    actual suspend fun provideDbDriver(schema: SqlSchema<QueryResult.AsyncValue<Unit>>): SqlDriver =
        WebWorkerDriver(
            Worker(
                js("sqljs.worker.js"),
            ),
        ).also { schema.create(it).await() }
}
