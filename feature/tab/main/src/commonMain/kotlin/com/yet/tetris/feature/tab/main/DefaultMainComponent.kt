package com.yet.tetris.feature.tab.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.childStackWebNavigation
import com.arkivanov.decompose.router.webhistory.WebNavigation
import com.arkivanov.decompose.value.Value
import com.yet.tetris.feature.settings.DefaultSettingsComponent
import com.yet.tetris.feature.tab.history.DefaultHistoryComponent
import com.yet.tetris.feature.tab.home.DefaultHomeComponent
import kotlinx.serialization.Serializable

class DefaultMainComponent(
    componentContext: ComponentContext,
    private val onGame: () -> Unit,
) : ComponentContext by componentContext, MainComponent {
    private val navigationStack = StackNavigation<Configuration>()

    private val bottomSheetSlot = SlotNavigation<BottomSheetConfiguration>()


    private val _childStackNavigation = childStack(
        source = navigationStack,
        serializer = Configuration.serializer(),
        initialConfiguration = Configuration.Home,
        handleBackButton = true,
        childFactory = ::createChildNavigation,
    )

    private fun createChildNavigation(
        config: Configuration,
        componentContext: ComponentContext,
    ): MainComponent.MainChild = when (config) {
        is Configuration.Home -> MainComponent.MainChild.HomeChild(
            component = DefaultHomeComponent(
                componentContext = componentContext,
                navigateToGame = onGame,
                openSettings = ::settingsClick
            )
        )

        is Configuration.History -> MainComponent.MainChild.HistoryChild(
            component = DefaultHistoryComponent(
                componentContext = componentContext,
            )
        )
    }

    override val childStackNavigation: Value<ChildStack<*, MainComponent.MainChild>> =
        _childStackNavigation

    override val childBottomSheetNavigation: Value<ChildSlot<*, MainComponent.BottomSheetChild>> =
        childSlot(
            source = bottomSheetSlot,
            serializer = BottomSheetConfiguration.serializer(),
            handleBackButton = true,
            childFactory = ::createChildSheet
        )

    private fun createChildSheet(
        config: BottomSheetConfiguration,
        componentContext: ComponentContext,
    ): MainComponent.BottomSheetChild = when(config){
        BottomSheetConfiguration.Settings -> MainComponent.BottomSheetChild.SettingsChild(
            component = DefaultSettingsComponent(
                componentContext = componentContext,
                onSettingsSaved = {},
                onDismiss = ::onDismissBottomSheet
            )
        )
    }


    override fun onDismissBottomSheet() {
        bottomSheetSlot.dismiss()
    }

    override fun openHome() {
        navigationStack.bringToFront(Configuration.Home)
    }

    override fun openHistory() {
        navigationStack.bringToFront(Configuration.History)
    }

    override fun settingsClick() {
        bottomSheetSlot.activate(BottomSheetConfiguration.Settings)
    }

    @OptIn(ExperimentalDecomposeApi::class)
    override val webNavigation: WebNavigation<*> =
        childStackWebNavigation(
            navigator = navigationStack,
            stack = _childStackNavigation,
            serializer = Configuration.serializer(),
            childSelector = {
                when (val child = it.instance) {
                    is MainComponent.MainChild.HistoryChild -> null
                    is MainComponent.MainChild.HomeChild -> null
                }
            },
        )

    @Serializable
    private sealed interface Configuration {
        @Serializable
        data object Home : Configuration

        @Serializable
        data object History : Configuration
    }

    @Serializable
    private sealed interface BottomSheetConfiguration {
        @Serializable
        data object Settings : BottomSheetConfiguration
    }
}