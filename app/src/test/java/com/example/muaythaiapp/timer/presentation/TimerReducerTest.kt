package com.example.muaythaiapp.timer.presentation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TimerReducerTest {

    @Test
    fun `prep phase moves into round when countdown ends`() {
        val transition = reduceTimerTick(
            TimerUiState(
                phase = TimerPhase.Prep,
                isRunning = true,
                remainingSeconds = 1,
            ),
        )

        assertEquals(TimerPhase.Round, transition.nextState.phase)
        assertEquals(DEFAULT_ROUND_DURATION_SECONDS, transition.nextState.remainingSeconds)
        assertTrue(transition.nextState.isRunning)
        assertFalse(transition.shouldStopTicker)
    }

    @Test
    fun `burnout mode activates in the last thirty seconds of a round`() {
        val transition = reduceTimerTick(
            TimerUiState(
                phase = TimerPhase.Round,
                isRunning = true,
                remainingSeconds = 31,
            ),
        )

        assertEquals(30, transition.nextState.remainingSeconds)
        assertTrue(transition.nextState.isBurnoutActive)
        assertEquals(750L, transition.nextState.burnoutBeepIntervalMillis)
    }

    @Test
    fun `round completion moves to rest and persists round metadata`() {
        val transition = reduceTimerTick(
            TimerUiState(
                phase = TimerPhase.Round,
                isRunning = true,
                remainingSeconds = 1,
                currentRound = 2,
                totalRounds = 5,
                completedRounds = 1,
            ),
        )

        assertEquals(TimerPhase.Rest, transition.nextState.phase)
        assertEquals(DEFAULT_REST_DURATION_SECONDS, transition.nextState.remainingSeconds)
        assertEquals(3, transition.nextState.currentRound)
        assertEquals(2, transition.nextState.completedRounds)
        assertNotNull(transition.completedRound)
        assertEquals(2, transition.completedRound?.roundNumber)
        assertFalse(transition.shouldStopTicker)
    }

    @Test
    fun `last round completion stops the session`() {
        val transition = reduceTimerTick(
            TimerUiState(
                phase = TimerPhase.Round,
                isRunning = true,
                remainingSeconds = 1,
                currentRound = 5,
                totalRounds = 5,
                completedRounds = 4,
            ),
        )

        assertTrue(transition.nextState.isSessionComplete)
        assertFalse(transition.nextState.isRunning)
        assertEquals(0, transition.nextState.remainingSeconds)
        assertEquals(5, transition.nextState.completedRounds)
        assertTrue(transition.shouldStopTicker)
    }
}
