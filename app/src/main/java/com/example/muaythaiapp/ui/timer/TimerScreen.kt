package com.example.muaythaiapp.ui.timer

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muaythaiapp.timer.presentation.TimerPhase
import com.example.muaythaiapp.timer.presentation.TimerUiState
import com.example.muaythaiapp.ui.theme.BoneWhite
import com.example.muaythaiapp.ui.theme.BurnoutCrimson
import com.example.muaythaiapp.ui.theme.CageBlack
import com.example.muaythaiapp.ui.theme.CornerBlack
import com.example.muaythaiapp.ui.theme.DeepCrimson
import com.example.muaythaiapp.ui.theme.MatteBlack
import com.example.muaythaiapp.ui.theme.MuayThaiAPPTheme
import com.example.muaythaiapp.ui.theme.SteelGray

@Composable
fun TimerScreen(
    uiState: TimerUiState,
    onStartPauseClick: () -> Unit,
    onResetClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val progress by animateFloatAsState(targetValue = uiState.progressFraction, label = "timerProgress")
    val infiniteTransition = rememberInfiniteTransition(label = "burnoutPulse")
    val burnoutPulse by infiniteTransition.animateFloat(
        initialValue = 0.18f,
        targetValue = 0.78f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 420, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "burnoutAlpha",
    )

    val accentColor = when {
        uiState.isSessionComplete -> BoneWhite
        uiState.isBurnoutActive -> BurnoutCrimson
        uiState.isRoundPeriod -> BoneWhite
        uiState.isRestPeriod -> SteelGray
        else -> BoneWhite
    }

    val backgroundColor by animateColorAsState(
        targetValue = when {
            uiState.isSessionComplete -> MatteBlack
            uiState.isRoundPeriod -> DeepCrimson
            else -> MatteBlack
        },
        label = "phaseBackground",
    )

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            backgroundColor,
            backgroundColor.copy(alpha = 0.95f),
            MatteBlack,
        ),
    )

    Surface(
        modifier = modifier.fillMaxSize(),
        color = backgroundColor,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
        ) {
            if (uiState.isBurnoutActive) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BoneWhite.copy(alpha = burnoutPulse * 0.12f)),
                )
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                TimerHeader(
                    uiState = uiState,
                    accentColor = accentColor,
                )
                TimerStatsRow(
                    uiState = uiState,
                    accentColor = accentColor,
                )
                CountdownCard(
                    uiState = uiState,
                    progress = progress,
                    accentColor = accentColor,
                )
                ActionRow(
                    uiState = uiState,
                    accentColor = accentColor,
                    onStartPauseClick = onStartPauseClick,
                    onResetClick = onResetClick,
                )
            }
        }
    }
}

@Composable
private fun TimerHeader(
    uiState: TimerUiState,
    accentColor: Color,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = "NAKA RED",
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp,
            ),
            color = BoneWhite,
        )
        Text(
            text = uiState.phaseTagline.uppercase(),
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
            ),
            color = accentColor,
        )
        Text(
            text = uiState.sessionTitle.uppercase(),
            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Black),
            color = BoneWhite,
        )
        Text(
            text = uiState.nextCueLabel,
            style = MaterialTheme.typography.bodyLarge,
            color = SteelGray,
        )
    }
}

@Composable
private fun TimerStatsRow(
    uiState: TimerUiState,
    accentColor: Color,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TimerStatCard(
            modifier = Modifier.weight(1f),
            label = "Phase",
            value = uiState.phaseLabel,
            accentColor = accentColor,
        )
        TimerStatCard(
            modifier = Modifier.weight(1f),
            label = "Round",
            value = uiState.roundLabel,
            accentColor = BoneWhite,
        )
        TimerStatCard(
            modifier = Modifier.weight(1f),
            label = "Done",
            value = uiState.roundsSummary,
            accentColor = SteelGray,
        )
    }
}

@Composable
private fun TimerStatCard(
    label: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CageBlack.copy(alpha = 0.92f)),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.24f)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                ),
                color = SteelGray,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = BoneWhite,
            )
        }
    }
}

@Composable
private fun CountdownCard(
    uiState: TimerUiState,
    progress: Float,
    accentColor: Color,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CageBlack.copy(alpha = 0.90f)),
        border = BorderStroke(
            width = if (uiState.isBurnoutActive) 2.dp else 1.dp,
            color = accentColor.copy(alpha = if (uiState.isBurnoutActive) 0.85f else 0.28f),
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            RoundRing(
                progress = progress,
                accentColor = accentColor,
                size = 280.dp,
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = formatTime(uiState.remainingSeconds),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 82.sp,
                        lineHeight = 82.sp,
                        letterSpacing = (-2).sp,
                    ),
                    color = BoneWhite,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.statusLabel.uppercase(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp,
                    ),
                    color = accentColor,
                )
            }
            ProgressStrip(progress = progress, accentColor = accentColor)
            if (uiState.isBurnoutActive) {
                BurnoutBanner(uiState = uiState)
            }
        }
    }
}

@Composable
private fun RoundRing(
    progress: Float,
    accentColor: Color,
    size: Dp,
) {
    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = this.size.minDimension * 0.06f
            drawCircle(
                color = CornerBlack,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth),
            )
            drawArc(
                color = accentColor,
                startAngle = -90f,
                sweepAngle = 360f * progress.coerceIn(0f, 1f),
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round,
                ),
            )
        }
    }
}

@Composable
private fun ProgressStrip(
    progress: Float,
    accentColor: Color,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(12.dp)
            .border(
                width = 1.dp,
                color = BoneWhite.copy(alpha = 0.12f),
                shape = MaterialTheme.shapes.extraLarge,
            )
            .background(CornerBlack, MaterialTheme.shapes.extraLarge)
            .padding(2.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .background(accentColor, MaterialTheme.shapes.extraLarge),
        )
    }
}

@Composable
private fun BurnoutBanner(
    uiState: TimerUiState,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BurnoutCrimson.copy(alpha = 0.22f)),
        border = BorderStroke(1.dp, BurnoutCrimson),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "BURNOUT MODE",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                    ),
                    color = BoneWhite,
                )
                Text(
                    text = "${uiState.remainingSeconds}s left in the red zone",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BoneWhite.copy(alpha = 0.86f),
                )
            }
            Text(
                text = "${uiState.burnoutBeepIntervalMillis} ms",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                color = BoneWhite,
            )
        }
    }
}

@Composable
private fun ActionRow(
    uiState: TimerUiState,
    accentColor: Color,
    onStartPauseClick: () -> Unit,
    onResetClick: () -> Unit,
) {
    val buttonContentColor = if (uiState.isBurnoutActive) BoneWhite else MatteBlack

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Button(
            onClick = onStartPauseClick,
            modifier = Modifier
                .weight(1f)
                .height(64.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = accentColor,
                contentColor = buttonContentColor,
            ),
            shape = MaterialTheme.shapes.extraLarge,
        ) {
            Text(
                text = uiState.primaryActionLabel,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.8.sp,
                ),
            )
        }

        OutlinedButton(
            onClick = onResetClick,
            modifier = Modifier
                .weight(1f)
                .height(64.dp),
            shape = MaterialTheme.shapes.extraLarge,
            border = BorderStroke(1.dp, BoneWhite.copy(alpha = 0.24f)),
        ) {
            Text(
                text = "Reset",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.8.sp,
                ),
                color = BoneWhite,
            )
        }
    }
}

private fun formatTime(seconds: Int): String {
    val safeSeconds = seconds.coerceAtLeast(0)
    val minutes = safeSeconds / 60
    val remaining = safeSeconds % 60
    return "%02d:%02d".format(minutes, remaining)
}

@Preview(showBackground = true)
@Composable
fun TimerScreenPreviewPrep() {
    MuayThaiAPPTheme(darkTheme = true, dynamicColor = false) {
        TimerScreen(
            uiState = TimerUiState(
                phase = TimerPhase.Prep,
                remainingSeconds = 8,
            ),
            onStartPauseClick = {},
            onResetClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TimerScreenPreviewRound() {
    MuayThaiAPPTheme(darkTheme = true, dynamicColor = false) {
        TimerScreen(
            uiState = TimerUiState(
                phase = TimerPhase.Round,
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
fun TimerScreenPreviewBurnout() {
    MuayThaiAPPTheme(darkTheme = true, dynamicColor = false) {
        TimerScreen(
            uiState = TimerUiState(
                phase = TimerPhase.Round,
                remainingSeconds = 18,
                isRunning = true,
                currentRound = 5,
                completedRounds = 4,
            ),
            onStartPauseClick = {},
            onResetClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TimerScreenPreviewRest() {
    MuayThaiAPPTheme(darkTheme = true, dynamicColor = false) {
        TimerScreen(
            uiState = TimerUiState(
                phase = TimerPhase.Rest,
                remainingSeconds = 41,
                currentRound = 3,
                completedRounds = 2,
            ),
            onStartPauseClick = {},
            onResetClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TimerScreenPreviewComplete() {
    MuayThaiAPPTheme(darkTheme = true, dynamicColor = false) {
        TimerScreen(
            uiState = TimerUiState(
                phase = TimerPhase.Round,
                remainingSeconds = 0,
                currentRound = 5,
                totalRounds = 5,
                completedRounds = 5,
                isSessionComplete = true,
            ),
            onStartPauseClick = {},
            onResetClick = {},
        )
    }
}
