package com.lebaillyapp.uiwavedeformation

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.lebaillyapp.uiwavedeformation.model.WaveDeformationConfig
import com.lebaillyapp.uiwavedeformation.ui.components.DeformableBitmapGridTouch
import com.lebaillyapp.uiwavedeformation.ui.screen.WaveGridDragScreen
import com.lebaillyapp.uiwavedeformation.ui.screen.WaveGridScreen
import com.lebaillyapp.uiwavedeformation.ui.theme.UIWaveDeformationTheme
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        enableEdgeToEdge()
        setContent {
            UIWaveDeformationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    //todo phase 1 -------------DONT TOUCH

                    /**

                  //  WaveGridScreen(modifier = Modifier.fillMaxSize().padding(innerPadding) )
                    WaveGridDragScreen(
                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                        config = WaveDeformationConfig(
                            cellSize = 20f,
                            waveAmpl = 50f,
                            waveFreq = 2f,
                            waveSpeed = 1000f,

                            waveCoolDown = 150L,
                            damping = 0.2f,
                            minValEraseWave = 0.3f,

                            frameRateAnim = 16L, //framerate (16L = 60fps / 32L = 30fps / 42L = 24fps)

                            pointRadius = 3f,
                            maxAmplAllowed = 50f,
                            colorBase = Color(0xFF00E5FF),
                            colorAccent = Color(0xFF651FFF),
                            trailBaseColor = Color(0xFF351679),
                            trailStrokeWidth = 2f,
                            trailMinAlpha = 0.05f,
                            trailMaxAlpha = 0.3f
                        )
                    )

                    */

                    //todo phase 2 -------------
                    // Phase 2: Bitmap deformation
                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {

                       // FullscreenCenterCropBitmap(drawableRes = R.drawable.demogirl)

                        val bitmap = ImageBitmap.imageResource(id = R.drawable.demogirl)
                        DeformableBitmapGridTouch(bitmap = bitmap)


                    }

                }

            }
        }
    }
}

@Composable
fun FullscreenCenterCropBitmap(
    @DrawableRes drawableRes: Int,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    // Charger le bitmap Android depuis drawable
    val context = LocalContext.current
    val androidBitmap = remember(drawableRes) {
        val drawable = ContextCompat.getDrawable(context, drawableRes)
        if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else {
            // fallback bitmap vide
            Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        }
    }

    // Convertir en ImageBitmap Compose
    val imageBitmap = androidBitmap.asImageBitmap()

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        val bitmapWidth = imageBitmap.width.toFloat()
        val bitmapHeight = imageBitmap.height.toFloat()

        // Calcul centerCrop scale pour remplir l'écran sans déformation
        val scale = maxOf(canvasWidth / bitmapWidth, canvasHeight / bitmapHeight)

        // Bitmap redimensionné
        val scaledWidth = bitmapWidth * scale
        val scaledHeight = bitmapHeight * scale

        // Offset pour centrer la bitmap (crop si plus grande que canvas)
        val offsetX = (canvasWidth - scaledWidth) / 2f
        val offsetY = (canvasHeight - scaledHeight) / 2f

        drawImage(
            image = imageBitmap,
            srcSize = IntSize(imageBitmap.width, imageBitmap.height),
            dstSize = IntSize(scaledWidth.toInt(), scaledHeight.toInt()),
            dstOffset = IntOffset(offsetX.toInt(), offsetY.toInt())
        )
    }
}


