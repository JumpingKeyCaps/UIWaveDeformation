package com.lebaillyapp.uiwavedeformation.animation

import androidx.compose.ui.geometry.Offset
import com.lebaillyapp.uiwavedeformation.model.Wave
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sin

/**
 * Gère la logique des ondes : propagation, calcul des déformations en fonction du temps et position.
 *
 * @param damping Facteur d'amortissement des ondes (entre 0 et 1).
 */
class WaveAnimationManager(private val damping: Float = 0.95f) {

    private val waves = mutableListOf<Wave>()

    /**
     * Ajoute une nouvelle onde à gérer.
     */
    fun addWave(wave: Wave) {
        waves.add(wave)
    }

    /**
     * Nettoie les ondes trop amorties (amplitude trop faible).
     */
    fun cleanupWaves() {
        waves.removeAll { it.amplitude < 0.01f }
    }

    /**
     * Calcule la déformation verticale en un point donné (x, y) à un temps donné (en ms),
     * en sommant les contributions de toutes les ondes.
     *
     * @param point Position du point à calculer.
     * @param currentTime Temps courant en ms.
     * @return Valeur de déformation verticale (Float).
     */
    fun calculateDeformation(point: Offset, currentTime: Long): Float {
        var deformation = 0f

        val iterator = waves.iterator()
        while (iterator.hasNext()) {
            val wave = iterator.next()
            val elapsedTime = (currentTime - wave.startTime) / 1000f // en secondes
            val distance = distanceBetween(point, wave.origin)

            val waveFront = wave.speed * elapsedTime
            val relativeDistance = distance - waveFront

            if (relativeDistance > 0) {
                // Le point est devant la front d’onde, pas encore affecté
                continue
            }

            // Calcul de la contribution de l’onde sur ce point
            val omega = wave.frequency * 2 * PI.toFloat()
            val k = omega / wave.speed // nombre d’onde

            val amplitude = wave.amplitude * damping.pow(elapsedTime)
            if (amplitude < 0.01f) {
                // Onde trop faible, on peut la supprimer
                iterator.remove()
                continue
            }

            val phase = k * distance - omega * elapsedTime
            deformation += amplitude * sin(phase)
        }

        return deformation
    }

    private fun distanceBetween(p1: Offset, p2: Offset): Float {
        return (p1 - p2).getDistance()
    }
}