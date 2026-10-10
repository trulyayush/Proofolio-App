package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import com.example.ui.theme.NexusTokens
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class RadarDimension(
    val title: String,
    val score: Int,
    val description: String,
    val recommendation: String = ""
)

@Composable
fun InteractiveRadarChart(
    dimensions: List<RadarDimension>,
    modifier: Modifier = Modifier
) {
    if (dimensions.isEmpty()) return

    var selectedIndex by remember { mutableStateOf<Int?>(0) }
    val progress = remember { Animatable(0f) }

    LaunchedEffect(dimensions) {
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
                    .pointerInput(dimensions) {
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
        val selected = selectedIndex?.let { dimensions.getOrNull(it) } ?: dimensions.firstOrNull()
        if (selected != null) {
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = selected.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = NexusTokens.Foreground
                        )
                        Text(
                            text = "Verified Skill Dimension",
                            style = MaterialTheme.typography.labelSmall,
                            color = NexusTokens.ForegroundSecondary
                        )
                    }

                    ScoreBadge(score = selected.score)
                }

                if (selected.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))
                    Text(
                        text = selected.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = NexusTokens.ForegroundSecondary,
                        lineHeight = 20.sp
                    )
                }

                // If recommendation is blank, don't draw it
                if (selected.recommendation.isNotBlank()) {
                    Spacer(modifier = Modifier.height(NexusTokens.Spacing.sm))
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
    }
}

// Overload for map of skills for convenience
@Composable
fun InteractiveRadarChart(
    skills: Map<String, Int>,
    modifier: Modifier = Modifier
) {
    val dimensions = remember(skills) {
        listOf(
            RadarDimension("Backend", skills["backend"] ?: 0, if (skills.containsKey("backend")) "Score ${skills["backend"]}/100" else "Not shown"),
            RadarDimension("Frontend", skills["frontend"] ?: 0, if (skills.containsKey("frontend")) "Score ${skills["frontend"]}/100" else "Not shown"),
            RadarDimension("Data", skills["data"] ?: 0, if (skills.containsKey("data")) "Score ${skills["data"]}/100" else "Not shown"),
            RadarDimension("AI/ML", skills["ai_ml"] ?: 0, if (skills.containsKey("ai_ml")) "Score ${skills["ai_ml"]}/100" else "Not shown"),
            RadarDimension("DevOps", skills["devops"] ?: 0, if (skills.containsKey("devops")) "Score ${skills["devops"]}/100" else "Not shown"),
            RadarDimension("Security", skills["security"] ?: 0, if (skills.containsKey("security")) "Score ${skills["security"]}/100" else "Not shown")
        )
    }
    InteractiveRadarChart(dimensions = dimensions, modifier = modifier)
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
    if (count == 0) return
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
        val scoreFraction = (dimensions[i].score / 100f).coerceIn(0.05f, 1f) * progress
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
