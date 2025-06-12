package com.lebaillyapp.uiwavedeformation.model

import androidx.compose.ui.geometry.Offset

/**
 * Représente une onde sinusoïdale déclenchée sur la grille.
 *
 * @property origin Le point d'origine de l'onde dans la grille.
 * @property amplitude L'amplitude maximale de la déformation.
 * @property frequency La fréquence angulaire (rad/s) de l'onde.
 * @property speed La vitesse de propagation de l'onde.
 * @property startTime Le timestamp (en ms) auquel l'onde a été déclenchée.
 */
data class Wave(
    val origin: Offset,
    val amplitude: Float,
    val frequency: Float,
    val speed: Float,
    val startTime: Long
)