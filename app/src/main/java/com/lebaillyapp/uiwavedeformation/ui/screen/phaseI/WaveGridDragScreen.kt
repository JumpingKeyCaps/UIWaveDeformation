package com.lebaillyapp.uiwavedeformation.ui.screen.phaseI

import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lebaillyapp.uiwavedeformation.model.WaveDeformationConfig
import com.lebaillyapp.uiwavedeformation.ui.components.phaseI.GridCanvas
import com.lebaillyapp.uiwavedeformation.ui.components.phaseI.WaveTrailCanvas
import com.lebaillyapp.uiwavedeformation.viewmodel.phaseI.WaveDragViewModel

/**
 * Variante avancée de l'écran principal :
 * déclenche des vagues en glissant un ou plusieurs doigts,
 * pour simuler un effet fluide comme à la surface de l'eau.
 *
 * @param waveDragViewModel ViewModel contenant la grille et la logique d'onde.
 * @param modifier Modifier Compose standard.
 */
@Composable
fun WaveGridDragScreen(
    modifier: Modifier = Modifier,
    waveDragViewModel: WaveDragViewModel = viewModel(),
    config: WaveDeformationConfig = WaveDeformationConfig()
) {
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    val gridPoints by waveDragViewModel.gridPoints.collectAsState()
    val activeWaves by waveDragViewModel.activeWaves.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                if (canvasSize != size) {
                    canvasSize = size
                    waveDragViewModel.initGrid(
                        width = size.width.toFloat(),
                        height = size.height.toFloat(),
                        cellSize = config.cellSize,
                        waveAmpl = config.waveAmpl,
                        waveFreq = config.waveFreq,
                        waveSpeed = config.waveSpeed,
                        waveCoolDown = config.waveCoolDown,
                        damping = config.damping,
                        minValEraseWave = config.minValEraseWave,
                        frameRateAnim = config.frameRateAnim
                    )
                }
            }
            .pointerInput(Unit) {
                forEachGesture {
                    awaitPointerEventScope {
                        val event = awaitPointerEvent()
                        event.changes.forEach { change ->
                            if (change.pressed) {
                                waveDragViewModel.launchWaveAt(
                                    change.position.x,
                                    change.position.y,
                                    pointerId = change.id.value.toInt()
                                )
                            }
                        }

                        while (true) {
                            val move = awaitPointerEvent()
                            move.changes.forEach { change ->
                                if (change.pressed) {
                                    waveDragViewModel.launchWaveAt(
                                        change.position.x,
                                        change.position.y,
                                        pointerId = change.id.value.toInt()
                                    )
                                }
                            }
                            if (move.changes.all { !it.pressed }) break
                        }
                    }
                }
            }
    ) {
        GridCanvas(
            gridPoints = gridPoints,
            pointRadius = config.pointRadius,
            maxAmplAllowed = config.maxAmplAllowed,
            colorBase = config.colorBase,
            colorAccent = config.colorAccent,
            modifier = Modifier.fillMaxSize()
        )
        if (waveDragViewModel.showWaveTrails) {
            WaveTrailCanvas(
                waves = activeWaves,
                modifier = Modifier.fillMaxSize(),
                baseColor = config.trailBaseColor,
                baseStrokeWidth = config.trailStrokeWidth,
                minAlpha = config.trailMinAlpha,
                maxAlpha = config.trailMaxAlpha
            )
        }
    }
}

