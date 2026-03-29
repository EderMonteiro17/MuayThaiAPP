package com.example.muaythaiapp.di

import android.content.Context
import androidx.room.Room
import com.example.muaythaiapp.data.training.TrainingRepositoryImpl
import com.example.muaythaiapp.data.training.local.MuayThaiDatabase
import com.example.muaythaiapp.data.training.local.TreinoDao
import com.example.muaythaiapp.domain.training.TrainingRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TrainingDatabaseModule {

    @Provides
    @Singleton
    fun provideMuayThaiDatabase(
        @ApplicationContext context: Context,
    ): MuayThaiDatabase {
        return Room.databaseBuilder(
            context,
            MuayThaiDatabase::class.java,
            MUAY_THAI_DATABASE_NAME,
        ).build()
    }

    @Provides
    fun provideTreinoDao(database: MuayThaiDatabase): TreinoDao = database.treinoDao()

    private const val MUAY_THAI_DATABASE_NAME = "muaythai.db"
}

@Module
@InstallIn(SingletonComponent::class)
abstract class TrainingRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTrainingRepository(
        repositoryImpl: TrainingRepositoryImpl,
    ): TrainingRepository
}
