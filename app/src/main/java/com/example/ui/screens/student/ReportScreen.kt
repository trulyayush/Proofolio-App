package com.example.ui.screens.student

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.api.ApiResult
import com.example.data.api.EvidenceItem
import com.example.data.api.HackathonItem
import com.example.data.api.Listing
import com.example.data.api.MatchItem
import com.example.data.api.ReportResponse
import com.example.ui.components.InteractiveRadarChart
import com.example.ui.components.NexusButton
import com.example.ui.components.NexusButtonVariant
import com.example.ui.components.NexusCard
import com.example.ui.components.NexusHeader
import com.example.ui.components.NexusSkillChip
import com.example.ui.components.RadarDimension
import com.example.ui.components.ScoreBadge
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentAmberLight
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentEmeraldLight
import com.example.ui.theme.AccentRose
import com.example.ui.theme.AccentRoseLight
import com.example.ui.theme.NeutralWarmSurfaceVariant
import com.example.ui.theme.NexusTokens
import com.example.ui.theme.TextTertiaryMuted
import com.example.viewmodel.AppViewModel
import com.example.viewmodel.Load
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val INTEREST_LABELS = mapOf(
    "ai_engineer" to "AI Engineer",
    "systems" to "Systems & Low-Level",
    "backend" to "Backend & Distributed Systems",
    "fullstack" to "Full-Stack Web",
    "mobile" to "Mobile (Android / iOS)",
    "devops" to "DevOps & Cloud Infrastructure",
    "security" to "Security & Cryptography",
    "data_eng" to "Data Engineering",
    "embedded" to "Embedded & IoT",
    "graphics" to "Graphics & Game Tech",
    "open_source" to "Open-Source Contributor",
    "research" to "Research & Prototyping"
)

private val SKILL_TAGS_ORDER = listOf(
    "backend" to "Backend",
    "frontend" to "Frontend",
    "data" to "Data",
    "ai_ml" to "AI/ML",
    "devops" to "DevOps",
    "security" to "Security"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReportScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit,
    onVerifyRepo: (repoFullName: String) -> Unit,
    onContinueViva: (sessionId: String) -> Unit
) {
    BackHandler {
        onBack()
    }

    LaunchedEffect(Unit) {
        viewModel.loadReport()
        viewModel.loadListings()
    }

    val reportState by viewModel.reportState.collectAsStateWithLifecycle()
    val listingsState by viewModel.listingsState.collectAsStateWithLifecycle()

    val listingsMap: Map<String, Listing> = remember(listingsState) {
        (listingsState as? Load.Ready)?.value?.listings?.associateBy { it.id } ?: emptyMap()
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Apply with Consent Dialog State
    var applyDialogMatch by remember { mutableStateOf<MatchItem?>(null) }
    var shareConsentChecked by remember { mutableStateOf(false) }
    var isSubmittingApply by remember { mutableStateOf(false) }
    var applyErrorMessage by remember { mutableStateOf<String?>(null) }

    if (applyDialogMatch != null) {
        val match = applyDialogMatch!!
        AlertDialog(
            onDismissRequest = {
                if (!isSubmittingApply) {
                    applyDialogMatch = null
                    applyErrorMessage = null
                }
            },
            title = {
                Text(
                    text = "Apply to ${match.title} at ${match.venture}?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = NexusTokens.Foreground
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Applying shares your VerifiedBuild report (scores, answers, code evidence and hackathons) with ${match.venture} for this role. Nobody else sees it.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NexusTokens.ForegroundSecondary,
                        lineHeight = 20.sp
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !isSubmittingApply) {
                                shareConsentChecked = !shareConsentChecked
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = shareConsentChecked,
                            onCheckedChange = { shareConsentChecked = it },
                            enabled = !isSubmittingApply,
                            colors = CheckboxDefaults.colors(
                                checkedColor = NexusTokens.Accent,
                                uncheckedColor = NexusTokens.BorderStrong
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "I agree to share my report with ${match.venture}.",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = NexusTokens.Foreground
                        )
                    }

                    if (applyErrorMessage != null) {
                        Surface(
                            color = NexusTokens.ErrorBackground,
                            shape = RoundedCornerShape(NexusTokens.Radius.sm),
                            border = BorderStroke(1.dp, NexusTokens.ErrorBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = applyErrorMessage ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = NexusTokens.ErrorText,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                NexusButton(
                    onClick = {
                        isSubmittingApply = true
                        applyErrorMessage = null
                        coroutineScope.launch {
                            val res = viewModel.applyWithConsent(match.listingId)
                            isSubmittingApply = false
                            when (res) {
                                is ApiResult.Ok -> {
                                    applyDialogMatch = null
                                    snackbarHostState.showSnackbar(
                                        message = "Applied. ${match.venture} can now see your report."
                                    )
                                }
                                is ApiResult.Err -> {
                                    if (res.code == "ALREADY_APPLIED") {
                                        applyDialogMatch = null
                                    } else {
                                        applyErrorMessage = res.message
                                    }
                                }
                            }
                        }
                    },
                    variant = NexusButtonVariant.Primary,
                    enabled = shareConsentChecked && !isSubmittingApply,
                    isLoading = isSubmittingApply,
                    testTag = "confirm_apply_dialog_button"
                ) {
                    Text("Apply")
                }
            },
            dismissButton = {
                NexusButton(
                    onClick = {
                        applyDialogMatch = null
                        applyErrorMessage = null
                    },
                    variant = NexusButtonVariant.Secondary,
                    enabled = !isSubmittingApply,
                    testTag = "cancel_apply_dialog_button"
                ) {
                    Text("Cancel")
                }
            },
            containerColor = NexusTokens.Surface,
            shape = RoundedCornerShape(NexusTokens.Radius.lg)
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier
            .fillMaxSize()
            .background(NexusTokens.Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("report_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NexusHeader(
                title = "VerifiedBuild Report",
                subtitle = "Engineering skill evaluation",
                onBack = onBack
            )

            when (val state = reportState) {
                is Load.Loading, is Load.Idle -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = NexusTokens.Accent)
                    }
                }

                is Load.Failed -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        NexusCard(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.padding(16.dp),
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
                                    text = state.error.message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = NexusTokens.ErrorText,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                NexusButton(
                                    onClick = { viewModel.loadReport() },
                                    variant = NexusButtonVariant.Secondary,
                                    icon = Icons.Default.Refresh
                                ) {
                                    Text("Retry")
                                }
                            }
                        }
                    }
                }

                is Load.Ready -> {
                    val report = state.value

                    if (report.status != "done") {
                        // In Progress: Show ONLY a card "Finish your viva to see your scores", with the Dashboard's Continue behaviour. Show no numbers.
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            NexusCard(
                                modifier = Modifier.fillMaxWidth(),
                                backgroundColor = NexusTokens.Surface,
                                borderColor = NexusTokens.Border
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = NexusTokens.Accent,
                                        modifier = Modifier.size(44.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "Finish your viva to see your scores",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = NexusTokens.Foreground,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Your skill evaluation report will be generated as soon as you finish answering all questions.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = NexusTokens.ForegroundSecondary,
                                        textAlign = TextAlign.Center
                                    )

                                    val vivaInProgress = report.vivaInProgress
                                    if (vivaInProgress != null) {
                                        Spacer(modifier = Modifier.height(20.dp))
                                        NexusButton(
                                            onClick = { onContinueViva(vivaInProgress.sessionId) },
                                            variant = NexusButtonVariant.Primary,
                                            icon = Icons.Default.PlayArrow,
                                            testTag = "continue_viva_from_report_button"
                                        ) {
                                            Text("Continue viva (${vivaInProgress.answered} of ${vivaInProgress.total})")
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // Done: Show the 8 sections in order
                        ReportDoneContent(
                            report = report,
                            listingsMap = listingsMap,
                            onVerifyRepo = onVerifyRepo,
                            onOpenApplyDialog = { match ->
                                applyDialogMatch = match
                                shareConsentChecked = false
                                applyErrorMessage = null
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ReportDoneContent(
    report: ReportResponse,
    listingsMap: Map<String, Listing>,
    onVerifyRepo: (repoFullName: String) -> Unit,
    onOpenApplyDialog: (MatchItem) -> Unit
) {
    val scrollState = rememberScrollState()

    // Section 2 animation: count up from 0 to report.overall over ~800ms
    val animatedOverall = remember { Animatable(0f) }
    LaunchedEffect(report.overall) {
        animatedOverall.snapTo(0f)
        animatedOverall.animateTo(
            targetValue = report.overall.toFloat(),
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // SECTION 1: Header
        // student name, college, "@{githubUsername}".
        // If report.evidence spans more than one repoFullName: "Based on {n} repos" with the repo names. Otherwise: "Repo: {repo.fullName}".
        val distinctRepos = report.evidence.map { it.repoFullName }.distinct()
        val repoSubtitle = if (distinctRepos.size > 1) {
            "Based on ${distinctRepos.size} repos (${distinctRepos.joinToString(", ")})"
        } else {
            val repoName = report.repo?.fullName ?: distinctRepos.firstOrNull() ?: "—"
            "Repo: $repoName"
        }

        NexusCard {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = report.student.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = NexusTokens.Foreground
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${report.student.college} · @${report.student.githubUsername}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NexusTokens.ForegroundSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = repoSubtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = NexusTokens.ForegroundTertiary
                )
            }
        }

        // SECTION 2: Overall
        // "{overall}/100" (large, ScoreBadge), with the caption "Average score of every answer". Animated count up from 0 over ~800ms.
        NexusCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Overall Verified Score",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NexusTokens.Foreground
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Average score of every answer",
                        style = MaterialTheme.typography.bodySmall,
                        color = NexusTokens.ForegroundSecondary
                    )
                }

                ScoreBadge(
                    score = animatedOverall.value.roundToInt(),
                    label = "Overall",
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
        }

        // SECTION 3: "You said / You showed"
        // Two-column table with column headers "You said" and "You showed".
        // One row per report.claimedVsVerified item, in order:
        // claimed == true: left = the skill name as a SkillChip; right = the level chip.
        // claimed == false: left = "—" in muted text; right = the tag display name and the level chip, with a small "not on your profile" under it.
        // Level chip labels and colours come from PROJECT_RULES.md. Caption: "Only what you showed counts toward matches."
        NexusCard {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "You said / You showed",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = NexusTokens.Foreground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Only what you showed counts toward matches.",
                    style = MaterialTheme.typography.bodySmall,
                    color = NexusTokens.ForegroundSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Table Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NexusTokens.SurfaceSecondary, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "You said",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = NexusTokens.ForegroundSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "You showed",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = NexusTokens.ForegroundSecondary,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    report.claimedVsVerified.forEachIndexed { idx, item ->
                        Surface(
                            color = if (idx % 2 == 0) NexusTokens.Surface else NexusTokens.SurfaceSecondary.copy(alpha = 0.35f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Left Column: You said
                                Box(modifier = Modifier.weight(1f)) {
                                    if (item.claimed) {
                                        NexusSkillChip(skill = item.name)
                                    } else {
                                        Text(
                                            text = "—",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextTertiaryMuted
                                        )
                                    }
                                }

                                // Right Column: You showed
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.End
                                ) {
                                    if (item.claimed) {
                                        ReportLevelChip(level = item.level)
                                    } else {
                                        val displayTag = SKILL_TAGS_ORDER.find { it.first == item.tag }?.second ?: item.tag
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            Text(
                                                text = displayTag,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Medium,
                                                color = NexusTokens.Foreground,
                                                modifier = Modifier.padding(end = 6.dp)
                                            )
                                            ReportLevelChip(level = item.level)
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "not on your profile",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                            color = NexusTokens.ForegroundTertiary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // SECTION 4: Radar chart
        // 6 dimensions, one per skill tag in the order and with the display names in PROJECT_RULES.md.
        // title = the tag display name; score = report.skills[tag] ?: 0;
        // description = "{level label} · {score}/100" when the tag is in report.skills, otherwise "Not shown · your repo had no question on this skill";
        // level label from API thresholds: 70 or more is Strong, 40–69 is Partial, below 40 is Not verified.
        // recommendation = "".
        val radarDimensions = remember(report.skills) {
            SKILL_TAGS_ORDER.map { (tag, displayName) ->
                val score = report.skills[tag] ?: 0
                val desc = if (report.skills.containsKey(tag)) {
                    val levelLabel = when {
                        score >= 70 -> "Strong"
                        score >= 40 -> "Partial"
                        else -> "Not verified"
                    }
                    "$levelLabel · $score/100"
                } else {
                    "Not shown · your repo had no question on this skill"
                }
                RadarDimension(
                    title = displayName,
                    score = score,
                    description = desc,
                    recommendation = ""
                )
            }
        }

        NexusCard {
            Text(
                text = "Verified Skills Radar",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = NexusTokens.Foreground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "6 engineering dimensions evaluated through direct repository analysis and oral viva.",
                style = MaterialTheme.typography.bodySmall,
                color = NexusTokens.ForegroundSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))
            InteractiveRadarChart(dimensions = radarDimensions)
        }

        // SECTION 5: "Your hackathons" (only if report.hackathons is not empty)
        if (report.hackathons.isNotEmpty()) {
            NexusCard {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Your hackathons",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NexusTokens.Foreground
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        report.hackathons.forEach { h ->
                            HackathonReportCard(
                                hackathon = h,
                                onVerifyNow = { onVerifyRepo(h.repoFullName) }
                            )
                        }
                    }
                }
            }
        }

        // SECTION 6: "Your answers"
        // One card per report.evidence item, in order:
        // "Q{n} · {skill display name} · {score}/10"; question; "Your answer:" and answerExcerpt;
        // if evidenceQuote is not blank: quote in italics, between curly quotes, with a left border;
        // "Covered" with a ✓ per item (emerald), and "Missed" with a • per item (muted);
        // "Show the code" toggle that expands a code block with snippet;
        // footer of plain facts in neutral grey: Mic/Keyboard, duration, follow-up, pasted.
        if (report.evidence.isNotEmpty()) {
            Text(
                text = "Your answers",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = NexusTokens.Foreground
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                report.evidence.forEachIndexed { idx, item ->
                    EvidenceAnswerCard(
                        index = idx + 1,
                        evidence = item
                    )
                }
            }
        }

        // SECTION 7: "How the repo was built (from GitHub)" (only if buildHistory != null)
        val bh = report.buildHistory
        if (bh != null) {
            NexusCard {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "How the repo was built (from GitHub)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NexusTokens.Foreground
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val commitSharePct = (bh.yourCommitShare * 100).roundToInt()
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "• ${bh.commits} commits over ${bh.spanDays} days, active on ${bh.activeDays} days",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NexusTokens.Foreground
                        )
                        Text(
                            text = "• ${bh.authors} author(s) · $commitSharePct% of commits by you",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NexusTokens.Foreground
                        )
                        Text(
                            text = "• Median ${bh.medianLinesPerCommit} lines changed per commit",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NexusTokens.Foreground
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Facts from GitHub. They don't change your score.",
                        style = MaterialTheme.typography.labelSmall,
                        color = NexusTokens.ForegroundTertiary
                    )
                }
            }
        }

        // SECTION 8: "Internships matched on what you showed"
        // One card per report.matches item, in order:
        // title, venture, "{match}% match" (ScoreBadge), why text;
        // listing's description and tags from listings Map (tags as SkillChip with interest labels);
        // Apply button or disabled "Applied ✓"
        if (report.matches.isNotEmpty()) {
            Text(
                text = "Internships matched on what you showed",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = NexusTokens.Foreground
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                report.matches.forEach { match ->
                    val listing = listingsMap[match.listingId]
                    MatchedInternshipCard(
                        match = match,
                        listing = listing,
                        onApply = { onOpenApplyDialog(match) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ReportLevelChip(level: String) {
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
        border = BorderStroke(1.dp, textCol.copy(alpha = 0.25f))
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = textCol,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun HackathonReportCard(
    hackathon: HackathonItem,
    onVerifyNow: () -> Unit
) {
    val resultTitle = hackathon.result.replaceFirstChar { it.uppercase() }

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
                                imageVector = Icons.Default.Verified,
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
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (hackathon.verified) {
                val scorePart = "Viva ${hackathon.vivaScore ?: "—"}/100"
                val commitSharePart = if (hackathon.yourCommitShare != null) {
                    " · ${(hackathon.yourCommitShare * 100).roundToInt()}% of commits by you"
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
                        testTag = "verify_hackathon_button"
                    ) {
                        Text("Verify now")
                    }
                }
            }
        }
    }
}

@Composable
private fun EvidenceAnswerCard(
    index: Int,
    evidence: EvidenceItem
) {
    var isCodeExpanded by remember { mutableStateOf(false) }

    val skillDisplayName = SKILL_TAGS_ORDER.find { it.first == evidence.skill }?.second ?: evidence.skill

    NexusCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = NexusTokens.Surface,
        borderColor = NexusTokens.Border
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header: "Q{n} · {skill display name} · {score}/10"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Q$index · $skillDisplayName",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = NexusTokens.Foreground
                )
                Surface(
                    color = NexusTokens.SurfaceSecondary,
                    shape = RoundedCornerShape(NexusTokens.Radius.pill),
                    border = BorderStroke(1.dp, NexusTokens.Border)
                ) {
                    Text(
                        text = "${evidence.score}/10",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = NexusTokens.Accent,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // The Question
            Text(
                text = evidence.question,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = NexusTokens.Foreground
            )

            Spacer(modifier = Modifier.height(10.dp))

            // "Your answer:" and answerExcerpt
            Text(
                text = "Your answer:",
                style = MaterialTheme.typography.labelSmall,
                color = NexusTokens.ForegroundTertiary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = evidence.answerExcerpt,
                style = MaterialTheme.typography.bodyMedium,
                color = NexusTokens.Foreground
            )

            // Evidence quote if not blank
            if (evidence.evidenceQuote.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NexusTokens.SurfaceSecondary.copy(alpha = 0.5f), RoundedCornerShape(NexusTokens.Radius.sm))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(28.dp)
                            .background(NexusTokens.Accent, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "“${evidence.evidenceQuote}”",
                        style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                        color = NexusTokens.ForegroundSecondary
                    )
                }
            }

            // Covered & Missed
            if (evidence.covered.isNotEmpty() || evidence.missed.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                if (evidence.covered.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Covered",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = AccentEmerald
                        )
                        evidence.covered.forEach { item ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = AccentEmerald,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = item,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NexusTokens.Foreground
                                )
                            }
                        }
                    }
                }

                if (evidence.missed.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Missed",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = NexusTokens.ForegroundTertiary
                        )
                        evidence.missed.forEach { item ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "•",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NexusTokens.ForegroundTertiary,
                                    modifier = Modifier.width(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = item,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NexusTokens.ForegroundSecondary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // "Show the code" toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isCodeExpanded = !isCodeExpanded }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = null,
                        tint = NexusTokens.Accent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isCodeExpanded) "Hide code" else "Show the code",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = NexusTokens.Accent
                    )
                }

                Icon(
                    imageVector = if (isCodeExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = NexusTokens.Accent,
                    modifier = Modifier.size(18.dp)
                )
            }

            AnimatedVisibility(visible = isCodeExpanded) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    color = Color(0xFF1E1E24),
                    shape = RoundedCornerShape(NexusTokens.Radius.md),
                    border = BorderStroke(1.dp, Color(0xFF2E2E38))
                ) {
                    Column {
                        // Header "{repoFullName} · {file} · lines {lines}"
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF282830))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${evidence.repoFullName} · ${evidence.file} · lines ${evidence.lines}",
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFFE2E2EA),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        val snippetLines = evidence.snippet.lines()
                        val codeScrollState = rememberScrollState()
                        val hScrollState = rememberScrollState()

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 240.dp)
                                .verticalScroll(codeScrollState)
                                .horizontalScroll(hScrollState)
                                .padding(12.dp)
                        ) {
                            snippetLines.forEachIndexed { sIdx, line ->
                                val lineNum = evidence.snippetStartLine + sIdx
                                Row(verticalAlignment = Alignment.Top) {
                                    Text(
                                        text = "$lineNum ".padStart(5, ' '),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 12.sp,
                                            lineHeight = 18.sp
                                        ),
                                        color = Color(0xFF6E6E82)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = line,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 12.sp,
                                            lineHeight = 18.sp
                                        ),
                                        color = Color(0xFFF1F1F4),
                                        softWrap = false
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer of plain facts in neutral grey
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Mode
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (evidence.mode.lowercase() == "voice") Icons.Default.Mic else Icons.Default.Keyboard,
                        contentDescription = null,
                        tint = TextTertiaryMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (evidence.mode.lowercase() == "voice") "Spoken" else "Typed",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiaryMuted
                    )
                }

                if (evidence.durationS != null) {
                    Text(
                        text = "·",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiaryMuted
                    )
                    Text(
                        text = "${evidence.durationS}s",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiaryMuted
                    )
                }

                if (evidence.hadFollowUp) {
                    Text(
                        text = "·",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiaryMuted
                    )
                    Text(
                        text = "Follow-up asked",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiaryMuted
                    )
                }

                if (evidence.pasted) {
                    Text(
                        text = "·",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiaryMuted
                    )
                    Text(
                        text = "Text was pasted",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiaryMuted
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MatchedInternshipCard(
    match: MatchItem,
    listing: Listing?,
    onApply: () -> Unit
) {
    NexusCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = NexusTokens.Surface,
        borderColor = NexusTokens.Border
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = match.why,
                style = MaterialTheme.typography.bodySmall,
                color = NexusTokens.ForegroundSecondary,
                lineHeight = 18.sp
            )

            if (listing != null) {
                if (listing.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = listing.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = NexusTokens.Foreground,
                        lineHeight = 20.sp
                    )
                }

                if (listing.tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listing.tags.forEach { tagId ->
                            val label = INTEREST_LABELS[tagId] ?: tagId
                            NexusSkillChip(skill = label)
                        }
                    }
                }
            }

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
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = NexusTokens.SuccessText,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Applied ✓",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = NexusTokens.SuccessText
                            )
                        }
                    }
                } else {
                    NexusButton(
                        onClick = onApply,
                        variant = NexusButtonVariant.Primary,
                        testTag = "apply_button_${match.listingId}"
                    ) {
                        Text("Apply")
                    }
                }
            }
        }
    }
}
