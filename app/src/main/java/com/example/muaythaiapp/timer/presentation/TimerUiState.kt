package com.example.muaythaiapp.timer.presentation

import kotlin.math.max

/** Default duration for a training round in seconds. */
const val DEFAULT_ROUND_DURATION_SECONDS = 3 * 60
/** Default duration for a rest period in seconds. */
const val DEFAULT_REST_DURATION_SECONDS = 60

/**
 * Represents the different phases of a timer.
 */
enum class TimerPhase {
    /** Active training phase. */
    Round,
    /** Recovery/rest phase. */
    Rest
}

/**
 * State class for the Timer screen.
 *
 * @property phase The current [TimerPhase].
 * @property isRunning Whether the timer is currently counting down.
 * @property remainingSeconds The number of seconds left in the current phase.
 * @property currentRound The current round number (starting from 1).
 * @property completedRounds The total number of rounds completed in this session.
 * @property roundDurationSeconds Configured duration for a Round phase.
 * @property restDurationSeconds Configured duration for a Rest phase.
 */
data class TimerUiState(
    val phase: TimerPhase = TimerPhase.Round,
    val isRunning: Boolean = false,
    val remainingSeconds: Int = DEFAULT_ROUND_DURATION_SECONDS,
    val currentRound: Int = 1,
    val completedRounds: Int = 0,
    val roundDurationSeconds: Int = DEFAULT_ROUND_DURATION_SECONDS,
    val restDurationSeconds: Int = DEFAULT_REST_DURATION_SECONDS,
) {
    /**
     * Returns the total duration of the current phase in seconds.
     */
    val phaseDurationSeconds: Int
        get() = when (phase) {
            TimerPhase.Round -> roundDurationSeconds
            TimerPhase.Rest -> restDurationSeconds
        }

    /**
     * Alias for [phaseDurationSeconds].
     */
    val totalSeconds: Int
        get() = phaseDurationSeconds

    /**
     * True if the current phase is [TimerPhase.Rest].
     */
    val isRestPeriod: Boolean
        get() = phase == TimerPhase.Rest

    /**
     * The progress of the current phase as a fraction between 0.0 and 1.0.
     */
    val progressFraction: Float
        get() = remainingSeconds.toFloat() / max(phaseDurationSeconds, 1)

    /**
     * A user-friendly label for the current phase.
     */
    val phaseLabel: String
        get() = when (phase) {
            TimerPhase.Round -> "Round"
            TimerPhase.Rest -> "Rest"
        }
}
