package com.lebaillyapp.uiwavedeformation.ui.components.phaseII

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import com.lebaillyapp.uiwavedeformation.viewmodel.phaseII.WaveTileViewModel

import kotlinx.coroutines.delay
import kotlin.math.hypot
import kotlin.math.max

@Composable
fun WaveDeformableBitmapGridHybrid(
    modifier: Modifier,
    bitmap: ImageBitmap,
    viewModel: WaveTileViewModel,
    baseCols: Int = 32,
    baseRows: Int = 32,
    subdivisionRadiusPx: Float = 250f,
    drawBackLayer: Boolean = true,
    defaultDeformFactor: Float = 1.05f,
) {
    val context = LocalContext.current

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

        val androidBitmap = bitmap.asAndroidBitmap()
        val paint = android.graphics.Paint().apply {
            isFilterBitmap = false  // IMPORTANT pour éviter le flou
            isAntiAlias = true
        }

        val nativeCanvas = drawContext.canvas.nativeCanvas

        if (drawBackLayer) {
            val androidBitmap = bitmap.asAndroidBitmap()
            // Floute la bitmap (fait une copie floutée, pour ne pas modifier l'original)
            val blurredBitmap = blurBitmap(context, androidBitmap.copy(Bitmap.Config.ARGB_8888, true), 20f)

            nativeCanvas.drawBitmap(
                blurredBitmap,
                null,
                android.graphics.RectF(offsetX, offsetY, offsetX + drawWidth, offsetY + drawHeight),
                paint
            )
        }


        val deformationPoints = viewModel.getWaveOrigins()

        val cellWidth = drawWidth / baseCols
        val cellHeight = drawHeight / baseRows

        val bmpCellWidth = bmpWidth.toFloat() / baseCols
        val bmpCellHeight = bmpHeight.toFloat() / baseRows

        for (row in 0 until baseRows) {
            for (col in 0 until baseCols) {
                val centerX = offsetX + (col + 0.5f) * cellWidth
                val centerY = offsetY + (row + 0.5f) * cellHeight
                val center = Offset(centerX, centerY)

                val proximity = deformationPoints.minOfOrNull { waveOrigin ->
                    distanceBetween(waveOrigin, center)
                } ?: Float.MAX_VALUE

                val subdivide = proximity < subdivisionRadiusPx
                val subdivCount = if (subdivide) 2 else 1

                val subWidth = cellWidth / subdivCount
                val subHeight = cellHeight / subdivCount

                val bmpSubWidth = bmpCellWidth / subdivCount
                val bmpSubHeight = bmpCellHeight / subdivCount

                for (subRow in 0 until subdivCount) {
                    for (subCol in 0 until subdivCount) {
                        val localCenterX = offsetX + col * cellWidth + (subCol + 0.5f) * subWidth
                        val localCenterY = offsetY + row * cellHeight + (subRow + 0.5f) * subHeight
                        val subCenter = Offset(localCenterX, localCenterY)

                        val deform = viewModel.getDeformationAt(subCenter)
                        val useFactor = if (deform == Offset.Zero) defaultDeformFactor else 1f

                        val halfWidth = (subWidth / 2f) * useFactor
                        val halfHeight = (subHeight / 2f) * useFactor

                        val dstRect = android.graphics.RectF(
                            localCenterX - halfWidth + deform.x,
                            localCenterY - halfHeight + deform.y,
                            localCenterX + halfWidth + deform.x,
                            localCenterY + halfHeight + deform.y
                        )

                        val srcLeft = (col * bmpCellWidth + subCol * bmpSubWidth).toInt().coerceIn(0, bmpWidth - 1)
                        val srcTop = (row * bmpCellHeight + subRow * bmpSubHeight).toInt().coerceIn(0, bmpHeight - 1)
                        val srcRight = (col * bmpCellWidth + (subCol + 1) * bmpSubWidth).toInt().coerceIn(0, bmpWidth)
                        val srcBottom = (row * bmpCellHeight + (subRow + 1) * bmpSubHeight).toInt().coerceIn(0, bmpHeight)

                        val srcRect = android.graphics.Rect(srcLeft, srcTop, srcRight, srcBottom)

                        nativeCanvas.drawBitmap(androidBitmap, srcRect, dstRect, paint)
                    }
                }
            }
        }










    }
}

private fun distanceBetween(a: Offset, b: Offset): Float {
    val dx = a.x - b.x
    val dy = a.y - b.y
    return hypot(dx, dy)
}


fun blurBitmap(context: Context, bitmap: Bitmap, radius: Float): Bitmap {
    val renderScript = RenderScript.create(context)
    val input = Allocation.createFromBitmap(renderScript, bitmap)
    val output = Allocation.createTyped(renderScript, input.type)
    val script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
    script.setRadius(radius)
    script.setInput(input)
    script.forEach(output)
    output.copyTo(bitmap)
    renderScript.destroy()
    return bitmap
}