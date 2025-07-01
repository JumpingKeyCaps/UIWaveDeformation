package com.lebaillyapp.uiwavedeformation.model

import androidx.compose.ui.geometry.Offset

/**
 * Paramètres d'une onde pour la déformation de l'image.
 *
 * @property center Centre de l'onde dans les coordonnées de la composable.
 * @property amplitude Amplitude initiale de l'onde.
 * @property frequency Fréquence de l'onde.
 * @property damping Coefficient d'amortissement.
 * @property startTime Temps de départ de l'onde en millisecondes.
 * @property speed Vitesse de propagation de l'onde.
 */
data class WaveParams(
    val center: Offset,
    val amplitude: Float,
    val frequency: Float,
    val damping: Float,
    val startTime: Long,
    val speed: Float
)