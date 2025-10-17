package com.yet.tetris.feature.home

import com.app.common.decompose.PreviewComponentContext
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.webhistory.WebNavigationOwner
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.yet.tetris.domain.model.game.Difficulty

@OptIn(ExperimentalDecomposeApi::class)
class PreviewHomeComponent :
    HomeComponent,
    ComponentContext by PreviewComponentContext,
    WebNavigationOwner.NoOp {
    override val model: Value<HomeComponent.Model> =
        MutableValue(HomeComponent.Model.Loading)
    override val childBottomSheetNavigation: Value<ChildSlot<*, HomeComponent.BottomSheetChild>> =
        MutableValue(ChildSlot<Any, HomeComponent.BottomSheetChild>(null))

    override fun onDismissBottomSheet() {
        TODO("Not yet implemented")
    }

    override fun onStartNewGame() {
        TODO("Not yet implemented")
    }

    override fun onResumeGame() {
        TODO("Not yet implemented")
    }

    override fun onDifficultyChanged(difficulty: Difficulty) {
        TODO("Not yet implemented")
    }

    override fun onOpenSettings() {
        TODO("Not yet implemented")
    }

    override fun onOpenHistory() {
        TODO("Not yet implemented")
    }
}
