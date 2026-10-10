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
fun TermsConditionsScreen(
    onBack: () -> Unit
) {
    val ivoryBackground = Color(0xFFFCFBF8)
    val charcoalPrimary = Color(0xFF191A1C)
    val mutedGrey = Color(0xFF71717A)
    val subtleBorder = Color(0xFFE7E5E0)

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .testTag("terms_conditions_screen"),
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
                            modifier = Modifier.testTag("terms_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = charcoalPrimary
                            )
                        }
                        Text(
                            text = "Terms & Conditions",
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
                    text = "Welcome to Proofolio. By accessing or using our mobile application and platform services, you agree to be bound by these Terms & Conditions. Please read them carefully before creating an account or submitting projects.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = charcoalPrimary,
                    lineHeight = 22.sp
                )

                TermsSection(
                    title = "1. Account Eligibility & Security",
                    content = "• You must provide accurate and truthful information during registration.\n• You are responsible for maintaining the security of your account credentials and GitHub integration access.\n• Users must be students, educational administrators, or verified recruiters acting in a professional evaluation capacity."
                )

                TermsSection(
                    title = "2. Submitted Content & Intellectual Property",
                    content = "• You retain ownership of your submitted projects, source code, and resumes.\n• By submitting content, you grant Proofolio the license to process, analyze, and display your code repositories and assessment summaries to authorized admins and recruiters for evaluation purposes."
                )

                TermsSection(
                    title = "3. Acceptable Use",
                    content = "• You agree not to submit malicious code, fraudulent repositories, or plagiarized work.\n• Misuse of the AI interview or assessment systems, automated scraping, or attempts to bypass security controls are strictly prohibited."
                )

                TermsSection(
                    title = "4. AI Assessments & Internship Matching",
                    content = "• AI evaluations, scores, and recommendations are provided to assist in talent discovery and do not guarantee internship placement, interviews, or employment.\n• Employers and recruiters make independent hiring decisions."
                )

                TermsSection(
                    title = "5. Admin & Recruiter Access",
                    content = "• Authorized administrators and recruiters are granted controlled access to student profile data and verified badges solely for evaluation and recruitment."
                )

                TermsSection(
                    title = "6. Suspension & Termination",
                    content = "• Proofolio reserves the right to suspend or terminate accounts that violate these Terms & Conditions or engage in fraudulent or abusive platform activity."
                )

                TermsSection(
                    title = "7. Limitation of Liability",
                    content = "• Proofolio is provided on an 'as is' basis without warranties of any kind. We shall not be liable for indirect, incidental, or consequential damages arising from platform usage."
                )

                TermsSection(
                    title = "8. Contact Information",
                    content = "• For questions regarding these Terms & Conditions, please contact us at legal@proofolio.app."
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun TermsSection(title: String, content: String) {
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
