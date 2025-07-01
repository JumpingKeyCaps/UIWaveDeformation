package com.lebaillyapp.uiwavedeformation.model

import androidx.compose.ui.geometry.Offset

data class WaveP2b(
    val center: Offset,
    val amplitude: Float = 20f,
    val frequency: Float = 0.02f,
    val speed: Float = 6f,
    val damping: Float = 1.5f,
    val timestamp: Long = System.currentTimeMillis()

)