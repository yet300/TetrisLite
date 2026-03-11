package com.yet.tetris.ui.view.game

import com.yet.tetris.domain.model.effects.IntensityLevel
import com.yet.tetris.domain.model.effects.VisualEffectBurst
import com.yet.tetris.domain.model.effects.VisualEffectEvent
import com.yet.tetris.domain.model.effects.VisualTextKey
import com.yet.tetris.feature.game.GameComponent
import com.yet.tetris.ui.components.Scaffold
import com.yet.tetris.ui.strings.Strings
import com.yet.tetris.ui.view.game.components.NextPiecePreview
import com.yet.tetris.ui.view.game.components.StatItem
import com.yet.tetris.ui.view.game.dialog.ErrorDialog
import com.yet.tetris.ui.view.game.dialog.GameOverDialog
import com.yet.tetris.ui.view.game.dialog.PauseDialog
import com.yet.tetris.ui.view.game.rendering.WebBoardCell
import com.yet.tetris.ui.view.game.rendering.WebLineSweepEffect
import com.yet.tetris.ui.view.game.rendering.WebLockGlowEffect
import com.yet.tetris.ui.view.game.rendering.colorWithAlpha
import com.yet.tetris.ui.view.game.rendering.webBoardChromeStyle
import com.yet.tetris.ui.view.game.rendering.webThemeEffectStyle
import com.yet.tetris.ui.view.game.rendering.webThemeMotionStyle
import com.yet.tetris.ui.view.settings.SettingsSheet
import com.yet.tetris.utils.RProps
import com.yet.tetris.utils.formatTime
import com.yet.tetris.utils.reactKey
import com.yet.tetris.utils.useAsState
import js.objects.unsafeJso
import kotlinx.browser.window
import mui.icons.material.Pause
import mui.material.Box
import mui.material.Drawer
import mui.material.DrawerAnchor
import mui.material.IconButton
import mui.system.sx
import react.FC
import react.useEffect
import react.useEffectOnce
import react.useState
import web.cssom.AlignItems
import web.cssom.AutoLengthProperty
import web.cssom.BackdropFilter
import web.cssom.Border
import web.cssom.BoxSizing
import web.cssom.ClassName
import web.cssom.Color
import web.cssom.Display
import web.cssom.FlexDirection
import web.cssom.FlexWrap
import web.cssom.JustifyContent
import web.cssom.Overflow
import web.cssom.Padding
import web.cssom.Position
import web.cssom.Transform
import web.cssom.integer
import web.cssom.number
import web.cssom.pct
import web.cssom.px
import web.cssom.rem
import web.cssom.vh
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

private data class WebFloatingText(
    val id: String,
    val text: String,
    val isHigh: Boolean,
    val power: Double,
    val durationMs: Int,
    val textColor: String,
    val strokeColor: String,
    val pulseDurationMs: Int,
    val pulseCount: Int,
)

private data class WebParticleBurst(
    val id: String,
    val isHigh: Boolean,
    val power: Double,
    val particleCount: Int,
    val seed: Int,
    val durationMs: Int,
    val primaryColor: String,
    val secondaryColor: String,
    val opacityBoost: Double,
    val usesSquares: Boolean,
)

private data class WebViewport(
    val width: Int,
    val height: Int,
)

private enum class WebLayoutClass {
    Compact,
    Medium,
    Expanded,
}

private data class WebLayoutMetrics(
    val rootPaddingRem: Double,
    val gapRem: Double,
    val boardCanvasPx: Double,
    val boardMaxWidthPx: Double,
    val boardMaxHeightPx: Double,
    val rightPaneWidthPx: Double,
    val leftPaneWidthPx: Double,
    val previewMainPx: Double,
    val previewSmallPx: Double,
)

@OptIn(ExperimentalWasmJsInterop::class)
val GameContent =
    FC<RProps<GameComponent>> { props ->
        val model by props.component.model.useAsState()
        val dialogSlot by props.component.childSlot.useAsState()
        val sheetSlot by props.component.sheetSlot.useAsState()
        val activeSheet = sheetSlot.child?.instance

        val (shakeClass, setShakeClass) = useState("")
        val (shakeDurationMs, setShakeDurationMs) = useState(220)
        val (contentScale, setContentScale) = useState(1.0)
        val (scaleTransitionMs, setScaleTransitionMs) = useState(180)
        val (flashAlpha, setFlashAlpha) = useState(0.0)
        val (flashColor, setFlashColor) = useState("#ffffff")
        val (flashFadeDurationMs, setFlashFadeDurationMs) = useState(180)
        val (floatingTexts, setFloatingTexts) = useState<List<WebFloatingText>>(emptyList())
        val (particleBursts, setParticleBursts) = useState<List<WebParticleBurst>>(emptyList())
        val (lineSweeps, setLineSweeps) = useState<List<WebLineSweepEffect>>(emptyList())
        val (lockGlows, setLockGlows) = useState<List<WebLockGlowEffect>>(emptyList())
        val (boardEffectTimeMs, setBoardEffectTimeMs) = useState(window.performance.now())
        val (viewport, setViewport) =
            useState(
                WebViewport(
                    width = window.innerWidth,
                    height = window.innerHeight,
                ),
            )
        val (reduceMotion, setReduceMotion) =
            useState(
                window.matchMedia("(prefers-reduced-motion: reduce)").matches,
            )
        val (layoutClass, layoutMetrics) = resolveWebLayoutMetrics(viewport)
        val gameState = model.gameState
        val formattedTime = formatTime(model.elapsedTime)
        val nextPieces = gameState?.previewPieces.orEmpty()
        val effectStyle = webThemeEffectStyle(model.settings)
        val motionStyle = webThemeMotionStyle(model.settings.themeConfig.visualTheme)
        val boardChrome = webBoardChromeStyle(model.settings, reduceMotion)

        fun triggerScreenShake(
            intensity: IntensityLevel,
            power: Float,
        ) {
            if (reduceMotion) {
                setShakeClass("")
                setContentScale(1.0)
                return
            }
            val isHigh = intensity == IntensityLevel.HIGH
            val durationMs = if (isHigh) motionStyle.shakeDurationHighMs else motionStyle.shakeDurationLowMs
            setShakeClass("")
            setShakeDurationMs(durationMs)
            setScaleTransitionMs(durationMs)
            window.setTimeout(
                handler = {
                    setShakeClass(if (isHigh) "juice-shake-high" else "juice-shake-low")
                },
                timeout = 0,
            )

            setContentScale(if (isHigh) 1.04 else 1.02)
            window.setTimeout(
                handler = {
                    setContentScale(1.0)
                },
                timeout = motionStyle.scaleResetDelayMs,
            )
            window.setTimeout(
                handler = {
                    setShakeClass("")
                },
                timeout = durationMs,
            )
        }

        fun triggerScreenFlash(power: Float) {
            setFlashColor(effectStyle.flashColor)
            setFlashFadeDurationMs(if (reduceMotion) max(90, motionStyle.flashFadeDurationMs / 2) else motionStyle.flashFadeDurationMs)
            val startAlpha = if (reduceMotion) 0.26 else 0.45
            val endAlpha = if (reduceMotion) min(0.54, effectStyle.flashBoost) else effectStyle.flashBoost
            setFlashAlpha(startAlpha + ((endAlpha - startAlpha) * power))
            window.setTimeout(
                handler = {
                    setFlashAlpha(0.0)
                },
                timeout = 16,
            )
        }

        fun addFloatingText(
            textKey: VisualTextKey,
            comboStreak: Int,
            intensity: IntensityLevel,
            power: Float,
            sequence: Long,
        ) {
            val isHigh = intensity == IntensityLevel.HIGH
            val baseDurationMs = if (isHigh) 1100.0 else 780.0
            val durationMs =
                (
                    baseDurationMs *
                        if (isHigh) {
                            motionStyle.floatingDurationHighMultiplier
                        } else {
                            motionStyle.floatingDurationLowMultiplier
                        } *
                        if (reduceMotion) 0.72 else 1.0
                ).toInt()
            val id = "$sequence-${window.performance.now()}"
            val entry =
                WebFloatingText(
                    id = id,
                    text = resolveFloatingTextMessage(textKey, comboStreak),
                    isHigh = isHigh,
                    power = power.toDouble(),
                    durationMs = durationMs,
                    textColor = if (isHigh) effectStyle.textHigh else effectStyle.textLow,
                    strokeColor = if (isHigh) effectStyle.textStrokeHigh else effectStyle.textStrokeLow,
                    pulseDurationMs = if (reduceMotion) durationMs else motionStyle.pulseDurationMs,
                    pulseCount =
                        if (reduceMotion) {
                            1
                        } else if (isHigh) {
                            max(3, durationMs / motionStyle.pulseDurationMs)
                        } else {
                            1
                        },
                )

            setFloatingTexts { previous -> previous + entry }

            window.setTimeout(
                handler = {
                    setFloatingTexts { previous -> previous.filterNot { it.id == id } }
                },
                timeout = durationMs + 100,
            )
        }

        fun addParticleBurst(
            burst: VisualEffectBurst,
            intensity: IntensityLevel,
            power: Float,
            particleCount: Int,
            sequence: Long,
        ) {
            val id = "$sequence-${burst.id}-${window.performance.now()}"
            val entry =
                WebParticleBurst(
                    id = id,
                    isHigh = intensity == IntensityLevel.HIGH,
                    power = power.toDouble(),
                    particleCount = if (reduceMotion) max(6, (particleCount * 0.55).toInt()) else particleCount,
                    seed = burst.id.toInt(),
                    durationMs = (550.0 * motionStyle.particleDurationMultiplier * if (reduceMotion) 0.72 else 1.0).toInt(),
                    primaryColor = effectStyle.particlePrimary,
                    secondaryColor = effectStyle.particleSecondary,
                    opacityBoost = effectStyle.particleOpacityBoost,
                    usesSquares = effectStyle.particleUsesSquares,
                )

            setParticleBursts { previous -> previous + entry }

            window.setTimeout(
                handler = {
                    setParticleBursts { previous -> previous.filterNot { it.id == id } }
                },
                timeout = entry.durationMs + 100,
            )
        }

        fun addLineSweep(
            burst: VisualEffectBurst,
            sequence: Long,
        ) {
            if (burst.clearedRows.isEmpty()) return
            val durationMs = 520.0 * motionStyle.sweepDurationMultiplier * if (reduceMotion) 0.8 else 1.0
            val id = "$sequence-sweep-${burst.id}"
            val entry =
                WebLineSweepEffect(
                    id = id,
                    clearedRows = burst.clearedRows,
                    createdAtMs = window.performance.now(),
                    durationMs = durationMs,
                    primaryColor = effectStyle.sweepPrimary,
                    secondaryColor = effectStyle.sweepSecondary,
                    fillColor = effectStyle.sweepFill,
                    opacityBoost = effectStyle.sweepOpacityBoost,
                )
            setLineSweeps { previous -> previous + entry }
            window.setTimeout(
                handler = {
                    setLineSweeps { previous -> previous.filterNot { it.id == id } }
                },
                timeout = durationMs.toInt() + 100,
            )
        }

        fun addLockGlow(
            burst: VisualEffectBurst,
            sequence: Long,
        ) {
            if (burst.lockCells.isEmpty()) return
            val durationMs = 460.0 * motionStyle.lockGlowDurationMultiplier * if (reduceMotion) 0.78 else 1.0
            val id = "$sequence-lock-${burst.id}"
            val entry =
                WebLockGlowEffect(
                    id = id,
                    cells = burst.lockCells.map { WebBoardCell(x = it.x, y = it.y) },
                    createdAtMs = window.performance.now(),
                    durationMs = durationMs,
                    primaryColor = effectStyle.lockGlowPrimary,
                    secondaryColor = effectStyle.lockGlowSecondary,
                    opacityBoost = effectStyle.lockGlowOpacityBoost,
                    cornerRadiusFactor = effectStyle.lockGlowCornerRadiusFactor,
                )
            setLockGlows { previous -> previous + entry }
            window.setTimeout(
                handler = {
                    setLockGlows { previous -> previous.filterNot { it.id == id } }
                },
                timeout = durationMs.toInt() + 100,
            )
        }

        fun processVisualEffectBurst(
            sequence: Long,
            burst: VisualEffectBurst,
        ) {
            burst.events.forEach { event ->
                when (event) {
                    is VisualEffectEvent.ScreenShake ->
                        triggerScreenShake(
                            event.intensity,
                            event.power,
                        )

                    is VisualEffectEvent.ScreenFlash -> triggerScreenFlash(event.power)
                    is VisualEffectEvent.FloatingText ->
                        addFloatingText(
                            textKey = event.textKey,
                            comboStreak = burst.comboStreak,
                            intensity = event.intensity,
                            power = event.power,
                            sequence = sequence,
                        )

                    is VisualEffectEvent.Explosion ->
                        addParticleBurst(
                            burst = burst,
                            intensity = event.intensity,
                            power = event.power,
                            particleCount = event.particleCount,
                            sequence = sequence,
                        )
                }
            }
            addLineSweep(burst = burst, sequence = sequence)
            addLockGlow(burst = burst, sequence = sequence)
        }

        useEffect(model.visualEffectFeed.sequence) {
            model.visualEffectFeed.latest?.let { burst ->
                val sequence = model.visualEffectFeed.sequence
                processVisualEffectBurst(sequence = sequence, burst = burst)
                props.component.onVisualEffectConsumed(sequence)
            }
        }

        useEffectOnce {
            val mediaQuery = window.matchMedia("(prefers-reduced-motion: reduce)")
            val listener: (dynamic) -> Unit = {
                setReduceMotion(mediaQuery.matches)
            }

            mediaQuery.asDynamic().addEventListener("change", listener)

            val cleanup: () -> Unit = {
                mediaQuery.asDynamic().removeEventListener("change", listener)
            }

            cleanup
        }

        useEffect(lineSweeps.size, lockGlows.size, reduceMotion, boardChrome.shimmerEnabled) {
            if (lineSweeps.isEmpty() && lockGlows.isEmpty() && !boardChrome.shimmerEnabled) {
                return@useEffect
            }

            var frameHandle = 0
            lateinit var tick: (Double) -> Unit
            tick = { time ->
                setBoardEffectTimeMs(time)
                frameHandle = window.requestAnimationFrame(tick)
            }

            frameHandle = window.requestAnimationFrame(tick)

            val cleanup: () -> Unit = {
                window.cancelAnimationFrame(frameHandle)
            }

            cleanup
        }

        // Keyboard controls
        useEffectOnce {
            val handleKeyDown = { event: dynamic ->
                val target = event.target
                val tagName = target?.tagName?.toString()?.lowercase()
                val isEditableTarget =
                    target?.isContentEditable == true ||
                        tagName == "input" ||
                        tagName == "textarea" ||
                        tagName == "select"

                if (!isEditableTarget) {
                    val key = event.key?.toString()?.lowercase() ?: ""
                    val code = event.code?.toString() ?: ""

                    val handled =
                        when {
                            code == "ArrowLeft" || code == "KeyA" || key == "arrowleft" || key == "a" -> {
                                props.component.onMoveLeft()
                                true
                            }

                            code == "ArrowRight" || code == "KeyD" || key == "arrowright" || key == "d" -> {
                                props.component.onMoveRight()
                                true
                            }

                            code == "ArrowDown" || code == "KeyS" || key == "arrowdown" || key == "s" -> {
                                props.component.onMoveDown()
                                true
                            }

                            code == "ArrowUp" || code == "KeyW" || code == "Space" || key == "arrowup" || key == "w" || key == " " -> {
                                props.component.onRotate()
                                true
                            }

                            code == "Enter" || code == "NumpadEnter" || code == "KeyV" || key == "enter" || key == "v" -> {
                                props.component.onHardDrop()
                                true
                            }

                            code == "KeyC" || code == "KeyH" || key == "c" || key == "h" -> {
                                props.component.onHold()
                                true
                            }

                            code == "Escape" || code == "KeyP" || key == "escape" || key == "p" -> {
                                props.component.onPause()
                                true
                            }

                            else -> false
                        }

                    if (handled) {
                        event.preventDefault()
                    }
                }
            }

            window.addEventListener("keydown", handleKeyDown)

            val cleanup: () -> Unit = {
                window.removeEventListener("keydown", handleKeyDown)
            }

            cleanup
        }

        useEffectOnce {
            val handleResize: (dynamic) -> Unit = {
                setViewport(
                    WebViewport(
                        width = window.innerWidth,
                        height = window.innerHeight,
                    ),
                )
            }

            window.addEventListener("resize", handleResize)

            val cleanup: () -> Unit = {
                window.removeEventListener("resize", handleResize)
            }

            cleanup
        }

        Scaffold {
            sx {
                backgroundColor = Color("#000000")
                overflow = Overflow.hidden
            }

            Box {
                sx {
                    position = Position.relative
                    display = Display.flex
                    flexDirection = FlexDirection.column
                    height = 100.vh
                    width = 100.pct
                    padding = layoutMetrics.rootPaddingRem.rem
                    boxSizing = BoxSizing.borderBox
                    overflow = Overflow.hidden
                }

                Box {
                    className = shakeClass.toClassName()
                    sx {
                        position = Position.relative
                        display = Display.flex
                        flexDirection =
                            if (layoutClass == WebLayoutClass.Compact) {
                                FlexDirection.column
                            } else {
                                FlexDirection.row
                            }
                        flexGrow = number(1.0)
                        minHeight = 0.px
                        minWidth = 0.px
                        gap = layoutMetrics.gapRem.rem
                        transform = "scale($contentScale)".unsafeCast<Transform>()
                        asDynamic().transition = "transform ${scaleTransitionMs}ms ease-out"
                        asDynamic().animationDuration = "${shakeDurationMs}ms"
                    }

                    when (layoutClass) {
                        WebLayoutClass.Compact -> {
                            Box {
                                sx {
                                    display = Display.flex
                                    flexDirection = FlexDirection.column
                                    gap = layoutMetrics.gapRem.rem
                                }

                                Box {
                                    sx {
                                        display = Display.flex
                                        alignItems = AlignItems.center
                                        gap = 0.45.rem
                                        flexWrap = FlexWrap.wrap
                                    }

                                    IconButton {
                                        sx {
                                            backgroundColor = Color("rgba(255, 255, 255, 0.1)")
                                            backdropFilter = "blur(10px)".unsafeCast<BackdropFilter>()
                                            border =
                                                "1px solid rgba(255, 255, 255, 0.2)".unsafeCast<Border>()
                                            padding = 0.42.rem
                                            minWidth = "auto".unsafeCast<web.cssom.MinWidth>()
                                            hover {
                                                backgroundColor = Color("rgba(255, 255, 255, 0.2)")
                                            }
                                        }
                                        onClick = { props.component.onPause() }
                                        Pause()
                                    }

                                    IconButton {
                                        sx {
                                            backgroundColor = Color("rgba(255, 255, 255, 0.1)")
                                            backdropFilter = "blur(10px)".unsafeCast<BackdropFilter>()
                                            border =
                                                "1px solid rgba(255, 255, 255, 0.2)".unsafeCast<Border>()
                                            padding = 0.42.rem
                                            minWidth = "auto".unsafeCast<web.cssom.MinWidth>()
                                            hover {
                                                backgroundColor = Color("rgba(255, 255, 255, 0.2)")
                                            }
                                        }
                                        onClick = { props.component.onHold() }
                                        +"H"
                                    }

                                    Box {
                                        sx {
                                            display = Display.flex
                                            gap = 0.7.rem
                                            padding = Padding(0.45.rem, 0.75.rem)
                                            backgroundColor = Color("rgba(255, 255, 255, 0.1)")
                                            backdropFilter = "blur(10px)".unsafeCast<BackdropFilter>()
                                            border =
                                                "1px solid rgba(255, 255, 255, 0.2)".unsafeCast<Border>()
                                            borderRadius = 0.75.rem
                                            flexGrow = number(1.0)
                                            justifyContent = JustifyContent.spaceAround
                                            minWidth = 0.px
                                            flexWrap = FlexWrap.wrap
                                        }

                                        StatItem {
                                            label = Strings.SCORE
                                            value = gameState?.score?.toString() ?: "0"
                                        }

                                        StatItem {
                                            label = Strings.LINES
                                            value = gameState?.linesCleared?.toString() ?: "0"
                                        }

                                        StatItem {
                                            label = Strings.LEVEL
                                            value = gameState?.level?.toString() ?: "1"
                                        }
                                    }
                                }
                            }

                            Box {
                                sx {
                                    flexGrow = number(1.0)
                                    minHeight = 0.px
                                    minWidth = 0.px
                                    display = Display.flex
                                    alignItems = AlignItems.center
                                    justifyContent = JustifyContent.center
                                    overflow = Overflow.hidden
                                }

                                gameState?.let { currentGameState ->
                                    GameBoard {
                                        this.gameState = currentGameState
                                        this.settings = model.settings
                                        this.ghostY = model.ghostPieceY
                                        this.onDragStarted = { props.component.onDragStarted() }
                                        this.onDragged = { deltaX, deltaY ->
                                            props.component.onDragged(deltaX, deltaY)
                                        }
                                        this.onDragEnded = { props.component.onDragEnded() }
                                        this.onTap = { props.component.onRotate() }
                                        this.onBoardSizeChanged = { height ->
                                            props.component.onBoardSizeChanged(height)
                                        }
                                        this.canvasWidthPx = layoutMetrics.boardCanvasPx
                                        this.maxBoardWidthPx = layoutMetrics.boardMaxWidthPx
                                        this.maxBoardHeightPx = layoutMetrics.boardMaxHeightPx
                                        this.lineSweeps = lineSweeps
                                        this.lockGlows = lockGlows
                                        this.effectTimeMs = boardEffectTimeMs
                                        this.reducedMotion = reduceMotion
                                    }
                                }
                            }

                            gameState?.let { currentGameState ->
                                Box {
                                    sx {
                                        display = Display.flex
                                        alignItems = AlignItems.center
                                        gap = 0.45.rem
                                        flexWrap = FlexWrap.wrap
                                    }

                                    NextPiecePreview {
                                        piece = currentGameState.holdPiece
                                        settings = model.settings
                                        title = Strings.HOLD
                                        canvasSize = layoutMetrics.previewSmallPx
                                        compact = true
                                    }

                                    val compactQueueCount = min(nextPieces.size, 3)
                                    for (index in 0 until compactQueueCount) {
                                        NextPiecePreview {
                                            piece = nextPieces[index]
                                            settings = model.settings
                                            title = Strings.NEXT
                                            showTitle = index == 0
                                            canvasSize = layoutMetrics.previewSmallPx
                                            compact = true
                                        }
                                    }

                                    Box {
                                        sx {
                                            display = Display.flex
                                            alignItems = AlignItems.center
                                            justifyContent = JustifyContent.center
                                            padding = Padding(0.45.rem, 0.75.rem)
                                            backgroundColor = Color("rgba(255, 255, 255, 0.1)")
                                            backdropFilter = "blur(10px)".unsafeCast<BackdropFilter>()
                                            border =
                                                "1px solid rgba(255, 255, 255, 0.2)".unsafeCast<Border>()
                                            borderRadius = 0.75.rem
                                            minWidth = 88.px
                                        }

                                        StatItem {
                                            label = Strings.TIME
                                            value = formattedTime
                                        }
                                    }
                                }
                            }
                        }

                        WebLayoutClass.Medium -> {
                            Box {
                                sx {
                                    flexGrow = number(1.0)
                                    minHeight = 0.px
                                    minWidth = 0.px
                                    display = Display.flex
                                    alignItems = AlignItems.center
                                    justifyContent = JustifyContent.center
                                    overflow = Overflow.hidden
                                }

                                gameState?.let { currentGameState ->
                                    GameBoard {
                                        this.gameState = currentGameState
                                        this.settings = model.settings
                                        this.ghostY = model.ghostPieceY
                                        this.onDragStarted = { props.component.onDragStarted() }
                                        this.onDragged = { deltaX, deltaY ->
                                            props.component.onDragged(deltaX, deltaY)
                                        }
                                        this.onDragEnded = { props.component.onDragEnded() }
                                        this.onTap = { props.component.onRotate() }
                                        this.onBoardSizeChanged = { height ->
                                            props.component.onBoardSizeChanged(height)
                                        }
                                        this.canvasWidthPx = layoutMetrics.boardCanvasPx
                                        this.maxBoardWidthPx = layoutMetrics.boardMaxWidthPx
                                        this.maxBoardHeightPx = layoutMetrics.boardMaxHeightPx
                                        this.lineSweeps = lineSweeps
                                        this.lockGlows = lockGlows
                                        this.effectTimeMs = boardEffectTimeMs
                                        this.reducedMotion = reduceMotion
                                    }
                                }
                            }

                            Box {
                                sx {
                                    width = layoutMetrics.rightPaneWidthPx.px
                                    display = Display.flex
                                    flexDirection = FlexDirection.column
                                    gap = layoutMetrics.gapRem.rem
                                    minHeight = 0.px
                                }

                                Box {
                                    sx {
                                        display = Display.flex
                                        alignItems = AlignItems.center
                                        gap = 0.45.rem
                                    }

                                    IconButton {
                                        sx {
                                            backgroundColor = Color("rgba(255, 255, 255, 0.1)")
                                            backdropFilter = "blur(10px)".unsafeCast<BackdropFilter>()
                                            border =
                                                "1px solid rgba(255, 255, 255, 0.2)".unsafeCast<Border>()
                                            padding = 0.45.rem
                                            minWidth = "auto".unsafeCast<web.cssom.MinWidth>()
                                            hover {
                                                backgroundColor = Color("rgba(255, 255, 255, 0.2)")
                                            }
                                        }
                                        onClick = { props.component.onPause() }
                                        Pause()
                                    }

                                    IconButton {
                                        sx {
                                            backgroundColor = Color("rgba(255, 255, 255, 0.1)")
                                            backdropFilter = "blur(10px)".unsafeCast<BackdropFilter>()
                                            border =
                                                "1px solid rgba(255, 255, 255, 0.2)".unsafeCast<Border>()
                                            padding = 0.45.rem
                                            minWidth = "auto".unsafeCast<web.cssom.MinWidth>()
                                            hover {
                                                backgroundColor = Color("rgba(255, 255, 255, 0.2)")
                                            }
                                        }
                                        onClick = { props.component.onHold() }
                                        +"H"
                                    }
                                }

                                Box {
                                    sx {
                                        display = Display.flex
                                        gap = 0.8.rem
                                        padding = Padding(0.55.rem, 0.85.rem)
                                        backgroundColor = Color("rgba(255, 255, 255, 0.1)")
                                        backdropFilter = "blur(10px)".unsafeCast<BackdropFilter>()
                                        border =
                                            "1px solid rgba(255, 255, 255, 0.2)".unsafeCast<Border>()
                                        borderRadius = 0.75.rem
                                        justifyContent = JustifyContent.spaceAround
                                        flexWrap = FlexWrap.wrap
                                    }

                                    StatItem {
                                        label = Strings.SCORE
                                        value = gameState?.score?.toString() ?: "0"
                                    }
                                    StatItem {
                                        label = Strings.LINES
                                        value = gameState?.linesCleared?.toString() ?: "0"
                                    }
                                    StatItem {
                                        label = Strings.LEVEL
                                        value = gameState?.level?.toString() ?: "1"
                                    }
                                }

                                gameState?.let { currentGameState ->
                                    Box {
                                        sx {
                                            display = Display.flex
                                            flexDirection = FlexDirection.column
                                            gap = 0.5.rem
                                            padding = 0.55.rem
                                            backgroundColor = Color("rgba(255, 255, 255, 0.1)")
                                            backdropFilter = "blur(10px)".unsafeCast<BackdropFilter>()
                                            border =
                                                "1px solid rgba(255, 255, 255, 0.2)".unsafeCast<Border>()
                                            borderRadius = 0.75.rem
                                            minHeight = 0.px
                                        }

                                        NextPiecePreview {
                                            piece = currentGameState.holdPiece
                                            settings = model.settings
                                            title = Strings.HOLD
                                            canvasSize = layoutMetrics.previewMainPx
                                            chrome = false
                                        }

                                        val mediumQueueCount = min(nextPieces.size, 3)
                                        for (index in 0 until mediumQueueCount) {
                                            NextPiecePreview {
                                                piece = nextPieces[index]
                                                settings = model.settings
                                                title = Strings.NEXT
                                                showTitle = index == 0
                                                canvasSize = layoutMetrics.previewSmallPx
                                                compact = true
                                                chrome = false
                                            }
                                        }
                                    }
                                }

                                Box {
                                    sx {
                                        flexGrow = number(1.0)
                                    }
                                }

                                Box {
                                    sx {
                                        display = Display.flex
                                        alignItems = AlignItems.center
                                        justifyContent = JustifyContent.center
                                        padding = Padding(0.55.rem, 0.85.rem)
                                        backgroundColor = Color("rgba(255, 255, 255, 0.1)")
                                        backdropFilter = "blur(10px)".unsafeCast<BackdropFilter>()
                                        border =
                                            "1px solid rgba(255, 255, 255, 0.2)".unsafeCast<Border>()
                                        borderRadius = 0.75.rem
                                    }

                                    StatItem {
                                        label = Strings.TIME
                                        value = formattedTime
                                    }
                                }
                            }
                        }

                        WebLayoutClass.Expanded -> {
                            Box {
                                sx {
                                    width = layoutMetrics.leftPaneWidthPx.px
                                    display = Display.flex
                                    flexDirection = FlexDirection.column
                                    gap = layoutMetrics.gapRem.rem
                                    minHeight = 0.px
                                }

                                Box {
                                    sx {
                                        display = Display.flex
                                        gap = 0.8.rem
                                        padding = Padding(0.65.rem, 0.95.rem)
                                        backgroundColor = Color("rgba(255, 255, 255, 0.1)")
                                        backdropFilter = "blur(10px)".unsafeCast<BackdropFilter>()
                                        border =
                                            "1px solid rgba(255, 255, 255, 0.2)".unsafeCast<Border>()
                                        borderRadius = 0.75.rem
                                        justifyContent = JustifyContent.spaceAround
                                        flexWrap = FlexWrap.wrap
                                    }

                                    StatItem {
                                        label = Strings.SCORE
                                        value = gameState?.score?.toString() ?: "0"
                                    }
                                    StatItem {
                                        label = Strings.LINES
                                        value = gameState?.linesCleared?.toString() ?: "0"
                                    }
                                    StatItem {
                                        label = Strings.LEVEL
                                        value = gameState?.level?.toString() ?: "1"
                                    }
                                }

                                gameState?.let { currentGameState ->
                                    NextPiecePreview {
                                        piece = currentGameState.holdPiece
                                        settings = model.settings
                                        title = Strings.HOLD
                                        canvasSize = layoutMetrics.previewMainPx
                                    }
                                }

                                Box {
                                    sx {
                                        flexGrow = number(1.0)
                                    }
                                }

                                Box {
                                    sx {
                                        display = Display.flex
                                        alignItems = AlignItems.center
                                        gap = 0.5.rem
                                    }

                                    IconButton {
                                        sx {
                                            backgroundColor = Color("rgba(255, 255, 255, 0.1)")
                                            backdropFilter = "blur(10px)".unsafeCast<BackdropFilter>()
                                            border =
                                                "1px solid rgba(255, 255, 255, 0.2)".unsafeCast<Border>()
                                            padding = 0.5.rem
                                            minWidth = "auto".unsafeCast<web.cssom.MinWidth>()
                                            hover {
                                                backgroundColor = Color("rgba(255, 255, 255, 0.2)")
                                            }
                                        }
                                        onClick = { props.component.onPause() }
                                        Pause()
                                    }

                                    IconButton {
                                        sx {
                                            backgroundColor = Color("rgba(255, 255, 255, 0.1)")
                                            backdropFilter = "blur(10px)".unsafeCast<BackdropFilter>()
                                            border =
                                                "1px solid rgba(255, 255, 255, 0.2)".unsafeCast<Border>()
                                            padding = 0.5.rem
                                            minWidth = "auto".unsafeCast<web.cssom.MinWidth>()
                                            hover {
                                                backgroundColor = Color("rgba(255, 255, 255, 0.2)")
                                            }
                                        }
                                        onClick = { props.component.onHold() }
                                        +"H"
                                    }
                                }
                            }

                            Box {
                                sx {
                                    flexGrow = number(1.0)
                                    minHeight = 0.px
                                    minWidth = 0.px
                                    display = Display.flex
                                    alignItems = AlignItems.center
                                    justifyContent = JustifyContent.center
                                    overflow = Overflow.hidden
                                }

                                gameState?.let { currentGameState ->
                                    GameBoard {
                                        this.gameState = currentGameState
                                        this.settings = model.settings
                                        this.ghostY = model.ghostPieceY
                                        this.onDragStarted = { props.component.onDragStarted() }
                                        this.onDragged = { deltaX, deltaY ->
                                            props.component.onDragged(deltaX, deltaY)
                                        }
                                        this.onDragEnded = { props.component.onDragEnded() }
                                        this.onTap = { props.component.onRotate() }
                                        this.onBoardSizeChanged = { height ->
                                            props.component.onBoardSizeChanged(height)
                                        }
                                        this.canvasWidthPx = layoutMetrics.boardCanvasPx
                                        this.maxBoardWidthPx = layoutMetrics.boardMaxWidthPx
                                        this.maxBoardHeightPx = layoutMetrics.boardMaxHeightPx
                                        this.lineSweeps = lineSweeps
                                        this.lockGlows = lockGlows
                                        this.effectTimeMs = boardEffectTimeMs
                                        this.reducedMotion = reduceMotion
                                    }
                                }
                            }

                            gameState?.let { currentGameState ->
                                Box {
                                    sx {
                                        width = layoutMetrics.rightPaneWidthPx.px
                                        display = Display.flex
                                        flexDirection = FlexDirection.column
                                        gap = 0.55.rem
                                        padding = 0.65.rem
                                        backgroundColor = Color("rgba(255, 255, 255, 0.1)")
                                        backdropFilter = "blur(10px)".unsafeCast<BackdropFilter>()
                                        border =
                                            "1px solid rgba(255, 255, 255, 0.2)".unsafeCast<Border>()
                                        borderRadius = 0.75.rem
                                        minHeight = 0.px
                                        overflow = "auto".unsafeCast<Overflow>()
                                    }

                                    val expandedQueueCount = min(nextPieces.size, 3)
                                    for (index in 0 until expandedQueueCount) {
                                        NextPiecePreview {
                                            piece = nextPieces[index]
                                            settings = model.settings
                                            title = Strings.NEXT
                                            showTitle = index == 0
                                            canvasSize = layoutMetrics.previewMainPx
                                            chrome = false
                                        }
                                    }

                                    Box {
                                        sx {
                                            flexGrow = number(1.0)
                                        }
                                    }

                                    Box {
                                        sx {
                                            display = Display.flex
                                            alignItems = AlignItems.center
                                            justifyContent = JustifyContent.center
                                            padding = Padding(0.55.rem, 0.85.rem)
                                            backgroundColor = Color("rgba(255, 255, 255, 0.1)")
                                            backdropFilter = "blur(10px)".unsafeCast<BackdropFilter>()
                                            border =
                                                "1px solid rgba(255, 255, 255, 0.2)".unsafeCast<Border>()
                                            borderRadius = 0.75.rem
                                        }

                                        StatItem {
                                            label = Strings.TIME
                                            value = formattedTime
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Box {
                    sx {
                        position = Position.absolute
                        top = 0.px
                        left = 0.px
                        right = 0.px
                        bottom = 0.px
                        asDynamic().pointerEvents = "none"
                        asDynamic().zIndex = 10
                    }

                    Box {
                        sx {
                            position = Position.absolute
                            top = 0.px
                            left = 0.px
                            right = 0.px
                            bottom = 0.px
                            backgroundColor = Color(colorWithAlpha(flashColor, flashAlpha))
                            asDynamic().transition = "background-color ${flashFadeDurationMs}ms ease-out"
                        }
                    }

                    floatingTexts.forEach { textEntry ->
                        Box {
                            key = textEntry.id.reactKey()
                            className =
                                if (textEntry.isHigh) {
                                    "juice-floating-text juice-text-rise juice-text-pulse-high"
                                } else {
                                    "juice-floating-text juice-text-rise"
                                }.toClassName()
                            sx {
                                position = Position.absolute
                                top = 50.pct
                                left = 50.pct
                                transform = "translate(-50%, -50%)".unsafeCast<Transform>()
                                fontSize =
                                    if (textEntry.isHigh) {
                                        (44 + (12 * textEntry.power)).px
                                    } else {
                                        (24 + (6 * textEntry.power)).px
                                    }
                                fontWeight = integer(900)
                                color = Color(textEntry.textColor)
                                asDynamic()["WebkitTextStrokeWidth"] =
                                    if (textEntry.isHigh) "2.6px" else "1.6px"
                                asDynamic()["WebkitTextStrokeColor"] = textEntry.strokeColor
                                asDynamic()["textShadow"] =
                                    "0 3px 0 rgba(0,0,0,0.48), 0 6px 12px rgba(0,0,0,0.35)"
                                asDynamic()["fontFamily"] =
                                    "'Impact','Arial Black','Segoe UI',sans-serif"
                                asDynamic()["letterSpacing"] =
                                    if (textEntry.isHigh) "0.10em" else "0.08em"
                                asDynamic()["--juice-duration"] = "${textEntry.durationMs}ms"
                                asDynamic()["--juice-pulse-duration"] = "${textEntry.pulseDurationMs}ms"
                                asDynamic()["--juice-pulse-count"] = textEntry.pulseCount.toString()
                            }
                            +textEntry.text
                        }
                    }

                    particleBursts.forEach { burst ->
                        for (index in 0 until burst.particleCount) {
                            val angle =
                                seededFloat(
                                    seed = burst.seed,
                                    index = index,
                                    salt = 11,
                                ) * (PI * 2)
                            val distanceScale =
                                0.45 + seededFloat(
                                    seed = burst.seed,
                                    index = index,
                                    salt = 23,
                                ) * 0.75
                            val distance =
                                (80 + ((210 - 80) * burst.power)) * distanceScale
                            val sizeScale =
                                if (burst.isHigh) {
                                    6
                                } else {
                                    3
                                }
                            val dx = cos(angle) * distance
                            val dy = sin(angle) * distance - 36
                            val size =
                                2 + seededFloat(
                                    seed = burst.seed,
                                    index = index,
                                    salt = 37,
                                ) * sizeScale
                            Box {
                                key = "${burst.id}-$index".reactKey()
                                className = "juice-particle-burst".toClassName()
                                sx {
                                    position = Position.absolute
                                    top = 50.pct
                                    left = 50.pct
                                    width = size.px
                                    height = size.px
                                    borderRadius = if (burst.usesSquares) 2.px else 999.px
                                    backgroundColor =
                                        Color(
                                            colorWithAlpha(
                                                hex =
                                                    if (seededFloat(burst.seed, index, 41) > 0.48) {
                                                        burst.primaryColor
                                                    } else {
                                                        burst.secondaryColor
                                                    },
                                                alpha =
                                                    (
                                                        if (burst.isHigh) {
                                                            0.95
                                                        } else {
                                                            0.9
                                                        }
                                                    ) * burst.opacityBoost,
                                            ),
                                        )
                                    transform = "translate(-50%, -50%)".unsafeCast<Transform>()
                                    asDynamic()["--juice-dx"] = "${dx}px"
                                    asDynamic()["--juice-dy"] = "${dy}px"
                                    asDynamic()["--juice-duration"] = "${burst.durationMs}ms"
                                }
                            }
                        }
                    }
                }
            }
        }

        // Dialogs
        dialogSlot.child?.instance?.let { dialog ->
            when (dialog) {
                is GameComponent.DialogChild.Pause -> {
                    PauseDialog {
                        component = props.component
                    }
                }

                is GameComponent.DialogChild.GameOver -> {
                    GameOverDialog {
                        component = props.component
                        score = model.finalScore
                        lines = model.finalLinesCleared
                    }
                }

                is GameComponent.DialogChild.Error -> {
                    ErrorDialog {
                        message = dialog.message
                        onDismiss = { props.component.onDismissDialog() }
                    }
                }
            }
        }

        // Sheets
        Drawer {
            anchor = DrawerAnchor.bottom
            open = activeSheet != null
            onClose = { _, _ -> props.component.onDismissSheet() }

            ModalProps =
                unsafeJso {
                    sx {
                        zIndex = integer(1400)
                    }
                }

            PaperProps =
                unsafeJso {
                    sx {
                        borderTopLeftRadius = 16.px
                        borderTopRightRadius = 16.px

                        maxHeight = 90.vh
                        maxWidth = 600.px

                        marginLeft = "auto".unsafeCast<AutoLengthProperty>()
                        marginRight = "auto".unsafeCast<AutoLengthProperty>()
                    }
                }

            activeSheet?.let { child ->
                when (child) {
                    is GameComponent.SheetChild.Settings -> {
                        SettingsSheet {
                            component = child.component
                        }
                    }
                }
            }
        }
    }

private fun resolveWebLayoutMetrics(viewport: WebViewport): Pair<WebLayoutClass, WebLayoutMetrics> {
    val width = viewport.width.toDouble()
    val height = viewport.height.toDouble()
    val isShortHeight = height < 560.0
    val boardAspect = 0.5 // 10 cols / 20 rows

    val layoutClass =
        when {
            width < 760.0 || (width < 900.0 && !isShortHeight) -> WebLayoutClass.Compact
            width < 1240.0 || isShortHeight -> WebLayoutClass.Medium
            else -> WebLayoutClass.Expanded
        }

    val metrics =
        when (layoutClass) {
            WebLayoutClass.Compact -> {
                val rootPaddingRem = if (width < 430.0) 0.35 else 0.55
                val gapRem = if (isShortHeight) 0.35 else 0.55
                val reservedTopPx = if (width < 430.0) 238.0 else 210.0
                val widthBased = clamp(width * 0.86, 260.0, 440.0)
                val heightBased = clamp((height - reservedTopPx) * boardAspect, 230.0, 460.0)
                val boardWidth = min(widthBased, heightBased)

                WebLayoutMetrics(
                    rootPaddingRem = rootPaddingRem,
                    gapRem = gapRem,
                    boardCanvasPx = boardWidth,
                    boardMaxWidthPx = boardWidth,
                    boardMaxHeightPx = boardWidth / boardAspect,
                    rightPaneWidthPx = 0.0,
                    leftPaneWidthPx = 0.0,
                    previewMainPx = if (width < 430.0) 40.0 else 46.0,
                    previewSmallPx = if (width < 430.0) 34.0 else 38.0,
                )
            }

            WebLayoutClass.Medium -> {
                val rootPaddingRem = 0.6
                val gapRem = 0.65
                val widthBased = clamp(width * 0.54, 330.0, 520.0)
                val heightBased = clamp((height - 52.0) * boardAspect, 280.0, 560.0)
                val boardWidth = min(widthBased, heightBased)

                WebLayoutMetrics(
                    rootPaddingRem = rootPaddingRem,
                    gapRem = gapRem,
                    boardCanvasPx = boardWidth,
                    boardMaxWidthPx = boardWidth,
                    boardMaxHeightPx = boardWidth / boardAspect,
                    rightPaneWidthPx = clamp(width * 0.34, 250.0, 360.0),
                    leftPaneWidthPx = 0.0,
                    previewMainPx = 58.0,
                    previewSmallPx = 46.0,
                )
            }

            WebLayoutClass.Expanded -> {
                val rootPaddingRem = 0.7
                val gapRem = 0.75
                val widthBased = clamp(width * 0.43, 400.0, 620.0)
                val heightBased = clamp((height - 48.0) * boardAspect, 320.0, 640.0)
                val boardWidth = min(widthBased, heightBased)

                WebLayoutMetrics(
                    rootPaddingRem = rootPaddingRem,
                    gapRem = gapRem,
                    boardCanvasPx = boardWidth,
                    boardMaxWidthPx = boardWidth,
                    boardMaxHeightPx = boardWidth / boardAspect,
                    rightPaneWidthPx = clamp(width * 0.22, 260.0, 360.0),
                    leftPaneWidthPx = clamp(width * 0.20, 220.0, 320.0),
                    previewMainPx = 64.0,
                    previewSmallPx = 52.0,
                )
            }
        }

    return layoutClass to metrics
}

private fun clamp(
    value: Double,
    minValue: Double,
    maxValue: Double,
): Double = max(minValue, min(maxValue, value))

private fun seededFloat(
    seed: Int,
    index: Int,
    salt: Int,
): Double {
    var value = seed * 1103515245 + index * 12345 + salt * 1013904223
    value = value xor (value shl 13)
    value = value xor (value ushr 17)
    value = value xor (value shl 5)
    return (value and 0x7fffffff).toDouble() / 0x7fffffff.toDouble()
}

private fun String.toClassName(): ClassName = unsafeCast<ClassName>()

private fun resolveFloatingTextMessage(
    textKey: VisualTextKey,
    comboStreak: Int,
): String {
    val base =
        when (textKey) {
            VisualTextKey.SINGLE -> "SINGLE!"
            VisualTextKey.DOUBLE -> "DOUBLE!"
            VisualTextKey.TRIPLE -> "TRIPLE!"
            VisualTextKey.TETRIS -> "TETRIS!!!"
            VisualTextKey.CLEAR -> "CLEAR!"
        }

    return if (comboStreak >= 2) {
        "$base COMBO x$comboStreak!"
    } else {
        base
    }
}
