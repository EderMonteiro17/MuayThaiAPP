package com.example.muaythaiapp.domain.profile

enum class FighterStance(
    val title: String,
    val subtitle: String,
) {
    Orthodox(
        title = "Orthodox",
        subtitle = "Lead left. Cross hand loaded.",
    ),
    Southpaw(
        title = "Southpaw",
        subtitle = "Lead right. Rear kick angle changed.",
    ),
}

enum class ExperienceLevel(
    val title: String,
    val description: String,
    val levelLibraryFilter: String,
) {
    Beginner(
        title = "Beginner",
        description = "Stance, jab, cross, teep. Build clean mechanics first.",
        levelLibraryFilter = "beginner",
    ),
    Medium(
        title = "Medium",
        description = "Combinations, checks, catching, and cleaner exits.",
        levelLibraryFilter = "medium",
    ),
    Advanced(
        title = "Advanced",
        description = "Clinch, counters, pace changes, and fight-ready rounds.",
        levelLibraryFilter = "advanced",
    ),
}

enum class PrimaryGoal(
    val title: String,
    val description: String,
    val burnoutThresholdSeconds: Int,
) {
    Cardio(
        title = "Cardio",
        description = "Keep output high without stretching burnout too long.",
        burnoutThresholdSeconds = 30,
    ),
    Technique(
        title = "Technique",
        description = "Shorter burnout so form does not break under fatigue.",
        burnoutThresholdSeconds = 20,
    ),
    FightPrep(
        title = "Fight Prep",
        description = "Long red-zone finish for late-round composure.",
        burnoutThresholdSeconds = 45,
    ),
}

enum class MovementLimitation(
    val title: String,
    val backendValue: String,
) {
    NoJumping(
        title = "No jumping",
        backendValue = "no_jumping",
    ),
    BadKnees(
        title = "Bad knees",
        backendValue = "bad_knees",
    ),
    ShoulderCare(
        title = "Shoulder care",
        backendValue = "shoulder_care",
    ),
    WristCare(
        title = "Wrist care",
        backendValue = "wrist_care",
    ),
}

data class UserProfile(
    val stance: FighterStance,
    val experienceLevel: ExperienceLevel,
    val primaryGoal: PrimaryGoal,
    val limitations: Set<MovementLimitation> = emptySet(),
    val limitationNotes: String = "",
) {
    val levelLibraryFilter: String
        get() = experienceLevel.levelLibraryFilter

    val excludedExerciseTags: Set<String>
        get() = limitations.mapTo(linkedSetOf()) { it.backendValue }
}
