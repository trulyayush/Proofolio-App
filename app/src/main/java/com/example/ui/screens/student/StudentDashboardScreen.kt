package com.example.ui.screens.student

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SessionStore
import com.example.data.api.ApiResult
import com.example.data.api.ClaimedVsVerified
import com.example.data.api.HackathonItem
import com.example.data.api.MatchItem
import com.example.data.api.OkResponse
import com.example.data.api.ReportResponse
import com.example.data.api.ReposResponse
import com.example.data.api.VivaInProgress
import com.example.ui.components.InteractiveRadarChart
import com.example.ui.components.NexusButton
import com.example.ui.components.NexusButtonVariant
import com.example.ui.components.NexusCard
import com.example.ui.components.NexusEmptyState
import com.example.ui.components.ScoreBadge
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentAmberLight
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentEmeraldLight
import com.example.ui.theme.AccentRose
import com.example.ui.theme.AccentRoseLight
import com.example.ui.theme.NeutralWarmSurfaceVariant
import com.example.ui.theme.NexusTokens
import com.example.ui.theme.TextTertiaryMuted
import com.example.viewmodel.Load
import com.example.viewmodel.AppViewModel
import com.example.data.*
import androidx.compose.material3.Switch
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import kotlinx.coroutines.launch

// Helper for skill tag display names according to PROJECT_RULES.md
fun formatSkillTag(tag: String): String = when (tag.lowercase()) {
    "backend" -> "Backend"
    "frontend" -> "Frontend"
    "data" -> "Data"
    "ai_ml" -> "AI/ML"
    "devops" -> "DevOps"
    "security" -> "Security"
    else -> tag.replace('_', ' ').replaceFirstChar { it.uppercase() }
}

@Composable
fun VerifiedLevelBadge(
    level: String,
    modifier: Modifier = Modifier
) {
    // Exact colors and labels from PROJECT_RULES.md lines 40-44:
    // strong = "Strong" (AccentEmerald on AccentEmeraldLight)
    // partial = "Partial" (AccentAmber on AccentAmberLight)
    // not_verified = "Not verified" (AccentRose on AccentRoseLight)
    // not_shown = "Not shown" (TextTertiaryMuted on NeutralWarmSurfaceVariant)
    val (label, textCol, bgCol) = when (level.lowercase()) {
        "strong" -> Triple("Strong", AccentEmerald, AccentEmeraldLight)
        "partial" -> Triple("Partial", AccentAmber, AccentAmberLight)
        "not_verified" -> Triple("Not verified", AccentRose, AccentRoseLight)
        "not_shown" -> Triple("Not shown", TextTertiaryMuted, NeutralWarmSurfaceVariant)
        else -> Triple(level, TextTertiaryMuted, NeutralWarmSurfaceVariant)
    }

    Surface(
        color = bgCol,
        shape = RoundedCornerShape(NexusTokens.Radius.pill),
        border = BorderStroke(1.dp, textCol.copy(alpha = 0.25f)),
        modifier = modifier
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = textCol,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun StudentDashboardScreen(
    reportState: Load<ReportResponse>,
    sessionStore: SessionStore,
    applyingIds: Set<String>,
    reposState: Load<ReposResponse> = Load.Idle,
    isDemo: Boolean = false,
    demoView: AppViewModel.DemoView = AppViewModel.DemoView.STUDENT,
    onSetDemoView: (AppViewModel.DemoView) -> Unit = {},
    demoState: DemoState? = null,
    onNavigateDemo: (DemoScreen, Int) -> Unit = { _, _ -> },
    onNextDemoStep: () -> Unit = {},
    onPrevDemoStep: () -> Unit = {},
    onSelectStudent: (String) -> Unit = {},
    onSelectProject: (String) -> Unit = {},
    onAnswerQuestion: (String, String) -> Unit = { _, _ -> },
    onSkipQuestion: (String) -> Unit = {},
    onToggleShortlist: (String) -> Unit = {},
    onShareFeedback: (String, Boolean) -> Unit = { _, _ -> },
    onUpdateFilters: (Map<String, List<String>>) -> Unit = {},
    onLoadRepos: () -> Unit = {},
    resumeVivaError: String? = null,
    onClearResumeVivaError: () -> Unit = {},
    onRefresh: () -> Unit,
    onVerifyRepo: () -> Unit,
    onVerifyHackathonRepo: (String) -> Unit = {},
    onContinueViva: (sessionId: String) -> Unit,
    onViewReport: () -> Unit,
    onApply: (String) -> Unit,
    onAddHackathon: suspend (
        event: String,
        year: Int,
        result: String,
        teamSize: Int,
        repoFullName: String
    ) -> ApiResult<HackathonItem> = { _, _, _, _, _ -> ApiResult.Err("NOT_IMPL", "", null) },
    onDeleteHackathon: suspend (hackathonId: String) -> ApiResult<OkResponse> = { ApiResult.Err("NOT_IMPL", "", null) },
    onSignOut: () -> Unit,
    onExitDemo: () -> Unit = {},
    onResetDemo: () -> Unit = {}
) {
    LaunchedEffect(sessionStore.studentId) {
        if (reportState is Load.Idle) {
            onRefresh()
        }
    }

    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    var showAddHackathonSheet by remember { mutableStateOf(false) }
    var hackathonPendingRemoval by remember { mutableStateOf<HackathonItem?>(null) }
    var isRemovingHackathon by remember { mutableStateOf(false) }
    var removeErrorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NexusTokens.Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("student_dashboard_screen")
    ) {
        // --- Persistent Judges Demo Mode Banner & View Switcher ---
        if (isDemo) {
            Surface(
                color = Color(0xFFFFFBEB),
                border = BorderStroke(1.dp, Color(0xFFFDE68A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFD97706))
                            )
                            Text(
                                text = "DEMO MODE ACTIVE",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFF92400E)
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = onResetDemo,
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text(
                                    text = "Reset",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFB45309)
                                )
                            }
                            TextButton(
                                onClick = onExitDemo,
                                modifier = Modifier
                                    .height(30.dp)
                                    .testTag("exit_demo_banner_button")
                            ) {
                                Text(
                                    text = "Exit Demo",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF92400E)
                                )
                            }
                        }
                    }

                    // View Switcher Tabs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DemoTabButton(
                            title = "Student View",
                            selected = demoView == AppViewModel.DemoView.STUDENT,
                            onClick = { onSetDemoView(AppViewModel.DemoView.STUDENT) },
                            modifier = Modifier.weight(1f)
                        )
                        DemoTabButton(
                            title = "Admin Dashboard",
                            selected = demoView == AppViewModel.DemoView.ADMIN,
                            onClick = { onSetDemoView(AppViewModel.DemoView.ADMIN) },
                            modifier = Modifier.weight(1f)
                        )
                        DemoTabButton(
                            title = "Recruiter Portal",
                            selected = demoView == AppViewModel.DemoView.RECRUITER,
                            onClick = { onSetDemoView(AppViewModel.DemoView.RECRUITER) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Switch based on demoView if in demo mode, otherwise show student dashboard
        if (isDemo && demoView == AppViewModel.DemoView.ADMIN) {
            AdminDashboardDemoView(demoState = demoState, onToggleShortlist = onToggleShortlist)
        } else if (isDemo && demoView == AppViewModel.DemoView.RECRUITER) {
            RecruiterDashboardDemoView(demoState = demoState)
        } else if (isDemo) {
            val state = demoState ?: DemoFixtures.createInitialState(DemoMode.GUIDED)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Journey Stepper Header
                DemoJourneyHeader(
                    currentScreen = state.currentScreen,
                    currentStep = state.currentStep,
                    onNavigate = onNavigateDemo,
                    onPrev = onPrevDemoStep,
                    onNext = onNextDemoStep
                )

                // Isolation & Simulated Data Banner
                Surface(
                    color = Color(0xFFF4F3F0),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFE7E5E0))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF059669),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Demo Mode: Data is simulated, isolated, and will not affect production records.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF3F3F46)
                        )
                    }
                }

                // Render current screen of the 7-step journey
                when (state.currentScreen) {
                    DemoScreen.STUDENT_PROFILE -> DemoStudentProfileView(state, onSelectStudent)
                    DemoScreen.REPOSITORY -> DemoRepositoryView(state, onSelectProject)
                    DemoScreen.INTERVIEW -> DemoInterviewView(state, onAnswerQuestion, onSkipQuestion)
                    DemoScreen.ASSESSMENT -> DemoAssessmentReportView(state)
                    DemoScreen.ADMIN_DASHBOARD -> AdminDashboardDemoView(demoState = demoState, onToggleShortlist = onToggleShortlist)
                    DemoScreen.FEEDBACK -> DemoFeedbackView(state, onShareFeedback)
                    DemoScreen.RECRUITER_DASHBOARD -> RecruiterDashboardDemoView(demoState = demoState)
                    else -> DemoStudentProfileView(state, onSelectStudent)
                }
            }
        } else {
        Surface(
            color = NexusTokens.Surface,
            border = BorderStroke(1.dp, NexusTokens.Border)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Student Identity
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = RoundedCornerShape(NexusTokens.Radius.md),
                        color = NexusTokens.SurfaceSecondary,
                        border = BorderStroke(1.dp, NexusTokens.Border),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            val initials = (sessionStore.studentName ?: "Student")
                                .split(" ")
                                .mapNotNull { it.firstOrNull()?.toString() }
                                .take(2)
                                .joinToString("")
                                .uppercase()
                            Text(
                                text = initials.ifEmpty { "VB" },
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = NexusTokens.Accent
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = sessionStore.studentName ?: "Student",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NexusTokens.Foreground
                        )
                        val ghName = sessionStore.githubUsername
                        if (ghName != null) {
                            Text(
                                text = "@$ghName",
                                style = MaterialTheme.typography.bodySmall,
                                color = NexusTokens.ForegroundSecondary
                            )
                        }
                    }
                }

                // Action controls
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onRefresh,
                        modifier = Modifier.testTag("dashboard_refresh_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh report",
                            tint = NexusTokens.ForegroundSecondary
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    NexusButton(
                        onClick = onSignOut,
                        variant = NexusButtonVariant.Outline,
                        icon = Icons.AutoMirrored.Filled.ExitToApp,
                        testTag = "dashboard_sign_out_button"
                    ) {
                        Text("Sign out")
                    }
                }
            }
        }

        // --- Content Area ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when (reportState) {
                is Load.Loading, is Load.Idle -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = NexusTokens.Accent,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Loading your report…",
                                style = MaterialTheme.typography.bodyMedium,
                                color = NexusTokens.ForegroundSecondary
                            )
                        }
                    }
                }

                is Load.Failed -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        NexusCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 500.dp),
                            backgroundColor = NexusTokens.Surface,
                            borderColor = NexusTokens.Border
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = NexusTokens.ErrorText,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = reportState.error.message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = NexusTokens.Foreground,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(18.dp))
                                NexusButton(
                                    onClick = onRefresh,
                                    variant = NexusButtonVariant.Primary,
                                    testTag = "dashboard_retry_button"
                                ) {
                                    Text("Retry")
                                }
                            }
                        }
                    }
                }

                is Load.Ready -> {
                    val report = reportState.value
                    val isDone = report.status == "done"

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(horizontal = 20.dp, vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 840.dp)
                        ) {
                            if (resumeVivaError != null) {
                                Surface(
                                    color = NexusTokens.ErrorBackground,
                                    shape = RoundedCornerShape(NexusTokens.Radius.sm),
                                    border = BorderStroke(1.dp, NexusTokens.ErrorBorder),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            modifier = Modifier.weight(1f),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ErrorOutline,
                                                contentDescription = null,
                                                tint = NexusTokens.ErrorText,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = resumeVivaError,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = NexusTokens.ErrorText
                                            )
                                        }
                                        IconButton(onClick = onClearResumeVivaError, modifier = Modifier.size(24.dp)) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Dismiss",
                                                tint = NexusTokens.ErrorText,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            if (!isDone) {
                                // Card B: Finish your viva (when vivaInProgress != null)
                                if (report.vivaInProgress != null) {
                                    CardBFinishViva(
                                        vivaInProgress = report.vivaInProgress,
                                        onContinue = { onContinueViva(report.vivaInProgress.sessionId) },
                                        smaller = false
                                    )
                                    Spacer(modifier = Modifier.height(24.dp))
                                } else if (report.repo == null) {
                                    // Card A: Verify repository (only when report.repo == null and vivaInProgress == null)
                                    CardAVerifyRepo(
                                        onPickRepo = onVerifyRepo
                                    )
                                    Spacer(modifier = Modifier.height(24.dp))
                                } else {
                                    CardAVerifyRepo(
                                        onPickRepo = onVerifyRepo
                                    )
                                    Spacer(modifier = Modifier.height(24.dp))
                                }

                                // Claimed Skills
                                if (report.claimedVsVerified.isNotEmpty()) {
                                    Text(
                                        text = "Claimed skills",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = NexusTokens.Foreground
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "These will be verified once you answer questions about your repository code.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = NexusTokens.ForegroundSecondary
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))

                                    FlowRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        report.claimedVsVerified.forEach { item ->
                                            Surface(
                                                color = NexusTokens.Surface,
                                                shape = RoundedCornerShape(NexusTokens.Radius.md),
                                                border = BorderStroke(1.dp, NexusTokens.Border)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = item.name,
                                                        style = MaterialTheme.typography.labelLarge,
                                                        fontWeight = FontWeight.Medium,
                                                        color = NexusTokens.Foreground
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    VerifiedLevelBadge(level = item.level)
                                                }
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(24.dp))
                                }
                            } else {
                                // --- State: Viva Completed (status == "done") ---

                                // 1. Overall Score Banner
                                NexusCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    backgroundColor = NexusTokens.Surface,
                                    borderColor = NexusTokens.Border
                                ) {
                                    Column(modifier = Modifier.padding(20.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Surface(
                                                        color = NexusTokens.SuccessBackground,
                                                        shape = RoundedCornerShape(NexusTokens.Radius.pill),
                                                        border = BorderStroke(1.dp, NexusTokens.SuccessBorder)
                                                    ) {
                                                        Row(
                                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(6.dp)
                                                                    .clip(CircleShape)
                                                                    .background(NexusTokens.SuccessText)
                                                            )
                                                            Spacer(modifier = Modifier.width(6.dp))
                                                            Text(
                                                                text = "Verification Complete",
                                                                style = MaterialTheme.typography.labelSmall,
                                                                fontWeight = FontWeight.SemiBold,
                                                                color = NexusTokens.SuccessText
                                                            )
                                                        }
                                                    }

                                                    if (report.repo != null) {
                                                        Spacer(modifier = Modifier.width(10.dp))
                                                        Text(
                                                            text = report.repo.fullName,
                                                            style = MaterialTheme.typography.labelLarge,
                                                            fontWeight = FontWeight.Medium,
                                                            color = NexusTokens.ForegroundSecondary
                                                        )
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(10.dp))

                                                Text(
                                                    text = "Technical Assessment Report",
                                                    style = MaterialTheme.typography.headlineSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = NexusTokens.Foreground
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = "Verified score based on repository analysis, build history, and code viva answers.",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = NexusTokens.ForegroundSecondary
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(16.dp))

                                            ScoreBadge(score = report.overall)
                                        }

                                        Spacer(modifier = Modifier.height(20.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            NexusButton(
                                                onClick = onVerifyRepo,
                                                variant = NexusButtonVariant.Secondary,
                                                icon = Icons.Default.Code,
                                                testTag = "dashboard_verify_another_button"
                                            ) {
                                                Text("Verify another repository")
                                            }

                                            NexusButton(
                                                onClick = onViewReport,
                                                variant = NexusButtonVariant.Outline,
                                                icon = Icons.Default.Description,
                                                testTag = "dashboard_view_report_button"
                                            ) {
                                                Text("View full report")
                                            }
                                        }
                                    }
                                }

                                if (report.vivaInProgress != null) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    CardBFinishViva(
                                        vivaInProgress = report.vivaInProgress,
                                        onContinue = { onContinueViva(report.vivaInProgress.sessionId) },
                                        smaller = true
                                    )
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                // 2. Interactive Radar Chart
                                if (report.skills.isNotEmpty()) {
                                    Text(
                                        text = "Verified Skill Dimensions",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = NexusTokens.Foreground
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Evaluated across your repository commits and code viva responses.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = NexusTokens.ForegroundSecondary
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))

                                    InteractiveRadarChart(skills = report.skills)

                                    Spacer(modifier = Modifier.height(24.dp))
                                }

                                // 3. Claimed vs Verified Skills
                                if (report.claimedVsVerified.isNotEmpty()) {
                                    Text(
                                        text = "Claimed vs Verified Skills",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = NexusTokens.Foreground
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Verification breakdown for each engineering capability.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = NexusTokens.ForegroundSecondary
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))

                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        report.claimedVsVerified.forEach { item ->
                                            Surface(
                                                color = NexusTokens.Surface,
                                                shape = RoundedCornerShape(NexusTokens.Radius.md),
                                                border = BorderStroke(1.dp, NexusTokens.Border),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Column {
                                                        Text(
                                                            text = item.name,
                                                            style = MaterialTheme.typography.titleSmall,
                                                            fontWeight = FontWeight.SemiBold,
                                                            color = NexusTokens.Foreground
                                                        )
                                                        Text(
                                                            text = formatSkillTag(item.tag),
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = NexusTokens.ForegroundTertiary
                                                        )
                                                    }

                                                    VerifiedLevelBadge(level = item.level)
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))
                                }

                                // 4. Build History (Neutral facts as per rule 15)
                                if (report.buildHistory != null) {
                                    val bh = report.buildHistory
                                    Text(
                                        text = "Build History",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = NexusTokens.Foreground
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Repository commit cadence and contribution telemetry.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = NexusTokens.ForegroundSecondary
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        BuildHistoryMetricCard(
                                            title = "Commits",
                                            value = "${bh.commits}",
                                            subtitle = "authored",
                                            modifier = Modifier.weight(1f)
                                        )
                                        BuildHistoryMetricCard(
                                            title = "Active Days",
                                            value = "${bh.activeDays}",
                                            subtitle = "${bh.spanDays}d span",
                                            modifier = Modifier.weight(1f)
                                        )
                                        BuildHistoryMetricCard(
                                            title = "Author Share",
                                            value = "${(bh.yourCommitShare * 100).toInt()}%",
                                            subtitle = "${bh.authors} authors",
                                            modifier = Modifier.weight(1f)
                                        )
                                        BuildHistoryMetricCard(
                                            title = "Median Lines",
                                            value = "${bh.medianLinesPerCommit}",
                                            subtitle = "per commit",
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))
                                }
                            }

                            // Your Hackathons section (built from report.hackathons)
                            YourHackathonsSection(
                                hackathons = report.hackathons,
                                onAddHackathon = { showAddHackathonSheet = true },
                                onVerifyHackathon = onVerifyHackathonRepo,
                                onRemoveHackathon = { item ->
                                    hackathonPendingRemoval = item
                                    removeErrorMessage = null
                                }
                            )

                            // 5. Matched Internships (Always shown if listings match)
                            if (report.matches.isNotEmpty()) {
                                Text(
                                    text = "Matched Internships",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = NexusTokens.Foreground
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Ventures looking for students with your verified skill profile.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NexusTokens.ForegroundSecondary
                                )
                                Spacer(modifier = Modifier.height(12.dp))

                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    report.matches.forEach { match ->
                                        InternshipMatchCard(
                                            match = match,
                                            isApplying = applyingIds.contains(match.listingId),
                                            onApply = { onApply(match.listingId) }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddHackathonSheet) {
        AddHackathonSheet(
            reposState = reposState,
            onLoadRepos = onLoadRepos,
            onSaveHackathon = onAddHackathon,
            onDismiss = { showAddHackathonSheet = false }
        )
    }

    if (hackathonPendingRemoval != null) {
        val item = hackathonPendingRemoval!!
        AlertDialog(
            onDismissRequest = {
                if (!isRemovingHackathon) {
                    hackathonPendingRemoval = null
                    removeErrorMessage = null
                }
            },
            title = {
                Text(
                    text = "Remove hackathon",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = NexusTokens.Foreground
                )
            },
            text = {
                Column {
                    Text(
                        text = "Remove ${item.event} ${item.year} from your profile?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NexusTokens.Foreground
                    )
                    if (removeErrorMessage != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = removeErrorMessage ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexusTokens.ErrorText
                        )
                    }
                }
            },
            confirmButton = {
                NexusButton(
                    onClick = {
                        isRemovingHackathon = true
                        removeErrorMessage = null
                        scope.launch {
                            val res = onDeleteHackathon(item.hackathonId)
                            isRemovingHackathon = false
                            when (res) {
                                is ApiResult.Ok -> {
                                    hackathonPendingRemoval = null
                                }
                                is ApiResult.Err -> {
                                    removeErrorMessage = res.message
                                }
                            }
                        }
                    },
                    enabled = !isRemovingHackathon,
                    variant = NexusButtonVariant.Primary,
                    testTag = "confirm_remove_hackathon_button"
                ) {
                    if (isRemovingHackathon) {
                        CircularProgressIndicator(
                            color = NexusTokens.OnAccent,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Removing…")
                    } else {
                        Text("Remove")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        hackathonPendingRemoval = null
                        removeErrorMessage = null
                    },
                    enabled = !isRemovingHackathon
                ) {
                    Text("Cancel", color = NexusTokens.ForegroundSecondary)
                }
            },
            containerColor = NexusTokens.Surface
        )
      }
    }
}

@Composable
private fun BuildHistoryMetricCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = NexusTokens.Surface,
        shape = RoundedCornerShape(NexusTokens.Radius.md),
        border = BorderStroke(1.dp, NexusTokens.Border),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = NexusTokens.ForegroundTertiary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = NexusTokens.Foreground
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = NexusTokens.ForegroundSecondary
            )
        }
    }
}

@Composable
private fun InternshipMatchCard(
    match: MatchItem,
    isApplying: Boolean,
    onApply: () -> Unit
) {
    NexusCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = NexusTokens.Surface,
        borderColor = NexusTokens.Border
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = match.venture,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = NexusTokens.Accent
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = match.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NexusTokens.Foreground
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                ScoreBadge(score = match.match, label = "Match")
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = match.why,
                style = MaterialTheme.typography.bodySmall,
                color = NexusTokens.ForegroundSecondary,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (match.applied) {
                    Surface(
                        color = NexusTokens.SuccessBackground,
                        shape = RoundedCornerShape(NexusTokens.Radius.pill),
                        border = BorderStroke(1.dp, NexusTokens.SuccessBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = NexusTokens.SuccessText,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Applied",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = NexusTokens.SuccessText
                            )
                        }
                    }
                } else {
                    NexusButton(
                        onClick = onApply,
                        enabled = !isApplying,
                        variant = NexusButtonVariant.Primary,
                        icon = Icons.Default.Work,
                        testTag = "dashboard_apply_button_${match.listingId}"
                    ) {
                        if (isApplying) {
                            CircularProgressIndicator(
                                color = NexusTokens.OnAccent,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Applying…")
                        } else {
                            Text("Apply to role")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CardBFinishViva(
    vivaInProgress: VivaInProgress,
    onContinue: () -> Unit,
    smaller: Boolean = false,
    modifier: Modifier = Modifier
) {
    val repoName = vivaInProgress.repoFullName?.substringAfterLast("/") ?: "Repository"
    val subtitleText = "$repoName · ${vivaInProgress.answered} of ${vivaInProgress.total} answered"

    if (smaller) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            color = NexusTokens.Surface,
            shape = RoundedCornerShape(NexusTokens.Radius.md),
            border = BorderStroke(1.dp, NexusTokens.Border)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Finish your viva",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = NexusTokens.Foreground
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitleText,
                        style = MaterialTheme.typography.bodySmall,
                        color = NexusTokens.ForegroundSecondary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                NexusButton(
                    onClick = onContinue,
                    variant = NexusButtonVariant.Secondary,
                    testTag = "dashboard_finish_viva_button"
                ) {
                    Text("Continue")
                }
            }
        }
    } else {
        NexusCard(
            modifier = modifier.fillMaxWidth(),
            backgroundColor = NexusTokens.Surface,
            borderColor = NexusTokens.Border
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Finish your viva",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = NexusTokens.Foreground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitleText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = NexusTokens.ForegroundSecondary
                )

                Spacer(modifier = Modifier.height(18.dp))

                NexusButton(
                    onClick = onContinue,
                    variant = NexusButtonVariant.Primary,
                    icon = Icons.Default.PlayArrow,
                    testTag = "dashboard_finish_viva_button"
                ) {
                    Text("Continue")
                }
            }
        }
    }
}

@Composable
private fun CardAVerifyRepo(
    onPickRepo: () -> Unit,
    modifier: Modifier = Modifier
) {
    NexusCard(
        modifier = modifier.fillMaxWidth(),
        backgroundColor = NexusTokens.Surface,
        borderColor = NexusTokens.Border
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Verify your repository",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = NexusTokens.Foreground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Proofolio inspects your public GitHub code, generates 5 focused questions about what you built, and scores your answers to verify your engineering skills.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NexusTokens.ForegroundSecondary
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                StatusBadge(status = "PENDING")
            }

            Spacer(modifier = Modifier.height(20.dp))

            NexusButton(
                onClick = onPickRepo,
                variant = NexusButtonVariant.Primary,
                icon = Icons.Default.PlayArrow,
                modifier = Modifier.testTag("dashboard_verify_repo_button")
            ) {
                Text("Pick a repository to verify")
            }
        }
    }
}

@Composable
private fun YourHackathonsSection(
    hackathons: List<HackathonItem>,
    onAddHackathon: () -> Unit,
    onVerifyHackathon: (String) -> Unit,
    onRemoveHackathon: (HackathonItem) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your hackathons",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = NexusTokens.Foreground
            )

            if (hackathons.isNotEmpty()) {
                NexusButton(
                    onClick = onAddHackathon,
                    variant = NexusButtonVariant.Outline,
                    icon = Icons.Default.Add,
                    testTag = "add_hackathon_button"
                ) {
                    Text("Add hackathon")
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (hackathons.isEmpty()) {
            NexusEmptyState(
                title = "No Hackathons Added",
                description = "Add the hackathons you've done. Each one counts toward your matches once you verify it with a viva on its repo.",
                icon = Icons.Default.EmojiEvents,
                actionLabel = "Add a hackathon",
                onAction = onAddHackathon,
                modifier = Modifier.testTag("empty_add_hackathon_button")
            )
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                hackathons.forEach { item ->
                    DashboardHackathonCard(
                        hackathon = item,
                        onVerifyNow = { onVerifyHackathon(item.repoFullName) },
                        onRemove = { onRemoveHackathon(item) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun DashboardHackathonCard(
    hackathon: HackathonItem,
    onVerifyNow: () -> Unit,
    onRemove: () -> Unit
) {
    val resultTitle = hackathon.result.replaceFirstChar { it.uppercase() }
    var menuExpanded by remember { mutableStateOf(false) }

    Surface(
        color = NexusTokens.SurfaceSecondary,
        shape = RoundedCornerShape(NexusTokens.Radius.md),
        border = BorderStroke(1.dp, NexusTokens.Border),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "$resultTitle · ${hackathon.event} ${hackathon.year}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = NexusTokens.Foreground
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${hackathon.repoFullName} · team of ${hackathon.teamSize}",
                        style = MaterialTheme.typography.bodySmall,
                        color = NexusTokens.ForegroundSecondary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (hackathon.verified) {
                        Surface(
                            color = AccentEmeraldLight,
                            shape = RoundedCornerShape(NexusTokens.Radius.pill),
                            border = BorderStroke(1.dp, AccentEmerald.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = AccentEmerald,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Verified",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentEmerald
                                )
                            }
                        }
                    } else {
                        Surface(
                            color = NeutralWarmSurfaceVariant,
                            shape = RoundedCornerShape(NexusTokens.Radius.pill),
                            border = BorderStroke(1.dp, TextTertiaryMuted.copy(alpha = 0.25f))
                        ) {
                            Text(
                                text = "Not verified yet",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextTertiaryMuted,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Box {
                        IconButton(
                            onClick = { menuExpanded = true },
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("hackathon_overflow_${hackathon.hackathonId}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More options",
                                tint = NexusTokens.ForegroundSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                            modifier = Modifier.background(NexusTokens.Surface)
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Remove",
                                        color = NexusTokens.ErrorText
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    onRemove()
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = NexusTokens.ErrorText,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (hackathon.verified) {
                val scorePart = "Viva ${hackathon.vivaScore ?: "—"}/100"
                val commitSharePart = if (hackathon.yourCommitShare != null) {
                    " · ${(hackathon.yourCommitShare * 100).toInt()}% of commits by you"
                } else ""

                Text(
                    text = "$scorePart$commitSharePart",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = AccentEmerald
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    NexusButton(
                        onClick = onVerifyNow,
                        variant = NexusButtonVariant.Secondary,
                        testTag = "verify_hackathon_button_${hackathon.hackathonId}"
                    ) {
                        Text("Verify now")
                    }
                }
            }
        }
    }
}

@Composable
fun DemoJourneyHeader(
    currentScreen: DemoScreen,
    currentStep: Int,
    onNavigate: (DemoScreen, Int) -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Guided Tour — Step $currentStep of 7",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFFD97706)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onPrev,
                    enabled = currentStep > 1,
                    modifier = Modifier.height(32.dp).testTag("demo_prev_button")
                ) {
                    Text("Back", style = MaterialTheme.typography.labelSmall)
                }
                Button(
                    onClick = onNext,
                    enabled = currentStep < 7,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF191A1C)),
                    modifier = Modifier.height(32.dp).testTag("demo_next_button")
                ) {
                    Text("Next", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        val screens = listOf(
            DemoScreen.STUDENT_PROFILE to "Profile",
            DemoScreen.REPOSITORY to "Repo",
            DemoScreen.INTERVIEW to "AI Viva",
            DemoScreen.ASSESSMENT to "Report",
            DemoScreen.ADMIN_DASHBOARD to "Admin",
            DemoScreen.FEEDBACK to "Feedback",
            DemoScreen.RECRUITER_DASHBOARD to "Recruiter"
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            screens.forEachIndexed { index, (screen, label) ->
                val stepNum = index + 1
                val selected = currentScreen == screen
                Surface(
                    onClick = { onNavigate(screen, stepNum) },
                    shape = RoundedCornerShape(6.dp),
                    color = if (selected) Color(0xFF191A1C) else Color(0xFFF4F3F0),
                    modifier = Modifier.weight(1f).height(28.dp).testTag("demo_step_$stepNum")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium),
                            color = if (selected) Color.White else Color(0xFF71717A),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DemoStudentProfileView(state: DemoState, onSelectStudent: (String) -> Unit) {
    val selectedStudent = DemoFixtures.students.find { it.id == state.selectedStudentId } ?: DemoFixtures.students.first()
    var isLoading by remember(state.selectedStudentId) { mutableStateOf(true) }

    LaunchedEffect(state.selectedStudentId) {
        isLoading = true
        kotlinx.coroutines.delay(300)
        isLoading = false
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Step 1: Student Profile & Background",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = NexusTokens.Foreground
        )
        Text(
            text = "Inspect pre-configured fictional student profiles demonstrating academic excellence, verified skills, and career interests.",
            style = MaterialTheme.typography.bodyMedium,
            color = NexusTokens.ForegroundSecondary
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DemoFixtures.students.forEach { student ->
                val selected = student.id == selectedStudent.id
                Surface(
                    onClick = { onSelectStudent(student.id) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (selected) Color(0xFF191A1C) else Color.White,
                    border = BorderStroke(1.dp, if (selected) Color(0xFF191A1C) else Color(0xFFE7E5E0))
                ) {
                    Text(
                        text = student.name,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium),
                        color = if (selected) Color.White else Color(0xFF191A1C),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }

        if (isLoading) {
            NexusCard {
                Box(
                    modifier = Modifier.fillMaxWidth().height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(36.dp),
                            color = Color(0xFFD97706),
                            strokeWidth = 3.dp
                        )
                        Text(
                            text = "Loading demo profile & verified records...",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexusTokens.ForegroundSecondary
                        )
                    }
                }
            }
        } else {
            NexusCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = selectedStudent.name,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = NexusTokens.Foreground
                            )
                            Text(
                                text = "${selectedStudent.university} · @${selectedStudent.githubUsername}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = NexusTokens.ForegroundSecondary
                            )
                        }
                        Surface(
                            color = AccentEmeraldLight,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "CGPA: ${selectedStudent.cgpa}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = AccentEmerald,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Text(
                        text = "Verified Skill Proficiencies:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = NexusTokens.Foreground
                    )

                    @OptIn(ExperimentalLayoutApi::class)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        selectedStudent.skills.forEach { skill ->
                            Surface(
                                color = NeutralWarmSurfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = skill,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NexusTokens.Foreground,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = "Career Interests: ${selectedStudent.interests.joinToString(", ")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = NexusTokens.ForegroundSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun DemoRepositoryView(state: DemoState, onSelectProject: (String) -> Unit) {
    val project = DemoFixtures.projects.find { it.repoFullName == state.selectedProjectId } ?: DemoFixtures.projects.first()

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Step 2: Repository Submission & AST Verification",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = NexusTokens.Foreground
        )
        Text(
            text = "Analyze verified Git repositories, commit timelines, team contribution share, and automated Abstract Syntax Tree (AST) build verification.",
            style = MaterialTheme.typography.bodyMedium,
            color = NexusTokens.ForegroundSecondary
        )

        NexusCard {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = project.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = NexusTokens.Foreground
                    )
                    Surface(
                        color = AccentEmeraldLight,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "AST Build Verified",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = AccentEmerald,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Text(
                    text = project.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = NexusTokens.ForegroundSecondary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Language", style = MaterialTheme.typography.labelSmall, color = TextTertiaryMuted)
                        Text(text = project.language, style = MaterialTheme.typography.titleSmall, color = NexusTokens.Foreground)
                    }
                    Column {
                        Text(text = "Commits", style = MaterialTheme.typography.labelSmall, color = TextTertiaryMuted)
                        Text(text = "${project.commits}", style = MaterialTheme.typography.titleSmall, color = NexusTokens.Foreground)
                    }
                    Column {
                        Text(text = "Active Days", style = MaterialTheme.typography.labelSmall, color = TextTertiaryMuted)
                        Text(text = "${project.activeDays}", style = MaterialTheme.typography.titleSmall, color = NexusTokens.Foreground)
                    }
                    Column {
                        Text(text = "Commit Share", style = MaterialTheme.typography.labelSmall, color = TextTertiaryMuted)
                        Text(text = "${(project.commitShare * 100).toInt()}%", style = MaterialTheme.typography.titleSmall, color = AccentEmerald)
                    }
                }
            }
        }
    }
}

@Composable
fun DemoInterviewView(
    state: DemoState,
    onAnswerQuestion: (String, String) -> Unit,
    onSkipQuestion: (String) -> Unit
) {
    val session = DemoFixtures.interviewSessions.first()
    val question = session.questions.firstOrNull() ?: DemoFixtures.questions.first()
    val currentAnswer = state.interview.answers[question.questionId] ?: question.defaultAnswer
    val isSkipped = state.interview.skippedQuestionIds.contains(question.questionId)

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Step 3: AI Interview & Technical Viva",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = NexusTokens.Foreground
        )
        Text(
            text = "Simulate AI-driven technical interviews with deterministic sample answers, voice/text input, and skip controls.",
            style = MaterialTheme.typography.bodyMedium,
            color = NexusTokens.ForegroundSecondary
        )

        NexusCard {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Question 1 of ${session.questions.size}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFFD97706)
                    )
                    if (isSkipped) {
                        Surface(
                            color = AccentRoseLight,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Skipped",
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentRose,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Text(
                    text = question.question,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = NexusTokens.Foreground
                )

                Surface(
                    color = NexusTokens.SurfaceSecondary,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, NexusTokens.Border)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Code Evidence File: ${question.file} (${question.lines})",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = NexusTokens.ForegroundSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = question.snippet,
                            style = MaterialTheme.typography.bodySmall,
                            color = NexusTokens.Foreground
                        )
                    }
                }

                OutlinedTextField(
                    value = currentAnswer,
                    onValueChange = { newVal -> onAnswerQuestion(question.questionId, newVal) },
                    label = { Text("Candidate Answer (Simulated / Typed)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        NexusButton(
                            onClick = { onSkipQuestion(question.questionId) },
                            variant = NexusButtonVariant.Secondary
                        ) {
                            Text("Skip Question")
                        }
                        NexusButton(
                            onClick = { /* Answer recorded */ },
                            variant = NexusButtonVariant.Primary
                        ) {
                            Text("Submit Answer")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DemoAssessmentReportView(state: DemoState) {
    val assessment = DemoFixtures.assessments.first()
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoading = true
        kotlinx.coroutines.delay(350)
        isLoading = false
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Step 4: Assessment Report & Skill Breakdown",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = NexusTokens.Foreground
        )
        Text(
            text = "Review comprehensive AI assessment scores, radar chart analytics, and verified source code evidence quotes.",
            style = MaterialTheme.typography.bodyMedium,
            color = NexusTokens.ForegroundSecondary
        )

        if (isLoading) {
            NexusCard {
                Box(
                    modifier = Modifier.fillMaxWidth().height(220.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(36.dp),
                            color = Color(0xFF059669),
                            strokeWidth = 3.dp
                        )
                        Text(
                            text = "Synthesizing AI assessment report & skill metrics...",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexusTokens.ForegroundSecondary
                        )
                    }
                }
            }
        } else {
            NexusCard {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Overall Proofolio Score",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = NexusTokens.Foreground
                            )
                            Text(
                                text = "Based on AST analysis, commit activity & AI viva",
                                style = MaterialTheme.typography.bodySmall,
                                color = NexusTokens.ForegroundSecondary
                            )
                        }
                        Surface(
                            color = AccentEmeraldLight,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = "${assessment.overallScore} / 100",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = AccentEmerald,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }

                    Text(
                        text = "Skill Proficiency Breakdown:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = NexusTokens.Foreground
                    )

                    assessment.skills.forEach { (skill, score) ->
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = formatSkillTag(skill),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NexusTokens.Foreground
                                )
                                Text(
                                    text = "$score%",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = NexusTokens.Foreground
                                )
                            }
                            LinearProgressIndicator(
                                progress = { score / 100f },
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                color = AccentEmerald,
                                trackColor = NeutralWarmSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DemoFeedbackView(state: DemoState, onShareFeedback: (String, Boolean) -> Unit) {
    val feedback = DemoFixtures.feedback

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Step 6: Feedback Sharing & Evaluation Summary",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = NexusTokens.Foreground
        )
        Text(
            text = "Review structured assessment summaries, strengths, improvements, and control student visibility.",
            style = MaterialTheme.typography.bodyMedium,
            color = NexusTokens.ForegroundSecondary
        )

        NexusCard {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Evaluation Summary",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = NexusTokens.Foreground
                )
                Text(
                    text = feedback.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = NexusTokens.ForegroundSecondary
                )

                Text(
                    text = "Key Strengths:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = NexusTokens.Foreground
                )
                feedback.strengths.forEach { str ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(AccentEmerald))
                        Text(text = str, style = MaterialTheme.typography.bodySmall, color = NexusTokens.Foreground)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Visible to Student Profile",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = NexusTokens.Foreground
                    )
                    Switch(
                        checked = feedback.releasedToStudent,
                        onCheckedChange = { checked -> onShareFeedback(feedback.feedbackId, checked) }
                    )
                }
            }
        }
    }
}

@Composable
fun DemoTabButton(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        color = if (selected) Color(0xFFD97706) else Color.White,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, if (selected) Color(0xFFB45309) else Color(0xFFE5E7EB)),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = if (selected) Color.White else Color(0xFF374151)
            )
        }
    }
}

@Composable
fun AdminDashboardDemoView(
    demoState: DemoState? = null,
    onToggleShortlist: (String) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    val shortlistedIds = demoState?.admin?.shortlistedStudentIds ?: emptyList()

    val students = DemoData.adminStudents.filter {
        val matchesSearch = it.name.contains(searchQuery, ignoreCase = true) || it.university.contains(searchQuery, ignoreCase = true)
        val matchesFilter = when (selectedFilter) {
            "Approved" -> it.status == "SHARED_WITH_RECRUITER" || it.status == "APPROVED"
            "Pending" -> it.status == "UNDER_ADMIN_REVIEW"
            else -> true
        }
        matchesSearch && matchesFilter
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Step 5: Admin Control Center — Candidate Review & Matching",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = NexusTokens.Foreground
        )
        Text(
            text = "Review student assessment results, verify AST builds and repository evidence, manage application status, and toggle shortlists.",
            style = MaterialTheme.typography.bodyMedium,
            color = NexusTokens.ForegroundSecondary
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by student name or university...") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("All", "Approved", "Pending").forEach { filter ->
                NexusButton(
                    onClick = { selectedFilter = filter },
                    variant = if (selectedFilter == filter) NexusButtonVariant.Primary else NexusButtonVariant.Secondary
                ) {
                    Text(filter)
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        students.forEach { student ->
            val isShortlisted = shortlistedIds.contains(student.id)
            NexusCard {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = student.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = NexusTokens.Foreground
                            )
                            Text(
                                text = "${student.university} · @${student.githubUsername}",
                                style = MaterialTheme.typography.bodySmall,
                                color = NexusTokens.ForegroundSecondary
                            )
                        }
                        ScoreBadge(score = student.score)
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusBadge(status = student.status)
                        Text(
                            text = "Verified via AST & AI Viva",
                            style = MaterialTheme.typography.labelSmall,
                            color = AccentEmerald
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Admin Feedback: \"${student.feedback}\"",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexusTokens.ForegroundSecondary,
                            modifier = Modifier.weight(1f)
                        )
                        NexusButton(
                            onClick = { onToggleShortlist(student.id) },
                            variant = if (isShortlisted) NexusButtonVariant.Primary else NexusButtonVariant.Secondary
                        ) {
                            Text(if (isShortlisted) "Shortlisted" else "Shortlist")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecruiterDashboardDemoView(demoState: DemoState? = null) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Step 7: Recruiter Portal — Verified Top Talent & Matching",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = NexusTokens.Foreground
        )
        Text(
            text = "Explore pre-vetted candidates with authorized performance information, verified repository code evidence, and high AI match scores.",
            style = MaterialTheme.typography.bodyMedium,
            color = NexusTokens.ForegroundSecondary
        )

        DemoData.recruiterCandidates.forEach { candidate ->
            NexusCard {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = candidate.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = NexusTokens.Foreground
                            )
                            Text(
                                text = "Applied for: ${candidate.roleApplied} at ${candidate.company}",
                                style = MaterialTheme.typography.bodySmall,
                                color = NexusTokens.ForegroundSecondary
                            )
                        }
                        Surface(
                            color = AccentEmeraldLight,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "${candidate.matchScore}% Match",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = AccentEmerald,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Text(
                        text = "Verified Skill Proficiencies:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = NexusTokens.Foreground
                    )

                    @OptIn(ExperimentalLayoutApi::class)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        candidate.verifiedSkills.forEach { (skill, score) ->
                            Surface(
                                color = NeutralWarmSurfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "${formatSkillTag(skill)}: $score%",
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    color = NexusTokens.Foreground
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            NexusButton(
                                onClick = { /* View Code Evidence */ },
                                variant = NexusButtonVariant.Secondary
                            ) {
                                Text("View Code Evidence")
                            }
                            NexusButton(
                                onClick = { /* Contact Candidate */ },
                                variant = NexusButtonVariant.Primary
                            ) {
                                Text("Contact Candidate")
                            }
                        }
                    }
                }
            }
        }
    }
}

