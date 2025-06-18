package com.lebaillyapp.uiwavedeformation.ui.components.phaseII

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue
import kotlin.math.max


@Composable
fun DeformableBitmapGridTouch(
    modifier: Modifier = Modifier.fillMaxSize(),
    bitmap: ImageBitmap,
    caseSize: Int = 50,
    ringEffectThickness: Float = 200f,
    waveEffectSpeed: Float = 0.9f,
    deformAmplif: Float = 3.5f
) {
    var touchPoint by remember { mutableStateOf<Offset?>(null) }
    var touchStartTime by remember { mutableStateOf<Long?>(null) }
    var elapsedTime by remember { mutableStateOf(0L) }


    val gridCols = caseSize
    val gridRows = caseSize


    // Timer qui avance elapsedTime quand il y a un touchStartTime
    LaunchedEffect(touchStartTime) {
        if (touchStartTime != null) {
            val start = System.currentTimeMillis()
            while (true) {
                val now = System.currentTimeMillis()
                elapsedTime = now - start
                // Stop l’animation quand la vague dépasse maxRadius + marge
                if (elapsedTime * 0.5f > 3000f) { // maxRadius ici = 300f
                    touchPoint = null
                    touchStartTime = null
                    elapsedTime = 0L
                    break
                }
                delay(16L) // ~60fps
            }
        } else {
            elapsedTime = 0L
        }
    }

    val touchModifier = Modifier.pointerInput(Unit) {
        forEachGesture {
            awaitPointerEventScope {
                val down = awaitFirstDown()
                touchPoint = down.position
                touchStartTime = System.currentTimeMillis()

                // On ne suit plus les mouvements, l’onde est autonome
                // Attend le up pour réinitialiser si besoin
                while (true) {
                    val event = awaitPointerEvent()
                    val up = event.changes.all { it.changedToUpIgnoreConsumed() }
                    if (up) {
                        // Optional : stop l’onde au relâchement
                        // touchPoint = null
                        // touchStartTime = null
                        break
                    }
                }
            }
        }
    }

    Canvas(modifier = modifier.then(touchModifier)) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        val bmpWidth = bitmap.width
        val bmpHeight = bitmap.height

        // centerCrop
        val scale = max(canvasWidth / bmpWidth, canvasHeight / bmpHeight)
        val drawWidth = bmpWidth * scale
        val drawHeight = bmpHeight * scale

        val offsetX = (canvasWidth - drawWidth) / 2f
        val offsetY = (canvasHeight - drawHeight) / 2f

        val cellWidth = drawWidth / gridCols
        val cellHeight = drawHeight / gridRows

        val androidBitmap = bitmap.asAndroidBitmap()
        val paint = android.graphics.Paint().apply { isFilterBitmap = true }

        val srcRect = android.graphics.Rect()
        val dstRect = android.graphics.RectF()

        val nativeCanvas = drawContext.canvas.nativeCanvas

        val waveSpeed = waveEffectSpeed // pixels per millisecond
        val waveRadius = elapsedTime * waveSpeed

        for (row in 0 until gridRows) {
            for (col in 0 until gridCols) {
                val bmpSrcLeft = (col * bmpWidth / gridCols)
                val bmpSrcTop = (row * bmpHeight / gridRows)
                val bmpSrcRight = ((col + 1) * bmpWidth / gridCols)
                val bmpSrcBottom = ((row + 1) * bmpHeight / gridRows)
                srcRect.set(bmpSrcLeft, bmpSrcTop, bmpSrcRight, bmpSrcBottom)

                // Position écran
                val screenLeft = offsetX + col * cellWidth
                val screenTop = offsetY + row * cellHeight
                val screenRight = offsetX + (col + 1) * cellWidth
                val screenBottom = offsetY + (row + 1) * cellHeight

                val tileCenter = Offset(
                    (screenLeft + screenRight) / 2f,
                    (screenTop + screenBottom) / 2f
                )

                val distance = touchPoint?.let { tp -> (tp - tileCenter).getDistance() } ?: Float.MAX_VALUE

                val ringThickness = ringEffectThickness
                val inWave = distance in (waveRadius - ringThickness)..(waveRadius + ringThickness)


                val deformFactor = if (touchPoint != null && inWave) {1f + deformAmplif * (1f - ((distance - waveRadius) / ringThickness).absoluteValue)} else 1.1f

                val centerX = (screenLeft + screenRight) / 2f
                val centerY = (screenTop + screenBottom) / 2f
                val halfWidth = (screenRight - screenLeft) / 2f * deformFactor
                val halfHeight = (screenBottom - screenTop) / 2f * deformFactor

                dstRect.set(
                    centerX - halfWidth,
                    centerY - halfHeight,
                    centerX + halfWidth,
                    centerY + halfHeight
                )

                nativeCanvas.drawBitmap(androidBitmap, srcRect, dstRect, paint)
            }
        }
    }
}