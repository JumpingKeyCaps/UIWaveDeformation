package com.lebaillyapp.uiwavedeformation.viewmodel.phaseII

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import com.lebaillyapp.uiwavedeformation.animation.WaveAnimationManager
import com.lebaillyapp.uiwavedeformation.model.Wave

/**
 * ViewModel pour gérer l'interaction tactile avec le système d'ondes de déformation.
 */
class WaveTileViewModel(
    private val waveAnimationManager: WaveAnimationManager = WaveAnimationManager()
) : ViewModel() {

    private val _currentTime = mutableStateOf(System.currentTimeMillis())
    val currentTime: State<Long> = _currentTime

    private val lastEmissionMap = mutableMapOf<Int, Long>()
    private val emissionCooldownMs = 100L // 100ms entre deux ondes par doigt
    private val amplitudeWaver = 80f

    fun updateTime() {
        _currentTime.value = System.currentTimeMillis()
        waveAnimationManager.cleanupWaves()
    }

    /**
     * Ajoute une onde si le cooldown est respecté pour ce pointerId.
     */
    fun onTouch(position: Offset, pointerId: Int) {
        val now = System.currentTimeMillis()
        val lastEmission = lastEmissionMap[pointerId] ?: 0L
        if (now - lastEmission >= emissionCooldownMs) {
            waveAnimationManager.addWave(
                Wave(
                    origin = position,
                    startTime = now,
                    amplitude = amplitudeWaver,
                    frequency = 2f,
                    speed = 750f
                )
            )
            lastEmissionMap[pointerId] = now
        }
    }

    fun getDeformationAt(point: Offset): Offset = waveAnimationManager.calculateDeformation(point, _currentTime.value)

    fun getWaveOrigins(): List<Offset> = waveAnimationManager.getActiveWaves().map { it.origin }


}