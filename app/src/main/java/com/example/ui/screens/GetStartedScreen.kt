package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GetStartedScreen(
    onStudentClick: () -> Unit,
    onAdminClick: () -> Unit,
    onRecruiterClick: () -> Unit,
    onExploreDemoClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onTermsClick: () -> Unit
) {
    val backgroundColor = Color(0xFFFCFBF8)
    val charcoalPrimary = Color(0xFF191A1C)
    val mutedGrey = Color(0xFF71717A)
    val subtleBorder = Color(0xFFE7E5E0)

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("get_started_screen"),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Top Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo & Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(charcoalPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(18.dp)) {
                            val path = Path().apply {
                                moveTo(0f, size.height)
                                lineTo(size.width * 0.6f, 0f)
                                lineTo(size.width, size.height * 0.4f)
                            }
                            drawPath(
                                path = path,
                                color = Color(0xFFC5A059),
                                style = Stroke(width = 3.5f)
                            )
                        }
                    }
                    Text(
                        text = "Proofolio",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            letterSpacing = (-0.5).sp
                        ),
                        color = charcoalPrimary
                    )
                }

                // Top Right Tagline
                Text(
                    text = "Real Projects.\nBetter Opportunities.",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        lineHeight = 14.sp,
                        letterSpacing = 0.2.sp
                    ),
                    color = mutedGrey,
                    textAlign = TextAlign.End
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Hero Section
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Outlined Pill
                Surface(
                    color = Color.Transparent,
                    shape = RoundedCornerShape(50.dp),
                    border = BorderStroke(1.dp, subtleBorder)
                ) {
                    Text(
                        text = "GET STARTED",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = mutedGrey,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // Headline & Subtitle
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = androidx.compose.ui.text.buildAnnotatedString {
                                append("Your journey to\nwhat’s ")
                                pushStyle(androidx.compose.ui.text.SpanStyle(color = mutedGrey))
                                append("next.")
                                pop()
                            },
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 34.sp,
                                lineHeight = 40.sp,
                                letterSpacing = (-1.0).sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = charcoalPrimary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Choose how you want to begin. Each path is designed for your role, goals and opportunities.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            ),
                            color = mutedGrey
                        )
                    }

                    // 3D Illustration
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .padding(start = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val pathRibbon = Path().apply {
                                moveTo(size.width * 0.2f, size.height * 0.8f)
                                cubicTo(
                                    size.width * 0.1f, size.height * 0.2f,
                                    size.width * 0.9f, size.height * 0.1f,
                                    size.width * 0.8f, size.height * 0.7f
                                )
                                cubicTo(
                                    size.width * 0.75f, size.height * 0.9f,
                                    size.width * 0.4f, size.height * 0.9f,
                                    size.width * 0.3f, size.height * 0.6f
                                )
                            }
                            drawPath(
                                path = pathRibbon,
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFFF4F1EA), Color(0xFFE5DFD3)),
                                    start = Offset(0f, 0f),
                                    end = Offset(size.width, size.height)
                                ),
                                style = Stroke(width = 16f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                            )

                            drawRect(
                                color = Color(0xFFFFFFFF),
                                topLeft = Offset(size.width * 0.3f, size.height * 0.35f),
                                size = androidx.compose.ui.geometry.Size(size.width * 0.45f, size.height * 0.45f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Role Selection Cards using Column with verticalArrangement = Arrangement.spacedBy(16.dp)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                GetStartedRoleCard(
                    title = "Student",
                    description = "Showcase your skills, complete assessments and get matched with internships.",
                    icon = Icons.Default.School,
                    iconTint = Color(0xFFB45309),
                    iconContainerBg = Color(0xFFFDF8F3),
                    onClick = onStudentClick,
                    testTag = "role_student_card"
                )

                GetStartedRoleCard(
                    title = "Admin",
                    description = "Review students, manage assessments and handle internship programs.",
                    icon = Icons.Default.Security,
                    iconTint = Color(0xFF3B82F6),
                    iconContainerBg = Color(0xFFF0F4FF),
                    onClick = onAdminClick,
                    testTag = "role_admin_card"
                )

                GetStartedRoleCard(
                    title = "Recruiter",
                    description = "Discover top talent, explore profiles and find the right candidates.",
                    icon = Icons.Default.Work,
                    iconTint = Color(0xFF10B981),
                    iconContainerBg = Color(0xFFF0FDF4),
                    onClick = onRecruiterClick,
                    testTag = "role_recruiter_card"
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Divider with OR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Divider(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp),
                    color = subtleBorder
                )
                Text(
                    text = "OR",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, letterSpacing = 1.sp),
                    color = mutedGrey,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Divider(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp),
                    color = subtleBorder
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Explore Demo Card
            GetStartedDemoCard(
                onClick = onExploreDemoClick,
                testTag = "explore_demo_button"
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Footer
            Text(
                text = "SKILLS   /   PROJECTS   /   OPPORTUNITIES",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = mutedGrey.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Privacy Policy",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = mutedGrey,
                    modifier = Modifier
                        .clickable(onClick = onPrivacyPolicyClick)
                        .testTag("get_started_privacy_link")
                        .padding(4.dp)
                )
                Text(
                    text = " · ",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = mutedGrey
                )
                Text(
                    text = "Terms & Conditions",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                    color = mutedGrey,
                    modifier = Modifier
                        .clickable(onClick = onTermsClick)
                        .testTag("get_started_terms_link")
                        .padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun GetStartedRoleCard(
    title: String,
    description: String,
    icon: ImageVector,
    iconTint: Color,
    iconContainerBg: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(testTag),
        color = Color(0xFFFFFFFF),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFE7E5E0)),
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconContainerBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = Color(0xFF191A1C)
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    ),
                    color = Color(0xFF71717A)
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color(0xFF71717A),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun GetStartedDemoCard(
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag(testTag),
        color = Color(0xFFFFFFFF),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFE7E5E0)),
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF3F4F6)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color(0xFF4B5563),
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "Explore Demo",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    ),
                    color = Color(0xFF191A1C)
                )
                Text(
                    text = "Take a guided tour of the platform (For judges).",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    ),
                    color = Color(0xFF71717A)
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color(0xFF71717A),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
