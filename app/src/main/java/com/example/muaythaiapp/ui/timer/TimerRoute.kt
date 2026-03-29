package com.example.muaythaiapp.ui.timer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.muaythaiapp.timer.presentation.TimerUiState

@Composable
fun TimerRoute(
    uiState: TimerUiState,
    onStartPauseClick: () -> Unit,
    onResetClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TimerScreen(
        uiState = uiState,
        onStartPauseClick = onStartPauseClick,
        onResetClick = onResetClick,
        modifier = modifier,
    )
}
