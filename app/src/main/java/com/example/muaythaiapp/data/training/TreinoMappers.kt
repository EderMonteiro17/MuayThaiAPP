package com.example.muaythaiapp.data.training

import com.example.muaythaiapp.data.training.local.TreinoEntity
import com.example.muaythaiapp.domain.training.Treino

internal fun TreinoEntity.toDomain(): Treino {
    return Treino(
        id = id,
        completedAtMillis = completedAtMillis,
        roundNumber = roundNumber,
        phase = phase,
        durationSeconds = durationSeconds,
        completedRounds = completedRounds,
    )
}

internal fun Treino.toEntity(): TreinoEntity {
    return TreinoEntity(
        id = id,
        completedAtMillis = completedAtMillis,
        roundNumber = roundNumber,
        phase = phase,
        durationSeconds = durationSeconds,
        completedRounds = completedRounds,
    )
}
