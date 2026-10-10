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
    onSignOut: () -> Unit
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
        // --- Top Bar & Masthead ---
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
                        text = "VerifiedBuild inspects your public GitHub code, generates 5 focused questions about what you built, and scores your answers to verify your engineering skills.",
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
            Surface(
                color = NexusTokens.Surface,
                shape = RoundedCornerShape(NexusTokens.Radius.md),
                border = BorderStroke(1.dp, NexusTokens.Border),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = NexusTokens.ForegroundTertiary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Add the hackathons you've done. Each one counts toward your matches once you verify it with a viva on its repo.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NexusTokens.ForegroundSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    NexusButton(
                        onClick = onAddHackathon,
                        variant = NexusButtonVariant.Primary,
                        icon = Icons.Default.Add,
                        testTag = "empty_add_hackathon_button"
                    ) {
                        Text("Add a hackathon")
                    }
                }
            }
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

