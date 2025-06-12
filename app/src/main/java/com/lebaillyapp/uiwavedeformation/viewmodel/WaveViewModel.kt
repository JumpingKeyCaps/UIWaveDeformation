package com.lebaillyapp.uiwavedeformation.viewmodel

import android.util.Log
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
import kotlin.math.max

/**
 * ViewModel principal pour gérer l'état de la grille, les ondes en cours et la déformation.
 */
class WaveViewModel : ViewModel() {
    private val waveManager = WaveAnimationManager()

    private val _gridPoints = MutableStateFlow<List<List<GridPoint>>>(emptyList())
    val gridPoints: StateFlow<List<List<GridPoint>>> = _gridPoints

    private var animationJob: Job? = null

    fun initGrid(width: Float, height: Float, cellSize: Float = 40f) {
        Log.d("WaveViewModel2", "initGrid called with size=($width, $height)")
        val cols = (width / cellSize).toInt() + 1
        val rows = (height / cellSize).toInt() + 1

        val newGrid = List(rows) { row ->
            List(cols) { col ->
                val pos = Offset(col * cellSize, row * cellSize)
                GridPoint(
                    originalPosition = pos,
                    currentPosition = pos // <- initialisation importante !
                )
            }
        }
        _gridPoints.value = newGrid
    }

    fun launchWaveAt(x: Float, y: Float) {
        val now = System.currentTimeMillis()
        val wave = Wave(
            origin = Offset(x, y),
            startTime = now,
            amplitude = 50f,
            frequency = 2f,
            speed = 1000f
        )
        waveManager.addWave(wave)
        if (animationJob == null || animationJob?.isCompleted == true) {
            startAnimationLoop()
        }
    }

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
                waveManager.cleanupWaves()
                delay(16L)
                Log.d("WaveViewModel", "Animation frame updated")
            }
        }
    }
}