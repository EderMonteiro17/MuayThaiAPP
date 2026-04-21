package com.example.muaythaiapp.onboarding.presentation

import com.example.muaythaiapp.domain.profile.ExperienceLevel
import com.example.muaythaiapp.domain.profile.FighterStance
import com.example.muaythaiapp.domain.profile.MovementLimitation
import com.example.muaythaiapp.domain.profile.PrimaryGoal
import com.example.muaythaiapp.domain.profile.UserProfile

enum class OnboardingStep {
    Hero,
    Stance,
    Experience,
    Goal,
    Limitations,
}

data class FighterProfileDraft(
    val stance: FighterStance? = null,
    val experienceLevel: ExperienceLevel? = null,
    val primaryGoal: PrimaryGoal? = null,
    val limitations: Set<MovementLimitation> = emptySet(),
    val limitationNotes: String = "",
) {
    fun canContinue(step: OnboardingStep): Boolean = when (step) {
        OnboardingStep.Hero -> true
        OnboardingStep.Stance -> stance != null
        OnboardingStep.Experience -> experienceLevel != null
        OnboardingStep.Goal -> primaryGoal != null
        OnboardingStep.Limitations -> true
    }

    fun toUserProfileOrNull(): UserProfile? {
        val stanceValue = stance ?: return null
        val experienceValue = experienceLevel ?: return null
        val primaryGoalValue = primaryGoal ?: return null

        return UserProfile(
            stance = stanceValue,
            experienceLevel = experienceValue,
            primaryGoal = primaryGoalValue,
            limitations = limitations,
            limitationNotes = limitationNotes.trim(),
        )
    }
}

data class OnboardingUiState(
    val draft: FighterProfileDraft = FighterProfileDraft(),
    val isSaving: Boolean = false,
    val completedProfile: UserProfile? = null,
)
