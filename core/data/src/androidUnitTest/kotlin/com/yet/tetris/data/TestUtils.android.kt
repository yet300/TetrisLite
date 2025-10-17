package com.yet.tetris.data

import androidx.test.core.app.ApplicationProvider
import com.yet.tetris.database.db.DatabaseDriverFactory
import org.junit.runner.RunWith

actual fun createTestDatabaseDriverFactory(): DatabaseDriverFactory =
    DatabaseDriverFactory(
        context = ApplicationProvider.getApplicationContext(),
    )

@RunWith(org.robolectric.RobolectricTestRunner::class)
actual abstract class RobolectricTestRunner actual constructor()
