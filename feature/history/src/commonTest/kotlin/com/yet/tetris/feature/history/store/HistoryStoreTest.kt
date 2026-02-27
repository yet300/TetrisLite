package com.yet.tetris.feature.history.store

import com.arkivanov.mvikotlin.core.utils.isAssertOnMainThreadEnabled
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.yet.tetris.domain.model.game.Difficulty
import com.yet.tetris.domain.model.history.GameRecord
import com.yet.tetris.feature.history.DateFilter
import com.yet.tetris.feature.history.store.HistoryStore.Intent
import com.yet.tetris.feature.history.store.HistoryStore.Label
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
class HistoryStoreTest {
    private lateinit var repository: FakeGameHistoryRepository
    private lateinit var store: HistoryStore
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun before() {
        isAssertOnMainThreadEnabled = false
        Dispatchers.setMain(testDispatcher)

        repository = FakeGameHistoryRepository()
    }

    @AfterTest
    fun after() {
        Dispatchers.resetMain()
        isAssertOnMainThreadEnabled = true
    }

    @Test
    fun loads_games_from_repository_WHEN_created() =
        runTest {
            val games =
                listOf(
                    createGameRecord(id = "1", score = 100),
                    createGameRecord(id = "2", score = 200),
                )
            repository.setGames(games)

            createStore()

            assertEquals(games, store.state.games)
            assertEquals(games, store.state.filteredGames)
            assertEquals(1, repository.getAllGamesCallCount)
        }

    @Test
    fun sets_isLoading_during_initialization() =
        runTest {
            createStore()

            // After initialization completes, isLoading should be false
            assertFalse(store.state.isLoading)
        }

    @Test
    fun filters_all_games_by_default() =
        runTest {
            val games =
                listOf(
                    createGameRecord(id = "1", score = 100),
                    createGameRecord(id = "2", score = 200),
                )
            repository.setGames(games)

            createStore()

            assertEquals(DateFilter.ALL, store.state.dateFilter)
            assertEquals(games, store.state.filteredGames)
        }

    @Test
    fun filters_games_by_today_WHEN_Intent_FilterByDate_TODAY() =
        runTest {
            val now = Clock.System.now()
            val today = now.toEpochMilliseconds()
            val yesterday = now.minus(1.days).toEpochMilliseconds()

            val games =
                listOf(
                    createGameRecord(id = "1", score = 100, timestamp = today),
                    createGameRecord(id = "2", score = 200, timestamp = yesterday),
                )
            repository.setGames(games)
            createStore()

            store.accept(Intent.FilterByDate(DateFilter.TODAY))
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(DateFilter.TODAY, store.state.dateFilter)
            assertEquals(1, store.state.filteredGames.size)
            assertEquals("1", store.state.filteredGames[0].id)
        }

    @Test
    fun filters_games_by_this_week_WHEN_Intent_FilterByDate_THIS_WEEK() =
        runTest {
            val now = Clock.System.now()
            val thisWeek = now.minus(3.days).toEpochMilliseconds()
            val lastWeek = now.minus(10.days).toEpochMilliseconds()

            val games =
                listOf(
                    createGameRecord(id = "1", score = 100, timestamp = thisWeek),
                    createGameRecord(id = "2", score = 200, timestamp = lastWeek),
                )
            repository.setGames(games)
            createStore()

            store.accept(Intent.FilterByDate(DateFilter.THIS_WEEK))
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(DateFilter.THIS_WEEK, store.state.dateFilter)
            assertEquals(1, store.state.filteredGames.size)
            assertEquals("1", store.state.filteredGames[0].id)
        }

    @Test
    fun filters_games_by_all_WHEN_Intent_FilterByDate_ALL() =
        runTest {
            val now = Clock.System.now()
            val games =
                listOf(
                    createGameRecord(id = "1", score = 100, timestamp = now.toEpochMilliseconds()),
                    createGameRecord(id = "2", score = 200, timestamp = now.minus(10.days).toEpochMilliseconds()),
                )
            repository.setGames(games)
            createStore()

            store.accept(Intent.FilterByDate(DateFilter.ALL))
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(DateFilter.ALL, store.state.dateFilter)
            assertEquals(2, store.state.filteredGames.size)
        }

    @Test
    fun deletes_game_from_repository_WHEN_Intent_DeleteGame() =
        runTest {
            val games =
                listOf(
                    createGameRecord(id = "1", score = 100),
                    createGameRecord(id = "2", score = 200),
                )
            repository.setGames(games)
            createStore()

            store.accept(Intent.DeleteGame("1"))
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(1, repository.deleteGameCallCount)
            assertEquals(1, store.state.games.size)
            assertEquals("2", store.state.games[0].id)
        }

    @Test
    fun publishes_Label_GameDeleted_WHEN_Intent_DeleteGame() =
        runTest {
            val games =
                listOf(
                    createGameRecord(id = "1", score = 100),
                    createGameRecord(id = "2", score = 200),
                )
            repository.setGames(games)
            createStore()
            val labels = store.labels.test()

            store.accept(Intent.DeleteGame("1"))
            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(labels.any { it is Label.GameDeleted && it.id == "1" })
        }

    @Test
    fun reloads_games_after_delete() =
        runTest {
            val games =
                listOf(
                    createGameRecord(id = "1", score = 100),
                    createGameRecord(id = "2", score = 200),
                )
            repository.setGames(games)
            createStore()
            val initialCallCount = repository.getAllGamesCallCount

            store.accept(Intent.DeleteGame("1"))
            testDispatcher.scheduler.advanceUntilIdle()

            // Should have called getAllGames again after delete
            assertTrue(repository.getAllGamesCallCount > initialCallCount)
        }

    @Test
    fun refreshes_games_WHEN_Intent_Refresh() =
        runTest {
            val initialGames = listOf(createGameRecord(id = "1", score = 100))
            repository.setGames(initialGames)
            createStore()

            val updatedGames =
                listOf(
                    createGameRecord(id = "1", score = 100),
                    createGameRecord(id = "2", score = 200),
                )
            repository.setGames(updatedGames)

            store.accept(Intent.Refresh)
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(2, store.state.games.size)
            assertTrue(repository.getAllGamesCallCount >= 2)
        }

    @Test
    fun publishes_Label_ShowError_WHEN_load_fails() =
        runTest {
            repository.shouldThrowOnGetAll = true

            store = createStoreFactory().create()
            val labels = store.labels.test()
            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(labels.any { it is Label.ShowError })
            assertFalse(store.state.isLoading)
        }

    @Test
    fun publishes_Label_ShowError_WHEN_delete_fails() =
        runTest {
            repository.shouldThrowOnDelete = true
            val games = listOf(createGameRecord(id = "1", score = 100))
            repository.setGames(games)
            createStore()
            val labels = store.labels.test()

            store.accept(Intent.DeleteGame("1"))
            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(labels.any { it is Label.ShowError })
        }

    @Test
    fun maintains_filter_after_reload() =
        runTest {
            val now = Clock.System.now()
            val initialGames =
                listOf(
                    createGameRecord(id = "1", score = 100, timestamp = now.toEpochMilliseconds()),
                    createGameRecord(id = "2", score = 200, timestamp = now.minus(10.days).toEpochMilliseconds()),
                )
            repository.setGames(initialGames)
            createStore()

            store.accept(Intent.FilterByDate(DateFilter.THIS_WEEK))
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(listOf("1"), store.state.filteredGames.map { it.id })

            val reloadedGames =
                listOf(
                    createGameRecord(
                        id = "3",
                        score = 300,
                        timestamp = now.minus(10.days).toEpochMilliseconds(),
                    ),
                    createGameRecord(
                        id = "4",
                        score = 400,
                        timestamp = now.minus(12.days).toEpochMilliseconds(),
                    ),
                )
            repository.setGames(reloadedGames)

            store.accept(Intent.Refresh)
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(DateFilter.THIS_WEEK, store.state.dateFilter)
            assertTrue(store.state.filteredGames.isEmpty())
        }

    @Test
    fun empty_list_WHEN_no_games_exist() =
        runTest {
            repository.setGames(emptyList())

            createStore()

            assertTrue(store.state.games.isEmpty())
            assertTrue(store.state.filteredGames.isEmpty())
        }

    @Test
    fun deletes_game_from_filtered_list() =
        runTest {
            val now = Clock.System.now()
            val games =
                listOf(
                    createGameRecord(id = "1", score = 100, timestamp = now.toEpochMilliseconds()),
                    createGameRecord(id = "2", score = 200, timestamp = now.toEpochMilliseconds()),
                )
            repository.setGames(games)
            createStore()

            store.accept(Intent.FilterByDate(DateFilter.TODAY))
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(2, store.state.filteredGames.size)

            store.accept(Intent.DeleteGame("1"))
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(1, store.state.filteredGames.size)
            assertEquals("2", store.state.filteredGames[0].id)
        }

    private fun createStore() {
        store = createStoreFactory().create()
        testDispatcher.scheduler.advanceUntilIdle()
    }

    private fun createStoreFactory(): HistoryStoreFactory =
        HistoryStoreFactory(
            storeFactory = DefaultStoreFactory(),
            gameHistoryRepository = repository,
        )

    private fun createGameRecord(
        id: String,
        score: Long,
        timestamp: Long = Clock.System.now().toEpochMilliseconds(),
    ): GameRecord =
        GameRecord(
            id = id,
            score = score,
            linesCleared = score / 10,
            difficulty = Difficulty.NORMAL,
            timestamp = timestamp,
        )
}

/**
 * Collects all emitted labels from a Flow for testing purposes.
 */
fun <T> Flow<T>.test(): MutableList<T> {
    val list = ArrayList<T>()
    @Suppress("OPT_IN_USAGE")
    GlobalScope.launch(Dispatchers.Unconfined) { collect { list += it } }
    return list
}
