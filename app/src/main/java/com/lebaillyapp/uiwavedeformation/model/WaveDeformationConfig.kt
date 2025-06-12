package com.lebaillyapp.uiwavedeformation.model

import androidx.compose.ui.graphics.Color

data class WaveDeformationConfig(
    val cellSize: Float = 40f,
    val waveAmpl: Float = 50f,
    val waveFreq: Float = 2f,
    val waveSpeed: Float = 1000f,
    val waveCoolDown: Long = 100L,
    val damping: Float = 0.3f,
    val minValEraseWave: Float = 0.3f,
    val frameRateAnim: Long = 32L,

    val pointRadius: Float = 5f,
    val maxAmplAllowed: Float = 50f,
    val colorBase: Color = Color(0xFF3D5AFE),
    val colorAccent: Color = Color(0xFF00B0FF),

    val trailBaseColor: Color = Color(0xFF3D5AFE),
    val trailStrokeWidth: Float = 2f,
    val trailMinAlpha: Float = 0.05f,
    val trailMaxAlpha: Float = 0.3f
)