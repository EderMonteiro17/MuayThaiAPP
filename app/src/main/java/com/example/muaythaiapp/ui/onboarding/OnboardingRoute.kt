package com.example.muaythaiapp.ui.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.muaythaiapp.domain.profile.ExperienceLevel
import com.example.muaythaiapp.domain.profile.FighterStance
import com.example.muaythaiapp.domain.profile.MovementLimitation
import com.example.muaythaiapp.domain.profile.PrimaryGoal
import com.example.muaythaiapp.onboarding.presentation.OnboardingUiState

@Composable
fun OnboardingRoute(
    uiState: OnboardingUiState,
    onStanceSelected: (FighterStance) -> Unit,
    onExperienceLevelSelected: (ExperienceLevel) -> Unit,
    onPrimaryGoalSelected: (PrimaryGoal) -> Unit,
    onLimitationToggle: (MovementLimitation) -> Unit,
    onLimitationNotesChanged: (String) -> Unit,
    onSaveProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FighterOnboardingScreen(
        uiState = uiState,
        onStanceSelected = onStanceSelected,
        onExperienceLevelSelected = onExperienceLevelSelected,
        onPrimaryGoalSelected = onPrimaryGoalSelected,
        onLimitationToggle = onLimitationToggle,
        onLimitationNotesChanged = onLimitationNotesChanged,
        onSaveProfile = onSaveProfile,
        modifier = modifier,
    )
}
