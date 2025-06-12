package com.lebaillyapp.uiwavedeformation.ui.screen

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lebaillyapp.uiwavedeformation.ui.components.GridCanvas
import com.lebaillyapp.uiwavedeformation.viewmodel.WaveViewModel


/**
 * Écran principal affichant la grille déformée avec interaction tactile
 *
 * @param waveViewModel ViewModel contenant la grille et la logique d'onde.
 */
@Composable
fun WaveGridScreen(
    waveViewModel: WaveViewModel = viewModel()
) {
    // Observe la grille depuis le ViewModel
    val gridPoints = waveViewModel.gridPoints.collectAsState(initial = emptyList())

    Box(
        modifier = Modifier
            .fillMaxSize()
            // Capture le tap pour créer une onde à la position touchée
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    // Calculer la position relative normalisée entre 0f et 1f
                    val xNorm = offset.x / size.width
                    val yNorm = offset.y / size.height

                    waveViewModel.launchWaveAt(xNorm, yNorm)
                }
            }
    ) {
        // Affiche la grille déformée
        GridCanvas(
            gridPoints = gridPoints.value,
            pointRadius = 5f,
            lineColor = androidx.compose.ui.graphics.Color.LightGray,
            pointColor = androidx.compose.ui.graphics.Color.Cyan
        )
    }
}