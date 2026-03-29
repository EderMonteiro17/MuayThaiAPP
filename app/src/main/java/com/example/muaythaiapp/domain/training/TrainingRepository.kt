package com.example.muaythaiapp.domain.training

import kotlinx.coroutines.flow.Flow

interface TrainingRepository {
    fun observeTreinos(): Flow<List<Treino>>

    suspend fun saveTreino(treino: Treino): Long
}
