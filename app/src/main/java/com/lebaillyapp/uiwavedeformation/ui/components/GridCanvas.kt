package com.lebaillyapp.uiwavedeformation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

/**
 * Composable affichant une grille 2D simple sous forme de points et lignes.
 *
 * @param rows Nombre de lignes de la grille.
 * @param cols Nombre de colonnes de la grille.
 * @param pointRadius Rayon des points affichés.
 * @param lineColor Couleur des lignes de la grille.
 * @param pointColor Couleur des points.
 */
@Composable
fun GridCanvas(
    modifier: Modifier = Modifier.fillMaxSize(),
    rows: Int,
    cols: Int,
    pointRadius: Float = 6f,
    lineColor: Color = Color.Gray,
    pointColor: Color = Color.Blue
) {
    Canvas(modifier = modifier) {
        val widthStep = size.width / (cols - 1)
        val heightStep = size.height / (rows - 1)

        // Dessin des lignes verticales
        for (col in 0 until cols) {
            val x = col * widthStep
            drawLine(
                color = lineColor,
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = 1f
            )
        }

        // Dessin des lignes horizontales
        for (row in 0 until rows) {
            val y = row * heightStep
            drawLine(
                color = lineColor,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1f
            )
        }

        // Dessin des points
        for (row in 0 until rows) {
            val y = row * heightStep
            for (col in 0 until cols) {
                val x = col * widthStep
                drawCircle(
                    color = pointColor,
                    radius = pointRadius,
                    center = Offset(x, y)
                )
            }
        }
    }
}