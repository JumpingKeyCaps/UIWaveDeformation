package com.lebaillyapp.uiwavedeformation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.ui.geometry.Offset
import com.lebaillyapp.uiwavedeformation.animation.WaveAnimationManager
import com.lebaillyapp.uiwavedeformation.model.GridPoint
import com.lebaillyapp.uiwavedeformation.model.Wave
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class WaveDragViewModel : ViewModel() {
    private var waveManager = WaveAnimationManager(
        damping = 0.3f,
        minValueToRemoveWave = 0.3f
    )

    // pointerId -> last wave time (ms)
    private val lastWaveTimes = mutableMapOf<Int, Long>()
    private var waveCooldown = 100L // ms, ajustable

    //wave config
    private var wAmplitude = 50f
    private var wFrequency = 2f
    private var wSpeed = 1000f


    //framerate (16L = 60fps / 32L = 30fps / 42L = 24fps)
    private var frameRateValue = 16L


    private val _gridPoints = MutableStateFlow<List<List<GridPoint>>>(emptyList())
    val gridPoints: StateFlow<List<List<GridPoint>>> = _gridPoints

    private var animationJob: Job? = null



    fun initGrid(
        width: Float,
        height: Float,
        cellSize: Float = 40f,
        waveAmpl: Float = 50f,
        waveFreq: Float = 2f,
        waveSpeed: Float = 1000f,
        waveCoolDown: Long = 100L,
        damping: Float = 0.3f,
        minValEraseWave: Float = 0.3f,
        frameRateAnim: Long = 32L
    ) {
        //setup manager
        waveManager = WaveAnimationManager(
            damping = damping,
            minValueToRemoveWave = minValEraseWave
        )
        //setup wave config
        wAmplitude = waveAmpl
        wFrequency = waveFreq
        wSpeed = waveSpeed
        waveCooldown = waveCoolDown
        //setup framerate anim
        frameRateValue = frameRateAnim

        //setup grid
        val cols = (width / cellSize).toInt() + 1
        val rows = (height / cellSize).toInt() + 1
        val newGrid = List(rows) { row ->
            List(cols) { col ->
                GridPoint(originalPosition = Offset(col * cellSize, row * cellSize))
            }
        }
        _gridPoints.value = newGrid
    }

    fun launchWaveAt(x: Float, y: Float, pointerId: Int = -1) {
        val now = System.currentTimeMillis()

        if (pointerId != -1) {
            val lastTime = lastWaveTimes[pointerId] ?: 0L
            if (now - lastTime < waveCooldown) return
            lastWaveTimes[pointerId] = now
        }

        val wave = Wave(
            origin = Offset(x, y),
            startTime = now,
            amplitude = wAmplitude,
            frequency = wFrequency,
            speed = wSpeed
        )
        waveManager.addWave(wave)

        if (animationJob == null || animationJob?.isCompleted == true) {
            startAnimationLoop()
        }
    }

    private val _activeWaves = MutableStateFlow<List<Wave>>(emptyList())
    val activeWaves: StateFlow<List<Wave>> = _activeWaves
    var showWaveTrails = true // ou param dans initGrid

    private fun startAnimationLoop() {
        animationJob = viewModelScope.launch {
            while (waveManager.hasActiveWaves()) {
                val now = System.currentTimeMillis()
                val updatedGrid = _gridPoints.value.map { row ->
                    row.map { point ->
                        val deformation = waveManager.calculateDeformation(point.originalPosition, now)
                        point.copy(currentPosition = point.originalPosition + deformation)
                    }
                }
                _gridPoints.value = updatedGrid

                if (showWaveTrails) {
                    _activeWaves.value = waveManager.getWavesSnapshot(now)
                }

                waveManager.cleanupWaves()
                delay(frameRateValue)
            }
            _activeWaves.value = emptyList()
        }
    }
}