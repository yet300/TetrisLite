package com.yet.tetris.feature.home

import com.app.common.decompose.asValue
import com.app.common.decompose.coroutineScope
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.yet.tetris.domain.model.game.Difficulty
import com.yet.tetris.feature.history.HistoryComponent
import com.yet.tetris.feature.home.integration.stateToModel
import com.yet.tetris.feature.home.store.HomeStore
import com.yet.tetris.feature.home.store.HomeStoreFactory
import com.yet.tetris.feature.settings.SettingsComponent
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

internal class DefaultHomeComponent(
    componentContext: ComponentContext,
    private val navigateToGame: () -> Unit,
    private val homeStoreFactory: HomeStoreFactory,
    private val settingsComponentFactory: SettingsComponent.Factory,
    private val historyComponentFactory: HistoryComponent.Factory,
) : ComponentContext by componentContext,
    HomeComponent {
    private val store = instanceKeeper.getStore { homeStoreFactory.create() }

    private val bottomSheetSlot = SlotNavigation<BottomSheetConfiguration>()

    init {
        coroutineScope().launch {
            // Handle labels
            store.labels.collect {
                when (it) {
                    is HomeStore.Label.NavigateToGame -> navigateToGame()

                    is HomeStore.Label.ShowError -> {
                        // Handle error (could emit to UI or log)
                    }
                }
            }
        }
    }

    override val model: Value<HomeComponent.Model> =
        store.asValue().map(stateToModel)
    override val childBottomSheetNavigation: Value<ChildSlot<*, HomeComponent.BottomSheetChild>> =
        childSlot(
            source = bottomSheetSlot,
            serializer = BottomSheetConfiguration.serializer(),
            handleBackButton = true,
            childFactory = ::createChildSheet,
        )

    private fun createChildSheet(
        config: BottomSheetConfiguration,
        componentContext: ComponentContext,
    ): HomeComponent.BottomSheetChild =
        when (config) {
            BottomSheetConfiguration.Settings ->
                HomeComponent.BottomSheetChild.SettingsChild(
                    component =
                        settingsComponentFactory(
                            componentContext = componentContext,
                            onCloseRequest = ::onDismissBottomSheet,
                        ),
                )

            BottomSheetConfiguration.History ->
                HomeComponent.BottomSheetChild.HistoryChild(
                    component =
                        historyComponentFactory(
                            componentContext = componentContext,
                            dismiss = ::onDismissBottomSheet,
                        ),
                )
        }

    override fun onDismissBottomSheet() {
        bottomSheetSlot.dismiss()
    }

    override fun onStartNewGame() {
        store.accept(HomeStore.Intent.StartNewGame)
    }

    override fun onResumeGame() {
        store.accept(HomeStore.Intent.ResumeGame)
    }

    override fun onDifficultyChanged(difficulty: Difficulty) {
        store.accept(HomeStore.Intent.ChangeDifficulty(difficulty))
    }

    override fun onOpenSettings() {
        bottomSheetSlot.activate(BottomSheetConfiguration.Settings)
    }

    override fun onOpenHistory() {
        bottomSheetSlot.activate(BottomSheetConfiguration.History)
    }

    @Serializable
    private sealed interface BottomSheetConfiguration {
        @Serializable
        data object Settings : BottomSheetConfiguration

        @Serializable
        data object History : BottomSheetConfiguration
    }
}
internal class DefaultHomeComponentFactory
    constructor(
        private val homeStoreFactory: HomeStoreFactory,
        private val settingsComponentFactory: SettingsComponent.Factory,
        private val historyComponentFactory: HistoryComponent.Factory,
    ) : HomeComponent.Factory {
        override fun invoke(
            componentContext: ComponentContext,
            navigateToGame: () -> Unit,
        ): HomeComponent =
            DefaultHomeComponent(
                componentContext = componentContext,
                navigateToGame = navigateToGame,
                homeStoreFactory = homeStoreFactory,
                settingsComponentFactory = settingsComponentFactory,
                historyComponentFactory = historyComponentFactory,
            )
    }
