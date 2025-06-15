package com.lebaillyapp.uiwavedeformation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.lebaillyapp.uiwavedeformation.model.WaveDeformationConfig
import com.lebaillyapp.uiwavedeformation.ui.screen.WaveGridDragScreen
import com.lebaillyapp.uiwavedeformation.ui.screen.WaveGridScreen
import com.lebaillyapp.uiwavedeformation.ui.theme.UIWaveDeformationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UIWaveDeformationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                  //  WaveGridScreen(modifier = Modifier.fillMaxSize().padding(innerPadding) )
                    WaveGridDragScreen(
                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                        config = WaveDeformationConfig(
                            cellSize = 20f,
                            waveAmpl = 50f,
                            waveFreq = 2f,
                            waveSpeed = 1000f,

                            waveCoolDown = 150L,
                            damping = 0.2f,
                            minValEraseWave = 0.3f,

                            frameRateAnim = 16L, //framerate (16L = 60fps / 32L = 30fps / 42L = 24fps)

                            pointRadius = 3f,
                            maxAmplAllowed = 50f,
                            colorBase = Color(0xFF00E5FF),
                            colorAccent = Color(0xFF651FFF),
                            trailBaseColor = Color(0xFF351679),
                            trailStrokeWidth = 2f,
                            trailMinAlpha = 0.05f,
                            trailMaxAlpha = 0.3f
                        )
                    )
                }
            }
        }
    }
}
