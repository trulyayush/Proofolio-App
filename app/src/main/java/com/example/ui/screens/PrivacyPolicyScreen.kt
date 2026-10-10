package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PrivacyPolicyScreen(
    onBack: () -> Unit
) {
    val ivoryBackground = Color(0xFFFCFBF8)
    val charcoalPrimary = Color(0xFF191A1C)
    val mutedGrey = Color(0xFF71717A)
    val subtleBorder = Color(0xFFE7E5E0)

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("privacy_policy_screen"),
        color = ivoryBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Top App Bar
            Surface(
                color = ivoryBackground,
                border = BorderStroke(width = 0.dp, color = Color.Transparent),
                shadowElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("privacy_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = charcoalPrimary
                            )
                        }
                        Text(
                            text = "Privacy Policy",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = charcoalPrimary
                            )
                        )
                    }
                    Text(
                        text = "Draft v1.0",
                        style = MaterialTheme.typography.labelSmall,
                        color = mutedGrey
                    )
                }
            }

            HorizontalDivider(color = subtleBorder, thickness = 1.dp)

            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "Effective Date: October 10, 2026",
                    style = MaterialTheme.typography.bodySmall,
                    color = mutedGrey
                )

                Text(
                    text = "Proofolio (\"we\", \"our\", or \"us\") is committed to protecting your privacy. This Privacy Policy explains how we collect, use, disclose, and safeguard your information when you use our mobile application and platform services.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = charcoalPrimary,
                    lineHeight = 22.sp
                )

                PolicySection(
                    title = "1. Information We Collect",
                    content = "• Registration & Profile: Full name, email address, educational institution/college, and GitHub username.\n• Resume & Credentials: Resume PDF uploads, extracted academic achievements, work history, skills, CGPA, hackathon participations, and certificates.\n• Portfolio & Projects: GitHub repositories, public code links, commit statistics, project descriptions, and verified code contributions.\n• AI Assessment Data: AI-generated interview questions, typed answers, voice transcriptions, audio snippets, assessment scores, technical feedback, and automated integrity/plagiarism signals."
                )

                PolicySection(
                    title = "2. How We Use Your Information",
                    content = "• Generating technical assessments, code reviews, and AI viva interview questions.\n• Calculating verified skill scores and compilation readiness metrics.\n• Connecting qualified students with internship and career opportunities through our matching algorithm.\n• Enabling Admins to review student submissions, manage assessments, and oversee institutional programs.\n• Enabling Recruiters to explore verified candidate profiles and discover qualified talent."
                )

                PolicySection(
                    title = "3. Access & Information Sharing",
                    content = "• Students: Have full control over their profile data, resume uploads, and project submissions.\n• Admins: Can review student submissions, verify hackathon results, manage assessments, and handle institutional internship programs.\n• Recruiters: Can view verified student profiles, project badges, skill summaries, and matched opportunities. Contact details and private data are shared only in accordance with platform matching permissions."
                )

                PolicySection(
                    title = "4. AI Assessments & Limitations",
                    content = "• AI-generated interview evaluations and integrity signals are designed as supportive tooling for human reviewers.\n• AI assessment results may occasionally be imperfect or incomplete.\n• Automated integrity signals and similarity scores do not conclusively prove plagiarism or unauthorized AI use; they serve as advisory indicators for administrators and recruiters."
                )

                PolicySection(
                    title = "5. Demo Mode Isolation",
                    content = "• When Demo Mode is activated for judges or evaluators, the application operates in an isolated sandbox using predefined sample data.\n• Demo activity never creates real student accounts, updates production records, or alters live platform databases."
                )

                PolicySection(
                    title = "6. Data Security & Storage",
                    content = "• We implement industry-standard security measures, secure transport protocols (HTTPS), and encrypted cloud storage to protect your personal information.\n• Data is retained as long as your account remains active or as needed to provide platform services."
                )

                PolicySection(
                    title = "7. Third-Party Services",
                    content = "• We integrate with trusted third-party services including GitHub API (for repository fetching), AI processing models, and secure cloud storage and database infrastructure."
                )

                PolicySection(
                    title = "8. Your Privacy Rights & Contact",
                    content = "• You have the right to access, update, or request deletion of your personal account data at any time.\n• For privacy-related inquiries, data requests, or support, please contact us at support@proofolio.app."
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun PolicySection(title: String, content: String) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF191A1C)
            )
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF4B5563),
                lineHeight = 22.sp
            )
        )
    }
}
