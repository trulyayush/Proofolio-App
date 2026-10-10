package com.example.ui.screens.student

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.audio.AnswerRecorder
import com.example.data.api.AnalyzeResponse
import com.example.ui.components.NexusButton
import com.example.ui.components.NexusButtonVariant
import com.example.ui.components.NexusCard
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
import com.example.viva.VivaUiState
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VivaScreen(
    viewModel: AppViewModel,
    onDone: () -> Unit,
    onLeave: () -> Unit
) {
    val vivaController = viewModel.vivaController
    val state by vivaController.state.collectAsStateWithLifecycle()
    val lastAnalysis = viewModel.lastAnalysis
    val recorder = viewModel.recorder

    val recordedAnswer by viewModel.vivaRecordedAnswer.collectAsStateWithLifecycle()
    val typedText by viewModel.vivaTypedText.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val view = LocalView.current

    // Keep screen awake while this screen is visible
    DisposableEffect(view) {
        view.keepScreenOn = true
        onDispose {
            view.keepScreenOn = false
        }
    }

    // Trigger done when state.done is true
    LaunchedEffect(state.done) {
        if (state.done) {
            onDone()
        }
    }

    // Track question change & follow-up change to reset inputs in ViewModel and restart timer
    val currentQuestion = state.currentQuestion
    val questionKey = "${currentQuestion?.id}_${state.followUp ?: ""}"
    var prevQuestionKey by remember { mutableStateOf("") }

    var timeLeftS by remember { mutableIntStateOf(state.timeLimitS) }
    var isRecording by remember { mutableStateOf(false) }
    var recordingElapsedS by remember { mutableIntStateOf(0) }
    var recordError by remember { mutableStateOf<String?>(null) }
    var micPermissionDeniedMessage by remember { mutableStateOf<String?>(null) }

    // Active tab: 0 = Speak, 1 = Type
    var selectedTab by remember { mutableIntStateOf(0) }

    // Show leave confirmation dialog
    var showLeaveDialog by remember { mutableStateOf(false) }

    BackHandler {
        showLeaveDialog = true
    }

    // When question or follow-up changes, reset input in ViewModel and restart countdown
    LaunchedEffect(questionKey) {
        if (questionKey != prevQuestionKey && questionKey.isNotBlank() && currentQuestion != null) {
            prevQuestionKey = questionKey
            viewModel.notifyQuestionAppeared()
            timeLeftS = state.timeLimitS
            recordError = null
            micPermissionDeniedMessage = null
            isRecording = false
            recordingElapsedS = 0
        }
    }

    // Countdown timer: 1-second ticks
    LaunchedEffect(questionKey, timeLeftS > 0) {
        while (timeLeftS > 0) {
            delay(1000L)
            timeLeftS -= 1
            if (timeLeftS == 0 && isRecording) {
                // At 0: if recording, stop it but KEEP the recording. Show "Time's up. Submit what you have."
                isRecording = false
                val rec = recorder.stop()
                if (rec != null) {
                    viewModel.setRecordedAnswer(rec)
                }
            }
        }
    }

    // Elapsed timer while recording
    LaunchedEffect(isRecording) {
        while (isRecording) {
            recordingElapsedS = recorder.elapsedSeconds()
            delay(500L)
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            micPermissionDeniedMessage = null
            try {
                recordError = null
                recorder.start()
                isRecording = true
                recordingElapsedS = 0
            } catch (_: Exception) {
                isRecording = false
                recordError = "We couldn't start the microphone. You can type your answer instead."
            }
        } else {
            micPermissionDeniedMessage = "Microphone access is off. You can type your answer instead."
            selectedTab = 1 // Switch to Type tab
        }
    }

    fun startRecordingFlow() {
        recordError = null
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            try {
                recorder.start()
                isRecording = true
                recordingElapsedS = 0
            } catch (_: Exception) {
                isRecording = false
                recordError = "We couldn't start the microphone. You can type your answer instead."
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    fun stopRecordingFlow() {
        isRecording = false
        val rec = recorder.stop()
        if (rec == null) {
            recordError = "That recording was too short. Please try again."
        } else {
            recordError = null
            viewModel.setRecordedAnswer(rec)
        }
    }

    fun cancelAndRecordAgain() {
        recorder.discard()
        viewModel.setRecordedAnswer(null)
        isRecording = false
        recordingElapsedS = 0
        recordError = null
        startRecordingFlow()
    }

    if (showLeaveDialog) {
        AlertDialog(
            onDismissRequest = { showLeaveDialog = false },
            title = {
                Text(
                    text = "Leave the viva?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = NexusTokens.Foreground
                )
            },
            text = {
                Text(
                    text = "Your answers so far are saved. You can continue from your dashboard.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NexusTokens.ForegroundSecondary
                )
            },
            confirmButton = {
                NexusButton(
                    onClick = {
                        showLeaveDialog = false
                        recorder.discard()
                        onLeave()
                    },
                    variant = NexusButtonVariant.Destructive,
                    testTag = "leave_viva_confirm"
                ) {
                    Text("Leave")
                }
            },
            dismissButton = {
                NexusButton(
                    onClick = { showLeaveDialog = false },
                    variant = NexusButtonVariant.Secondary,
                    testTag = "stay_viva_button"
                ) {
                    Text("Stay")
                }
            },
            containerColor = NexusTokens.Surface,
            shape = RoundedCornerShape(NexusTokens.Radius.lg)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NexusTokens.Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("viva_screen")
    ) {
        // Top Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = NexusTokens.Surface,
            border = BorderStroke(1.dp, NexusTokens.Border)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        val qNum = if (state.questionNumber > 0) state.questionNumber else 1
                        val totalQ = if (state.questions.isNotEmpty()) state.questions.size else 5
                        Text(
                            text = "Question $qNum of $totalQ",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NexusTokens.Foreground
                        )
                        Text(
                            text = if (state.followUp != null) "Follow-up question" else "VerifiedBuild technical viva",
                            style = MaterialTheme.typography.labelSmall,
                            color = NexusTokens.ForegroundSecondary
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Timer Display
                        val timerMinutes = timeLeftS / 60
                        val timerSeconds = timeLeftS % 60
                        val timerFormatted = "%d:%02d".format(timerMinutes, timerSeconds)
                        Surface(
                            color = if (timeLeftS <= 15) AccentRoseLight else NexusTokens.SurfaceSecondary,
                            shape = RoundedCornerShape(NexusTokens.Radius.pill),
                            border = BorderStroke(1.dp, if (timeLeftS <= 15) AccentRose else NexusTokens.Border),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = timerFormatted,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (timeLeftS <= 15) AccentRose else NexusTokens.Foreground,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        IconButton(
                            onClick = { showLeaveDialog = true },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("leave_viva_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Leave the viva",
                                tint = NexusTokens.ForegroundSecondary
                            )
                        }
                    }
                }

                val progress = if (state.questions.isNotEmpty()) {
                    (state.answeredIds.size.toFloat() / state.questions.size.toFloat()).coerceIn(0f, 1f)
                } else 0f

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = NexusTokens.Accent,
                    trackColor = NexusTokens.Border
                )
            }
        }

        // Scrollable content area
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Error Card if present
            if (state.error != null) {
                Surface(
                    color = NexusTokens.ErrorBackground,
                    shape = RoundedCornerShape(NexusTokens.Radius.md),
                    border = BorderStroke(1.dp, NexusTokens.ErrorBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = NexusTokens.ErrorText,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = state.error?.message ?: "An error occurred",
                                style = MaterialTheme.typography.bodyMedium,
                                color = NexusTokens.ErrorText,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            if (state.canRetry) {
                                NexusButton(
                                    onClick = { viewModel.retryVivaSubmit() },
                                    variant = NexusButtonVariant.Primary,
                                    icon = Icons.Default.Refresh,
                                    testTag = "retry_viva_button"
                                ) {
                                    Text("Try again")
                                }
                            } else {
                                NexusButton(
                                    onClick = { vivaController.clearError() },
                                    variant = NexusButtonVariant.Secondary,
                                    testTag = "ok_viva_error_button"
                                ) {
                                    Text("OK")
                                }
                            }
                        }
                    }
                }
            }

            // Time's up banner
            if (timeLeftS == 0) {
                Surface(
                    color = AccentAmberLight,
                    shape = RoundedCornerShape(NexusTokens.Radius.md),
                    border = BorderStroke(1.dp, AccentAmber.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Time's up. Submit what you have.",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = AccentAmber
                        )
                    }
                }
            }

            // Last Transcript confirmation card if present
            if (state.lastTranscript != null) {
                Surface(
                    color = NexusTokens.InfoBackground,
                    shape = RoundedCornerShape(NexusTokens.Radius.md),
                    border = BorderStroke(1.dp, NexusTokens.InfoBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Answer saved. We heard: “${state.lastTranscript}”",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexusTokens.InfoText,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { vivaController.clearTranscript() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close transcript message",
                                tint = NexusTokens.InfoText,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            if (currentQuestion != null) {
                // Question Header Chips
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Difficulty chip
                    val (diffLabel, diffColor, diffBg) = when (currentQuestion.difficulty.lowercase()) {
                        "easy" -> Triple("Easy", AccentEmerald, AccentEmeraldLight)
                        "medium" -> Triple("Medium", AccentAmber, AccentAmberLight)
                        "hard" -> Triple("Hard", AccentRose, AccentRoseLight)
                        else -> Triple(currentQuestion.difficulty, TextTertiaryMuted, NeutralWarmSurfaceVariant)
                    }
                    Surface(
                        color = diffBg,
                        shape = RoundedCornerShape(NexusTokens.Radius.pill),
                        border = BorderStroke(1.dp, diffColor.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = diffLabel,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = diffColor,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    // Skill chip
                    Surface(
                        color = NexusTokens.SurfaceSecondary,
                        shape = RoundedCornerShape(NexusTokens.Radius.pill),
                        border = BorderStroke(1.dp, NexusTokens.Border)
                    ) {
                        Text(
                            text = currentQuestion.skill,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = NexusTokens.Foreground,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    // Focus file label if author changed it
                    val focusFiles = lastAnalysis?.contribution?.focusFiles ?: emptyList()
                    if (focusFiles.contains(currentQuestion.file)) {
                        Surface(
                            color = NexusTokens.AccentSubtle,
                            shape = RoundedCornerShape(NexusTokens.Radius.pill),
                            border = BorderStroke(1.dp, NexusTokens.Accent.copy(alpha = 0.25f))
                        ) {
                            Text(
                                text = "You changed this file",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = NexusTokens.Accent,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Code snippet Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF1E1E24), // Monospace dark card for high clarity
                    shape = RoundedCornerShape(NexusTokens.Radius.md),
                    border = BorderStroke(1.dp, Color(0xFF2E2E38))
                ) {
                    Column {
                        // Snippet header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF282830))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = null,
                                tint = Color(0xFFA5A6C2),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${currentQuestion.file} · lines ${currentQuestion.lines}",
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFFE2E2EA),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Code lines with prefix numbers
                        val snippetLines = currentQuestion.snippet.lines()
                        val codeScrollState = rememberScrollState()
                        val hScrollState = rememberScrollState()

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 280.dp)
                                .verticalScroll(codeScrollState)
                                .horizontalScroll(hScrollState)
                                .padding(12.dp)
                        ) {
                            snippetLines.forEachIndexed { idx, line ->
                                val lineNum = currentQuestion.snippetStartLine + idx
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

                // Follow-up card or Regular question
                if (state.followUp != null) {
                    // Highlighted card: "One more chance on this question"
                    Surface(
                        color = AccentAmberLight,
                        shape = RoundedCornerShape(NexusTokens.Radius.md),
                        border = BorderStroke(1.5.dp, AccentAmber.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "One more chance on this question",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = AccentAmber
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = state.followUp ?: "",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = NexusTokens.Foreground
                            )
                        }
                    }

                    // Original question muted and smaller
                    Column(modifier = Modifier.padding(horizontal = 4.dp)) {
                        Text(
                            text = "Original question:",
                            style = MaterialTheme.typography.labelSmall,
                            color = NexusTokens.ForegroundTertiary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = currentQuestion.question,
                            style = MaterialTheme.typography.bodyMedium,
                            color = NexusTokens.ForegroundSecondary
                        )
                    }
                } else {
                    Text(
                        text = currentQuestion.question,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = NexusTokens.Foreground,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                // Loading overlay banner if vivaController is submitting
                if (state.loading) {
                    Surface(
                        color = NexusTokens.AccentSubtle,
                        shape = RoundedCornerShape(NexusTokens.Radius.md),
                        border = BorderStroke(1.dp, NexusTokens.Accent.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = NexusTokens.Accent,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Saving your answer…",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = NexusTokens.Accent
                            )
                        }
                    }
                }

                // Answer Area: Speak & Type Tabs
                NexusCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = NexusTokens.Surface,
                    borderColor = NexusTokens.Border
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        TabRow(
                            selectedTabIndex = selectedTab,
                            containerColor = NexusTokens.Surface,
                            contentColor = NexusTokens.Accent,
                            indicator = { tabPositions ->
                                TabRowDefaults.SecondaryIndicator(
                                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                    color = NexusTokens.Accent,
                                    height = 2.dp
                                )
                            },
                            divider = {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp),
                                    color = NexusTokens.Border
                                ) {}
                            }
                        ) {
                            Tab(
                                selected = selectedTab == 0,
                                onClick = {
                                    if (!state.loading) selectedTab = 0
                                },
                                enabled = !state.loading,
                                text = {
                                    Text(
                                        text = "Speak",
                                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                                        color = if (selectedTab == 0) NexusTokens.Accent else NexusTokens.ForegroundSecondary
                                    )
                                }
                            )
                            Tab(
                                selected = selectedTab == 1,
                                onClick = {
                                    if (!state.loading) selectedTab = 1
                                },
                                enabled = !state.loading,
                                text = {
                                    Text(
                                        text = "Type",
                                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                        color = if (selectedTab == 1) NexusTokens.Accent else NexusTokens.ForegroundSecondary
                                    )
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (selectedTab == 0) {
                            // SPEAK TAB
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                if (micPermissionDeniedMessage != null) {
                                    Surface(
                                        color = AccentAmberLight,
                                        shape = RoundedCornerShape(NexusTokens.Radius.sm),
                                        border = BorderStroke(1.dp, AccentAmber.copy(alpha = 0.4f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = micPermissionDeniedMessage ?: "",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = AccentAmber,
                                            modifier = Modifier.padding(12.dp)
                                        )
                                    }
                                }

                                if (recordError != null) {
                                    Surface(
                                        color = NexusTokens.ErrorBackground,
                                        shape = RoundedCornerShape(NexusTokens.Radius.sm),
                                        border = BorderStroke(1.dp, NexusTokens.ErrorBorder),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = recordError ?: "",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = NexusTokens.ErrorText,
                                            modifier = Modifier.padding(12.dp)
                                        )
                                    }
                                }

                                when {
                                    isRecording -> {
                                        // Recording state
                                        val recMin = recordingElapsedS / 60
                                        val recSec = recordingElapsedS % 60
                                        val formattedRec = "%d:%02d".format(recMin, recSec)

                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(72.dp)
                                                    .clip(CircleShape)
                                                    .background(AccentRose)
                                                    .testTag("stop_recording_button"),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                IconButton(
                                                    onClick = { stopRecordingFlow() },
                                                    modifier = Modifier.size(72.dp),
                                                    enabled = !state.loading
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Stop,
                                                        contentDescription = "Stop recording",
                                                        tint = Color.White,
                                                        modifier = Modifier.size(36.dp)
                                                    )
                                                }
                                            }

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.GraphicEq,
                                                    contentDescription = null,
                                                    tint = AccentRose,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Recording $formattedRec · tap to stop",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = AccentRose
                                                )
                                            }
                                        }
                                    }

                                    recordedAnswer != null -> {
                                        // Stopped & valid recording present
                                        val rec = recordedAnswer!!
                                        val recMin = rec.seconds / 60
                                        val recSec = rec.seconds % 60
                                        val formatted = "%d:%02d".format(recMin, recSec)

                                        Column(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Surface(
                                                color = NexusTokens.SuccessBackground,
                                                shape = RoundedCornerShape(NexusTokens.Radius.pill),
                                                border = BorderStroke(1.dp, NexusTokens.SuccessBorder)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.GraphicEq,
                                                        contentDescription = null,
                                                        tint = NexusTokens.SuccessText,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text(
                                                        text = "Recorded $formatted",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        color = NexusTokens.SuccessText
                                                    )
                                                }
                                            }

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                NexusButton(
                                                    onClick = { cancelAndRecordAgain() },
                                                    variant = NexusButtonVariant.Secondary,
                                                    enabled = !state.loading,
                                                    modifier = Modifier.weight(1f),
                                                    testTag = "record_again_button"
                                                ) {
                                                    Text("Record again")
                                                }

                                                NexusButton(
                                                    onClick = { viewModel.submitVoiceAnswer() },
                                                    variant = NexusButtonVariant.Primary,
                                                    enabled = !state.loading,
                                                    isLoading = state.loading,
                                                    modifier = Modifier.weight(1f),
                                                    testTag = "submit_voice_button"
                                                ) {
                                                    Text("Submit answer")
                                                }
                                            }
                                        }
                                    }

                                    else -> {
                                        // Idle state
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(72.dp)
                                                    .clip(CircleShape)
                                                    .background(if (state.loading) NexusTokens.Border else NexusTokens.Accent)
                                                    .testTag("start_recording_button"),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                IconButton(
                                                    onClick = { startRecordingFlow() },
                                                    modifier = Modifier.size(72.dp),
                                                    enabled = !state.loading
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Mic,
                                                        contentDescription = "Start speaking",
                                                        tint = NexusTokens.OnAccent,
                                                        modifier = Modifier.size(36.dp)
                                                    )
                                                }
                                            }

                                            Text(
                                                text = "Tap to start speaking",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = NexusTokens.ForegroundSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            // TYPE TAB
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = typedText,
                                    onValueChange = { viewModel.onTypedTextChanged(it) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 140.dp)
                                        .testTag("viva_type_input"),
                                    placeholder = {
                                        Text(
                                            text = "Explain in your own words. Any language is fine.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = NexusTokens.ForegroundTertiary
                                        )
                                    },
                                    minLines = 6,
                                    maxLines = 12,
                                    enabled = !state.loading,
                                    shape = RoundedCornerShape(NexusTokens.Radius.md),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = NexusTokens.Accent,
                                        unfocusedBorderColor = NexusTokens.Border,
                                        focusedContainerColor = NexusTokens.Surface,
                                        unfocusedContainerColor = NexusTokens.Surface,
                                        disabledContainerColor = NexusTokens.SurfaceSecondary
                                    )
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${typedText.length} / 6000",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = NexusTokens.ForegroundTertiary
                                    )

                                    NexusButton(
                                        onClick = { viewModel.submitTextAnswer() },
                                        variant = NexusButtonVariant.Primary,
                                        enabled = typedText.isNotBlank() && !state.loading,
                                        isLoading = state.loading,
                                        icon = Icons.AutoMirrored.Filled.Send,
                                        testTag = "submit_text_button"
                                    ) {
                                        Text("Submit answer")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
