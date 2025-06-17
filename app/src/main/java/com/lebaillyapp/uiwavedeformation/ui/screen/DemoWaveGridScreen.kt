package com.lebaillyapp.uiwavedeformation.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import com.lebaillyapp.uiwavedeformation.ui.components.WaveDeformableBitmapGrid
import com.lebaillyapp.uiwavedeformation.viewmodel.WaveTileViewModel

@Composable
fun DemoWaveGridScreen(bitmap: ImageBitmap,
                       modifier: Modifier = Modifier,
                       backLayer: Boolean = false,
                       gridSize: Int = 40) {
    val viewModel = remember { WaveTileViewModel() }

    WaveDeformableBitmapGrid(
        modifier = modifier,
        bitmap = bitmap,
        viewModel = viewModel,
        drawBackLayer = backLayer,
        tileCols = gridSize,
        tileRows = gridSize
    )
}