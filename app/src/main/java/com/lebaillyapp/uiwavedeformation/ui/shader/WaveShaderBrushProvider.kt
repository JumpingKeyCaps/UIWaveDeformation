package com.lebaillyapp.uiwavedeformation.ui.shader

import android.content.Context
import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RawRes
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntSize


/**
 * Fournit un ShaderBrush minimal basé sur un RuntimeShader AGSL,
 * avec uniforms temps + centre + amplitude + fréquence.
 * Fallback transparent avant API 33.
 */
class WaveShaderBrushProvider(
    context: Context,
    @RawRes shaderResId: Int
) {
    private val shaderCode = context.resources.openRawResource(shaderResId)
        .bufferedReader().use { it.readText() }

    private val runtimeShader: RuntimeShader? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        RuntimeShader(shaderCode)
    } else null

    /**
     * @param size taille du canvas en pixels
     * @param timeSeconds temps en secondes pour animation
     * @param centerX position X du centre de l'onde [0f, width]
     * @param centerY position Y du centre de l'onde [0f, height]
     * @param amplitude amplitude de l'onde
     * @param frequency fréquence spatiale de l'onde
     */
    fun getBrush(
        size: IntSize,
        timeSeconds: Float,
        centerX: Float,
        centerY: Float,
        amplitude: Float,
        frequency: Float
    ): Brush {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || runtimeShader == null) {
            return SolidColor(androidx.compose.ui.graphics.Color.Transparent)
        }

        val shader = runtimeShader
        return object : ShaderBrush() {
            override fun createShader(size: Size): android.graphics.Shader {
                shader.setFloatUniform("uResolution", size.width, size.height)
                shader.setFloatUniform("uWaveCenter", centerX, centerY)
                shader.setFloatUniform("uTime", timeSeconds)
                shader.setFloatUniform("uAmplitude", amplitude)
                shader.setFloatUniform("uFrequency", frequency)
                return shader
            }
        }
    }
}