package com.example.muaythaiapp.data.training.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.muaythaiapp.domain.training.TrainingPhase

@Entity(tableName = "treinos")
data class TreinoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "completed_at_millis")
    val completedAtMillis: Long,
    @ColumnInfo(name = "round_number")
    val roundNumber: Int,
    @ColumnInfo(name = "phase")
    val phase: TrainingPhase,
    @ColumnInfo(name = "duration_seconds")
    val durationSeconds: Int,
    @ColumnInfo(name = "completed_rounds")
    val completedRounds: Int,
)
