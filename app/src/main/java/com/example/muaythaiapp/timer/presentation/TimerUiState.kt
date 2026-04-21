package com.example.muaythaiapp.timer.presentation

import kotlin.math.max

const val DEFAULT_PREP_DURATION_SECONDS = 10
const val DEFAULT_ROUND_DURATION_SECONDS = 3 * 60
const val DEFAULT_REST_DURATION_SECONDS = 60
const val DEFAULT_TOTAL_ROUNDS = 5
const val DEFAULT_BURNOUT_THRESHOLD_SECONDS = 30

enum class TimerPhase {
    Prep,
    Round,
    Rest,
}

data class TimerUiState(
    val phase: TimerPhase = TimerPhase.Prep,
    val isRunning: Boolean = false,
    val remainingSeconds: Int = DEFAULT_PREP_DURATION_SECONDS,
    val currentRound: Int = 1,
    val totalRounds: Int = DEFAULT_TOTAL_ROUNDS,
    val completedRounds: Int = 0,
    val prepDurationSeconds: Int = DEFAULT_PREP_DURATION_SECONDS,
    val roundDurationSeconds: Int = DEFAULT_ROUND_DURATION_SECONDS,
    val restDurationSeconds: Int = DEFAULT_REST_DURATION_SECONDS,
    val burnoutThresholdSeconds: Int = DEFAULT_BURNOUT_THRESHOLD_SECONDS,
    val isSessionComplete: Boolean = false,
) {
    val phaseDurationSeconds: Int
        get() = when (phase) {
            TimerPhase.Prep -> prepDurationSeconds
            TimerPhase.Round -> roundDurationSeconds
            TimerPhase.Rest -> restDurationSeconds
        }

    val totalSeconds: Int
        get() = max(phaseDurationSeconds, 1)

    val isPrepPeriod: Boolean
        get() = phase == TimerPhase.Prep

    val isRoundPeriod: Boolean
        get() = phase == TimerPhase.Round

    val isRestPeriod: Boolean
        get() = phase == TimerPhase.Rest

    val progressFraction: Float
        get() = remainingSeconds.coerceIn(0, totalSeconds).toFloat() / totalSeconds.toFloat()

    val isBurnoutActive: Boolean
        get() = isRoundPeriod &&
            !isSessionComplete &&
            remainingSeconds in 1..burnoutThresholdSeconds

    val burnoutProgress: Float
        get() = if (!isBurnoutActive) {
            0f
        } else {
            (burnoutThresholdSeconds - remainingSeconds).toFloat() /
                max(burnoutThresholdSeconds, 1).toFloat()
        }

    val phaseLabel: String
        get() = when {
            isSessionComplete -> "Complete"
            isPrepPeriod -> "Prep"
            isRestPeriod -> "Rest"
            else -> "Round"
        }

    val statusLabel: String
        get() = when {
            isSessionComplete -> "Session complete"
            isBurnoutActive -> "Burnout mode"
            isPrepPeriod -> "Get ready"
            isRestPeriod -> "Recover"
            else -> "Fight pace"
        }

    val primaryActionLabel: String
        get() = when {
            isSessionComplete -> "Restart"
            isRunning -> "Pause"
            else -> "Start"
        }

    val burnoutBeepIntervalMillis: Long
        get() = when {
            !isBurnoutActive -> 0L
            remainingSeconds <= 10 -> 250L
            remainingSeconds <= 20 -> 500L
            else -> 750L
        }

    val roundLabel: String
        get() = "Round $currentRound/$totalRounds"

    val roundsSummary: String
        get() = "$completedRounds completed"

    val nextCueLabel: String
        get() = when {
            isSessionComplete -> "Reset to run the session again"
            isBurnoutActive -> "Cue speed ${burnoutBeepIntervalMillis} ms"
            isPrepPeriod -> "Bell into round $currentRound"
            isRestPeriod -> "Next prep for round $currentRound"
            else -> "Keep the pressure on"
        }

    val phaseTagline: String
        get() = when {
            isSessionComplete -> "Work finished"
            isBurnoutActive -> "Do not fade"
            else -> when (phase) {
                TimerPhase.Prep -> "Set stance"
                TimerPhase.Round -> "Apply pressure"
                TimerPhase.Rest -> "Control breathing"
            }
        }

    val sessionTitle: String
        get() = when {
            isSessionComplete -> "Session complete"
            isPrepPeriod -> "Prep window"
            isRestPeriod -> "Rest window"
            else -> "Round live"
        }
}
