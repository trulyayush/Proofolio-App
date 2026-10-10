package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.NexusTokens

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, borderColor, label) = when (status.uppercase()) {
        "SUBMITTED" -> Quad(NexusTokens.SurfaceSecondary, NexusTokens.ForegroundSecondary, NexusTokens.Border, "Submitted")
        "INTERVIEW_PENDING" -> Quad(NexusTokens.WarningBackground, NexusTokens.WarningText, NexusTokens.WarningBorder, "Interview Pending")
        "EVALUATION_COMPLETED" -> Quad(NexusTokens.SuccessBackground, NexusTokens.SuccessText, NexusTokens.SuccessBorder, "Evaluation Released")
        "UNDER_ADMIN_REVIEW" -> Quad(NexusTokens.InfoBackground, NexusTokens.InfoText, NexusTokens.InfoBorder, "Under Admin Review")
        "SHARED_WITH_RECRUITER" -> Quad(NexusTokens.AccentSubtle, NexusTokens.Accent, NexusTokens.Border, "Shared with Recruiter")
        "SHORTLISTED" -> Quad(NexusTokens.SuccessBackground, NexusTokens.SuccessText, NexusTokens.SuccessBorder, "Shortlisted")
        "ACCEPTED" -> Quad(NexusTokens.SuccessBackground, NexusTokens.SuccessText, NexusTokens.SuccessBorder, "Mutual Connection")
        "PENDING" -> Quad(NexusTokens.WarningBackground, NexusTokens.WarningText, NexusTokens.WarningBorder, "Request Pending")
        else -> Quad(NexusTokens.SurfaceSecondary, NexusTokens.ForegroundSecondary, NexusTokens.Border, status)
    }

    Surface(
        modifier = modifier,
        color = bgColor,
        shape = RoundedCornerShape(NexusTokens.Radius.pill),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun SkillChip(
    skill: String,
    modifier: Modifier = Modifier
) {
    NexusSkillChip(skill = skill, modifier = modifier)
}

@Composable
fun ScoreBadge(
    score: Int,
    label: String = "Score",
    modifier: Modifier = Modifier
) {
    val (bg, fg, border) = when {
        score >= 85 -> Triple(NexusTokens.SuccessBackground, NexusTokens.SuccessText, NexusTokens.SuccessBorder)
        score >= 70 -> Triple(NexusTokens.WarningBackground, NexusTokens.WarningText, NexusTokens.WarningBorder)
        else -> Triple(NexusTokens.SurfaceSecondary, NexusTokens.ForegroundSecondary, NexusTokens.Border)
    }

    Surface(
        modifier = modifier,
        color = bg,
        shape = RoundedCornerShape(NexusTokens.Radius.sm),
        border = BorderStroke(1.dp, border)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$score",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = fg
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = "/ 100",
                style = MaterialTheme.typography.labelSmall,
                color = fg.copy(alpha = 0.8f)
            )
        }
    }
}
