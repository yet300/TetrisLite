package com.yet.tetris.feature.tab.main

import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.webhistory.WebNavigationOwner
import com.arkivanov.decompose.value.Value
import com.yet.tetris.feature.settings.SettingsComponent
import com.yet.tetris.feature.tab.history.HistoryComponent
import com.yet.tetris.feature.tab.home.HomeComponent

@OptIn(ExperimentalDecomposeApi::class)
interface MainComponent :WebNavigationOwner {

    val childStackNavigation: Value<ChildStack<*, MainChild>>

    val childBottomSheetNavigation: Value<ChildSlot<*, BottomSheetChild>>

    fun onDismissBottomSheet()

    fun openHome()
    fun openHistory()

    fun settingsClick()

    sealed class MainChild {
        class HomeChild(val component: HomeComponent) : MainChild()
        class HistoryChild(val component: HistoryComponent) : MainChild()
    }

    sealed interface BottomSheetChild {
        class SettingsChild(val component: SettingsComponent) : BottomSheetChild
    }
}