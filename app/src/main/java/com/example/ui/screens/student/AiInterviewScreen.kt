package com.example.ui.screens.student

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InterviewQuestionEntity
import com.example.data.model.ProjectEntity
import com.example.ui.components.NexusButton
import com.example.ui.components.NexusButtonVariant
import com.example.ui.components.NexusCard
import com.example.ui.components.NexusHeader
import com.example.ui.components.NexusTextField
import com.example.ui.theme.NexusTokens

@Composable
fun AiInterviewScreen(
    project: ProjectEntity,
    questions: List<InterviewQuestionEntity>,
    isListening: Boolean,
    transcriptionText: String,
    speechError: String?,
    onStartSpeech: (initial: String) -> Unit,
    onStopSpeech: () -> Unit,
    onSaveAnswer: (questionId: String, answer: String, isVoice: Boolean) -> Unit,
    onSubmitInterview: () -> Unit,
    onBack: () -> Unit
) {
    if (questions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = NexusTokens.Accent)
        }
        return
    }

    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    val currentQuestion = questions.getOrElse(currentQuestionIndex) { questions.first() }

    var answerText by remember(currentQuestion.id) {
        mutableStateOf(currentQuestion.studentAnswer)
    }

    var isVoiceRecorded by remember(currentQuestion.id) {
        mutableStateOf(currentQuestion.isVoiceTranscription)
    }

    var showSubmitConfirmation by remember { mutableStateOf(false) }
    var isSubmittingEvaluation by remember { mutableStateOf(false) }

    LaunchedEffect(transcriptionText) {
        if (isListening && transcriptionText.isNotBlank()) {
            answerText = transcriptionText
            isVoiceRecorded = true
            onSaveAnswer(currentQuestion.id, answerText, true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NexusTokens.Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("ai_interview_screen")
    ) {
        // App Header
        NexusHeader(
            title = "Technical Assessment Interview",
            subtitle = "Question ${currentQuestionIndex + 1} of ${questions.size} • ${project.title}",
            onBack = onBack
        )

        LinearProgressIndicator(
            progress = { (currentQuestionIndex + 1).toFloat() / questions.size },
            modifier = Modifier
                .fillMaxWidth()
                .height(2.5.dp),
            color = NexusTokens.Accent,
            trackColor = NexusTokens.Border
        )

        // Body
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(NexusTokens.Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(NexusTokens.Spacing.md)
        ) {
            // Question Card
            NexusCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = NexusTokens.SurfaceSecondary,
                        shape = RoundedCornerShape(NexusTokens.Radius.sm),
                        border = BorderStroke(1.dp, NexusTokens.Border)
                    ) {
                        Text(
                            text = currentQuestion.category,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = NexusTokens.Foreground,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Surface(
                        color = when (currentQuestion.difficulty.uppercase()) {
                            "ADVANCED" -> NexusTokens.WarningBackground
                            else -> NexusTokens.InfoBackground
                        },
                        shape = RoundedCornerShape(NexusTokens.Radius.sm),
                        border = BorderStroke(
                            1.dp,
                            when (currentQuestion.difficulty.uppercase()) {
                                "ADVANCED" -> NexusTokens.WarningBorder
                                else -> NexusTokens.InfoBorder
                            }
                        )
                    ) {
                        Text(
                            text = currentQuestion.difficulty,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = when (currentQuestion.difficulty.uppercase()) {
                                "ADVANCED" -> NexusTokens.WarningText
                                else -> NexusTokens.InfoText
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(NexusTokens.Spacing.sm))

                Text(
                    text = currentQuestion.questionText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = NexusTokens.Foreground,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(NexusTokens.Spacing.sm))

                // Rationale Box
                Surface(
                    color = NexusTokens.SurfaceSecondary,
                    shape = RoundedCornerShape(NexusTokens.Radius.sm),
                    border = BorderStroke(1.dp, NexusTokens.Border),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = NexusTokens.Accent,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Assessment Rationale: ${currentQuestion.rationale}",
                            style = MaterialTheme.typography.labelSmall,
                            color = NexusTokens.ForegroundSecondary
                        )
                    }
                }
            }

            // Student Answer Input Card
            NexusCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Technical Response",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = NexusTokens.Foreground
                    )

                    // Speech-to-text dictation button
                    NexusButton(
                        onClick = {
                            if (isListening) onStopSpeech() else onStartSpeech(answerText)
                        },
                        variant = if (isListening) NexusButtonVariant.Destructive else NexusButtonVariant.Secondary,
                        icon = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                        testTag = "voice_input_toggle_button"
                    ) {
                        Text(if (isListening) "Stop Recording" else "Voice Dictation")
                    }
                }

                // Voice Recording Live Banner
                AnimatedVisibility(visible = isListening) {
                    Surface(
                        color = NexusTokens.ErrorBackground,
                        shape = RoundedCornerShape(NexusTokens.Radius.sm),
                        border = BorderStroke(1.dp, NexusTokens.ErrorBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = NexusTokens.Spacing.xs)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.GraphicEq, contentDescription = null, tint = NexusTokens.ErrorText, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Listening... Speak clearly. You can edit the transcription below before submitting.",
                                style = MaterialTheme.typography.labelSmall,
                                color = NexusTokens.ErrorText
                            )
                        }
                    }
                }

                if (speechError != null) {
                    Surface(
                        color = NexusTokens.ErrorBackground,
                        shape = RoundedCornerShape(NexusTokens.Radius.sm),
                        border = BorderStroke(1.dp, NexusTokens.ErrorBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = speechError,
                            style = MaterialTheme.typography.labelSmall,
                            color = NexusTokens.ErrorText,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(NexusTokens.Spacing.sm))

                NexusTextField(
                    value = answerText,
                    onValueChange = {
                        answerText = it
                        onSaveAnswer(currentQuestion.id, it, isVoiceRecorded)
                    },
                    label = "Detailed Response & Implementation Rationale",
                    placeholder = "Explain your architecture, data structures, trade-offs, and edge cases...",
                    minLines = 6,
                    maxLines = 12,
                    testTag = "interview_answer_input"
                )

                Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NexusTokens.SuccessText, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Answers are auto-saved immediately",
                            style = MaterialTheme.typography.labelSmall,
                            color = NexusTokens.ForegroundSecondary
                        )
                    }

                    Text(
                        text = "${answerText.trim().split(Regex("""\s+""")).filter { it.isNotBlank() }.size} words",
                        style = MaterialTheme.typography.labelSmall,
                        color = NexusTokens.ForegroundTertiary
                    )
                }
            }
        }

        // Bottom Action Bar
        Surface(
            color = NexusTokens.Surface,
            border = BorderStroke(1.dp, NexusTokens.Border)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentQuestionIndex > 0) {
                    NexusButton(
                        onClick = { currentQuestionIndex-- },
                        variant = NexusButtonVariant.Outline,
                        testTag = "interview_prev_button"
                    ) {
                        Text("Previous")
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                if (currentQuestionIndex < questions.size - 1) {
                    NexusButton(
                        onClick = { currentQuestionIndex++ },
                        variant = NexusButtonVariant.Primary,
                        trailingIcon = Icons.AutoMirrored.Filled.ArrowForward,
                        testTag = "interview_next_button"
                    ) {
                        Text("Next Question")
                    }
                } else {
                    NexusButton(
                        onClick = { showSubmitConfirmation = true },
                        variant = NexusButtonVariant.Primary,
                        icon = Icons.AutoMirrored.Filled.Send,
                        testTag = "submit_interview_button"
                    ) {
                        Text("Submit for Evaluation")
                    }
                }
            }
        }
    }

    if (showSubmitConfirmation) {
        AlertDialog(
            onDismissRequest = { if (!isSubmittingEvaluation) showSubmitConfirmation = false },
            containerColor = NexusTokens.Surface,
            title = {
                Text(
                    text = "Submit Technical Assessment?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = NexusTokens.Foreground
                )
            },
            text = {
                Column {
                    Text(
                        text = "Once submitted, your answers will be evaluated across the 8-dimension engineering rubric. Your submission will be placed under administrator review until released.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NexusTokens.ForegroundSecondary
                    )

                    if (isSubmittingEvaluation) {
                        Spacer(modifier = Modifier.height(NexusTokens.Spacing.md))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = NexusTokens.Accent, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(NexusTokens.Spacing.xs))
                            Text("Evaluating answers against technical rubric...", style = MaterialTheme.typography.bodySmall, color = NexusTokens.Foreground)
                        }
                    }
                }
            },
            confirmButton = {
                NexusButton(
                    onClick = {
                        isSubmittingEvaluation = true
                        onSubmitInterview()
                    },
                    enabled = !isSubmittingEvaluation,
                    isLoading = isSubmittingEvaluation,
                    variant = NexusButtonVariant.Primary,
                    testTag = "confirm_submit_interview_button"
                ) {
                    Text("Confirm & Submit")
                }
            },
            dismissButton = {
                if (!isSubmittingEvaluation) {
                    NexusButton(
                        onClick = { showSubmitConfirmation = false },
                        variant = NexusButtonVariant.Outline
                    ) {
                        Text("Review Answers")
                    }
                }
            }
        )
    }
}
