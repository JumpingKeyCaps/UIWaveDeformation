package com.lebaillyapp.uiwavedeformation.viewmodel.phaseII.modeB

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import com.lebaillyapp.uiwavedeformation.animation.WaveAnimationManagerV2

/**
 * ViewModel pour gérer l'interaction tactile avec les ondes de déformation,
 * pilotées via WaveAnimationManagerV2, pour un shader AGSL en temps réel.
 *
 * Gère l'émission d'ondes par toucher, avec cooldown par pointerId,
 * met à jour le temps courant et nettoie les ondes amorties.
 */
class WaveTrailViewModel(
    private val waveAnimationManager: WaveAnimationManagerV2 = WaveAnimationManagerV2()
) : ViewModel() {

    private val _currentTime = mutableStateOf(System.currentTimeMillis())
    val currentTime: State<Long> = _currentTime

    private val lastEmissionMap = mutableMapOf<Int, Long>()
    private val emissionCooldownMs = 100L
    private val defaultAmplitude = 2500f

    /**
     * Met à jour le temps actuel et nettoie les ondes amorties.
     * À appeler périodiquement (ex: dans un LaunchedEffect ou timer).
     */
    fun updateTime() {
        _currentTime.value = System.currentTimeMillis()
        waveAnimationManager.cleanupWaves()
    }

    /**
     * Ajoute une onde si le cooldown pour ce pointerId est écoulé.
     *
     * @param position Position du toucher (Offset).
     * @param pointerId ID unique du pointer tactile.
     */
    fun onTouch(position: Offset, pointerId: Int) {
        val now = System.currentTimeMillis()
        val lastEmission = lastEmissionMap[pointerId] ?: 0L
        if (now - lastEmission >= emissionCooldownMs) {
            waveAnimationManager.addWave(
                origin = position,
                amplitude = defaultAmplitude,
                frequency = 2f,
                speed = 1000f
            )
            lastEmissionMap[pointerId] = now
        }
    }

    /**
     * Retourne la liste des ondes actives formatées pour le shader.
     */
    fun getActiveWavesForShader(): List<WaveAnimationManagerV2.WaveForShader> =
        waveAnimationManager.getActiveWavesForShader()

    /**
     * Retourne le temps courant utilisé pour le calcul dans le shader.
     */
    fun getCurrentTime(): Long = _currentTime.value
}