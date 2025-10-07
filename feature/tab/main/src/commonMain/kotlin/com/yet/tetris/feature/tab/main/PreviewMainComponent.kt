package com.yet.tetris.feature.tab.main

import com.app.common.decompose.PreviewComponentContext
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.webhistory.WebNavigationOwner
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.yet.tetris.feature.tab.home.PreviewHomeComponent

@OptIn(ExperimentalDecomposeApi::class)
class PreviewMainComponent :
    MainComponent,
    ComponentContext by PreviewComponentContext, WebNavigationOwner.NoOp {
    override val childStackNavigation: Value<ChildStack<*, MainComponent.MainChild>> =
        MutableValue(
            ChildStack(
                configuration = Unit,
                instance = MainComponent.MainChild.HomeChild(component = PreviewHomeComponent()),
            )
        )
    override val childBottomSheetNavigation: Value<ChildSlot<*, MainComponent.BottomSheetChild>> =
        MutableValue(ChildSlot<Any, MainComponent.BottomSheetChild>(null))


    override fun onDismissBottomSheet() {
        TODO("Not yet implemented")
    }

    override fun openHome() {
        TODO("Not yet implemented")
    }

    override fun openHistory() {
        TODO("Not yet implemented")
    }

    override fun settingsClick() {
        TODO("Not yet implemented")
    }
}
