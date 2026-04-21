package com.example.muaythaiapp.onboarding.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.muaythaiapp.data.profile.UserProfileRepository
import com.example.muaythaiapp.domain.profile.ExperienceLevel
import com.example.muaythaiapp.domain.profile.FighterStance
import com.example.muaythaiapp.domain.profile.MovementLimitation
import com.example.muaythaiapp.domain.profile.PrimaryGoal
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        OnboardingUiState(
            completedProfile = userProfileRepository.currentProfile.value,
        ),
    )
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun selectStance(stance: FighterStance) {
        _uiState.update { state -> state.copy(draft = state.draft.copy(stance = stance)) }
    }

    fun selectExperienceLevel(experienceLevel: ExperienceLevel) {
        _uiState.update { state -> state.copy(draft = state.draft.copy(experienceLevel = experienceLevel)) }
    }

    fun selectPrimaryGoal(primaryGoal: PrimaryGoal) {
        _uiState.update { state -> state.copy(draft = state.draft.copy(primaryGoal = primaryGoal)) }
    }

    fun toggleLimitation(limitation: MovementLimitation) {
        _uiState.update { state ->
            val updatedLimitations = state.draft.limitations.toMutableSet().apply {
                if (contains(limitation)) remove(limitation) else add(limitation)
            }

            state.copy(draft = state.draft.copy(limitations = updatedLimitations))
        }
    }

    fun updateLimitationNotes(notes: String) {
        _uiState.update { state -> state.copy(draft = state.draft.copy(limitationNotes = notes)) }
    }

    fun saveProfile() {
        val profile = _uiState.value.draft.toUserProfileOrNull() ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            userProfileRepository.saveProfile(profile)
            _uiState.update {
                it.copy(
                    isSaving = false,
                    completedProfile = profile,
                )
            }
        }
    }
}
