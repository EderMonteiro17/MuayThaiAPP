package com.example.muaythaiapp.ui.timer

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muaythaiapp.timer.presentation.TimerPhase
import com.example.muaythaiapp.timer.presentation.TimerUiState
import com.example.muaythaiapp.ui.theme.MuayThaiAPPTheme
import kotlin.math.max

/**
 * The main screen for the Muay Thai timer.
 * Displays the current round, time remaining, and control buttons.
 *
 * @param uiState The current state of the timer.
 * @param onStartPauseClick Callback triggered when the Start/Pause button is clicked.
 * @param onResetClick Callback triggered when the Reset button is clicked.
 * @param modifier Modifier to be applied to the screen.
 */
@Composable
fun TimerScreen(
    uiState: TimerUiState,
    onStartPauseClick: () -> Unit,
    onResetClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val targetProgress = rememberProgress(uiState.remainingSeconds, uiState.totalSeconds)
    val progress by animateFloatAsState(
        targetValue = targetProgress,
        label = "countdownProgress",
    )
    val phaseColor by animateColorAsState(
        targetValue = if (uiState.isRestPeriod) {
            Color(0xFF29D3C5) // Cyan/Teal for Rest
        } else {
            Color(0xFFFF7A18) // Orange for Round
        },
        label = "phaseColor",
    )
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
        ),
    )

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
        ) {
            TimerAtmosphere(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .height(240.dp),
                phaseColor = phaseColor,
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                PhaseHeader(
                    isRestPeriod = uiState.isRestPeriod,
                    currentRound = uiState.currentRound,
                    completedRounds = uiState.completedRounds,
                )
                Spacer(modifier = Modifier.height(24.dp))
                CountdownCard(
                    uiState = uiState,
                    progress = progress,
                    phaseColor = phaseColor,
                )
                Spacer(modifier = Modifier.height(24.dp))
                ActionRow(
                    isRunning = uiState.isRunning,
                    onStartPauseClick = onStartPauseClick,
                    onResetClick = onResetClick,
                )
            }
        }
    }
}

/**
 * Displays the header information for the current phase (Round or Rest).
 */
@Composable
private fun PhaseHeader(
    isRestPeriod: Boolean,
    currentRound: Int,
    completedRounds: Int,
) {
    val phaseLabel = if (isRestPeriod) "Rest" else "Round"
    val phaseAccent = if (isRestPeriod) Color(0xFF29D3C5) else Color(0xFFFF7A18)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = phaseLabel.uppercase(),
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
            ),
            color = phaseAccent,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Round $currentRound",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Black,
            ),
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$completedRounds completed",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/**
 * Displays the countdown timer inside a card with a circular progress indicator.
 */
@Composable
private fun CountdownCard(
    uiState: TimerUiState,
    progress: Float,
    phaseColor: Color,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(horizontal = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(22.dp),
            contentAlignment = Alignment.Center,
        ) {
            CircularCountdown(
                progress = progress,
                phaseColor = phaseColor,
                modifier = Modifier.fillMaxSize(),
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = formatTime(uiState.remainingSeconds),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 56.sp,
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = if (uiState.isRestPeriod) "Recovery window" else "Work window",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/**
 * Control buttons for the timer (Start/Pause and Reset).
 */
@Composable
private fun ActionRow(
    isRunning: Boolean,
    onStartPauseClick: () -> Unit,
    onResetClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Button(
            onClick = onStartPauseClick,
            modifier = Modifier
                .weight(1f)
                .height(58.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isRunning) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                },
                contentColor = if (isRunning) {
                    MaterialTheme.colorScheme.onError
                } else {
                    MaterialTheme.colorScheme.onPrimary
                },
            ),
            shape = MaterialTheme.shapes.large,
        ) {
            Text(
                text = if (isRunning) "Pause" else "Start",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
        }

        OutlinedButton(
            onClick = onResetClick,
            modifier = Modifier
                .weight(1f)
                .height(58.dp),
            shape = MaterialTheme.shapes.large,
        ) {
            Text(
                text = "Reset",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )
        }
    }
}

/**
 * Custom circular progress indicator for the countdown.
 */
@Composable
private fun CircularCountdown(
    progress: Float,
    phaseColor: Color,
    modifier: Modifier = Modifier,
) {
    val trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)

    Box(
        modifier = modifier.clip(MaterialTheme.shapes.extraLarge),
        contentAlignment = Alignment.Center,
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = size.minDimension * 0.08f
            val arcSize = size.minDimension - strokeWidth
            val topLeft = androidx.compose.ui.geometry.Offset(
                x = (size.width - arcSize) / 2f,
                y = (size.height - arcSize) / 2f,
            )
            drawCircle(
                color = trackColor,
                radius = arcSize / 2f,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth),
            )
            drawArc(
                color = phaseColor,
                startAngle = -90f,
                sweepAngle = 360f * progress.coerceIn(0f, 1f),
                useCenter = false,
                topLeft = topLeft,
                size = androidx.compose.ui.geometry.Size(arcSize, arcSize),
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = strokeWidth,
                    cap = androidx.compose.ui.graphics.StrokeCap.Round,
                ),
            )
        }
    }
}

/**
 * Background decorative elements to provide visual depth.
 */
@Composable
private fun TimerAtmosphere(
    modifier: Modifier = Modifier,
    phaseColor: Color,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(140.dp)
                .background(
                    color = phaseColor.copy(alpha = 0.16f),
                    shape = MaterialTheme.shapes.extraLarge,
                ),
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(96.dp)
                .background(
                    color = phaseColor.copy(alpha = 0.10f),
                    shape = MaterialTheme.shapes.extraLarge,
                ),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(180.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                    shape = MaterialTheme.shapes.extraLarge,
                ),
        )
    }
}

private fun rememberProgress(remainingSeconds: Int, totalSeconds: Int): Float {
    val safeTotal = max(totalSeconds, 1)
    return (remainingSeconds.coerceIn(0, safeTotal).toFloat() / safeTotal.toFloat()).coerceIn(0f, 1f)
}

private fun formatTime(seconds: Int): String {
    val safeSeconds = max(seconds, 0)
    val minutes = safeSeconds / 60
    val remaining = safeSeconds % 60
    return "%02d:%02d".format(minutes, remaining)
}

@Preview(showBackground = true)
@Composable
private fun TimerScreenPreviewRound() {
    MuayThaiAPPTheme(dynamicColor = false) {
        TimerScreen(
            uiState = TimerUiState(
                remainingSeconds = 143,
                isRunning = true,
                currentRound = 2,
                completedRounds = 1,
            ),
            onStartPauseClick = {},
            onResetClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TimerScreenPreviewRest() {
    MuayThaiAPPTheme(dynamicColor = false) {
        TimerScreen(
            uiState = TimerUiState(
                phase = TimerPhase.Rest,
                remainingSeconds = 41,
                isRunning = false,
                currentRound = 3,
                completedRounds = 2,
            ),
            onStartPauseClick = {},
            onResetClick = {},
        )
    }
}
