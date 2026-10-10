package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.NexusTokens
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentRose

@Composable
fun InterviewProgressBar(
    totalQuestions: Int,
    answeredIds: List<String>,
    skippedIds: List<String>,
    modifier: Modifier = Modifier
) {
    if (totalQuestions == 0) return

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(NexusTokens.Border),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        val segmentWeight = 1f / totalQuestions
        
        for (i in 0 until totalQuestions) {
            val questionId = "q_${i + 1}" // Assuming question IDs are indexed like this based on common patterns
            
            val color = when {
                answeredIds.contains(questionId) -> AccentEmerald
                skippedIds.contains(questionId) -> AccentRose
                else -> NexusTokens.Border
            }
            
            Box(
                modifier = Modifier
                    .weight(segmentWeight)
                    .height(6.dp)
                    .background(color)
            )
        }
    }
}
