package com.yet.tetris.feature.history

import com.app.common.decompose.PreviewComponentContext
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.webhistory.WebNavigationOwner
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

@OptIn(ExperimentalDecomposeApi::class)
class PreviewHistoryComponent :
    HistoryComponent,
    ComponentContext by PreviewComponentContext, WebNavigationOwner.NoOp {
    override val model: Value<HistoryComponent.Model> =
        MutableValue(HistoryComponent.Model.Loading)

    override fun onDismiss() {
        TODO("Not yet implemented")
    }


    override fun onRefresh() {
        TODO("Not yet implemented")
    }

    override fun onFilterChanged(filter: DateFilter) {
        TODO("Not yet implemented")
    }

    override fun onDeleteGame(id: String) {
        TODO("Not yet implemented")
    }

}