package com.yet.tetris.data

import com.yet.tetris.database.db.DatabaseDriverFactory

/**
 * Creates a test DatabaseManager with an in-memory SQLite database.
 * Platform-specific implementations provide the appropriate driver factory.
 */
expect fun createTestDatabaseDriverFactory(): DatabaseDriverFactory


expect abstract class RobolectricTestRunner()