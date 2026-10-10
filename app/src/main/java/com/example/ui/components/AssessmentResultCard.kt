package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.NexusTokens
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentRose

@Composable
fun AssessmentResultCard(
    skills: Map<String, Int>,
    modifier: Modifier = Modifier
) {
    val strengths = remember(skills) { skills.filter { it.value >= 70 }.keys.toList() }
    val improvementAreas = remember(skills) { skills.filter { it.value < 70 }.keys.toList() }

    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = "Assessment Results",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = NexusTokens.Foreground
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        InteractiveRadarChart(skills = skills)

        Spacer(modifier = Modifier.height(16.dp))

        if (strengths.isNotEmpty()) {
            Text(
                text = "Strengths",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = AccentEmerald
            )
            Text(
                text = strengths.joinToString(", ") { it.replaceFirstChar { char -> char.uppercase() } },
                style = MaterialTheme.typography.bodyMedium,
                color = NexusTokens.ForegroundSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (improvementAreas.isNotEmpty()) {
            Text(
                text = "Areas for Improvement",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = AccentRose
            )
            Text(
                text = improvementAreas.joinToString(", ") { it.replaceFirstChar { char -> char.uppercase() } },
                style = MaterialTheme.typography.bodyMedium,
                color = NexusTokens.ForegroundSecondary
            )
        }
    }
}
