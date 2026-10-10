package com.example.ui.screens.student

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AssessmentReportEntity
import com.example.data.model.InterviewQuestionEntity
import com.example.data.model.ProjectEntity
import com.example.ui.components.NexusButton
import com.example.ui.components.NexusButtonVariant
import com.example.ui.components.NexusCard
import com.example.ui.components.NexusHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.NexusTokens

@Composable
fun ProjectDetailScreen(
    project: ProjectEntity,
    questions: List<InterviewQuestionEntity>,
    report: AssessmentReportEntity?,
    onBack: () -> Unit,
    onStartInterview: () -> Unit,
    onViewReport: () -> Unit
) {
    val answeredCount = questions.count { it.isAnswered }
    val totalQuestions = questions.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NexusTokens.Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("project_detail_screen")
    ) {
        // App Header
        NexusHeader(
            title = "Project Assessment",
            subtitle = project.title,
            onBack = onBack
        )

        // Body
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(NexusTokens.Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(NexusTokens.Spacing.md)
        ) {
            // Project Header Card
            NexusCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = project.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = NexusTokens.Foreground
                        )
                        Text(
                            text = project.githubRepoUrl,
                            style = MaterialTheme.typography.bodySmall,
                            color = NexusTokens.Accent
                        )
                    }
                    StatusBadge(status = project.status)
                }

                Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))

                Text(
                    text = project.repoDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = NexusTokens.ForegroundSecondary,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(NexusTokens.Spacing.sm))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Languages: ${project.languages}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = NexusTokens.ForegroundTertiary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = NexusTokens.WarningText, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${project.starsCount} stars",
                            style = MaterialTheme.typography.labelSmall,
                            color = NexusTokens.ForegroundSecondary
                        )
                    }
                }
            }

            // Status Action Section
            when (project.status.uppercase()) {
                "SUBMITTED", "INTERVIEW_PENDING" -> {
                    NexusCard(
                        backgroundColor = NexusTokens.WarningBackground,
                        borderColor = NexusTokens.WarningBorder
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Quiz, contentDescription = null, tint = NexusTokens.WarningText, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(NexusTokens.Spacing.xs))
                            Text(
                                text = "Repository-Specific Technical Interview",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = NexusTokens.WarningText
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "4 technical questions generated from repository architecture. Answer via text or speech dictation. Progress: $answeredCount of $totalQuestions answered.",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexusTokens.WarningText,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(NexusTokens.Spacing.sm))
                        NexusButton(
                            onClick = onStartInterview,
                            variant = NexusButtonVariant.Primary,
                            icon = Icons.Default.Mic,
                            testTag = "start_interview_button"
                        ) {
                            Text(if (answeredCount > 0) "Resume Interview" else "Start Technical Interview")
                        }
                    }
                }

                "UNDER_ADMIN_REVIEW" -> {
                    NexusCard(
                        backgroundColor = NexusTokens.InfoBackground,
                        borderColor = NexusTokens.InfoBorder
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.HourglassTop, contentDescription = null, tint = NexusTokens.InfoText, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(NexusTokens.Spacing.xs))
                            Text(
                                text = "Under Administrator Review",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = NexusTokens.InfoText
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "“Your profile and project assessment are under review. An update will be sent when there is progress.”",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = NexusTokens.InfoText,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Detailed evaluations and radar breakdowns are held in private audit until released by the administrator.",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexusTokens.InfoText.copy(alpha = 0.85f)
                        )
                    }
                }

                "EVALUATION_COMPLETED", "SHARED_WITH_RECRUITER" -> {
                    NexusCard(
                        backgroundColor = NexusTokens.SuccessBackground,
                        borderColor = NexusTokens.SuccessBorder
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Assessment, contentDescription = null, tint = NexusTokens.SuccessText, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(NexusTokens.Spacing.xs))
                            Text(
                                text = "Assessment Report Released",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = NexusTokens.SuccessText
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "The administrator has released your assessment report. Inspect your 8-dimensional radar chart and growth recommendations.",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexusTokens.SuccessText,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(NexusTokens.Spacing.sm))
                        NexusButton(
                            onClick = onViewReport,
                            variant = NexusButtonVariant.Primary,
                            icon = Icons.Default.Assessment,
                            testTag = "view_report_button"
                        ) {
                            Text("View Radar Chart & Report")
                        }
                    }
                }
            }

            // Audited Repository Structure & README
            NexusCard {
                Text(
                    text = "Audited Repository Structure",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = NexusTokens.Foreground
                )
                Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))
                Surface(
                    color = NexusTokens.SurfaceSecondary,
                    shape = RoundedCornerShape(NexusTokens.Radius.sm),
                    border = BorderStroke(1.dp, NexusTokens.Border),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = project.fileStructureSummary,
                        style = MaterialTheme.typography.bodySmall,
                        color = NexusTokens.Foreground,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(NexusTokens.Spacing.md))

                Text(
                    text = "README Architecture Excerpt",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = NexusTokens.Foreground
                )
                Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))
                Surface(
                    color = NexusTokens.SurfaceSecondary,
                    shape = RoundedCornerShape(NexusTokens.Radius.sm),
                    border = BorderStroke(1.dp, NexusTokens.Border),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = project.readmeSnippet,
                        style = MaterialTheme.typography.bodySmall,
                        color = NexusTokens.ForegroundSecondary,
                        modifier = Modifier.padding(10.dp),
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
