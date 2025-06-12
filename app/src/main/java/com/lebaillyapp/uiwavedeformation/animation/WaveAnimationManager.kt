package com.lebaillyapp.uiwavedeformation.animation

import androidx.compose.ui.geometry.Offset
import com.lebaillyapp.uiwavedeformation.model.Wave
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Gère la logique des ondes type "ripple" amorties dans le temps.
 */
class WaveAnimationManager(
    private val damping: Float = 0.3f,
    private val minValueToRemoveWave: Float = 0.3f
    ) {
    private val waves = mutableListOf<Wave>()

    fun addWave(wave: Wave) = waves.add(wave)

    fun hasActiveWaves() = waves.isNotEmpty()

    fun cleanupWaves() {
        val now = System.currentTimeMillis()
        waves.removeAll { wave ->
            val elapsed = (now - wave.startTime) / 1000f
            wave.amplitude * damping.pow(elapsed) < minValueToRemoveWave
        }
    }

    fun calculateDeformation(point: Offset, currentTime: Long): Offset {
        var totalOffset = Offset.Zero

        val iterator = waves.iterator()
        while (iterator.hasNext()) {
            val wave = iterator.next()
            val elapsed = (currentTime - wave.startTime) / 1000f
            val distance = (point - wave.origin).getLength()
            val waveFront = wave.speed * elapsed
            val relDist = distance - waveFront

            if (relDist > 0f) continue // pas encore atteint

            val omega = wave.frequency * 2 * PI.toFloat()
            val k = omega / wave.speed
            val amplitude = wave.amplitude * damping.pow(elapsed)
            if (amplitude < 0.5f) {
                iterator.remove()
                continue
            }
            val phase = k * distance - omega * elapsed
            val waveEffect = sin(phase) * amplitude

            val direction = if (distance > 0f) (point - wave.origin) / distance else Offset.Zero
            totalOffset += direction * waveEffect
        }
        return totalOffset
    }


    fun getWavesSnapshot(currentTime: Long): List<Wave> {
        return waves.map { it.copy() } // safe copy
    }

}

fun Offset.getLength(): Float = sqrt(x * x + y * y)

