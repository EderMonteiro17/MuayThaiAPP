package com.example.muaythaiapp.data.training.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [TreinoEntity::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(TrainingPhaseConverter::class)
abstract class MuayThaiDatabase : RoomDatabase() {
    abstract fun treinoDao(): TreinoDao
}
