package com.yet.tetris.data

actual fun createTestDatabaseDriverFactory(): com.yet.tetris.database.db.DatabaseDriverFactory =
    com.yet.tetris.database.db
        .DatabaseDriverFactory()

actual abstract class RobolectricTestRunner actual constructor()
