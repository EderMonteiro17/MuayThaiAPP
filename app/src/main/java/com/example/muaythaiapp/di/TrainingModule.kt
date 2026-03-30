package com.example.muaythaiapp.di

import android.content.Context
import androidx.room.Room
import com.example.muaythaiapp.data.mysql.DatabaseHelper
import com.example.muaythaiapp.data.mysql.MySqlTrainingRepository
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
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LocalRepository

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RemoteRepository

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

    @Provides
    @Singleton
    fun provideDatabaseHelper(): DatabaseHelper {
        return DatabaseHelper()
    }

    private const val MUAY_THAI_DATABASE_NAME = "muaythai.db"
}

@Module
@InstallIn(SingletonComponent::class)
abstract class TrainingRepositoryModule {

    @Binds
    @Singleton
    @LocalRepository
    abstract fun bindLocalTrainingRepository(
        repositoryImpl: TrainingRepositoryImpl,
    ): TrainingRepository

    @Binds
    @Singleton
    @RemoteRepository
    abstract fun bindRemoteTrainingRepository(
        repositoryImpl: MySqlTrainingRepository,
    ): TrainingRepository

    // Default binding (currently using Local/Room)
    @Binds
    @Singleton
    abstract fun bindDefaultTrainingRepository(
        repositoryImpl: TrainingRepositoryImpl,
    ): TrainingRepository
}
