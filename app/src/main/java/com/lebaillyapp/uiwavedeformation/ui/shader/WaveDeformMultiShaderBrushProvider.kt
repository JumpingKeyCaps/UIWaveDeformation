package com.lebaillyapp.uiwavedeformation.ui.shader


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.RuntimeShader
import android.graphics.Shader as AndroidShader
import android.os.Build
import androidx.annotation.RawRes
import androidx.annotation.RequiresApi
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.unit.IntSize

/**
 * Extension pour créer un Android BitmapShader à partir d'un Android Bitmap.
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun Bitmap.asImageShader(): AndroidShader {
    return BitmapShader(this, AndroidShader.TileMode.CLAMP, AndroidShader.TileMode.CLAMP)
}

/**
 * ShaderBrush dynamique appliquant une déformation continue d'un bitmap via plusieurs ondes circulaires.
 */
class WaveDeformMultiShaderBrushProvider(
    context: Context,
    @RawRes shaderResId: Int
) {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val shader = RuntimeShader(
        context.resources.openRawResource(shaderResId).bufferedReader().use { it.readText() }
    )

    private fun padTo8(list: List<Float>): FloatArray =
        FloatArray(8) { index -> list.getOrElse(index) { 0f } }

    /**
     * Retourne un ShaderBrush configuré avec les paramètres d'ondes.
     */
    fun getBrush(
        size: IntSize,
        timeSeconds: Float,
        originalBitmap: ImageBitmap,
        waveCenters: List<Offset>,
        amplitudes: List<Float>,
        frequencies: List<Float>,
        speeds: List<Float>,
        dampings: List<Float>
    ): ShaderBrush {
        val waveCount = waveCenters.size.coerceAtMost(8)

        // Préparer les centres des ondes (tableau de 16 éléments pour 8 vec2)
        val centersArray = FloatArray(16) { 0f }
        for (i in 0 until waveCount) {
            centersArray[2 * i] = waveCenters[i].x
            centersArray[2 * i + 1] = waveCenters[i].y
        }

        return object : ShaderBrush() {
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun createShader(size: Size): AndroidShader {
                val bitmapShader = originalBitmap.asAndroidBitmap().asImageShader()
                shader.setInputShader("uTexture", bitmapShader)

                // Résolution de l'écran
                shader.setFloatUniform("uResolution", floatArrayOf(size.width, size.height))

                // NOUVEAU: Taille de l'image originale
                shader.setFloatUniform("uImageSize", floatArrayOf(
                    originalBitmap.width.toFloat(),
                    originalBitmap.height.toFloat()
                ))

                shader.setFloatUniform("uTime", timeSeconds)
                shader.setIntUniform("uWaveCount", waveCount)

                shader.setFloatUniform("uWaveCenters", centersArray)
                shader.setFloatUniform("uAmplitudes", padTo8(amplitudes))
                shader.setFloatUniform("uFrequencies", padTo8(frequencies))
                shader.setFloatUniform("uSpeeds", padTo8(speeds))
                shader.setFloatUniform("uDampings", padTo8(dampings))

                return shader
            }
        }
    }
}