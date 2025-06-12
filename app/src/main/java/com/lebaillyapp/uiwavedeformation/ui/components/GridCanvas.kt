package com.lebaillyapp.uiwavedeformation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import com.lebaillyapp.uiwavedeformation.animation.getLength
import com.lebaillyapp.uiwavedeformation.model.GridPoint

/**
 * Composable affichant une grille 2D sous forme de points déformés,
 * simulant une surface animée avec effet de vague.
 *
 * @param gridPoints Liste 2D de GridPoint contenant les positions originales et déformées.
 * @param pointRadius Rayon des points affichés.
 * @param pointColor Couleur des points.
 * @param modifier Modifier Compose standard.
 */
@Composable
fun GridCanvas(
    modifier: Modifier = Modifier,
    gridPoints: List<List<GridPoint>>,
    pointRadius: Float = 5f,
    maxAmplAllowed: Float = 50f,
    colorBase: Color = Color.White,
    colorAccent: Color = Color.Cyan,

) {
    Canvas(modifier = modifier) {
        for (row in gridPoints) {
            for (point in row) {
                val deformationMagnitude = point.currentPosition - point.originalPosition
                val amplitude = deformationMagnitude.getLength()
                // Normaliser l’amplitude pour qu’elle soit entre 0 et 1,
                val normalized = (amplitude / maxAmplAllowed).coerceIn(0f, 1f)

                // Interpolation linéaire entre colorBase et colorAccent
                val color = lerp(colorBase, colorAccent, normalized)

                drawCircle(
                    color = color,
                    radius = pointRadius,
                    center = point.currentPosition
                )
            }
        }
    }
}