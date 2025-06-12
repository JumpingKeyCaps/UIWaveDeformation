package com.lebaillyapp.uiwavedeformation.ui.screen

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lebaillyapp.uiwavedeformation.ui.components.GridCanvas
import com.lebaillyapp.uiwavedeformation.viewmodel.WaveViewModel

/**
 * Écran principal affichant la grille déformée avec interaction tactile.
 *
 * @param waveViewModel ViewModel contenant la grille et la logique d'onde.
 * @param modifier Modifier Compose standard.
 */
@Composable
fun WaveGridScreen(
    modifier: Modifier = Modifier,
    waveViewModel: WaveViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    val gridPoints by waveViewModel.gridPoints.collectAsState()
    var lastWidth by remember { mutableStateOf(0f) }
    var lastHeight by remember { mutableStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                val widthF = size.width.toFloat()
                val heightF = size.height.toFloat()
                if (widthF != lastWidth || heightF != lastHeight) {
                    lastWidth = widthF
                    lastHeight = heightF
                    waveViewModel.initGrid(widthF, heightF)
                }
            }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    waveViewModel.launchWaveAt(offset.x, offset.y)
                }
            }
    ) {
        GridCanvas(
            gridPoints = gridPoints,
            pointRadius = 5f,
            colorBase = Color(0xFF5D0824),
            colorAccent = Color(0xFFFF1744),
            modifier = Modifier.fillMaxSize()
        )
    }
}