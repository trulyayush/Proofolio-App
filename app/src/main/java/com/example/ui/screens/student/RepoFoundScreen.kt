package com.example.ui.screens.student

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.api.AnalyzeResponse
import com.example.ui.components.Floating3DBackground
import com.example.ui.components.NexusButton
import com.example.ui.components.NexusButtonVariant
import com.example.ui.components.NexusCard
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.NexusTokens

private val LANGUAGE_COLORS = listOf(
    Color(0xFF5558A6), // Accent violet
    Color(0xFF047857), // Emerald
    Color(0xFFB45309), // Amber
    Color(0xFF2563EB), // Blue
    Color(0xFF7C3AED), // Purple
    Color(0xFF898A8D)  // Muted gray for other
)

private data class LangSlice(val name: String, val pct: Double, val color: Color)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RepoFoundScreen(
    analysis: AnalyzeResponse?,
    onStartQuestion1: () -> Unit,
    onBack: () -> Unit
) {
    // Back goes to Dashboard; viva session remains saved on device
    BackHandler {
        onBack()
    }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NexusTokens.Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("repo_found_screen")
    ) {
        Floating3DBackground(modifier = Modifier.fillMaxSize())

        if (analysis == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No analysis found.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = NexusTokens.ForegroundSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    NexusButton(onClick = onBack, variant = NexusButtonVariant.Primary) {
                        Text("Return to Dashboard")
                    }
                }
            }
            return
        }

        val repoName = analysis.repo.fullName.substringAfterLast("/")

        // Prepare language distribution
        val totalLangValue = analysis.repo.languages.values.sum().coerceAtLeast(1.0)
        val sortedLangs = analysis.repo.languages.entries.sortedByDescending { it.value }
        val slices = mutableListOf<LangSlice>()

        if (sortedLangs.size <= 4) {
            sortedLangs.forEachIndexed { i, entry ->
                val fraction = (entry.value / totalLangValue) * 100.0
                slices.add(LangSlice(entry.key, fraction, LANGUAGE_COLORS.getOrElse(i) { LANGUAGE_COLORS.last() }))
            }
        } else {
            val top4 = sortedLangs.take(4)
            val rest = sortedLangs.drop(4)
            top4.forEachIndexed { i, entry ->
                val fraction = (entry.value / totalLangValue) * 100.0
                slices.add(LangSlice(entry.key, fraction, LANGUAGE_COLORS[i]))
            }
            val restFraction = (rest.sumOf { it.value } / totalLangValue) * 100.0
            slices.add(LangSlice("Other", restFraction, LANGUAGE_COLORS.last()))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Navigation bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 680.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("repo_found_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to dashboard",
                        tint = NexusTokens.Foreground
                    )
                }

                Surface(
                    color = NexusTokens.Surface,
                    shape = RoundedCornerShape(NexusTokens.Radius.pill),
                    border = BorderStroke(1.dp, NexusTokens.Border)
                ) {
                    Text(
                        text = "Repository Insights",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = NexusTokens.ForegroundSecondary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.size(48.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Main Content Card
            NexusCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 680.dp),
                backgroundColor = NexusTokens.Surface,
                borderColor = NexusTokens.Border
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    // Header
                    Text(
                        text = "We read $repoName",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = NexusTokens.Foreground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${analysis.repo.filesRead} files read",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NexusTokens.ForegroundSecondary
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Languages Bar
                    if (slices.isNotEmpty()) {
                        Text(
                            text = "Code languages",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = NexusTokens.Foreground
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Horizontal segmented bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(NexusTokens.Radius.pill))
                                .background(NexusTokens.Border)
                        ) {
                            slices.forEach { slice ->
                                val weight = slice.pct.toFloat().coerceAtLeast(0.01f)
                                Box(
                                    modifier = Modifier
                                        .weight(weight)
                                        .height(10.dp)
                                        .background(slice.color)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Legend
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            slices.forEach { slice ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(slice.color)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${slice.name} ${(slice.pct).toInt()}%",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = NexusTokens.ForegroundSecondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // Team repo card (if contribution.teamRepo == true)
                    if (analysis.contribution.teamRepo) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = NexusTokens.SurfaceSecondary,
                            shape = RoundedCornerShape(NexusTokens.Radius.md),
                            border = BorderStroke(1.dp, NexusTokens.Border)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Team repo",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = NexusTokens.Foreground
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "You made ${analysis.contribution.yourCommits} commits here. Your questions are about the files you changed:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NexusTokens.ForegroundSecondary
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                analysis.contribution.focusFiles.take(5).forEach { file ->
                                    Surface(
                                        color = NexusTokens.Surface,
                                        shape = RoundedCornerShape(NexusTokens.Radius.sm),
                                        border = BorderStroke(1.dp, NexusTokens.Border),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = file,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontFamily = FontFamily.Monospace,
                                            color = NexusTokens.Foreground,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // "What your code shows" section
                    if (analysis.repoSkills.isNotEmpty()) {
                        Text(
                            text = "What your code shows",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = NexusTokens.Foreground
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            analysis.repoSkills.forEach { item ->
                                Surface(
                                    color = NexusTokens.SurfaceSecondary,
                                    shape = RoundedCornerShape(NexusTokens.Radius.md),
                                    border = BorderStroke(1.dp, NexusTokens.Border),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Surface(
                                            color = NexusTokens.AccentSubtle,
                                            shape = RoundedCornerShape(NexusTokens.Radius.pill),
                                            border = BorderStroke(1.dp, NexusTokens.Accent.copy(alpha = 0.2f))
                                        ) {
                                            Text(
                                                text = formatSkillTag(item.tag),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.SemiBold,
                                                color = NexusTokens.Accent,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Text(
                                            text = item.evidence,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = NexusTokens.Foreground,
                                            lineHeight = 18.sp,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // Note box
                    Surface(
                        color = NexusTokens.SurfaceSecondary,
                        shape = RoundedCornerShape(NexusTokens.Radius.md),
                        border = BorderStroke(1.dp, NexusTokens.Border),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = NexusTokens.Accent,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Your 5 questions point at specific lines of this code. Each has 90 seconds. Speak or type, in any language.",
                                style = MaterialTheme.typography.bodySmall,
                                color = NexusTokens.ForegroundSecondary,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Primary Button: "Start question 1"
                    NexusButton(
                        onClick = onStartQuestion1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("repo_found_start_question_button"),
                        variant = NexusButtonVariant.Primary
                    ) {
                        Text("Start question 1")
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
