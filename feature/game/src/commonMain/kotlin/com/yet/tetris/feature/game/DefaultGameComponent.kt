package com.yet.tetris.feature.game


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
import com.arkivanov.decompose.value.subscribe
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.yet.tetris.feature.game.store.GameStore
import com.yet.tetris.feature.game.store.GameStoreFactory
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent


class DefaultGameComponent(
    componentContext: ComponentContext,
    private val navigateBack: () -> Unit,
) : ComponentContext by componentContext, GameComponent, KoinComponent {

    private val store = instanceKeeper.getStore { GameStoreFactory().create() }

    private val sheetNavigation = SlotNavigation<DialogConfig>()

    init {
        val backCallback = BackCallback(isEnabled = isGameActive()) {}
        backHandler.register(backCallback)

        store.asValue().subscribe(lifecycle) { state ->
            backCallback.isEnabled = isGameActive()
        }

        coroutineScope().launch {
            // Handle labels
            store.labels.collect {
                when (it) {
                    is GameStore.Label.GameOver -> sheetNavigation.activate(
                        DialogConfig.GameOver(
                            score = store.state.gameState?.score ?: 0,
                            lines = store.state.gameState?.linesCleared ?: 0
                        )
                    )

                    is GameStore.Label.NavigateBack -> onBackClick()

                    is GameStore.Label.ShowError -> sheetNavigation.activate(DialogConfig.Error(it.message))

                    GameStore.Label.GamePaused -> sheetNavigation.activate(DialogConfig.Pause)
                }
            }
        }
    }

    override val model: Value<GameComponent.Model> = store.asValue().map { state ->
        GameComponent.Model(
            isLoading = state.isLoading || state.gameState == null,
            gameState = state.gameState,
            settings = state.settings,
            elapsedTime = state.elapsedTime,
            isGameOver = state.gameState?.isGameOver ?: false,
            finalScore = state.gameState?.score ?: 0,
            finalLinesCleared = state.gameState?.linesCleared ?: 0,
            ghostPieceY = state.ghostPieceY
        )
    }

    override val childSlot: Value<ChildSlot<*, GameComponent.DialogChild>> =
        childSlot(
            source = sheetNavigation,
            serializer = DialogConfig.serializer(),
            key = "GameSheet",
            handleBackButton = true,
        ) { config, childComponentContext ->
            when (config) {
                is DialogConfig.Pause ->
                    GameComponent.DialogChild.Pause()

                is DialogConfig.GameOver ->
                    GameComponent.DialogChild.GameOver()

                is DialogConfig.Error -> GameComponent.DialogChild.Error(config.message)
            }
        }

    private fun isGameActive(): Boolean {
        val state = store.state
        return state.gameState != null && !state.isPaused && !(state.gameState.isGameOver)
    }

    override fun onDismissSheet() {
        sheetNavigation.dismiss()
    }

    override fun onBackClick() = navigateBack()

    override fun onRetry() {
        sheetNavigation.dismiss()
        store.accept(GameStore.Intent.RetryGame)
    }

    override fun onPause() {
        if (store.state.isPaused) return
        store.accept(GameStore.Intent.PauseGame)
    }

    override fun onResume() {
        store.accept(GameStore.Intent.ResumeGame)
    }

    override fun onQuit() {
        store.accept(GameStore.Intent.QuitGame)
    }

    override fun onMoveLeft() {
        store.accept(GameStore.Intent.MoveLeft)
    }

    override fun onMoveRight() {
        store.accept(GameStore.Intent.MoveRight)
    }

    override fun onMoveDown() {
        store.accept(GameStore.Intent.MoveDown)
    }

    override fun onRotate() {
        store.accept(GameStore.Intent.Rotate)
    }

    override fun onHardDrop() {
        store.accept(GameStore.Intent.HardDrop)
    }

    override fun onSwipe(deltaX: Float, deltaY: Float, velocityX: Float, velocityY: Float) {
        store.accept(GameStore.Intent.HandleSwipe(deltaX, deltaY, velocityX, velocityY))
    }

    override fun onBoardSizeChanged(height: Float) {
        store.accept(GameStore.Intent.OnBoardSizeChanged(height))
    }

    override fun onDragStarted() {
        store.accept(GameStore.Intent.DragStarted)
    }

    override fun onDragged(deltaX: Float, deltaY: Float) {
        store.accept(GameStore.Intent.Dragged(deltaX, deltaY))
    }

    override fun onDragEnded() {
        store.accept(GameStore.Intent.DragEnded)
    }

    @Serializable
    sealed interface DialogConfig {
        @Serializable
        data object Pause : DialogConfig

        @Serializable
        data class GameOver(val score: Long, val lines: Long) : DialogConfig

        @Serializable
        data class Error(val message: String) : DialogConfig
    }
}