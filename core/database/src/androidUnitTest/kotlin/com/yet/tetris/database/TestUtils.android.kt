package com.yet.tetris.database

import androidx.test.core.app.ApplicationProvider
import com.yet.tetris.database.db.DatabaseDriverFactory
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


actual fun createTestDatabaseDriverFactory(): DatabaseDriverFactory {
    return DatabaseDriverFactory(
        context = ApplicationProvider.getApplicationContext(),
    )
}


@RunWith(RobolectricTestRunner::class)
actual abstract class RobolectricTestRunner actual constructor()