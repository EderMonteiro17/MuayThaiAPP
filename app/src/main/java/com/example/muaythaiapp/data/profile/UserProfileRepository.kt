package com.example.muaythaiapp.data.profile

import com.example.muaythaiapp.domain.profile.UserProfile
import kotlinx.coroutines.flow.StateFlow

interface UserProfileRepository {
    val currentProfile: StateFlow<UserProfile?>

    suspend fun saveProfile(profile: UserProfile)
}
