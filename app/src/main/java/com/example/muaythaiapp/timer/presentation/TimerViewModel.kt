package com.example.muaythaiapp.timer.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.muaythaiapp.domain.training.TrainingRepository
import com.example.muaythaiapp.domain.training.TrainingPhase
import com.example.muaythaiapp.domain.training.Treino
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing the Muay Thai timer logic.
 * It handles the countdown, phase transitions (Round/Rest), and persists completed rounds.
 *
 * @property trainingRepository The repository used to save training data.
 */
@HiltViewModel
class TimerViewModel @Inject constructor(
    private val trainingRepository: TrainingRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimerUiState())
    /**
     * Observable state representing the current timer status and configuration.
     */
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    /**
     * Toggles the timer between running and paused states.
     */
    fun onStartPauseToggle() {
        if (_uiState.value.isRunning) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    /**
     * Resets the timer to its initial state and stops any active countdown.
     */
    fun resetTimer() {
        timerJob?.cancel()
        timerJob = null
        _uiState.value = TimerUiState()
    }

    private fun startTimer() {
        if (timerJob?.isActive == true) return

        _uiState.update { it.copy(isRunning = true) }
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(TICK_INTERVAL_MS)
                if (!_uiState.value.isRunning) break
                advanceTimerByOneSecond()
            }
        }
    }

    private fun pauseTimer() {
        _uiState.update { it.copy(isRunning = false) }
        timerJob?.cancel()
        timerJob = null
    }

    private suspend fun advanceTimerByOneSecond() {
        val currentState = _uiState.value
        when {
            currentState.remainingSeconds > 1 -> {
                _uiState.update {
                    it.copy(remainingSeconds = it.remainingSeconds - 1)
                }
            }
            currentState.phase == TimerPhase.Round -> {
                persistCompletedRound(
                    roundNumber = currentState.currentRound,
                    durationSeconds = currentState.roundDurationSeconds,
                )
                _uiState.value = currentState.copy(
                    phase = TimerPhase.Rest,
                    isRunning = true,
                    remainingSeconds = currentState.restDurationSeconds,
                    currentRound = currentState.currentRound + 1,
                    completedRounds = currentState.completedRounds + 1,
                )
            }
            else -> {
                _uiState.value = currentState.copy(
                    phase = TimerPhase.Round,
                    isRunning = true,
                    remainingSeconds = currentState.roundDurationSeconds,
                )
            }
        }
    }

    private fun persistCompletedRound(roundNumber: Int, durationSeconds: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                trainingRepository.saveTreino(
                    treino = Treino(
                        completedAtMillis = System.currentTimeMillis(),
                        roundNumber = roundNumber,
                        phase = TrainingPhase.ROUND,
                        durationSeconds = durationSeconds,
                        completedRounds = _uiState.value.completedRounds + 1,
                    ),
                )
            }
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
        timerJob = null
        super.onCleared()
    }

    private companion object {
        const val TICK_INTERVAL_MS = 1_000L
    }
}
