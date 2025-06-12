package com.lebaillyapp.uiwavedeformation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.lebaillyapp.uiwavedeformation.model.GridPoint

/**
 * Composable affichant une grille 2D avec déformation,
 * en utilisant les positions déformées de chaque GridPoint.
 *
 * @param gridPoints Liste 2D de GridPoint contenant les positions originales et déformées.
 * @param pointRadius Rayon des points affichés.
 * @param lineColor Couleur des lignes de la grille.
 * @param pointColor Couleur des points.
 * @param modifier Modifier Compose standard.
 */
@Composable
fun GridCanvas(
    gridPoints: List<List<GridPoint>>,
    pointRadius: Float = 6f,
    lineColor: Color = Color.Gray,
    pointColor: Color = Color.Blue,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    Canvas(modifier = modifier) {
        val rows = gridPoints.size
        val cols = if (rows > 0) gridPoints[0].size else 0

        if (rows == 0 || cols == 0) return@Canvas

        // Dessin des lignes verticales entre points déformés
        for (col in 0 until cols) {
            for (row in 0 until rows - 1) {
                val start = gridPoints[row][col].currentPosition
                val end = gridPoints[row + 1][col].currentPosition
                drawLine(
                    color = lineColor,
                    start = Offset(start.x * size.width / (cols - 1), start.y * size.height / (rows - 1)),
                    end = Offset(end.x * size.width / (cols - 1), end.y * size.height / (rows - 1)),
                    strokeWidth = 1f
                )
            }
        }

        // Dessin des lignes horizontales entre points déformés
        for (row in 0 until rows) {
            for (col in 0 until cols - 1) {
                val start = gridPoints[row][col].currentPosition
                val end = gridPoints[row][col + 1].currentPosition
                drawLine(
                    color = lineColor,
                    start = Offset(start.x * size.width / (cols - 1), start.y * size.height / (rows - 1)),
                    end = Offset(end.x * size.width / (cols - 1), end.y * size.height / (rows - 1)),
                    strokeWidth = 1f
                )
            }
        }

        // Dessin des points déformés
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val pos = gridPoints[row][col].currentPosition
                drawCircle(
                    color = pointColor,
                    radius = pointRadius,
                    center = Offset(pos.x * size.width / (cols - 1), pos.y * size.height / (rows - 1))
                )
            }
        }
    }
}