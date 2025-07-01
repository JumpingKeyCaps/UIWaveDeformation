package com.lebaillyapp.uiwavedeformation.ui.screen.phaseII.modeB

import android.graphics.BitmapFactory
import android.graphics.Paint
import android.os.Build
import android.os.SystemClock
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import com.lebaillyapp.uiwavedeformation.model.WaveParams
import com.lebaillyapp.uiwavedeformation.ui.shader.WaveDeformMultiShaderBrushProvider


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun WaterEffectComposable(
    modifier: Modifier = Modifier,
    @RawRes shaderResId: Int,
    @DrawableRes imageResId: Int,
    waveDurationSeconds: Float = 3f
) {
    val context = LocalContext.current

    val waveProvider = remember { WaveDeformMultiShaderBrushProvider(context, shaderResId) }

    val originalBitmap = remember(imageResId) {
        BitmapFactory.decodeResource(context.resources, imageResId).asImageBitmap()
    }

    val activeWaves = remember { mutableStateListOf<WaveParams>() }
    val currentTimeMs = remember { mutableStateOf(SystemClock.uptimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos {
                val now = SystemClock.uptimeMillis()
                currentTimeMs.value = now
                val waveDurationMs = (waveDurationSeconds * 1000).toLong()
                activeWaves.removeAll { (now - it.startTime) > waveDurationMs }
            }
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        if (event.type == PointerEventType.Press) {
                            event.changes.forEach { change ->
                                if (change.pressed && !change.previousPressed) {
                                    activeWaves.add(
                                        WaveParams(
                                            center = change.position,
                                            amplitude = 50f,
                                            frequency = 0.009f,
                                            damping = 0.00009f,
                                            speed  = 1f,
                                            startTime = SystemClock.uptimeMillis()
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
    ) {
        val now = currentTimeMs.value
        val wavesWithAge = activeWaves.map { wave ->
            wave to ((now - wave.startTime) / 1000f)
        }

        val visibleWaves = wavesWithAge
            .filter { (_, age) -> age <= waveDurationSeconds }
            .takeLast(16)

        val brush = if (visibleWaves.isNotEmpty()) {
            val centers = visibleWaves.map { it.first.center }
            val amplitudes = visibleWaves.map { (w, age) ->
                w.amplitude * (1f - (age / waveDurationSeconds).coerceIn(0f, 1f))
            }
            val frequencies = visibleWaves.map { it.first.frequency }
            val ages = visibleWaves.map { it.second }
            val dampings = visibleWaves.map { it.first.damping }

            waveProvider.getBrush(
                size = IntSize(size.width.toInt(), size.height.toInt()),
                originalBitmap = originalBitmap,
                waveCenters = centers,
                amplitudes = amplitudes,
                frequencies = frequencies,
                ages = ages,
                dampings = dampings
            )
        } else {
            waveProvider.getBrush(
                size = IntSize(size.width.toInt(), size.height.toInt()),
                originalBitmap = originalBitmap,
                waveCenters = emptyList(),
                amplitudes = emptyList(),
                frequencies = emptyList(),
                ages = emptyList(),
                dampings = emptyList()
            )
        }

        drawIntoCanvas { canvas ->
            val shader = brush.createShader(size)
            val paint = Paint().apply { this.shader = shader }
            canvas.nativeCanvas.drawRect(0f, 0f, size.width, size.height, paint)
        }
    }
}
