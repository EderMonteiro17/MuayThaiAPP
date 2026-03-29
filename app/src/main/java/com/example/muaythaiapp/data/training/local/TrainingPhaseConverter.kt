package com.example.muaythaiapp.data.training.local

import androidx.room.TypeConverter
import com.example.muaythaiapp.domain.training.TrainingPhase

class TrainingPhaseConverter {
    @TypeConverter
    fun fromTrainingPhase(phase: TrainingPhase): String = phase.name

    @TypeConverter
    fun toTrainingPhase(value: String): TrainingPhase {
        return runCatching { TrainingPhase.valueOf(value) }
            .getOrDefault(TrainingPhase.ROUND)
    }
}
