package com.example.muaythaiapp.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.muaythaiapp.domain.profile.ExperienceLevel
import com.example.muaythaiapp.domain.profile.FighterStance
import com.example.muaythaiapp.domain.profile.MovementLimitation
import com.example.muaythaiapp.domain.profile.PrimaryGoal
import com.example.muaythaiapp.onboarding.presentation.FighterProfileDraft
import com.example.muaythaiapp.onboarding.presentation.OnboardingStep
import com.example.muaythaiapp.onboarding.presentation.OnboardingUiState
import com.example.muaythaiapp.ui.theme.BoneWhite
import com.example.muaythaiapp.ui.theme.CageBlack
import com.example.muaythaiapp.ui.theme.DeepCrimson
import com.example.muaythaiapp.ui.theme.MatteBlack
import com.example.muaythaiapp.ui.theme.MuayThaiAPPTheme
import com.example.muaythaiapp.ui.theme.SteelGray
import kotlinx.coroutines.launch

@Composable
fun FighterOnboardingScreen(
    uiState: OnboardingUiState,
    onStanceSelected: (FighterStance) -> Unit,
    onExperienceLevelSelected: (ExperienceLevel) -> Unit,
    onPrimaryGoalSelected: (PrimaryGoal) -> Unit,
    onLimitationToggle: (MovementLimitation) -> Unit,
    onLimitationNotesChanged: (String) -> Unit,
    onSaveProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val steps = OnboardingStep.entries
    val pagerState = rememberPagerState(pageCount = { steps.size })
    val scope = rememberCoroutineScope()
    val currentStep = steps[pagerState.currentPage]
    val progress by animateFloatAsState(
        targetValue = (pagerState.currentPage + 1) / steps.size.toFloat(),
        animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing),
        label = "onboardingProgress",
    )

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MatteBlack,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MatteBlack,
                            DeepCrimson.copy(alpha = 0.24f),
                            MatteBlack,
                        ),
                    ),
                )
                .padding(horizontal = 20.dp, vertical = 24.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                OnboardingTopBar(
                    page = pagerState.currentPage,
                    pageCount = steps.size,
                    progress = progress,
                )

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f),
                    userScrollEnabled = false,
                ) { page ->
                    val step = steps[page]

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                    ) {
                        when (step) {
                            OnboardingStep.Hero -> HeroPage()
                            OnboardingStep.Stance -> StancePage(
                                selectedStance = uiState.draft.stance,
                                onStanceSelected = onStanceSelected,
                            )
                            OnboardingStep.Experience -> ExperiencePage(
                                selectedExperienceLevel = uiState.draft.experienceLevel,
                                onExperienceLevelSelected = onExperienceLevelSelected,
                            )
                            OnboardingStep.Goal -> GoalPage(
                                selectedPrimaryGoal = uiState.draft.primaryGoal,
                                onPrimaryGoalSelected = onPrimaryGoalSelected,
                            )
                            OnboardingStep.Limitations -> LimitationsPage(
                                draft = uiState.draft,
                                isSaving = uiState.isSaving,
                                onLimitationToggle = onLimitationToggle,
                                onLimitationNotesChanged = onLimitationNotesChanged,
                            )
                        }
                    }
                }

                OnboardingFooter(
                    currentStep = currentStep,
                    draft = uiState.draft,
                    isSaving = uiState.isSaving,
                    onBackClick = {
                        if (pagerState.currentPage > 0) {
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                        }
                    },
                    onPrimaryClick = {
                        if (pagerState.currentPage == steps.lastIndex) {
                            onSaveProfile()
                        } else {
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun OnboardingTopBar(
    page: Int,
    pageCount: Int,
    progress: Float,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "NAKA RED",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.8.sp,
                ),
                color = BoneWhite,
            )
            Text(
                text = "${page + 1}/$pageCount",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.4.sp,
                ),
                color = SteelGray,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(CageBlack, RoundedCornerShape(999.dp)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                    .height(8.dp)
                    .background(DeepCrimson, RoundedCornerShape(999.dp)),
            )
        }
    }
}

@Composable
private fun HeroPage() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        StepKicker(text = "FIGHTER ONBOARDING")
        Text(
            text = "TRAIN LIKE A\nNAK MUAY.",
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.Black,
                fontSize = 62.sp,
                lineHeight = 58.sp,
                letterSpacing = (-2).sp,
            ),
            color = BoneWhite,
        )
        Text(
            text = "Set your stance, experience, and target pace before the first bell. The app should adapt to the fighter, not the other way around.",
            style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 28.sp),
            color = BoneWhite.copy(alpha = 0.84f),
        )
        TagRow(tags = listOf("ROUND TIMER", "LEVEL SYSTEM", "BURNOUT MODE"))
        HeroPanel(
            title = "WHAT THIS SETS",
            body = "Stance drives mirrored drill presentation. Experience seeds the library filter. Primary goal changes the burnout window. Limitations mark exercises for exclusion before you build bad habits.",
        )
    }
}

@Composable
private fun StancePage(
    selectedStance: FighterStance?,
    onStanceSelected: (FighterStance) -> Unit,
) {
    QuestionPage(
        kicker = "STEP 01",
        title = "WHICH SIDE LEADS?",
        body = "Match the app to the way you stand so the drill previews and callouts stop lying to you.",
    ) {
        FighterStance.entries.forEach { stance ->
            SelectionCard(
                title = stance.title.uppercase(),
                subtitle = stance.subtitle,
                selected = stance == selectedStance,
                onClick = { onStanceSelected(stance) },
            )
        }
    }
}

@Composable
private fun ExperiencePage(
    selectedExperienceLevel: ExperienceLevel?,
    onExperienceLevelSelected: (ExperienceLevel) -> Unit,
) {
    QuestionPage(
        kicker = "STEP 02",
        title = "HOW SHARP ARE YOU?",
        body = "This becomes the default Level Library filter. If you lie here, the app will feed you the wrong work.",
    ) {
        ExperienceLevel.entries.forEach { experienceLevel ->
            SelectionCard(
                title = experienceLevel.title.uppercase(),
                subtitle = experienceLevel.description,
                supportingText = "Library filter: ${experienceLevel.levelLibraryFilter}",
                selected = experienceLevel == selectedExperienceLevel,
                onClick = { onExperienceLevelSelected(experienceLevel) },
            )
        }
    }
}

@Composable
private fun GoalPage(
    selectedPrimaryGoal: PrimaryGoal?,
    onPrimaryGoalSelected: (PrimaryGoal) -> Unit,
) {
    QuestionPage(
        kicker = "STEP 03",
        title = "WHAT IS THE MISSION?",
        body = "Primary goal tunes the burnout window. Fight prep stays in the red longer. Technique cuts the fade before slop starts.",
    ) {
        PrimaryGoal.entries.forEach { primaryGoal ->
            SelectionCard(
                title = primaryGoal.title.uppercase(),
                subtitle = primaryGoal.description,
                supportingText = "Burnout mode: ${primaryGoal.burnoutThresholdSeconds}s",
                selected = primaryGoal == selectedPrimaryGoal,
                onClick = { onPrimaryGoalSelected(primaryGoal) },
            )
        }
    }
}

@Composable
private fun LimitationsPage(
    draft: FighterProfileDraft,
    isSaving: Boolean,
    onLimitationToggle: (MovementLimitation) -> Unit,
    onLimitationNotesChanged: (String) -> Unit,
) {
    QuestionPage(
        kicker = "STEP 04",
        title = "WHAT DO WE PROTECT?",
        body = "Flag movement limits before the app starts suggesting the wrong exercises. Discipline starts with honest constraints.",
    ) {
        Text(
            text = "Quick filters",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
            color = BoneWhite,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            LimitationsColumn(
                modifier = Modifier.weight(1f),
                draft = draft,
                items = listOf(MovementLimitation.NoJumping, MovementLimitation.BadKnees),
                onLimitationToggle = onLimitationToggle,
            )
            LimitationsColumn(
                modifier = Modifier.weight(1f),
                draft = draft,
                items = listOf(MovementLimitation.ShoulderCare, MovementLimitation.WristCare),
                onLimitationToggle = onLimitationToggle,
            )
        }

        OutlinedTextField(
            value = draft.limitationNotes,
            onValueChange = onLimitationNotesChanged,
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            label = {
                Text(
                    text = "Notes for exercise filters",
                    color = SteelGray,
                )
            },
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = BoneWhite),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = CageBlack,
                unfocusedContainerColor = CageBlack,
                focusedTextColor = BoneWhite,
                unfocusedTextColor = BoneWhite,
                focusedIndicatorColor = DeepCrimson,
                unfocusedIndicatorColor = SteelGray.copy(alpha = 0.34f),
                focusedLabelColor = DeepCrimson,
                unfocusedLabelColor = SteelGray,
                cursorColor = DeepCrimson,
            ),
        )

        SummaryCard(draft = draft, isSaving = isSaving)
    }
}

@Composable
private fun LimitationsColumn(
    draft: FighterProfileDraft,
    items: List<MovementLimitation>,
    onLimitationToggle: (MovementLimitation) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items.forEach { limitation ->
            FilterChip(
                selected = limitation in draft.limitations,
                onClick = { onLimitationToggle(limitation) },
                label = {
                    Text(
                        text = limitation.title,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = DeepCrimson,
                    selectedLabelColor = BoneWhite,
                    containerColor = CageBlack,
                    labelColor = BoneWhite,
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = limitation in draft.limitations,
                    borderColor = SteelGray.copy(alpha = 0.28f),
                    selectedBorderColor = DeepCrimson,
                ),
            )
        }
    }
}

@Composable
private fun SummaryCard(
    draft: FighterProfileDraft,
    isSaving: Boolean,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CageBlack.copy(alpha = 0.94f)),
        border = BorderStroke(1.dp, DeepCrimson.copy(alpha = 0.45f)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "PROFILE LOCK",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.6.sp,
                ),
                color = DeepCrimson,
            )
            SummaryRow(label = "Stance", value = draft.stance?.title ?: "Choose one")
            SummaryRow(label = "Level", value = draft.experienceLevel?.title ?: "Choose one")
            SummaryRow(label = "Goal", value = draft.primaryGoal?.title ?: "Choose one")
            SummaryRow(
                label = "Burnout",
                value = draft.primaryGoal?.burnoutThresholdSeconds?.let { "$it seconds" } ?: "Pending",
            )
            AnimatedContent(targetState = isSaving, label = "savingState") { saving ->
                Text(
                    text = if (saving) "Saving fighter profile..." else "Ready to enter the timer with your rules locked in.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BoneWhite.copy(alpha = 0.78f),
                )
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
            ),
            color = SteelGray,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.ExtraBold),
            color = BoneWhite,
        )
    }
}

@Composable
private fun QuestionPage(
    kicker: String,
    title: String,
    body: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        StepKicker(text = kicker)
        Text(
            text = title,
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.Black,
                fontSize = 50.sp,
                lineHeight = 50.sp,
                letterSpacing = (-1.6).sp,
            ),
            color = BoneWhite,
        )
        Text(
            text = body,
            style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 28.sp),
            color = BoneWhite.copy(alpha = 0.84f),
        )
        content()
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun SelectionCard(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
    supportingText: String? = null,
) {
    val borderColor = if (selected) DeepCrimson else SteelGray.copy(alpha = 0.24f)
    val containerColor = if (selected) DeepCrimson.copy(alpha = 0.16f) else CageBlack.copy(alpha = 0.92f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = borderColor,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.1.sp,
                    ),
                    color = BoneWhite,
                )
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(
                            color = if (selected) DeepCrimson else Color.Transparent,
                            shape = CircleShape,
                        )
                        .border(
                            width = 1.dp,
                            color = if (selected) DeepCrimson else SteelGray.copy(alpha = 0.46f),
                            shape = CircleShape,
                        ),
                )
            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = BoneWhite.copy(alpha = 0.86f),
            )
            supportingText?.let {
                Text(
                    text = it.uppercase(),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                    ),
                    color = SteelGray,
                )
            }
        }
    }
}

@Composable
private fun StepKicker(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 2.sp,
        ),
        color = DeepCrimson,
    )
}

@Composable
private fun TagRow(tags: List<String>) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        tags.forEach { tag ->
            Box(
                modifier = Modifier
                    .background(CageBlack, RoundedCornerShape(999.dp))
                    .border(1.dp, SteelGray.copy(alpha = 0.24f), RoundedCornerShape(999.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
            ) {
                Text(
                    text = tag,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.1.sp,
                    ),
                    color = BoneWhite,
                )
            }
        }
    }
}

@Composable
private fun HeroPanel(
    title: String,
    body: String,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CageBlack.copy(alpha = 0.94f)),
        border = BorderStroke(1.dp, SteelGray.copy(alpha = 0.2f)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp,
                ),
                color = BoneWhite,
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 26.sp),
                color = BoneWhite.copy(alpha = 0.82f),
            )
        }
    }
}

@Composable
private fun OnboardingFooter(
    currentStep: OnboardingStep,
    draft: FighterProfileDraft,
    isSaving: Boolean,
    onBackClick: () -> Unit,
    onPrimaryClick: () -> Unit,
) {
    val canContinue = draft.canContinue(currentStep)
    val primaryLabel = if (currentStep == OnboardingStep.Limitations) "ENTER THE RED" else "NEXT STEP"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        OutlinedButton(
            onClick = onBackClick,
            modifier = Modifier
                .weight(1f)
                .height(60.dp),
            enabled = currentStep != OnboardingStep.Hero && !isSaving,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = BoneWhite),
            border = BorderStroke(1.dp, SteelGray.copy(alpha = 0.28f)),
        ) {
            Text(
                text = "BACK",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                ),
            )
        }

        Button(
            onClick = onPrimaryClick,
            modifier = Modifier
                .weight(1.35f)
                .height(60.dp),
            enabled = canContinue && !isSaving,
            colors = ButtonDefaults.buttonColors(
                containerColor = DeepCrimson,
                contentColor = BoneWhite,
                disabledContainerColor = DeepCrimson.copy(alpha = 0.3f),
            ),
        ) {
            Text(
                text = primaryLabel,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                ),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FighterOnboardingScreenPreview() {
    MuayThaiAPPTheme(darkTheme = true, dynamicColor = false) {
        FighterOnboardingScreen(
            uiState = OnboardingUiState(),
            onStanceSelected = {},
            onExperienceLevelSelected = {},
            onPrimaryGoalSelected = {},
            onLimitationToggle = {},
            onLimitationNotesChanged = {},
            onSaveProfile = {},
        )
    }
}
