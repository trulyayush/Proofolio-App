package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AssessmentReportEntity
import com.example.ui.theme.NexusTokens
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class RadarDimension(
    val title: String,
    val score: Int,
    val description: String,
    val recommendation: String
)

@Composable
fun InteractiveRadarChart(
    report: AssessmentReportEntity,
    modifier: Modifier = Modifier
) {
    val dimensions = remember(report) {
        listOf(
            RadarDimension("Technical Understanding", report.technicalUnderstandingScore, "Grasp of foundational CS concepts, framework lifecycles, and core paradigms.", "Deepen understanding of operating system primitives and memory models."),
            RadarDimension("Implementation Knowledge", report.implementationKnowledgeScore, "Hands-on fluency with language idioms, APIs, and clean code conventions.", "Refactor repeated patterns into reusable utility modules and domain extensions."),
            RadarDimension("Problem Solving", report.problemSolvingScore, "Analytical breakdown of engineering constraints and trade-off decisions.", "Practice documenting edge-case trade-offs in structured Architecture Decision Records (ADRs)."),
            RadarDimension("Architecture & Org", report.architectureScore, "Module boundary separation, dependency management, and scalability patterns.", "Ensure clean unidirectional data flow and isolate presentation from domain logic."),
            RadarDimension("Algorithms & Data", report.algorithmsScore, "Efficiency of data structures, complexity analysis, and algorithmic tuning.", "Benchmark lookup and indexing operations under large dataset scenarios."),
            RadarDimension("Debugging & Edge Cases", report.debuggingEdgeCasesScore, "Resilience against race conditions, nullability, and runtime exceptions.", "Add automated fuzzing and stress testing for concurrent async workflows."),
            RadarDimension("Project Explanation", report.explanationAbilityScore, "Ability to articulate system design, trade-offs, and rationale clearly.", "Focus on communicating the 'why' behind architectural choices rather than just the 'what'."),
            RadarDimension("Communication Clarity", report.communicationClarityScore, "Technical precision, structured answers, and professional dialogue.", "Summarize complex solutions with a high-level thesis followed by technical depth.")
        )
    }

    var selectedIndex by remember { mutableStateOf<Int?>(0) }
    val progress = remember { Animatable(0f) }

    LaunchedEffect(report.id) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = NexusTokens.Spacing.xs),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Radar Canvas
        Box(
            modifier = Modifier
                .size(310.dp)
                .padding(NexusTokens.Spacing.md),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .size(270.dp)
                    .pointerInput(report.id) {
                        detectTapGestures { tapOffset ->
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val radius = minOf(size.width, size.height) / 2f * 0.78f
                            val count = dimensions.size
                            val step = (2.0 * Math.PI) / count
                            var closestIndex = 0
                            var minDistance = Float.MAX_VALUE

                            for (i in 0 until count) {
                                val angle = -Math.PI / 2.0 + i * step
                                val vx = (center.x + radius * cos(angle)).toFloat()
                                val vy = (center.y + radius * sin(angle)).toFloat()
                                val dist = sqrt((tapOffset.x - vx) * (tapOffset.x - vx) + (tapOffset.y - vy) * (tapOffset.y - vy))
                                if (dist < minDistance) {
                                    minDistance = dist
                                    closestIndex = i
                                }
                            }
                            selectedIndex = closestIndex
                        }
                    }
            ) {
                drawSpiderWebRadar(
                    dimensions = dimensions,
                    progress = progress.value,
                    selectedIndex = selectedIndex,
                    gridColor = NexusTokens.BorderStrong,
                    accentColor = NexusTokens.Accent,
                    activeColor = NexusTokens.Foreground
                )
            }
        }

        // Tap hint
        Text(
            text = "Tap any axis or score point to inspect evaluation details",
            style = MaterialTheme.typography.labelSmall,
            color = NexusTokens.ForegroundTertiary,
            modifier = Modifier.padding(bottom = NexusTokens.Spacing.sm)
        )

        // Selected Dimension Inspection Card
        val selected = selectedIndex?.let { dimensions.getOrNull(it) } ?: dimensions.first()
        NexusCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = NexusTokens.Surface,
            borderColor = NexusTokens.Border
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = selected.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = NexusTokens.Foreground
                    )
                    Text(
                        text = "Assessment Dimension",
                        style = MaterialTheme.typography.labelSmall,
                        color = NexusTokens.ForegroundSecondary
                    )
                }

                ScoreBadge(score = selected.score)
            }

            Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))

            Text(
                text = selected.description,
                style = MaterialTheme.typography.bodySmall,
                color = NexusTokens.ForegroundSecondary,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(NexusTokens.Spacing.sm))

            // Targeted Suggestion Box
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = NexusTokens.SurfaceSecondary,
                shape = RoundedCornerShape(NexusTokens.Radius.sm),
                border = BorderStroke(1.dp, NexusTokens.Border)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Targeted Recommendation",
                        tint = NexusTokens.Accent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(NexusTokens.Spacing.xs))
                    Column {
                        Text(
                            text = "Targeted Growth Recommendation",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = NexusTokens.Accent
                        )
                        Text(
                            text = selected.recommendation,
                            style = MaterialTheme.typography.bodySmall,
                            color = NexusTokens.ForegroundSecondary
                        )
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawSpiderWebRadar(
    dimensions: List<RadarDimension>,
    progress: Float,
    selectedIndex: Int?,
    gridColor: Color,
    accentColor: Color,
    activeColor: Color
) {
    val center = Offset(size.width / 2f, size.height / 2f)
    val maxRadius = size.minDimension / 2f * 0.72f
    val count = dimensions.size
    val angleStep = (2.0 * Math.PI) / count

    // Concentric Guide Polygons
    val rings = listOf(0.2f, 0.4f, 0.6f, 0.8f, 1.0f)
    for (fraction in rings) {
        val ringPath = Path()
        val r = maxRadius * fraction
        for (i in 0 until count) {
            val angle = -Math.PI / 2.0 + i * angleStep
            val px = (center.x + r * cos(angle)).toFloat()
            val py = (center.y + r * sin(angle)).toFloat()
            if (i == 0) ringPath.moveTo(px, py) else ringPath.lineTo(px, py)
        }
        ringPath.close()
        drawPath(ringPath, gridColor.copy(alpha = 0.5f), style = Stroke(width = 0.9f))
    }

    // Radial Axis Lines
    for (i in 0 until count) {
        val angle = -Math.PI / 2.0 + i * angleStep
        val px = (center.x + maxRadius * cos(angle)).toFloat()
        val py = (center.y + maxRadius * sin(angle)).toFloat()
        drawLine(
            color = gridColor.copy(alpha = 0.6f),
            start = center,
            end = Offset(px, py),
            strokeWidth = 0.9f
        )
    }

    // Candidate Score Polygon
    val dataPath = Path()
    val pointPositions = mutableListOf<Offset>()

    for (i in 0 until count) {
        val angle = -Math.PI / 2.0 + i * angleStep
        val scoreFraction = (dimensions[i].score / 100f).coerceIn(0.1f, 1f) * progress
        val r = maxRadius * scoreFraction
        val px = (center.x + r * cos(angle)).toFloat()
        val py = (center.y + r * sin(angle)).toFloat()
        val pos = Offset(px, py)
        pointPositions.add(pos)
        if (i == 0) dataPath.moveTo(px, py) else dataPath.lineTo(px, py)
    }
    dataPath.close()

    // Shaded polygon with soft accent tint
    drawPath(dataPath, accentColor.copy(alpha = 0.16f), style = Fill)
    drawPath(dataPath, accentColor, style = Stroke(width = 2.0f))

    // Data Vertex Points
    for (i in 0 until count) {
        val pos = pointPositions[i]
        val isSelected = (i == selectedIndex)
        val pointRadius = if (isSelected) 5.0f else 3.2f

        drawCircle(
            color = Color.White,
            radius = pointRadius + 1.8f,
            center = pos
        )
        drawCircle(
            color = if (isSelected) activeColor else accentColor,
            radius = pointRadius,
            center = pos
        )
    }

    // Outer Vertex Numbers
    val textRadius = maxRadius * 1.15f
    val textPaint = android.graphics.Paint().apply {
        color = android.graphics.Color.parseColor("#66686C")
        textSize = 25f
        textAlign = android.graphics.Paint.Align.CENTER
        isAntiAlias = true
        typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
    }

    for (i in 0 until count) {
        val angle = -Math.PI / 2.0 + i * angleStep
        val tx = (center.x + textRadius * cos(angle)).toFloat()
        val ty = (center.y + textRadius * sin(angle)).toFloat() + 9f

        val scoreText = "${dimensions[i].score}"
        drawContext.canvas.nativeCanvas.drawText(scoreText, tx, ty, textPaint)
    }
}
