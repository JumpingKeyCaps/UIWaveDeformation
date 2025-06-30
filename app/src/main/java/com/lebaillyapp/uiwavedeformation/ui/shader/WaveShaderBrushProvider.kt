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
 * Provides a ShaderBrush based on a minimal AGSL RuntimeShader,
 * with time + center uniforms. Falls back gracefully below API 33.
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
     * @param size Canvas size in pixels
     * @param timeSeconds Time for animation
     * @param centerX X center [0f, width]
     * @param centerY Y center [0f, height]
     */
    fun getBrush(
        size: IntSize,
        timeSeconds: Float,
        centerX: Float,
        centerY: Float
    ): Brush {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || runtimeShader == null) {
            return SolidColor(androidx.compose.ui.graphics.Color.Transparent)
        }

        val shader = runtimeShader
        return object : ShaderBrush() {
            override fun createShader(size: Size): android.graphics.Shader {
                shader.setFloatUniform("resolution", size.width, size.height)
                shader.setFloatUniform("center", centerX, centerY)
                shader.setFloatUniform("time", timeSeconds)
                return shader
            }
        }
    }
}