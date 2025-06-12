package com.lebaillyapp.uiwavedeformation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.util.lerp
import com.lebaillyapp.uiwavedeformation.model.Wave

@Composable
fun WaveTrailCanvas(
    waves: List<Wave>,
    modifier: Modifier = Modifier,
    baseColor: Color = Color.White,
    baseStrokeWidth: Float = 4f,
    minAlpha: Float = 0.05f,
    maxAlpha: Float = 0.4f,
    maxDuration: Float = 2.5f // en secondes
) {
    val now = System.currentTimeMillis()

    Canvas(modifier = modifier) {
        waves.forEach { wave ->
            val elapsed = (now - wave.startTime) / 1000f
            val t = (elapsed / maxDuration).coerceIn(0f, 1f) // normalisé

            val alpha = lerp(maxAlpha, minAlpha, t)
            val stroke = lerp(baseStrokeWidth, 0.5f, t)
            val radius = wave.speed * elapsed

            drawCircle(
                color = baseColor.copy(alpha = alpha),
                center = wave.origin,
                radius = radius,
                style = Stroke(width = stroke)
            )
        }
    }
}