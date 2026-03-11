package com.yet.tetris.data

import com.yet.tetris.database.db.DatabaseDriverFactory
import java.nio.file.Files
import java.nio.file.Path

actual fun createTestDatabaseDriverFactory(): DatabaseDriverFactory {
    Files.deleteIfExists(Path.of("tetris_lite_db.db"))
    Files.deleteIfExists(Path.of("core/data/tetris_lite_db.db"))
    val tempDatabase = Files.createTempFile("tetris-lite-test-", ".db").toFile()
    tempDatabase.deleteOnExit()
    System.setProperty("com.yet.tetris.database.jdbcUrl", "jdbc:sqlite:${tempDatabase.absolutePath}")
    return DatabaseDriverFactory()
}

actual abstract class RobolectricTestRunner actual constructor()
