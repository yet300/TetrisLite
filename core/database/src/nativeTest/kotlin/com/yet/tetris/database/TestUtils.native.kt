package com.yet.tetris.database

import com.yet.tetris.database.db.DatabaseDriverFactory
import com.yet.tetris.database.db.DatabaseManager

actual fun createTestDatabaseDriverFactory(): DatabaseDriverFactory {
    return DatabaseDriverFactory()
}

actual abstract class RobolectricTestRunner actual constructor()