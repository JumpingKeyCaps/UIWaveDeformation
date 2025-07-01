package com.lebaillyapp.uiwavedeformation.ui.shader

import android.content.Context
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
 * Fournit un ShaderBrush basé sur un shader AGSL qui applique une déformation par multiples ondes.
 *
 * @param context Contexte Android utilisé pour accéder aux ressources.
 * @param shaderResId ID de la ressource raw contenant le shader AGSL.
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class WaveDeformMultiShaderBrushProvider(
    context: Context,
    @RawRes shaderResId: Int
) {
    private val shaderCode = context.resources.openRawResource(shaderResId).bufferedReader().use { it.readText() }
    private val shader = RuntimeShader(shaderCode)

    private fun padTo16(list: List<Float>): FloatArray =
        FloatArray(16) { idx -> list.getOrElse(idx) { 0f } }

    private fun padTo16Vec2(list: List<Offset>): FloatArray =
        FloatArray(32) { idx ->
            val waveIdx = idx / 2
            if (waveIdx >= list.size) 0f else if (idx % 2 == 0) list[waveIdx].x else list[waveIdx].y
        }

    /**
     * Crée un ShaderBrush configuré avec les données des ondes.
     *
     * @param size Taille du composable cible.
     * @param originalBitmap Image source à déformer.
     * @param waveCenters Liste des centres des ondes (max 16).
     * @param amplitudes Liste des amplitudes des ondes (max 16).
     * @param frequencies Liste des fréquences des ondes (max 16).
     * @param ages Liste des âges des ondes (max 16).
     * @param dampings Liste des coefficients d’atténuation (max 16).
     *
     * @return ShaderBrush prêt à être utilisé dans une composable Compose.
     */
    fun getBrush(
        size: IntSize,
        originalBitmap: ImageBitmap,
        waveCenters: List<Offset>,
        amplitudes: List<Float>,
        frequencies: List<Float>,
        ages: List<Float>,
        dampings: List<Float>
    ): ShaderBrush {
        val waveCount = waveCenters.size.coerceAtMost(16)

        val centersArray = padTo16Vec2(waveCenters.take(waveCount))
        val amplitudesArray = padTo16(amplitudes.take(waveCount))
        val frequenciesArray = padTo16(frequencies.take(waveCount))
        val agesArray = padTo16(ages.take(waveCount))
        val dampingsArray = padTo16(dampings.take(waveCount))

        return object : ShaderBrush() {
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun createShader(size: Size): AndroidShader {
                val bitmapShader = originalBitmap.asAndroidBitmap().let { bmp ->
                    BitmapShader(bmp, AndroidShader.TileMode.CLAMP, AndroidShader.TileMode.CLAMP)
                }
                shader.setInputShader("uTexture", bitmapShader)
                shader.setFloatUniform("uResolution", floatArrayOf(size.width, size.height))
                shader.setFloatUniform("uImageSize", floatArrayOf(originalBitmap.width.toFloat(), originalBitmap.height.toFloat()))

                shader.setIntUniform("uWaveCount", waveCount)

                shader.setFloatUniform("uWaveCenters", centersArray)
                shader.setFloatUniform("uAmplitudes", amplitudesArray)
                shader.setFloatUniform("uFrequencies", frequenciesArray)
                shader.setFloatUniform("uAges", agesArray)
                shader.setFloatUniform("uDampings", dampingsArray)

                return shader
            }
        }
    }
}