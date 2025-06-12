package com.lebaillyapp.uiwavedeformation.model


import androidx.compose.ui.geometry.Offset

/**
 * Représente un point dans la grille 2D avec sa position originale et sa position déformée actuelle.
 *
 * @property originalPosition La position fixe initiale du point dans la grille.
 * @property currentPosition La position du point après application de la déformation.
 */
data class GridPoint(
    val originalPosition: Offset,
    var currentPosition: Offset = originalPosition
)