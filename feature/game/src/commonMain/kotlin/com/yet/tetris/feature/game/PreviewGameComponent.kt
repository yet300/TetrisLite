package com.yet.tetris.feature.game

import com.app.common.decompose.PreviewComponentContext
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value

class PreviewGameComponent :
    GameComponent,
    ComponentContext by PreviewComponentContext {
    override val model: Value<GameComponent.Model> =
        MutableValue(GameComponent.Model())
    override val childSlot: Value<ChildSlot<*, GameComponent.DialogChild>> =
        MutableValue(ChildSlot<Any, GameComponent.DialogChild>(null))

    override val sheetSlot: Value<ChildSlot<*, GameComponent.SheetChild>> =
        MutableValue(ChildSlot<Any, GameComponent.SheetChild>(null))

    override fun onDismissDialog() {
        TODO("Not yet implemented")
    }

    override fun onDismissSheet() {
        TODO("Not yet implemented")
    }

    override fun onBackClick() {
        TODO("Not yet implemented")
    }

    override fun onRetry() {
        TODO("Not yet implemented")
    }

    override fun onPause() {
        TODO("Not yet implemented")
    }

    override fun onResume() {
        TODO("Not yet implemented")
    }

    override fun onSettings() {
        TODO("Not yet implemented")
    }

    override fun onQuit() {
        TODO("Not yet implemented")
    }

    override fun onMoveLeft() {
        TODO("Not yet implemented")
    }

    override fun onMoveRight() {
        TODO("Not yet implemented")
    }

    override fun onMoveDown() {
        TODO("Not yet implemented")
    }

    override fun onRotate() {
        TODO("Not yet implemented")
    }

    override fun onHardDrop() {
        TODO("Not yet implemented")
    }

    override fun onSwipe(
        deltaX: Float,
        deltaY: Float,
        velocityX: Float,
        velocityY: Float,
    ) {
        TODO("Not yet implemented")
    }

    override fun onBoardSizeChanged(height: Float) {
        TODO("Not yet implemented")
    }

    override fun onDragStarted() {
        TODO("Not yet implemented")
    }

    override fun onDragged(
        deltaX: Float,
        deltaY: Float,
    ) {
        TODO("Not yet implemented")
    }

    override fun onDragEnded() {
        TODO("Not yet implemented")
    }
}
