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
import com.yet.tetris.ui.view.settings.SettingsSheet
import com.yet.tetris.utils.RProps
import com.yet.tetris.utils.formatTime
import com.yet.tetris.utils.useAsState
import js.objects.unsafeJso
import kotlinx.browser.window
import mui.icons.material.Pause
import mui.material.Box
import mui.material.Container
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
import kotlin.math.sin

private data class WebFloatingText(
    val id: String,
    val text: String,
    val isHigh: Boolean,
    val power: Double,
    val durationMs: Int,
)

private data class WebParticleBurst(
    val id: String,
    val isHigh: Boolean,
    val power: Double,
    val particleCount: Int,
    val seed: Int,
)

@OptIn(ExperimentalWasmJsInterop::class)
val GameContent =
    FC<RProps<GameComponent>> { props ->
        val model by props.component.model.useAsState()
        val dialogSlot by props.component.childSlot.useAsState()
        val sheetSlot by props.component.sheetSlot.useAsState()
        val activeSheet = sheetSlot.child?.instance

        val (shakeClass, setShakeClass) = useState("")
        val (contentScale, setContentScale) = useState(1.0)
        val (flashAlpha, setFlashAlpha) = useState(0.0)
        val (floatingTexts, setFloatingTexts) = useState<List<WebFloatingText>>(emptyList())
        val (particleBursts, setParticleBursts) = useState<List<WebParticleBurst>>(emptyList())

        fun triggerScreenShake(
            intensity: IntensityLevel,
            power: Float,
        ) {
            val isHigh = intensity == IntensityLevel.HIGH
            setShakeClass("")
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
                timeout = 220,
            )
            window.setTimeout(
                handler = {
                    setShakeClass("")
                },
                timeout = if (isHigh) 320 else 240,
            )
        }

        fun triggerScreenFlash(power: Float) {
            setFlashAlpha(0.45 + (0.4 * power))
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
            val durationMs = if (isHigh) 1100 else 780
            val id = "$sequence-${window.performance.now()}"
            val entry =
                WebFloatingText(
                    id = id,
                    text = resolveFloatingTextMessage(textKey, comboStreak),
                    isHigh = isHigh,
                    power = power.toDouble(),
                    durationMs = durationMs,
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
                    particleCount = particleCount,
                    seed = burst.id.toInt(),
                )

            setParticleBursts { previous -> previous + entry }

            window.setTimeout(
                handler = {
                    setParticleBursts { previous -> previous.filterNot { it.id == id } }
                },
                timeout = 650,
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
        }

        useEffect(model.visualEffectFeed.sequence) {
            model.visualEffectFeed.latest?.let { burst ->
                val sequence = model.visualEffectFeed.sequence
                processVisualEffectBurst(sequence = sequence, burst = burst)
                props.component.onVisualEffectConsumed(sequence)
            }
        }

        // Keyboard controls
        useEffectOnce {
            val handleKeyDown = { event: dynamic ->
                when (event.key.toString().lowercase()) {
                    "arrowleft", "a" -> {
                        event.preventDefault()
                        props.component.onMoveLeft()
                    }

                    "arrowright", "d" -> {
                        event.preventDefault()
                        props.component.onMoveRight()
                    }

                    "arrowdown", "s" -> {
                        event.preventDefault()
                        props.component.onMoveDown()
                    }

                    "arrowup", "w", " " -> {
                        event.preventDefault()
                        props.component.onRotate()
                    }

                    "enter" -> {
                        event.preventDefault()
                        props.component.onHardDrop()
                    }

                    "escape", "p" -> {
                        event.preventDefault()
                        props.component.onPause()
                    }
                }
            }

            window.addEventListener("keydown", handleKeyDown)

            val cleanup: () -> Unit = {
                window.removeEventListener("keydown", handleKeyDown)
            }

            cleanup
        }

        Scaffold {
            sx {
                backgroundColor = Color("#000000")
                overflow = Overflow.hidden
            }

            Container {
                maxWidth = "lg"
                sx {
                    display = Display.flex
                    flexDirection = FlexDirection.column
                    height = 100.vh
                    padding = 0.5.rem
                    boxSizing = BoxSizing.borderBox
                    overflow = Overflow.hidden
                }

                Box {
                    sx {
                        position = Position.relative
                        display = Display.flex
                        flexDirection = FlexDirection.column
                        flexGrow = number(1.0)
                        minHeight = 0.px
                        overflow = Overflow.hidden
                    }

                    Box {
                        className = shakeClass.toClassName()
                        sx {
                            display = Display.flex
                            flexDirection = FlexDirection.column
                            flexGrow = number(1.0)
                            minHeight = 0.px
                            transform = "scale($contentScale)".unsafeCast<Transform>()
                            asDynamic().transition = "transform 180ms ease-out"
                        }

                        // Top row: Pause button, Stats, Next piece
                        Box {
                            sx {
                                display = Display.flex
                                justifyContent = JustifyContent.spaceBetween
                                alignItems = AlignItems.center
                                marginBottom = 0.5.rem
                                gap = 0.5.rem
                                flexWrap = FlexWrap.wrap
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

                            Box {
                                sx {
                                    display = Display.flex
                                    gap = 1.rem
                                    padding = Padding(0.5.rem, 1.rem)
                                    backgroundColor = Color("rgba(255, 255, 255, 0.1)")
                                    backdropFilter = "blur(10px)".unsafeCast<BackdropFilter>()
                                    border =
                                        "1px solid rgba(255, 255, 255, 0.2)".unsafeCast<Border>()
                                    borderRadius = 0.75.rem
                                    flexGrow = number(1.0)
                                    maxWidth = 400.px
                                    justifyContent = JustifyContent.spaceAround
                                    minWidth = 0.px
                                }

                                StatItem {
                                    label = Strings.SCORE
                                    value = model.gameState?.score?.toString() ?: "0"
                                }

                                StatItem {
                                    label = Strings.LINES
                                    value = model.gameState?.linesCleared?.toString() ?: "0"
                                }

                                StatItem {
                                    label = Strings.TIME
                                    value = formatTime(model.elapsedTime)
                                }
                            }

                            model.gameState?.nextPiece?.let { nextPiece ->
                                NextPiecePreview {
                                    piece = nextPiece
                                    settings = model.settings
                                }
                            }
                        }

                        // Game board - centered and responsive
                        Box {
                            sx {
                                flexGrow = number(1.0)
                                display = Display.flex
                                alignItems = AlignItems.center
                                justifyContent = JustifyContent.center
                                minHeight = 0.px
                                overflow = Overflow.hidden
                            }

                            model.gameState?.let { gameState ->
                                GameBoard {
                                    this.gameState = gameState
                                    this.settings = model.settings
                                    this.ghostY = model.ghostPieceY
                                    this.onDragStarted = { props.component.onDragStarted() }
                                    this.onDragged = { deltaX, deltaY ->
                                        props.component.onDragged(deltaX, deltaY)
                                    }
                                    this.onDragEnded = { props.component.onDragEnded() }
                                    this.onTap = { props.component.onRotate() }
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
                                backgroundColor = Color("rgba(255, 255, 255, $flashAlpha)")
                                asDynamic().transition = "background-color 180ms ease-out"
                            }
                        }

                        floatingTexts.forEach { textEntry ->
                            Box {
                                key = textEntry.id
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
                                    color =
                                        if (textEntry.isHigh) Color("#ffd54f") else Color("#ffffff")
                                    asDynamic()["WebkitTextStrokeWidth"] =
                                        if (textEntry.isHigh) "2.6px" else "1.6px"
                                    asDynamic()["WebkitTextStrokeColor"] =
                                        if (textEntry.isHigh) "rgba(58, 24, 0, 0.95)" else "rgba(11, 18, 32, 0.95)"
                                    asDynamic()["textShadow"] =
                                        "0 3px 0 rgba(0,0,0,0.48), 0 6px 12px rgba(0,0,0,0.35)"
                                    asDynamic()["fontFamily"] =
                                        "'Impact','Arial Black','Segoe UI',sans-serif"
                                    asDynamic()["letterSpacing"] =
                                        if (textEntry.isHigh) "0.10em" else "0.08em"
                                    asDynamic()["--juice-duration"] = "${textEntry.durationMs}ms"
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
                                val distance =
                                    (80 + ((210 - 80) * burst.power)) *
                                            (
                                                    0.45 + (
                                                            seededFloat(
                                                                seed = burst.seed,
                                                                index = index,
                                                                salt = 23,
                                                            ) * 0.75
                                                            )
                                                    )
                                val dx = cos(angle) * distance
                                val dy = sin(angle) * distance - 36
                                val size =
                                    2 +
                                            seededFloat(
                                                seed = burst.seed,
                                                index = index,
                                                salt = 37,
                                            ) *
                                            if (burst.isHigh) {
                                                6
                                            } else {
                                                3
                                            }
                                Box {
                                    key = "${burst.id}-$index"
                                    className = "juice-particle-burst".toClassName()
                                    sx {
                                        position = Position.absolute
                                        top = 50.pct
                                        left = 50.pct
                                        width = size.px
                                        height = size.px
                                        borderRadius = 999.px
                                        backgroundColor =
                                            if (burst.isHigh) {
                                                Color("rgba(255, 237, 153, 0.95)")
                                            } else {
                                                Color("rgba(255, 255, 255, 0.9)")
                                            }
                                        transform = "translate(-50%, -50%)".unsafeCast<Transform>()
                                        asDynamic()["--juice-dx"] = "${dx}px"
                                        asDynamic()["--juice-dy"] = "${dy}px"
                                        asDynamic()["--juice-duration"] = "550ms"
                                    }
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
