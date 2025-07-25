// Reemplazar: app/src/main/java/com/IoT/carrobot/MainActivity.kt
package com.IoT.carrobot

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.IoT.carrobot.presentation.ui.CarroBotApp
import com.IoT.carrobot.presentation.ui.theme.CarroBotTheme
import com.IoT.carrobot.presentation.viewmodel.MainViewModel
import timber.log.Timber

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar Timber
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // Forzar orientación horizontal
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        setContent {
            CarroBotTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CarroBotApp(viewModel = viewModel)
                }
            }
        }

        Timber.d("CarroBot MainActivity creada en modo horizontal")
    }
}