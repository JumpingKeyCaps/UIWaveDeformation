package com.lebaillyapp.uiwavedeformation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import com.lebaillyapp.uiwavedeformation.ui.screen.phaseII.AdaptiveTileDemo
import com.lebaillyapp.uiwavedeformation.ui.theme.UIWaveDeformationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        enableEdgeToEdge()
        setContent {
            UIWaveDeformationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    //todo phase 1 -------------DONT TOUCH

                    /**

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

                    */

                    //todo phase 2 -------------
                    // Phase 2: Bitmap deformation
                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {

                        //todo phase 2a ------------- FULL CPU
                        val bitmap = ImageBitmap.imageResource(id = R.drawable.demogirl)
                        /**
                      //  DeformableBitmapGridTouch(bitmap = bitmap)

                        DemoWaveGridScreen(bitmap,modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxSize(0.99f),
                            backLayer = true,
                            gridSize = 40)
                        */

                        AdaptiveTileDemo(bitmap,modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxSize(0.99f))

                    }

                }

            }
        }
    }
}



