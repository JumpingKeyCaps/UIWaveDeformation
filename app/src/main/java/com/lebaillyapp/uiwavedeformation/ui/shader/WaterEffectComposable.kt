package com.lebaillyapp.uiwavedeformation.ui.shader

import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize

/**
 * Composable affichant une image avec un effet de déformation d'ondes d'eau
 * généré par un shader AGSL, réactif aux interactions tactiles.
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun WaterEffectComposable(
    modifier: Modifier = Modifier,
    @RawRes shaderResId: Int,
    @DrawableRes imageResId: Int,
    waveDurationSeconds: Float = 15f // NOUVEAU: Durée de vie des vagues (15s au lieu de 5s)
) {
    val context = LocalContext.current

    // Provider du shader
    val waveProvider = remember { WaveDeformMultiShaderBrushProvider(context, shaderResId) }

    // Chargement de l'image
    val originalBitmap: ImageBitmap = remember(imageResId) {
        val androidBitmap = BitmapFactory.decodeResource(context.resources, imageResId)
        androidBitmap.asImageBitmap()
    }

    // Animation du temps
    val infiniteTransition = rememberInfiniteTransition(label = "waterEffectTransition")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "timeAnimation"
    )

    // État des ondes actives
    val activeWaves = remember { mutableStateListOf<WaveParams>() }

    // Nettoyage des anciennes ondes avec la nouvelle durée
    LaunchedEffect(time) {
        activeWaves.removeAll { wave ->
            (time - wave.startTime) > waveDurationSeconds
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        activeWaves.add(
                            WaveParams(
                                center = offset,
                                amplitude = 30f,      // Réduit pour plus de subtilité
                                frequency = 0.003f,    // RÉDUIT: Moins de répétitions = vagues plus larges
                                speed = 0.1f,           // RÉDUIT: Plus lent = vagues plus longues
                                damping = 0.001f,     // RÉDUIT: Moins d'atténuation = propagation plus loin
                                startTime = time
                            )
                        )
                    },
                    onDrag = { change, _ ->
                        activeWaves.lastOrNull()?.let { lastWave ->
                            activeWaves[activeWaves.lastIndex] = lastWave.copy(center = change.position)
                        }
                    },
                    onDragEnd = {
                        // Optionnel: ajouter une logique de fin
                    }
                )
            }
    ) {
        val currentBrush = waveProvider.getBrush(
            size = IntSize(size.width.toInt(), size.height.toInt()),
            timeSeconds = time,
            originalBitmap = originalBitmap,
            waveCenters = activeWaves.map { it.center },
            amplitudes = activeWaves.map { it.amplitude },
            frequencies = activeWaves.map { it.frequency },
            speeds = activeWaves.map { it.speed },
            dampings = activeWaves.map { it.damping }
        )

        drawIntoCanvas { canvas ->
            val shader = currentBrush.createShader(size)
            val paint = android.graphics.Paint().apply {
                this.shader = shader
            }
            canvas.nativeCanvas.drawRect(
                0f, 0f, size.width, size.height, paint
            )
        }
    }
}

/**
 * Classe de données pour encapsuler les paramètres d'une onde individuelle.
 */
data class WaveParams(
    val center: Offset,
    val amplitude: Float,
    val frequency: Float,
    val speed: Float,
    val damping: Float,
    val startTime: Float
)