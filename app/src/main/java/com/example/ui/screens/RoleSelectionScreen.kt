package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.Floating3DBackground
import com.example.ui.theme.NexusTokens

/**
 * Editorial Landing Screen:
 * Exceptionally clean, memorable, quiet confidence, bold typography, warm-white canvas,
 * refined 3D background, and three distinct architectural role options.
 */
@Composable
fun RoleSelectionScreen(
    onSelectRole: (String) -> Unit // "STUDENT", "ADMIN", "RECRUITER"
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NexusTokens.Background)
            .testTag("role_selection_screen")
    ) {
        // Signature 3D Visual Language: multi-faceted polyhedra with soft studio lighting
        Floating3DBackground(
            modifier = Modifier.fillMaxSize()
        )

        // Scrollable Editorial Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // 1. Technical Platform Masthead Badge
            Surface(
                color = NexusTokens.Surface,
                shape = RoundedCornerShape(NexusTokens.Radius.pill),
                border = BorderStroke(1.dp, NexusTokens.Border),
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(NexusTokens.Accent)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "NX-2026 // VERIFIED INTELLIGENCE ENGINE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.2.sp,
                        color = NexusTokens.Foreground
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Main Display Headline
            Text(
                text = "NexusIntern",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 44.sp,
                    lineHeight = 48.sp,
                    letterSpacing = (-1.2).sp
                ),
                fontWeight = FontWeight.Bold,
                color = NexusTokens.Foreground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Subtitle with comfortable reading width
            Text(
                text = "Autonomous repository-specific technical assessments, verified competency dossiers, and zero-fabrication internship matching.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                ),
                color = NexusTokens.ForegroundSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 500.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Technical Credibility Strip
            Surface(
                color = NexusTokens.SurfaceSecondary,
                shape = RoundedCornerShape(NexusTokens.Radius.md),
                border = BorderStroke(1.dp, NexusTokens.Border),
                modifier = Modifier.widthIn(max = 540.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 9.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CredibilityMetric(label = "EVALUATIONS", value = "Code-Verified")
                    Box(modifier = Modifier.width(1.dp).height(18.dp).background(NexusTokens.Border))
                    CredibilityMetric(label = "RANKING", value = "Zero-Fabrication")
                    Box(modifier = Modifier.width(1.dp).height(18.dp).background(NexusTokens.Border))
                    CredibilityMetric(label = "PRIVACY", value = "Role-Gated")
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Section Label
            Text(
                text = "SELECT ACCESS PORTAL",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.4.sp,
                color = NexusTokens.ForegroundTertiary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 4. The Three Distinct Architectural Role Portals
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 540.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Portal 01: Student
                ArchitecturalRoleCard(
                    index = "01",
                    roleName = "Student Engineering Portal",
                    tagline = "Technical portfolio, codebase extraction & AI radar evaluation",
                    description = "Submit GitHub repositories, complete structured AI voice & text technical interviews, and publish verified competency radar reports.",
                    icon = Icons.Default.School,
                    tags = listOf("Codebase Extraction", "AI Radar Dossier", "Mutual Network"),
                    badgeText = "Candidate Workspace",
                    badgeColor = NexusTokens.SuccessBackground,
                    badgeTextColor = NexusTokens.SuccessText,
                    badgeBorder = NexusTokens.SuccessBorder,
                    ctaText = "Enter Student Workspace",
                    testTag = "login_as_student_button",
                    onClick = { onSelectRole("STUDENT") }
                )

                // Portal 02: Admin
                ArchitecturalRoleCard(
                    index = "02",
                    roleName = "Institutional Command Center",
                    tagline = "Multi-axis talent filtering, audit dossiers & recruitment campaigns",
                    description = "Inspect student repositories, audit verified radar evaluations, release feedback reports, and curate targeted candidate forwarding.",
                    icon = Icons.Default.AdminPanelSettings,
                    tags = listOf("Audit Dossiers", "Talent Matrix", "Governance"),
                    badgeText = "Institutional Authority",
                    badgeColor = NexusTokens.InfoBackground,
                    badgeTextColor = NexusTokens.InfoText,
                    badgeBorder = NexusTokens.InfoBorder,
                    ctaText = "Launch Command Console",
                    testTag = "login_as_admin_button",
                    onClick = { onSelectRole("ADMIN") }
                )

                // Portal 03: Recruiter
                ArchitecturalRoleCard(
                    index = "03",
                    roleName = "Enterprise Recruiter Portal",
                    tagline = "Verified evidence matching, AI justifications & direct outreach",
                    description = "Post technical requirements, inspect candidates with match score breakdowns, and review code-backed performance evidence.",
                    icon = Icons.Default.Business,
                    tags = listOf("Verified Evidence", "AI Match Justification", "Direct Outreach"),
                    badgeText = "Curated Hiring",
                    badgeColor = NexusTokens.WarningBackground,
                    badgeTextColor = NexusTokens.WarningText,
                    badgeBorder = NexusTokens.WarningBorder,
                    ctaText = "Access Recruiter Portal",
                    testTag = "login_as_recruiter_button",
                    onClick = { onSelectRole("RECRUITER") }
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Trust & Integrity Guarantee
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = NexusTokens.ForegroundTertiary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Role-gated privacy • Transparent rubric scoring • No fabricated rankings",
                    style = MaterialTheme.typography.labelSmall,
                    color = NexusTokens.ForegroundTertiary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CredibilityMetric(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            fontSize = 9.sp,
            letterSpacing = 0.8.sp,
            color = NexusTokens.ForegroundTertiary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = NexusTokens.Foreground
        )
    }
}

@Composable
private fun ArchitecturalRoleCard(
    index: String,
    roleName: String,
    tagline: String,
    description: String,
    icon: ImageVector,
    tags: List<String>,
    badgeText: String,
    badgeColor: Color,
    badgeTextColor: Color,
    badgeBorder: Color,
    ctaText: String,
    testTag: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(NexusTokens.Radius.lg),
        colors = CardDefaults.cardColors(
            containerColor = if (isPressed) NexusTokens.SurfaceSecondary else NexusTokens.Surface
        ),
        border = BorderStroke(
            1.dp,
            if (isPressed) NexusTokens.Accent else NexusTokens.Border
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isPressed) 1.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Top Bar: Index + Role Badge + Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Architectural Numeric Index
                    Text(
                        text = index,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = NexusTokens.ForegroundTertiary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Status Badge
                    Surface(
                        color = badgeColor,
                        shape = RoundedCornerShape(NexusTokens.Radius.pill),
                        border = BorderStroke(1.dp, badgeBorder)
                    ) {
                        Text(
                            text = badgeText,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp,
                            color = badgeTextColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                // Clean Icon Box
                Surface(
                    color = NexusTokens.SurfaceSecondary,
                    shape = RoundedCornerShape(NexusTokens.Radius.sm),
                    border = BorderStroke(1.dp, NexusTokens.Border),
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = NexusTokens.Foreground,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Role Title
            Text(
                text = roleName,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp,
                    lineHeight = 22.sp
                ),
                fontWeight = FontWeight.SemiBold,
                color = NexusTokens.Foreground
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Description
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                ),
                color = NexusTokens.ForegroundSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Capability Tags
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                tags.forEach { tag ->
                    Surface(
                        color = NexusTokens.SurfaceSecondary,
                        shape = RoundedCornerShape(NexusTokens.Radius.sm),
                        border = BorderStroke(1.dp, NexusTokens.Border)
                    ) {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = NexusTokens.ForegroundSecondary,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Trigger Strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = ctaText,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = NexusTokens.Accent
                )

                Surface(
                    color = NexusTokens.AccentSubtle,
                    shape = CircleShape,
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = NexusTokens.Accent,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        }
    }
}
