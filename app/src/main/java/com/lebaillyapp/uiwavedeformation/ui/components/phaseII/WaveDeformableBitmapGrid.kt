package com.lebaillyapp.uiwavedeformation.ui.components.phaseII

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import com.lebaillyapp.uiwavedeformation.viewmodel.phaseII.WaveTileViewModel
import kotlinx.coroutines.delay
import kotlin.math.max

@Composable
fun WaveDeformableBitmapGrid(
    modifier: Modifier,
    bitmap: ImageBitmap,
    viewModel: WaveTileViewModel,
    tileCols: Int = 40,
    tileRows: Int = 40,
    drawBackLayer: Boolean = false,
    defaultDeformFactor: Float = 1.05f // évite les interstices visuels
) {
    val currentTime by viewModel.currentTime

    LaunchedEffect(Unit) {
        while (true) {
            viewModel.updateTime()
            delay(16L)
        }
    }

    val touchModifier = Modifier.pointerInput(Unit) {
        forEachGesture {
            awaitPointerEventScope {
                val event = awaitPointerEvent()
                event.changes.forEach { change ->
                    if (change.pressed) {
                        viewModel.onTouch(change.position, change.id.value.toInt())
                    }
                }
                while (true) {
                    val move = awaitPointerEvent()
                    move.changes.forEach { change ->
                        if (change.pressed) {
                            viewModel.onTouch(change.position, change.id.value.toInt())
                        }
                    }
                    if (move.changes.all { !it.pressed }) break
                }
            }
        }
    }

    Canvas(modifier = modifier.then(touchModifier)) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        val bmpWidth = bitmap.width
        val bmpHeight = bitmap.height

        val scale = max(canvasWidth / bmpWidth, canvasHeight / bmpHeight)
        val drawWidth = bmpWidth * scale
        val drawHeight = bmpHeight * scale

        val offsetX = (canvasWidth - drawWidth) / 2f
        val offsetY = (canvasHeight - drawHeight) / 2f

        val cellWidth = drawWidth / tileCols
        val cellHeight = drawHeight / tileRows

        val androidBitmap = bitmap.asAndroidBitmap()
        val paint = android.graphics.Paint().apply { isFilterBitmap = true }

        val srcRect = android.graphics.Rect()
        val dstRect = android.graphics.RectF()

        val nativeCanvas = drawContext.canvas.nativeCanvas

        // 🧠 OPTION 2 — Fond bitmap complet (non déformé)
        if (drawBackLayer) {
            nativeCanvas.drawBitmap(
                androidBitmap,
                null,
                android.graphics.RectF(
                    offsetX,
                    offsetY,
                    offsetX + drawWidth,
                    offsetY + drawHeight
                ),
                paint
            )
        }


        for (row in 0 until tileRows) {
            for (col in 0 until tileCols) {
                val srcLeft = col * bmpWidth / tileCols
                val srcTop = row * bmpHeight / tileRows
                val srcRight = (col + 1) * bmpWidth / tileCols
                val srcBottom = (row + 1) * bmpHeight / tileRows
                srcRect.set(srcLeft, srcTop, srcRight, srcBottom)

                val centerX = offsetX + (col + 0.5f) * cellWidth
                val centerY = offsetY + (row + 0.5f) * cellHeight
                val center = Offset(centerX, centerY)

                val offset = viewModel.getDeformationAt(center)

                // 🧠 OPTION 1 — Recouvrement léger
                val deformX = offset.x
                val deformY = offset.y

                val useFactor = if (offset == Offset.Zero) defaultDeformFactor else 1f
                val halfWidth = (cellWidth / 2f) * useFactor
                val halfHeight = (cellHeight / 2f) * useFactor

                dstRect.set(
                    center.x - halfWidth + deformX,
                    center.y - halfHeight + deformY,
                    center.x + halfWidth + deformX,
                    center.y + halfHeight + deformY
                )

                nativeCanvas.drawBitmap(androidBitmap, srcRect, dstRect, paint)


            }
        }
    }
}


