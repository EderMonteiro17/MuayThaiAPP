package com.example.muaythaiapp.di

import com.example.muaythaiapp.data.profile.PreferencesUserProfileRepository
import com.example.muaythaiapp.data.profile.UserProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileModule {

    @Binds
    @Singleton
    abstract fun bindUserProfileRepository(
        repository: PreferencesUserProfileRepository,
    ): UserProfileRepository
}
