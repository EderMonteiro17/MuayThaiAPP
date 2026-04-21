package com.example.muaythaiapp.data.profile.remote

import com.example.muaythaiapp.domain.profile.UserProfile

data class UserProfilePayload(
    val stance: String,
    val experienceLevel: String,
    val primaryGoal: String,
    val levelLibraryFilter: String,
    val burnoutThresholdSeconds: Int,
    val exerciseExclusions: List<String>,
    val limitationNotes: String,
)

fun UserProfile.toPayload(): UserProfilePayload = UserProfilePayload(
    stance = stance.name.lowercase(),
    experienceLevel = experienceLevel.name.lowercase(),
    primaryGoal = primaryGoal.name.lowercase(),
    levelLibraryFilter = levelLibraryFilter,
    burnoutThresholdSeconds = primaryGoal.burnoutThresholdSeconds,
    exerciseExclusions = excludedExerciseTags.toList(),
    limitationNotes = limitationNotes,
)
