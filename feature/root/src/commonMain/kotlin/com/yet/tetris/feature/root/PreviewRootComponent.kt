package com.yet.tetris.feature.root

import com.app.common.decompose.PreviewComponentContext
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.webhistory.WebNavigationOwner
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.yet.tetris.feature.tab.main.PreviewMainComponent

@OptIn(ExperimentalDecomposeApi::class)
class PreviewRootComponent :
    RootComponent,
    ComponentContext by PreviewComponentContext, WebNavigationOwner.NoOp {
    override val childStack: Value<ChildStack<*, RootComponent.Child>> =
        MutableValue(
            ChildStack(
                configuration = Unit,
                instance = RootComponent.Child.Tab(PreviewMainComponent()),
            )
        )

    override fun onBackClicked() {
        TODO("Not yet implemented")
    }
}
