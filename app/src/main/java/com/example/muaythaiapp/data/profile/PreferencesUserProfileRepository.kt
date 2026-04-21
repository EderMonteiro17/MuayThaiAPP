package com.example.muaythaiapp.data.profile

import android.content.Context
import com.example.muaythaiapp.domain.profile.ExperienceLevel
import com.example.muaythaiapp.domain.profile.FighterStance
import com.example.muaythaiapp.domain.profile.MovementLimitation
import com.example.muaythaiapp.domain.profile.PrimaryGoal
import com.example.muaythaiapp.domain.profile.UserProfile
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class PreferencesUserProfileRepository @Inject constructor(
    @ApplicationContext context: Context,
) : UserProfileRepository {

    private val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val profileState = MutableStateFlow(loadProfile())

    override val currentProfile: StateFlow<UserProfile?> = profileState.asStateFlow()

    override suspend fun saveProfile(profile: UserProfile) {
        sharedPreferences.edit()
            .putString(KEY_STANCE, profile.stance.name)
            .putString(KEY_EXPERIENCE, profile.experienceLevel.name)
            .putString(KEY_PRIMARY_GOAL, profile.primaryGoal.name)
            .putStringSet(KEY_LIMITATIONS, profile.limitations.mapTo(linkedSetOf()) { it.name })
            .putString(KEY_LIMITATION_NOTES, profile.limitationNotes)
            .apply()

        profileState.value = profile
    }

    private fun loadProfile(): UserProfile? {
        val stance = sharedPreferences.getString(KEY_STANCE, null)?.toEnumOrNull<FighterStance>() ?: return null
        val experience = sharedPreferences.getString(KEY_EXPERIENCE, null)?.toEnumOrNull<ExperienceLevel>() ?: return null
        val primaryGoal = sharedPreferences.getString(KEY_PRIMARY_GOAL, null)?.toEnumOrNull<PrimaryGoal>() ?: return null
        val limitations = sharedPreferences
            .getStringSet(KEY_LIMITATIONS, emptySet())
            .orEmpty()
            .mapNotNullTo(linkedSetOf()) { it.toEnumOrNull<MovementLimitation>() }

        return UserProfile(
            stance = stance,
            experienceLevel = experience,
            primaryGoal = primaryGoal,
            limitations = limitations,
            limitationNotes = sharedPreferences.getString(KEY_LIMITATION_NOTES, "").orEmpty(),
        )
    }

    private inline fun <reified T : Enum<T>> String.toEnumOrNull(): T? {
        return enumValues<T>().firstOrNull { it.name == this }
    }

    private companion object {
        const val PREFERENCES_NAME = "fighter_profile_preferences"
        const val KEY_STANCE = "stance"
        const val KEY_EXPERIENCE = "experience_level"
        const val KEY_PRIMARY_GOAL = "primary_goal"
        const val KEY_LIMITATIONS = "limitations"
        const val KEY_LIMITATION_NOTES = "limitation_notes"
    }
}
