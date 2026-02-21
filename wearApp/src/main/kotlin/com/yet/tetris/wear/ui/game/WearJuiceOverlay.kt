package com.yet.tetris.wear.ui.game

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.wear.compose.material.Text
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

@Composable
fun rememberWearJuiceOverlayState(): WearJuiceOverlayState {
    val scope = rememberCoroutineScope()
    return remember(scope) { WearJuiceOverlayState(scope) }
}

class WearJuiceOverlayState internal constructor(
    private val scope: CoroutineScope,
) {
    var shakeOffsetX by mutableFloatStateOf(0f)
        private set
    var shakeOffsetY by mutableFloatStateOf(0f)
        private set
    var contentScale by mutableFloatStateOf(1f)
        private set

    val flashAlpha = Animatable(0f)
    val activeTexts = mutableStateListOf<WearJuiceFloatingText>()
    val activeBursts = mutableStateListOf<WearJuiceParticleBurst>()

    private var localId by mutableStateOf(0L)

    fun dispatchBurst(burst: VisualEffectBurst) {
        burst.events.forEach { event ->
            when (event) {
                is VisualEffectEvent.ScreenShake -> triggerScreenShake(event)
                is VisualEffectEvent.ScreenFlash -> triggerFlash(event.power)
                is VisualEffectEvent.FloatingText -> addFloatingText(event, burst)
                is VisualEffectEvent.Explosion -> addParticleBurst(event, burst.id)
            }
        }
    }

    private fun triggerScreenShake(event: VisualEffectEvent.ScreenShake) {
        scope.launch {
            val isHigh = event.intensity == IntensityLevel.HIGH
            val amplitude = if (isHigh) lerp(6f, 13f, event.power) else lerp(2f, 5f, event.power)
            val scalePunch =
                if (isHigh) lerp(0.018f, 0.04f, event.power) else lerp(0.007f, 0.018f, event.power)
            val frameCount = if (isHigh) 11 else 8
            val frameDelayMs = if (isHigh) 16L else 20L

            repeat(frameCount) { step ->
                val progress = step.toFloat() / frameCount
                val decay = 1f - progress
                val xDirection = if (step % 2 == 0) 1f else -1f
                val yDirection = if (step % 3 == 0) -1f else 1f

                shakeOffsetX = xDirection * amplitude * decay
                shakeOffsetY = yDirection * amplitude * 0.34f * decay
                contentScale = 1f + (scalePunch * decay)
                delay(frameDelayMs)
            }

            shakeOffsetX = 0f
            shakeOffsetY = 0f
            contentScale = 1f
        }
    }

    private fun triggerFlash(power: Float) {
        scope.launch {
            flashAlpha.snapTo(maxOf(flashAlpha.value, lerp(0.32f, 0.72f, power)))
            flashAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 160, easing = FastOutLinearInEasing),
            )
        }
    }

    private fun addFloatingText(
        event: VisualEffectEvent.FloatingText,
        burst: VisualEffectBurst,
    ) {
        localId += 1
        val duration = if (event.intensity == IntensityLevel.HIGH) 900L else 680L
        val entry =
            WearJuiceFloatingText(
                id = (burst.id shl 16) + localId,
                message = resolveWearText(event.textKey, burst.comboStreak),
                intensity = event.intensity,
                power = event.power,
                createdAtMillis = System.currentTimeMillis(),
                durationMillis = duration,
            )
        activeTexts += entry
        scope.launch {
            delay(duration + 80L)
            activeTexts.removeAll { it.id == entry.id }
        }
    }

    private fun addParticleBurst(
        event: VisualEffectEvent.Explosion,
        burstId: Long,
    ) {
        localId += 1
        val entry =
            WearJuiceParticleBurst(
                id = (burstId shl 16) + localId,
                power = event.power,
                particleCount = event.particleCount.coerceIn(18, 70),
                seed = burstId.toInt(),
                createdAtMillis = System.currentTimeMillis(),
                durationMillis = 520L,
            )
        activeBursts += entry
        scope.launch {
            delay(entry.durationMillis + 80L)
            activeBursts.removeAll { it.id == entry.id }
        }
    }
}

@Composable
fun WearJuiceOverlay(
    state: WearJuiceOverlayState,
    modifier: Modifier = Modifier,
) {
    var frameTimeMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(state.activeTexts.size, state.activeBursts.size) {
        if (state.activeTexts.isEmpty() && state.activeBursts.isEmpty()) {
            return@LaunchedEffect
        }
        while (true) {
            withFrameNanos {
                frameTimeMillis = System.currentTimeMillis()
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

        state.activeTexts.forEach { text ->
            val progress =
                ((frameTimeMillis - text.createdAtMillis).toFloat() / text.durationMillis)
                    .coerceIn(0f, 1f)
            if (progress < 1f) {
                val isHigh = text.intensity == IntensityLevel.HIGH
                val rise = if (isHigh) 48f else 30f
                val pulse =
                    if (isHigh) {
                        1f + (sin(progress * PI.toFloat() * 9f) * 0.09f * (1f - progress))
                    } else {
                        1f + (0.03f * (1f - progress))
                    }
                val fontSize =
                    if (isHigh) lerp(18f, 24f, text.power) else lerp(11f, 15f, text.power)
                val outlineColor = if (isHigh) Color(0xFF2D1200) else Color.Black
                val outlineThickness = if (isHigh) 2f else 1.3f
                val baseAlpha = 1f - progress

                listOf(
                    Offset(-1f, 0f),
                    Offset(1f, 0f),
                    Offset(0f, -1f),
                    Offset(0f, 1f),
                    Offset(-0.8f, -0.8f),
                    Offset(0.8f, -0.8f),
                    Offset(-0.8f, 0.8f),
                    Offset(0.8f, 0.8f),
                ).forEach { offset ->
                    Text(
                        modifier =
                            Modifier
                                .align(Alignment.Center)
                                .graphicsLayer {
                                    translationX = offset.x * outlineThickness
                                    translationY =
                                        (-rise * progress) + (offset.y * outlineThickness)
                                    scaleX = pulse
                                    scaleY = pulse
                                    alpha = baseAlpha
                                },
                        text = text.message,
                        color = outlineColor,
                        fontWeight = FontWeight.Black,
                        fontSize = fontSize.sp,
                    )
                }

                Text(
                    modifier =
                        Modifier
                            .align(Alignment.Center)
                            .graphicsLayer {
                                translationY = -rise * progress
                                scaleX = pulse
                                scaleY = pulse
                                alpha = baseAlpha
                            },
                    text = text.message,
                    color = if (isHigh) Color(0xFFFFD54F) else Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = fontSize.sp,
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

                val maxRadius = lerp(32f, 74f, burst.power)
                val alpha = (1f - progress) * lerp(0.55f, 0.95f, burst.power)

                for (index in 0 until burst.particleCount) {
                    val angle = seededFloat(burst.seed, index, 11) * (PI.toFloat() * 2f)
                    val speedScale = 0.45f + seededFloat(burst.seed, index, 23) * 0.75f
                    val radius = maxRadius * progress * speedScale
                    val x = centerX + cos(angle) * radius
                    val y = centerY + sin(angle) * radius - (progress * 10f)
                    val particleRadius = 1.2f + seededFloat(burst.seed, index, 37) * 2f

                    drawCircle(
                        color = Color(0xFFFFF0B2).copy(alpha = alpha),
                        radius = particleRadius,
                        center = Offset(x, y),
                    )
                }
            }
        }
    }
}

data class WearJuiceFloatingText(
    val id: Long,
    val message: String,
    val intensity: IntensityLevel,
    val power: Float,
    val createdAtMillis: Long,
    val durationMillis: Long,
)

data class WearJuiceParticleBurst(
    val id: Long,
    val power: Float,
    val particleCount: Int,
    val seed: Int,
    val createdAtMillis: Long,
    val durationMillis: Long,
)

private fun resolveWearText(
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

    return if (comboStreak >= 2) "$base COMBO x$comboStreak!" else base
}

private fun seededFloat(
    seed: Int,
    index: Int,
    salt: Int,
): Float {
    var value =
        seed.toLong() * 1_103_515_245L + index.toLong() * 12_345L + salt.toLong() * 1_013_904_223L
    value = value xor (value shl 13)
    value = value xor (value shr 17)
    value = value xor (value shl 5)
    val positive = value and 0x7fff_ffffL
    return positive.toFloat() / 0x7fff_ffffL
}

private fun lerp(
    start: Float,
    end: Float,
    fraction: Float,
): Float = start + ((end - start) * fraction.coerceIn(0f, 1f))
