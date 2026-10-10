package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
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
fun DemoWelcomeScreen(
    onStartGuidedTour: () -> Unit,
    onExploreFreely: () -> Unit,
    onExitDemo: () -> Unit
) {
    val ivoryBackground = Color(0xFFFCFBF8)
    val charcoalPrimary = Color(0xFF191A1C)
    val mutedGrey = Color(0xFF71717A)
    val subtleBorder = Color(0xFFE7E5E0)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = ivoryBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Top bar with Back/Exit and Demo badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onExitDemo,
                        modifier = Modifier.testTag("demo_exit_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Exit Demo",
                            tint = charcoalPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Exit Demo",
                            color = charcoalPrimary,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    Surface(
                        color = Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFFFDE68A))
                    ) {
                        Text(
                            text = "DEMO MODE",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color(0xFF92400E)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Hero Header
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        color = Color.Transparent,
                        border = BorderStroke(1.dp, subtleBorder),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "FOR JUDGES & EVALUATORS",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.5.sp),
                            color = mutedGrey
                        )
                    }

                    Text(
                        text = "Welcome to Proofolio Demo",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Bold,
                            color = charcoalPrimary,
                            lineHeight = 40.sp
                        )
                    )

                    Text(
                        text = "Explore how Proofolio evaluates student projects and connects talent with internship opportunities.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = mutedGrey,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Options
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Guided Tour Card (Primary)
                    Card(
                        onClick = onStartGuidedTour,
                        colors = CardDefaults.cardColors(containerColor = charcoalPrimary),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("start_guided_tour_card")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = Color(0xFFFDE68A),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "Start Guided Tour",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                }
                                Text(
                                    text = "Step-through from Student Profile → AI Interview → Admin Review → Recruiter Dashboard.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFFD4D4D8)
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Explore Freely Card (Secondary)
                    OutlinedCard(
                        onClick = onExploreFreely,
                        colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, subtleBorder),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("explore_freely_card")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Explore Freely",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = charcoalPrimary
                                )
                                Text(
                                    text = "Navigate freely between student profiles, admin assessments, and recruiter talent pools at your own pace.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = mutedGrey
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = charcoalPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Footer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ISOLATED SESSION   /   NO API KEYS REQUIRED   /   ZERO DATA PERSISTENCE",
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                    color = mutedGrey
                )
            }
        }
    }
}
