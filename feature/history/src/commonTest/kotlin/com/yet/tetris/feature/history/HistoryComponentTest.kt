package com.yet.tetris.feature.history

import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.yet.tetris.domain.model.game.Difficulty
import com.yet.tetris.domain.model.history.GameRecord
import com.yet.tetris.feature.history.store.FakeGameHistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
@Suppress("TestFunctionName")
class HistoryComponentTest {

    private lateinit var repository: FakeGameHistoryRepository
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun before() {
        Dispatchers.setMain(testDispatcher)
        
        repository = FakeGameHistoryRepository()
        
        startKoin {
            modules(
                module {
                    single<com.arkivanov.mvikotlin.core.store.StoreFactory> { DefaultStoreFactory() }
                    single<com.yet.tetris.domain.repository.GameHistoryRepository> { repository }
                }
            )
        }
    }

    @AfterTest
    fun after() {
        stopKoin()
        Dispatchers.resetMain()
    }

    @Test
    fun WHEN_created_THEN_model_is_Content() = runTest {
        val component = createComponent()
        testDispatcher.scheduler.advanceUntilIdle()

        val model = component.model.value
        assertTrue(model is HistoryComponent.Model.Content, "Expected Content model but got ${model::class.simpleName}")
    }

    @Test
    fun WHEN_created_with_games_THEN_games_displayed() = runTest {
        val games = listOf(
            createGameRecord(id = "1", score = 100),
            createGameRecord(id = "2", score = 200)
        )
        repository.setGames(games)
        
        val component = createComponent()
        testDispatcher.scheduler.advanceUntilIdle()

        val model = component.model.value as HistoryComponent.Model.Content
        assertEquals(2, model.games.size)
        assertEquals(games, model.games)
    }

    @Test
    fun WHEN_created_THEN_default_filter_is_ALL() = runTest {
        val component = createComponent()
        testDispatcher.scheduler.advanceUntilIdle()

        val model = component.model.value as HistoryComponent.Model.Content
        assertEquals(DateFilter.ALL, model.currentFilter)
    }

    @Test
    fun WHEN_onFilterChanged_THEN_filter_updated_in_model() = runTest {
        val now = Clock.System.now()
        val games = listOf(
            createGameRecord(id = "1", score = 100, timestamp = now.toEpochMilliseconds()),
            createGameRecord(id = "2", score = 200, timestamp = now.minus(10.days).toEpochMilliseconds())
        )
        repository.setGames(games)
        val component = createComponent()
        testDispatcher.scheduler.advanceUntilIdle()

        component.onFilterChanged(DateFilter.THIS_WEEK)
        testDispatcher.scheduler.advanceUntilIdle()

        val model = component.model.value as HistoryComponent.Model.Content
        assertEquals(DateFilter.THIS_WEEK, model.currentFilter)
        assertEquals(1, model.games.size)
    }

    @Test
    fun WHEN_onFilterChanged_to_TODAY_THEN_only_today_games_shown() = runTest {
        val now = Clock.System.now()
        val games = listOf(
            createGameRecord(id = "1", score = 100, timestamp = now.toEpochMilliseconds()),
            createGameRecord(id = "2", score = 200, timestamp = now.minus(1.days).toEpochMilliseconds())
        )
        repository.setGames(games)
        val component = createComponent()
        testDispatcher.scheduler.advanceUntilIdle()

        component.onFilterChanged(DateFilter.TODAY)
        testDispatcher.scheduler.advanceUntilIdle()

        val model = component.model.value as HistoryComponent.Model.Content
        assertEquals(1, model.games.size)
        assertEquals("1", model.games[0].id)
    }

    @Test
    fun WHEN_onFilterChanged_to_ALL_THEN_all_games_shown() = runTest {
        val now = Clock.System.now()
        val games = listOf(
            createGameRecord(id = "1", score = 100, timestamp = now.toEpochMilliseconds()),
            createGameRecord(id = "2", score = 200, timestamp = now.minus(10.days).toEpochMilliseconds())
        )
        repository.setGames(games)
        val component = createComponent()
        testDispatcher.scheduler.advanceUntilIdle()

        component.onFilterChanged(DateFilter.THIS_WEEK)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, (component.model.value as HistoryComponent.Model.Content).games.size)

        component.onFilterChanged(DateFilter.ALL)
        testDispatcher.scheduler.advanceUntilIdle()

        val model = component.model.value as HistoryComponent.Model.Content
        assertEquals(2, model.games.size)
    }

    @Test
    fun WHEN_onDeleteGame_THEN_game_removed_from_list() = runTest {
        val games = listOf(
            createGameRecord(id = "1", score = 100),
            createGameRecord(id = "2", score = 200)
        )
        repository.setGames(games)
        val component = createComponent()
        testDispatcher.scheduler.advanceUntilIdle()

        component.onDeleteGame("1")
        testDispatcher.scheduler.advanceUntilIdle()

        val model = component.model.value as HistoryComponent.Model.Content
        assertEquals(1, model.games.size)
        assertEquals("2", model.games[0].id)
    }

    @Test
    fun WHEN_onRefresh_THEN_games_reloaded() = runTest {
        val initialGames = listOf(createGameRecord(id = "1", score = 100))
        repository.setGames(initialGames)
        val component = createComponent()
        testDispatcher.scheduler.advanceUntilIdle()
        
        val updatedGames = listOf(
            createGameRecord(id = "1", score = 100),
            createGameRecord(id = "2", score = 200)
        )
        repository.setGames(updatedGames)

        component.onRefresh()
        testDispatcher.scheduler.advanceUntilIdle()

        val model = component.model.value as HistoryComponent.Model.Content
        assertEquals(2, model.games.size)
    }

    @Test
    fun WHEN_onDismiss_THEN_dismiss_callback_called() = runTest {
        var dismissCalled = false
        val component = createComponent(dismiss = { dismissCalled = true })
        testDispatcher.scheduler.advanceUntilIdle()

        component.onDismiss()

        assertTrue(dismissCalled, "dismiss callback should have been called")
    }

    @Test
    fun WHEN_empty_history_THEN_empty_list_in_model() = runTest {
        repository.setGames(emptyList())
        
        val component = createComponent()
        testDispatcher.scheduler.advanceUntilIdle()

        val model = component.model.value as HistoryComponent.Model.Content
        assertTrue(model.games.isEmpty(), "Games list should be empty")
    }

    @Test
    fun WHEN_filter_changed_multiple_times_THEN_last_filter_active() = runTest {
        val now = Clock.System.now()
        val games = listOf(
            createGameRecord(id = "1", score = 100, timestamp = now.toEpochMilliseconds()),
            createGameRecord(id = "2", score = 200, timestamp = now.minus(10.days).toEpochMilliseconds())
        )
        repository.setGames(games)
        val component = createComponent()
        testDispatcher.scheduler.advanceUntilIdle()

        component.onFilterChanged(DateFilter.THIS_WEEK)
        testDispatcher.scheduler.advanceUntilIdle()
        
        component.onFilterChanged(DateFilter.ALL)
        testDispatcher.scheduler.advanceUntilIdle()
        
        component.onFilterChanged(DateFilter.TODAY)
        testDispatcher.scheduler.advanceUntilIdle()

        val model = component.model.value as HistoryComponent.Model.Content
        assertEquals(DateFilter.TODAY, model.currentFilter)
    }

    @Test
    fun WHEN_delete_all_games_THEN_empty_list() = runTest {
        val games = listOf(
            createGameRecord(id = "1", score = 100),
            createGameRecord(id = "2", score = 200)
        )
        repository.setGames(games)
        val component = createComponent()
        testDispatcher.scheduler.advanceUntilIdle()

        component.onDeleteGame("1")
        testDispatcher.scheduler.advanceUntilIdle()
        
        component.onDeleteGame("2")
        testDispatcher.scheduler.advanceUntilIdle()

        val model = component.model.value as HistoryComponent.Model.Content
        assertTrue(model.games.isEmpty())
    }

    private fun createComponent(
        dismiss: () -> Unit = {}
    ): DefaultHistoryComponent {
        val lifecycle = LifecycleRegistry()
        val componentContext = DefaultComponentContext(lifecycle = lifecycle)
        
        return DefaultHistoryComponent(
            componentContext = componentContext,
            dismiss = dismiss
        )
    }

    private fun createGameRecord(
        id: String,
        score: Long,
        timestamp: Long = Clock.System.now().toEpochMilliseconds()
    ): GameRecord {
        return GameRecord(
            id = id,
            score = score,
            linesCleared = score / 10,
            difficulty = Difficulty.NORMAL,
            timestamp = timestamp
        )
    }
}
