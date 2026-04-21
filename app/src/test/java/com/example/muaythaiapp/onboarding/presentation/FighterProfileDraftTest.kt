package com.example.muaythaiapp.onboarding.presentation

import com.example.muaythaiapp.data.profile.remote.toPayload
import com.example.muaythaiapp.domain.profile.ExperienceLevel
import com.example.muaythaiapp.domain.profile.FighterStance
import com.example.muaythaiapp.domain.profile.MovementLimitation
import com.example.muaythaiapp.domain.profile.PrimaryGoal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FighterProfileDraftTest {

    @Test
    fun `cannot continue past unanswered stance step`() {
        val draft = FighterProfileDraft()

        assertFalse(draft.canContinue(OnboardingStep.Stance))
        assertTrue(draft.canContinue(OnboardingStep.Hero))
    }

    @Test
    fun `draft builds user profile once required answers exist`() {
        val draft = FighterProfileDraft(
            stance = FighterStance.Southpaw,
            experienceLevel = ExperienceLevel.Advanced,
            primaryGoal = PrimaryGoal.FightPrep,
            limitations = setOf(MovementLimitation.BadKnees),
            limitationNotes = "Keep jump knees out",
        )

        val profile = draft.toUserProfileOrNull()

        assertNotNull(profile)
        assertEquals("advanced", profile?.levelLibraryFilter)
        assertEquals(45, profile?.primaryGoal?.burnoutThresholdSeconds)
        assertTrue(profile?.excludedExerciseTags?.contains("bad_knees") == true)
    }

    @Test
    fun `payload mapping keeps backend contract aligned`() {
        val payload = FighterProfileDraft(
            stance = FighterStance.Orthodox,
            experienceLevel = ExperienceLevel.Medium,
            primaryGoal = PrimaryGoal.Cardio,
            limitations = linkedSetOf(MovementLimitation.NoJumping, MovementLimitation.WristCare),
            limitationNotes = "Favor shadow work",
        ).toUserProfileOrNull()?.toPayload()

        assertEquals("orthodox", payload?.stance)
        assertEquals("medium", payload?.experienceLevel)
        assertEquals("cardio", payload?.primaryGoal)
        assertEquals(30, payload?.burnoutThresholdSeconds)
        assertEquals(listOf("no_jumping", "wrist_care"), payload?.exerciseExclusions)
    }
}
