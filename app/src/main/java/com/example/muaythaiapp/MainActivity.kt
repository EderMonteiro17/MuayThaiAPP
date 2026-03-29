package com.example.muaythaiapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.muaythaiapp.timer.presentation.TimerViewModel
import com.example.muaythaiapp.ui.timer.TimerRoute
import com.example.muaythaiapp.ui.theme.MuayThaiAPPTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity of the Muay Thai application.
 * This activity serves as the entry point for the app and sets up the main UI using Jetpack Compose.
 * It is annotated with [AndroidEntryPoint] to enable Hilt dependency injection.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is starting.
     * This is where most initialization should go: calling [setContent], [enableEdgeToEdge], etc.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in [onSaveInstanceState]. **Note: Otherwise it is null.**
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MuayThaiAPPTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val viewModel: TimerViewModel = hiltViewModel()
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                    TimerRoute(
                        uiState = uiState,
                        onStartPauseClick = viewModel::onStartPauseToggle,
                        onResetClick = viewModel::resetTimer,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
