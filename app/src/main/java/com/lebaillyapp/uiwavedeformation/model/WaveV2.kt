package com.lebaillyapp.uiwavedeformation.model

import androidx.compose.ui.geometry.Offset

data class WaveV2(
    val origin: Offset,
    val startTime: Long,
    val amplitude: Float,
    val frequency: Float,
    val speed: Float,
    val damping: Float
)