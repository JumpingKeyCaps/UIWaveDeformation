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
                            cellSize = 40f,
                            waveAmpl = 50f,
                            waveFreq = 2f,
                            waveSpeed = 1000f,
                            waveCoolDown = 100L,
                            damping = 0.3f,
                            minValEraseWave = 0.3f,
                            frameRateAnim = 32L,
                            pointRadius = 5f,
                            maxAmplAllowed = 50f,
                            colorBase = Color(0xFF3D5AFE),
                            colorAccent = Color(0xFF00B0FF),
                            trailBaseColor = Color(0xFF3D5AFE),
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
