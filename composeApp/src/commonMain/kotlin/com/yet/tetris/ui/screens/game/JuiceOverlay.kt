package com.yet.tetris.ui.screens.game

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.yet.tetris.domain.model.effects.IntensityLevel
import com.yet.tetris.domain.model.effects.VisualEffectBurst
import com.yet.tetris.domain.model.effects.VisualEffectEvent
import com.yet.tetris.domain.model.effects.VisualTextKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun rememberJuiceOverlayState(): JuiceOverlayState {
    val scope = rememberCoroutineScope()
    return remember(scope) { JuiceOverlayState(scope = scope) }
}

@OptIn(ExperimentalTime::class)
class JuiceOverlayState internal constructor(
    private val scope: CoroutineScope,
) {
    var shakeOffsetX by mutableFloatStateOf(0f)
        private set
    var shakeOffsetY by mutableFloatStateOf(0f)
        private set
    var contentScale by mutableFloatStateOf(1f)
        private set

    val flashAlpha = Animatable(0f)

    val activeTexts = mutableStateListOf<JuiceFloatingText>()
    val activeBursts = mutableStateListOf<JuiceParticleBurst>()

    private var localId: Long by mutableStateOf(0L)

    fun dispatchBurst(burst: VisualEffectBurst) {
        burst.events.forEach { event ->
            when (event) {
                is VisualEffectEvent.ScreenShake -> triggerScreenShake(event)
                is VisualEffectEvent.ScreenFlash -> triggerScreenFlash(event)
                is VisualEffectEvent.FloatingText -> addFloatingText(event, burst)
                is VisualEffectEvent.Explosion -> addParticleBurst(event, burst.id)
            }
        }
    }

    private fun nextLocalId(seed: Long): Long {
        localId += 1
        return (seed shl 16) + localId
    }

    private fun addFloatingText(
        event: VisualEffectEvent.FloatingText,
        burst: VisualEffectBurst,
    ) {
        val durationMillis = if (event.intensity == IntensityLevel.HIGH) 1100L else 780L
        val entry =
            JuiceFloatingText(
                id = nextLocalId(burst.id),
                message = resolveFloatingTextMessage(event.textKey, burst.comboStreak),
                intensity = event.intensity,
                power = event.power,
                createdAtMillis = Clock.System.now().toEpochMilliseconds(),
                durationMillis = durationMillis,
            )
        activeTexts += entry
        scope.launch {
            delay(durationMillis + 80L)
            activeTexts.removeAll { it.id == entry.id }
        }
    }

    private fun addParticleBurst(
        event: VisualEffectEvent.Explosion,
        burstId: Long,
    ) {
        val durationMillis = 550L
        val entry =
            JuiceParticleBurst(
                id = nextLocalId(burstId),
                intensity = event.intensity,
                power = event.power,
                particleCount = event.particleCount,
                seed = burstId.toInt(),
                createdAtMillis = Clock.System.now().toEpochMilliseconds(),
                durationMillis = durationMillis,
            )
        activeBursts += entry
        scope.launch {
            delay(durationMillis + 80L)
            activeBursts.removeAll { it.id == entry.id }
        }
    }

    private fun triggerScreenShake(event: VisualEffectEvent.ScreenShake) {
        scope.launch {
            val isHigh = event.intensity == IntensityLevel.HIGH
            val amplitude =
                if (isHigh) {
                    lerp(12f, 22f, event.power)
                } else {
                    lerp(3f, 8f, event.power)
                }
            val scalePunch =
                if (isHigh) {
                    lerp(0.022f, 0.05f, event.power)
                } else {
                    lerp(0.008f, 0.02f, event.power)
                }
            val frameCount = if (isHigh) 14 else 9
            val frameDelayMs = if (isHigh) 16L else 22L

            repeat(frameCount) { step ->
                val progress = step.toFloat() / frameCount
                val decay = 1f - progress
                val xDirection = if (step % 2 == 0) 1f else -1f
                val yDirection = if (step % 3 == 0) -1f else 1f

                shakeOffsetX = xDirection * amplitude * decay
                shakeOffsetY = yDirection * amplitude * 0.35f * decay
                contentScale = 1f + scalePunch * decay

                delay(frameDelayMs)
            }

            shakeOffsetX = 0f
            shakeOffsetY = 0f
            contentScale = 1f
        }
    }

    private fun triggerScreenFlash(event: VisualEffectEvent.ScreenFlash) {
        scope.launch {
            val targetAlpha = lerp(0.45f, 0.85f, event.power)
            flashAlpha.snapTo(maxOf(flashAlpha.value, targetAlpha))
            flashAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 180, easing = FastOutLinearInEasing),
            )
        }
    }
}

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

@OptIn(ExperimentalTime::class)
@Composable
fun JuiceOverlay(
    state: JuiceOverlayState,
    modifier: Modifier = Modifier,
) {
    var frameTimeMillis by remember { mutableLongStateOf(Clock.System.now().toEpochMilliseconds()) }

    LaunchedEffect(state.activeTexts.size, state.activeBursts.size) {
        if (state.activeTexts.isEmpty() && state.activeBursts.isEmpty()) {
            return@LaunchedEffect
        }

        while (true) {
            withFrameNanos {
                frameTimeMillis = Clock.System.now().toEpochMilliseconds()
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (state.flashAlpha.value > 0.001f) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = state.flashAlpha.value)),
            )
        }

        state.activeTexts.forEach { floatingText ->
            val progress =
                ((frameTimeMillis - floatingText.createdAtMillis).toFloat() / floatingText.durationMillis)
                    .coerceIn(0f, 1f)
            if (progress < 1f) {
                val isHigh = floatingText.intensity == IntensityLevel.HIGH
                val riseDistance = if (isHigh) 220f else 130f
                val pulse =
                    if (isHigh) {
                        1f + (sin(progress * PI.toFloat() * 10f) * 0.12f * (1f - progress))
                    } else {
                        1f + ((1f - progress) * 0.04f)
                    }
                val baseTranslationY = -riseDistance * progress
                val baseAlpha = 1f - progress
                val outlineColor = if (isHigh) Color(0xFF3A1800) else Color(0xFF0B1220)
                val outlineThickness = if (isHigh) 3.4f else 2.1f
                val fontSize =
                    if (isHigh) {
                        lerp(44f, 56f, floatingText.power)
                    } else {
                        lerp(
                            24f,
                            30f,
                            floatingText.power,
                        )
                    }.sp

                listOf(
                    Offset(-1f, 0f),
                    Offset(1f, 0f),
                    Offset(0f, -1f),
                    Offset(0f, 1f),
                    Offset(-0.8f, -0.8f),
                    Offset(0.8f, -0.8f),
                    Offset(-0.8f, 0.8f),
                    Offset(0.8f, 0.8f),
                ).forEach { outlineOffset ->
                    Text(
                        modifier =
                            Modifier
                                .align(Alignment.Center)
                                .graphicsLayer {
                                    translationX = outlineOffset.x * outlineThickness
                                    translationY =
                                        baseTranslationY + (outlineOffset.y * outlineThickness)
                                    scaleX = pulse
                                    scaleY = pulse
                                    alpha = baseAlpha
                                },
                        text = floatingText.message,
                        color = outlineColor,
                        fontWeight = if (isHigh) FontWeight.Black else FontWeight.Bold,
                        fontSize = fontSize,
                    )
                }

                Text(
                    modifier =
                        Modifier
                            .align(Alignment.Center)
                            .graphicsLayer {
                                translationY = baseTranslationY
                                scaleX = pulse
                                scaleY = pulse
                                alpha = baseAlpha
                            },
                    text = floatingText.message,
                    color = if (isHigh) Color(0xFFFFD54F) else Color.White,
                    fontWeight = if (isHigh) FontWeight.Black else FontWeight.Bold,
                    fontSize = fontSize,
                )
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2f
            val centerY = size.height / 2f

            state.activeBursts.forEach { burst ->
                val progress =
                    ((frameTimeMillis - burst.createdAtMillis).toFloat() / burst.durationMillis)
                        .coerceIn(0f, 1f)
                if (progress >= 1f) {
                    return@forEach
                }

                val maxRadius = lerp(80f, 210f, burst.power)
                val baseAlpha = (1f - progress) * lerp(0.65f, 1f, burst.power)

                for (index in 0 until burst.particleCount) {
                    val angle =
                        seededFloat(
                            seed = burst.seed,
                            index = index,
                            salt = 11,
                        ) * (PI.toFloat() * 2f)
                    val speedScale =
                        0.45f + seededFloat(seed = burst.seed, index = index, salt = 23) * 0.75f
                    val radius = maxRadius * progress * speedScale
                    val x = centerX + cos(angle) * radius
                    val y = centerY + sin(angle) * radius - progress * 36f
                    val particleSize =
                        2f + seededFloat(seed = burst.seed, index = index, salt = 37) *
                                if (burst.intensity == IntensityLevel.HIGH) {
                                    6f
                                } else {
                                    3f
                                }

                    val color =
                        if (burst.intensity == IntensityLevel.HIGH) {
                            Color(1f, 0.93f, 0.6f, baseAlpha)
                        } else {
                            Color.White.copy(alpha = baseAlpha)
                        }

                    drawCircle(
                        color = color,
                        radius = particleSize,
                        center = Offset(x = x, y = y),
                    )
                }
            }
        }
    }
}

data class JuiceFloatingText(
    val id: Long,
    val message: String,
    val intensity: IntensityLevel,
    val power: Float,
    val createdAtMillis: Long,
    val durationMillis: Long,
)

data class JuiceParticleBurst(
    val id: Long,
    val intensity: IntensityLevel,
    val power: Float,
    val particleCount: Int,
    val seed: Int,
    val createdAtMillis: Long,
    val durationMillis: Long,
)

private fun seededFloat(
    seed: Int,
    index: Int,
    salt: Int,
): Float {
    var value = seed * 1103515245 + index * 12345 + salt * 1013904223
    value = value xor (value shl 13)
    value = value xor (value ushr 17)
    value = value xor (value shl 5)

    return (value and 0x7FFFFFFF) / 0x7FFFFFFF.toFloat()
}

private fun lerp(
    start: Float,
    end: Float,
    fraction: Float,
): Float = start + ((end - start) * fraction.coerceIn(0f, 1f))
