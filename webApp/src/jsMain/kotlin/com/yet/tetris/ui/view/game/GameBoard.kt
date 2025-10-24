package com.yet.tetris.ui.view.game

import com.yet.tetris.domain.model.game.GameState
import com.yet.tetris.ui.view.game.gestures.GestureHandler
import com.yet.tetris.ui.view.game.rendering.BoardRenderer
import js.objects.unsafeJso
import mui.material.Box
import mui.system.sx
import react.FC
import react.Props
import react.dom.html.ReactHTML.canvas
import react.useEffect
import react.useRef
import web.canvas.CanvasRenderingContext2D
import web.canvas.ID
import web.cssom.AlignItems
import web.cssom.Border
import web.cssom.BoxShadow
import web.cssom.Color
import web.cssom.Display
import web.cssom.JustifyContent
import web.cssom.MaxHeight
import web.cssom.MaxWidth
import web.cssom.pct
import web.cssom.px
import web.html.HTMLCanvasElement

external interface GameBoardProps : Props {
    var gameState: GameState
    var settings: com.yet.tetris.domain.model.settings.GameSettings
    var ghostY: Int?
    var onDragStarted: (() -> Unit)?
    var onDragged: ((deltaX: Float, deltaY: Float) -> Unit)?
    var onDragEnded: (() -> Unit)?
    var onTap: (() -> Unit)?
}

@OptIn(ExperimentalWasmJsInterop::class)
val GameBoard = FC<GameBoardProps> { props ->
    val canvasRef = useRef<HTMLCanvasElement>()
    val lastPosRef = useRef<dynamic>()
    val didStartDraggingRef = useRef(false)
    val startTimeRef = useRef<Double>()
    val dragThresholdRef = useRef(5.0) // Pixels before considering it a drag
    val totalDragRef = useRef<dynamic>() // Track total drag distance

    // Setup gesture event listeners
    useEffect(Unit) {
        val canvas = canvasRef.current ?: return@useEffect

        val cleanup = GestureHandler.setupGestureListeners(
            canvas,
            lastPosRef,
            didStartDraggingRef,
            startTimeRef,
            totalDragRef,
            props.onDragStarted,
            props.onDragged,
            props.onDragEnded,
            props.onTap
        )

        cleanup
    }

    useEffect(props.gameState, props.ghostY) {
        val canvas = canvasRef.current ?: return@useEffect
        val ctx = canvas.getContext(CanvasRenderingContext2D.ID) ?: return@useEffect

        BoardRenderer.render(canvas, ctx, props.gameState, props.ghostY, props.settings)
    }

    Box {
        sx {
            display = Display.flex
            alignItems = AlignItems.center
            justifyContent = JustifyContent.center
            width = 100.pct
            height = 100.pct
        }

        canvas {
            ref = canvasRef
            width = 300.0
            style = unsafeJso {
                width = "min(90vw, 400px)".unsafeCast<web.cssom.Width>()
                maxWidth = "100%".unsafeCast<MaxWidth>()
                maxHeight = "calc(100vh - 120px)".unsafeCast<MaxHeight>()
                height = "auto".unsafeCast<web.cssom.Height>()
                border = "2px solid rgba(255, 255, 255, 0.3)".unsafeCast<Border>()
                borderRadius = 8.px
                backgroundColor = Color("#000000")
                boxShadow = "0 10px 40px rgba(0, 0, 0, 0.5)".unsafeCast<BoxShadow>()
                touchAction = "none".unsafeCast<web.cssom.TouchAction>()
                cursor = "pointer".unsafeCast<web.cssom.Cursor>()
                display = "block".unsafeCast<Display>()
            }
        }
    }
}
