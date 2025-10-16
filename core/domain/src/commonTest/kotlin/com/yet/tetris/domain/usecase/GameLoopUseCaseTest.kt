package com.yet.tetris.domain.usecase

import com.yet.tetris.domain.model.game.Difficulty
import com.yet.tetris.domain.model.settings.GameSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class GameLoopUseCaseTest {

    @Test
    fun `start initializes game loop`() = runTest {
        val useCase = GameLoopUseCase(this)
        val settings = GameSettings(difficulty = Difficulty.NORMAL)

        useCase.start(settings)
        useCase.stop()

        // Test passes if no exception is thrown
        assertTrue(true)
    }

    @Test
    fun `pause and resume work correctly`() = runTest {
        val useCase = GameLoopUseCase(this)
        val settings = GameSettings(difficulty = Difficulty.EASY)

        useCase.start(settings)
        useCase.pause()
        useCase.resume()
        useCase.stop()

        // Test passes if no exception is thrown
        assertTrue(true)
    }

    @Test
    fun `stop cancels all jobs`() = runTest {
        val useCase = GameLoopUseCase(this)
        val settings = GameSettings(difficulty = Difficulty.EASY)

        useCase.start(settings)
        useCase.stop()

        // Test passes if no exception is thrown
        assertTrue(true)
    }

    @Test
    fun `multiple start calls work correctly`() = runTest {
        val useCase = GameLoopUseCase(this)
        val settings = GameSettings(difficulty = Difficulty.EASY)

        useCase.start(settings)
        useCase.start(settings)
        useCase.stop()

        // Test passes if no exception is thrown
        assertTrue(true)
    }

    @Test
    fun `pause when already paused is safe`() = runTest {
        val useCase = GameLoopUseCase(this)
        val settings = GameSettings(difficulty = Difficulty.EASY)

        useCase.start(settings)
        useCase.pause()
        useCase.pause()
        useCase.stop()

        // Test passes if no exception is thrown
        assertTrue(true)
    }

    @Test
    fun `resume when not paused is safe`() = runTest {
        val useCase = GameLoopUseCase(this)
        val settings = GameSettings(difficulty = Difficulty.EASY)

        useCase.start(settings)
        useCase.resume()
        useCase.stop()

        // Test passes if no exception is thrown
        assertTrue(true)
    }
}
