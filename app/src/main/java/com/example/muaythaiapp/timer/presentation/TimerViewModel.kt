package com.example.muaythaiapp.timer.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.muaythaiapp.data.profile.UserProfileRepository
import com.example.muaythaiapp.domain.training.TrainingPhase
import com.example.muaythaiapp.domain.training.TrainingRepository
import com.example.muaythaiapp.domain.training.Treino
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val trainingRepository: TrainingRepository,
    private val userProfileRepository: UserProfileRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(createInitialState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    fun onStartPauseToggle() {
        when {
            _uiState.value.isSessionComplete -> restartSession()
            _uiState.value.isRunning -> pauseTimer()
            else -> startTimer()
        }
    }

    fun resetTimer() {
        stopTicker()
        _uiState.value = createInitialState(_uiState.value)
    }

    private fun startTimer() {
        if (timerJob?.isActive == true) return

        _uiState.update { it.copy(isRunning = true) }
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(TICK_INTERVAL_MS)
                if (!_uiState.value.isRunning) break
                handleTimerTick()
            }
        }
    }

    private fun pauseTimer() {
        _uiState.update { it.copy(isRunning = false) }
        stopTicker()
    }

    internal fun handleTimerTick() {
        val transition = reduceTimerTick(_uiState.value)
        _uiState.value = transition.nextState

        transition.completedRound?.let { completedRound ->
            persistCompletedRound(
                roundNumber = completedRound.roundNumber,
                durationSeconds = completedRound.durationSeconds,
                completedRounds = completedRound.completedRounds,
            )
        }

        if (transition.shouldStopTicker) {
            stopTicker()
        }
    }

    private fun restartSession() {
        stopTicker()
        _uiState.value = createInitialState(_uiState.value)
        startTimer()
    }

    private fun createInitialState(): TimerUiState = TimerUiState(
        burnoutThresholdSeconds = resolveBurnoutThresholdSeconds(DEFAULT_BURNOUT_THRESHOLD_SECONDS),
    )

    private fun createInitialState(referenceState: TimerUiState): TimerUiState = TimerUiState(
        totalRounds = referenceState.totalRounds,
        prepDurationSeconds = referenceState.prepDurationSeconds,
        roundDurationSeconds = referenceState.roundDurationSeconds,
        restDurationSeconds = referenceState.restDurationSeconds,
        burnoutThresholdSeconds = resolveBurnoutThresholdSeconds(referenceState.burnoutThresholdSeconds),
    )

    private fun resolveBurnoutThresholdSeconds(defaultThreshold: Int): Int {
        return userProfileRepository.currentProfile.value
            ?.primaryGoal
            ?.burnoutThresholdSeconds
            ?: defaultThreshold
    }

    private fun persistCompletedRound(
        roundNumber: Int,
        durationSeconds: Int,
        completedRounds: Int,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                trainingRepository.saveTreino(
                    Treino(
                        completedAtMillis = System.currentTimeMillis(),
                        roundNumber = roundNumber,
                        phase = TrainingPhase.ROUND,
                        durationSeconds = durationSeconds,
                        completedRounds = completedRounds,
                    ),
                )
            }
        }
    }

    private fun stopTicker() {
        timerJob?.cancel()
        timerJob = null
    }

    override fun onCleared() {
        stopTicker()
        super.onCleared()
    }

    private companion object {
        const val TICK_INTERVAL_MS = 1_000L
    }
}

internal data class TimerTransition(
    val nextState: TimerUiState,
    val completedRound: CompletedRoundRecord? = null,
    val shouldStopTicker: Boolean = false,
)

internal data class CompletedRoundRecord(
    val roundNumber: Int,
    val durationSeconds: Int,
    val completedRounds: Int,
)

internal fun reduceTimerTick(currentState: TimerUiState): TimerTransition {
    if (currentState.isSessionComplete) {
        return TimerTransition(
            nextState = currentState.copy(isRunning = false),
            shouldStopTicker = true,
        )
    }

    if (currentState.remainingSeconds > 1) {
        return TimerTransition(
            nextState = currentState.copy(remainingSeconds = currentState.remainingSeconds - 1),
        )
    }

    return when (currentState.phase) {
        TimerPhase.Prep -> TimerTransition(
            nextState = currentState.copy(
                phase = TimerPhase.Round,
                isRunning = true,
                remainingSeconds = currentState.roundDurationSeconds,
            ),
        )

        TimerPhase.Round -> {
            val completedRounds = currentState.completedRounds + 1
            val completedRound = CompletedRoundRecord(
                roundNumber = currentState.currentRound,
                durationSeconds = currentState.roundDurationSeconds,
                completedRounds = completedRounds,
            )

            if (completedRounds >= currentState.totalRounds) {
                TimerTransition(
                    nextState = currentState.copy(
                        isRunning = false,
                        remainingSeconds = 0,
                        completedRounds = completedRounds,
                        isSessionComplete = true,
                    ),
                    completedRound = completedRound,
                    shouldStopTicker = true,
                )
            } else {
                TimerTransition(
                    nextState = currentState.copy(
                        phase = TimerPhase.Rest,
                        isRunning = true,
                        remainingSeconds = currentState.restDurationSeconds,
                        currentRound = currentState.currentRound + 1,
                        completedRounds = completedRounds,
                    ),
                    completedRound = completedRound,
                )
            }
        }

        TimerPhase.Rest -> TimerTransition(
            nextState = currentState.copy(
                phase = TimerPhase.Prep,
                isRunning = true,
                remainingSeconds = currentState.prepDurationSeconds,
            ),
        )
    }
}
