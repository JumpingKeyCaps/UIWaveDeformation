package com.lebaillyapp.uiwavedeformation.ui.shader

import android.content.Context
import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RawRes
import androidx.annotation.RequiresApi
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Shader

/**
 * ShaderBrush qui applique un RuntimeShader de déformation bitmap via plusieurs ondes paramétrées.
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class WaveDeformMultiShaderBrushProvider(
    context: Context,
    @RawRes shaderResId: Int
) {
    private val shader = RuntimeShader(
        context.resources.openRawResource(shaderResId).bufferedReader().use { it.readText() }
    )

    private fun padTo8(input: List<Float>): FloatArray {
        val padded = FloatArray(8) { 0f }
        input.take(8).forEachIndexed { i, v -> padded[i] = v }
        return padded
    }

    fun getBrush(
        size: IntSize,
        timeSeconds: Float,
        waveCenters: List<Offset>,
        amplitudes: List<Float>,
        frequencies: List<Float>,
        speeds: List<Float>,
        dampings: List<Float>
    ): ShaderBrush {
        val waveCount = waveCenters.size.coerceAtMost(8)

        val centersArray = FloatArray(8 * 2) { 0f }  // Toujours 8 vec2 (16 floats)
        for (i in 0 until waveCount) {
            centersArray[2 * i] = waveCenters[i].x
            centersArray[2 * i + 1] = waveCenters[i].y
        }

        return object : ShaderBrush() {
            override fun createShader(size: Size): Shader {
                shader.setFloatUniform("uResolution", floatArrayOf(size.width, size.height))
                shader.setFloatUniform("uTime", floatArrayOf(timeSeconds))
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