package com.lebaillyapp.uiwavedeformation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.ui.geometry.Offset
import com.lebaillyapp.uiwavedeformation.animation.WaveAnimationManager
import com.lebaillyapp.uiwavedeformation.model.GridPoint
import com.lebaillyapp.uiwavedeformation.model.Wave
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.max

/**
 * ViewModel principal pour gérer l'état de la grille, les ondes en cours et la déformation.
 */
class WaveViewModel : ViewModel() {

    private val rows = 20
    private val cols = 20

    private val waveManager = WaveAnimationManager()

    // Grille originale + déformée
    private val _gridPoints = MutableStateFlow(createGrid())
    val gridPoints: StateFlow<List<List<GridPoint>>> = _gridPoints

    init {
        startAnimationLoop()
    }

    private fun createGrid(): List<List<GridPoint>> {
        return List(rows) { row ->
            List(cols) { col ->
                GridPoint(
                    originalPosition = Offset(col.toFloat(), row.toFloat()),
                    currentPosition = Offset(col.toFloat(), row.toFloat())
                )
            }
        }
    }

    fun triggerWave(origin: Offset) {
        val wave = Wave(
            origin = origin,
            amplitude = 10f,
            frequency = 2f,
            speed = 5f,
            startTime = System.currentTimeMillis()
        )
        waveManager.addWave(wave)
    }

    private fun startAnimationLoop() {
        viewModelScope.launch {
            while (true) {
                val currentTime = System.currentTimeMillis()
                val newGrid = _gridPoints.value.map { row ->
                    row.map { point ->
                        val deformationY = waveManager.calculateDeformation(point.originalPosition, currentTime)
                        // Mise à jour position déformée sur y uniquement
                        point.copy(
                            currentPosition = Offset(
                                point.originalPosition.x,
                                point.originalPosition.y + deformationY
                            )
                        )
                    }
                }
                _gridPoints.value = newGrid
                delay(16) // ~60fps
            }
        }
    }
}