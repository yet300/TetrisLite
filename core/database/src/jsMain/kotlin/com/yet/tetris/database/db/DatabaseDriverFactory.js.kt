package com.yet.tetris.database.db

import app.cash.sqldelight.async.coroutines.awaitCreate
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import org.w3c.dom.Worker

actual class DatabaseDriverFactory {
    actual suspend fun provideDbDriver(schema: SqlSchema<QueryResult.AsyncValue<Unit>>): SqlDriver {
        val worker =
            Worker(js("""new URL("sqlite.worker.js", import.meta.url)""").unsafeCast<String>())
        val driver: SqlDriver = WebWorkerDriver(worker)
        schema.awaitCreate(driver)
        return driver
    }
}
