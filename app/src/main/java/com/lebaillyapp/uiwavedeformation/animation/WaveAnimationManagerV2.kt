package com.lebaillyapp.uiwavedeformation.animation

import androidx.compose.ui.geometry.Offset
import com.lebaillyapp.uiwavedeformation.model.WaveV2
import kotlin.collections.forEach
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Gère une liste d'ondes amorties et prépare les données
 * formatées pour un shader déformant.
 */
class WaveAnimationManagerV2(
    private val damping: Float = 0.3f,
    private val minAmplitudeThreshold: Float = 0.01f,
    private val maxWaves: Int = 16
) {

    private val waves = mutableListOf<WaveV2>()



    /**
     * Ajoute une nouvelle onde avec ses paramètres initiaux.
     */
    fun addWave(
        origin: Offset,
        amplitude: Float = 150f,
        frequency: Float = 0.009f,
        speed: Float = 200f
    ) {
        if (waves.size >= maxWaves) {
            // Supprime la plus ancienne onde
            waves.removeAt(0)
        }
        waves.add(
            WaveV2(
                origin = origin,
                startTime = System.currentTimeMillis(),
                amplitude = amplitude,
                frequency = frequency,
                speed = speed,
                damping = damping
            )
        )
    }

    /**
     * Nettoie les ondes dont l'amplitude amortie est trop faible.
     */
    fun cleanupWaves() {
        val now = System.currentTimeMillis()
        waves.removeAll { wave ->
            val elapsed = (now - wave.startTime) / 1000f
            val currentAmp = wave.amplitude * wave.damping.pow(elapsed)
            currentAmp < minAmplitudeThreshold
        }
    }

    /**
     * Renvoie la liste des ondes actives avec leur amplitude amortie et âge.
     */
    data class WaveForShader(
        val center: Offset,
        val amplitude: Float,
        val frequency: Float,
        val age: Float,
        val damping: Float
    )

    fun getActiveWavesForShader(): List<WaveForShader> {
        val now = System.currentTimeMillis()
        cleanupWaves()
        return waves.map { wave ->
            val elapsed = (now - wave.startTime) / 1000f
            val amp = wave.amplitude * wave.damping.pow(elapsed)
            WaveForShader(
                center = wave.origin,
                amplitude = amp,
                frequency = wave.frequency,
                age = elapsed,
                damping = wave.damping
            )
        }
    }

    /**
     * Calcule la déformation en un point donné à l'instant currentTime.
     * (utile pour debug ou usage CPU)
     */
    fun calculateDeformation(point: Offset, currentTime: Long = System.currentTimeMillis()): Offset {
        var totalOffset = Offset.Zero
        val wavesCopy = waves.toList()

        wavesCopy.forEach { wave ->
            val elapsed = (currentTime - wave.startTime) / 1000f
            val distance = (point - wave.origin).getLength()
            val waveFront = wave.speed * elapsed
            val relDist = distance - waveFront

            if (relDist > 0f) return@forEach // onde pas encore arrivée ici

            val omega = wave.frequency * 2 * PI.toFloat()
            val k = omega / wave.speed
            val amplitude = wave.amplitude * wave.damping.pow(elapsed)
            if (amplitude < minAmplitudeThreshold) return@forEach
            val phase = k * distance - omega * elapsed
            val waveEffect = kotlin.math.sin(phase) * amplitude

            val direction = if (distance > 0f) (point - wave.origin) / distance else Offset.Zero
            totalOffset += direction * waveEffect
        }
        return totalOffset
    }
}

private fun Offset.getLengthV2(): Float = sqrt(x * x + y * y)