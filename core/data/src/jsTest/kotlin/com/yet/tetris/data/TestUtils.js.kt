package com.yet.tetris.data

import com.yet.tetris.database.db.DatabaseDriverFactory

actual fun createTestDatabaseDriverFactory(): DatabaseDriverFactory {
    return DatabaseDriverFactory()
}

actual abstract class RobolectricTestRunner actual constructor()