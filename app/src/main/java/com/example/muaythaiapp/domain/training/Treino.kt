package com.example.muaythaiapp.domain.training

data class Treino(
    val id: Long = 0L,
    val completedAtMillis: Long,
    val roundNumber: Int,
    val phase: TrainingPhase,
    val durationSeconds: Int,
    val completedRounds: Int,
)
