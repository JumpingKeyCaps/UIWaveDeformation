package com.lebaillyapp.uiwavedeformation.viewmodel.phaseII

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerId
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lebaillyapp.uiwavedeformation.model.WaveP2b
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.pow

class WaveDeformViewModel : ViewModel() {

    private val _waves = MutableStateFlow<List<WaveP2b>>(emptyList())
    val waves: StateFlow<List<WaveP2b>> = _waves.asStateFlow()

    private val frameDelayMillis = 32L // ~60 FPS

    // Mutex pour accès thread-safe à lastWaveTimestamps
    private val lastWaveMutex = Mutex()
    private val lastWaveTimestamps = mutableMapOf<PointerId, Long>()

    // Pour émettre le temps système régulièrement
    private val _now = MutableStateFlow(System.currentTimeMillis())

    init {
        // Mise à jour périodique du temps système
        viewModelScope.launch {
            while (isActive) {
                _now.value = System.currentTimeMillis()
                pruneOldWaves()
                delay(frameDelayMillis)
            }
        }
    }

    /**
     * Ajoute une nouvelle wave au centre donné en limitant le nombre total de waves.
     * Nettoie aussi les waves obsolètes avant l'ajout.
     */
    private fun addWave(center: Offset) {
        val now = System.currentTimeMillis()
        _waves.update { current ->
            current.filter { now - it.timestamp <= WAVE_LIFETIME_MS } +
                    WaveP2b(center = center, timestamp = now)
        }
        // Limite le nombre de waves après ajout
        _waves.update { it.takeLast(MAX_WAVES) }
    }

    /**
     * Nettoie les waves trop vieilles (> WAVE_LIFETIME_MS)
     */
    private fun pruneOldWaves() {
        val now = System.currentTimeMillis()
        _waves.update { list ->
            list.filter { now - it.timestamp <= WAVE_LIFETIME_MS }
        }
    }

    /**
     * Donne le temps écoulé en secondes depuis le premier lancement
     * Calculé à partir de _now.
     */
    fun getTimeSeconds(): Float {
        // Optionnel: peut être remplacé par un compteur cumulatif si besoin
        return (_now.value / 1000f)
    }

    // --- Gestion des drags ---

    /**
     * Commence un drag : ajoute une wave immédiatement.
     */
    fun onDragStart(position: Offset, pointerId: PointerId) {

        viewModelScope.launch {
            addWave(position)
            lastWaveMutex.withLock {
                lastWaveTimestamps[pointerId] = System.currentTimeMillis()
            }
        }
    }

    /**
     * Pendant le drag, ajoute une wave si l'intervalle minimal est respecté.
     */
    fun onDrag(position: Offset, pointerId: PointerId) {
        Log.d("WaveDeformViewModel", "onDragStart: $pointerId  at $position")
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            var shouldAdd = false
            lastWaveMutex.withLock {
                val lastTime = lastWaveTimestamps[pointerId] ?: 0L
                if (now - lastTime >= TRAIL_INTERVAL_MS) {
                    shouldAdd = true
                    lastWaveTimestamps[pointerId] = now
                }
            }
            if (shouldAdd) {
                addWave(position)
            }
        }
    }

    /**
     * Fin du drag, nettoie les timestamps pour ce pointer.
     */
    fun onDragEnd(pointerId: PointerId) {
        viewModelScope.launch {
            lastWaveMutex.withLock {
                lastWaveTimestamps.remove(pointerId)
            }
        }
    }

    // --- Paramètres pour le shader, avec fade dynamique ---

    private data class WaveParams(
        val center: Offset,
        val amplitude: Float,
        val frequency: Float,
        val speed: Float,
        val damping: Float,
    )

    /**
     * Combine waves et temps système pour calculer les paramètres dynamiques des waves
     * (amplitude avec fade, autres paramètres statiques).
     */
    private val waveParams: StateFlow<List<WaveParams>> = combine(_waves, _now) { waves, now ->
        waves.map { wave ->
            val age = (now - wave.timestamp).coerceAtLeast(0L).toFloat()
            val norm = (1f - (age / WAVE_LIFETIME_MS)).coerceIn(0f, 1f)
            WaveParams(
                center = wave.center,
                amplitude = wave.amplitude * norm.pow(FADE_CURVE),
                frequency = wave.frequency,
                speed = wave.speed,
                damping = wave.damping
            )
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val waveCenters: StateFlow<List<Offset>> =
        waveParams.map { it.map { wp -> wp.center } }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val amplitudes: StateFlow<List<Float>> =
        waveParams.map { it.map { wp -> wp.amplitude } }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val frequencies: StateFlow<List<Float>> =
        waveParams.map { it.map { wp -> wp.frequency } }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val speeds: StateFlow<List<Float>> =
        waveParams.map { it.map { wp -> wp.speed } }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val dampings: StateFlow<List<Float>> =
        waveParams.map { it.map { wp -> wp.damping } }
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    companion object {
        private const val MAX_WAVES = 8
        private const val WAVE_LIFETIME_MS = 3500L
        private const val FADE_CURVE = 2f // >1 = fade doux, <1 = agressif
        private const val TRAIL_INTERVAL_MS = 100L // Intervalle minimal entre ondes pour un même pointer
    }
}