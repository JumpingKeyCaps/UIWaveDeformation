package com.lebaillyapp.uiwavedeformation.ui.screen.phaseII

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lebaillyapp.uiwavedeformation.ui.components.phaseII.WaveDeformableBitmapGridHybrid
import com.lebaillyapp.uiwavedeformation.viewmodel.phaseII.WaveTileViewModel

@Composable
fun AdaptiveTileDemo(bitmap: ImageBitmap,modifier: Modifier = Modifier){
    // Récupère ou crée un ViewModel (tu peux passer un factory si besoin)
    val waveViewModel: WaveTileViewModel = viewModel()

    WaveDeformableBitmapGridHybrid(
        modifier = modifier,
        bitmap = bitmap,
        viewModel = waveViewModel,
        baseCols = 42,
        baseRows = 42,
        subdivisionRadiusPx = 300f,
        drawBackLayer = true,
        defaultDeformFactor = 1.05f
    )
}