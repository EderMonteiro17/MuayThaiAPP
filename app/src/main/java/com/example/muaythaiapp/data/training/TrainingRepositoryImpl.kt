package com.example.muaythaiapp.data.training

import com.example.muaythaiapp.data.training.local.TreinoDao
import com.example.muaythaiapp.domain.training.TrainingRepository
import com.example.muaythaiapp.domain.training.Treino
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrainingRepositoryImpl @Inject constructor(
    private val treinoDao: TreinoDao,
) : TrainingRepository {
    override fun observeTreinos(): Flow<List<Treino>> {
        return treinoDao.observeTreinos().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveTreino(treino: Treino): Long {
        return treinoDao.insert(treino.toEntity())
    }
}
