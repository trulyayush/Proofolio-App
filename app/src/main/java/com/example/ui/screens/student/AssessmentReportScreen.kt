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
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Shield
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
import com.example.data.model.ProjectEntity
import com.example.ui.components.InteractiveRadarChart
import com.example.ui.components.NexusCard
import com.example.ui.components.NexusHeader
import com.example.ui.components.ScoreBadge
import com.example.ui.theme.NexusTokens

@Composable
fun AssessmentReportScreen(
    project: ProjectEntity,
    report: AssessmentReportEntity,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NexusTokens.Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("assessment_report_screen")
    ) {
        // App Header
        NexusHeader(
            title = "Technical Assessment Report",
            subtitle = "${project.title} • Released by Admin",
            badgeText = "Released",
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
            // Overall Score Card
            NexusCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Overall Technical Competency",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = NexusTokens.Foreground
                        )
                        Text(
                            text = "Audited against standardized 8-dimensional engineering rubric.",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexusTokens.ForegroundSecondary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    ScoreBadge(score = report.overallScore, modifier = Modifier.padding(start = 12.dp))
                }
            }

            // Admin Remarks Banner
            if (report.adminRemarks.isNotBlank()) {
                NexusCard(
                    backgroundColor = NexusTokens.SuccessBackground,
                    borderColor = NexusTokens.SuccessBorder
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FormatQuote, contentDescription = null, tint = NexusTokens.SuccessText, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(NexusTokens.Spacing.xs))
                        Text(
                            text = "Administrator Review & Approval Remarks",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = NexusTokens.SuccessText
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = report.adminRemarks,
                        style = MaterialTheme.typography.bodyMedium,
                        color = NexusTokens.SuccessText,
                        lineHeight = 22.sp
                    )
                }
            }

            // Radar Chart Section
            NexusCard {
                Text(
                    text = "Interactive Radar Competency Breakdown",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = NexusTokens.Foreground
                )
                Text(
                    text = "8-axis spider-web graph displaying normalized score distribution across implementation dimensions.",
                    style = MaterialTheme.typography.bodySmall,
                    color = NexusTokens.ForegroundSecondary,
                    modifier = Modifier.padding(top = 2.dp, bottom = 6.dp)
                )

                InteractiveRadarChart(report = report)
            }

            // Concrete Strengths
            NexusCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NexusTokens.SuccessText, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(NexusTokens.Spacing.xs))
                    Text(
                        text = "Demonstrated Technical Strengths",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = NexusTokens.Foreground
                    )
                }
                Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))
                Text(
                    text = report.strengths,
                    style = MaterialTheme.typography.bodyMedium,
                    color = NexusTokens.Foreground,
                    lineHeight = 22.sp
                )
            }

            // Growth Areas & Recommendations
            NexusCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = NexusTokens.WarningText, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(NexusTokens.Spacing.xs))
                    Text(
                        text = "Identified Growth Areas & Recommendations",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = NexusTokens.Foreground
                    )
                }
                Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))
                Text(
                    text = report.recommendations,
                    style = MaterialTheme.typography.bodyMedium,
                    color = NexusTokens.Foreground,
                    lineHeight = 22.sp
                )
            }

            // Technical Integrity Statement
            NexusCard(
                backgroundColor = NexusTokens.SurfaceSecondary,
                borderColor = NexusTokens.Border
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = NexusTokens.ForegroundSecondary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(NexusTokens.Spacing.xs))
                    Text(
                        text = "Technical Consistency & Integrity Audit",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = NexusTokens.Foreground
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = report.integrityObservations,
                    style = MaterialTheme.typography.bodySmall,
                    color = NexusTokens.ForegroundSecondary,
                    lineHeight = 19.sp
                )
            }

            Spacer(modifier = Modifier.height(NexusTokens.Spacing.md))
        }
    }
}
