package com.example.muaythaiapp.data.training.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TreinoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(treino: TreinoEntity): Long

    @Query(
        """
        SELECT *
        FROM treinos
        ORDER BY completed_at_millis DESC, id DESC
        """
    )
    fun observeTreinos(): Flow<List<TreinoEntity>>

    @Query("DELETE FROM treinos")
    suspend fun clearTreinos()
}
