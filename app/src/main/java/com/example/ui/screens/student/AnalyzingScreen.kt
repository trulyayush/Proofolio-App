package com.example.ui.screens.student

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.api.AnalyzeResponse
import com.example.ui.components.Floating3DBackground
import com.example.ui.components.NexusButton
import com.example.ui.components.NexusButtonVariant
import com.example.ui.components.NexusCard
import com.example.ui.theme.NexusTokens
import com.example.viewmodel.Load

@Composable
fun AnalyzingScreen(
    repoFullName: String,
    analyzeState: Load<AnalyzeResponse>,
    onStartAnalyze: (String) -> Unit,
    onCancelAnalyze: () -> Unit,
    onPickAnotherRepo: () -> Unit,
    onBack: () -> Unit
) {
    val repoName = repoFullName.substringAfterLast("/")

    // When this screen opens, start analysis once
    LaunchedEffect(repoFullName) {
        onStartAnalyze(repoFullName)
    }

    // Cancel analysis if user presses back
    BackHandler {
        onCancelAnalyze()
        onBack()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NexusTokens.Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("analyzing_screen"),
        contentAlignment = Alignment.Center
    ) {
        Floating3DBackground(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            NexusCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 520.dp),
                backgroundColor = NexusTokens.Surface,
                borderColor = NexusTokens.Border
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (analyzeState) {
                        is Load.Loading, is Load.Idle -> {
                            CircularProgressIndicator(
                                color = NexusTokens.Accent,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(48.dp)
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Text(
                                text = "Reading $repoName and writing 5 questions about your code. This usually takes 15–30 seconds.",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = NexusTokens.Foreground,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(28.dp))

                            NexusButton(
                                onClick = {
                                    onCancelAnalyze()
                                    onBack()
                                },
                                variant = NexusButtonVariant.Outline,
                                icon = Icons.AutoMirrored.Filled.ArrowBack,
                                testTag = "analyzing_back_button"
                            ) {
                                Text("Cancel")
                            }
                        }

                        is Load.Failed -> {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = NexusTokens.ErrorText,
                                modifier = Modifier.size(44.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Unable to analyze $repoName",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = NexusTokens.Foreground,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = analyzeState.error.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = NexusTokens.ForegroundSecondary,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                NexusButton(
                                    onClick = { onStartAnalyze(repoFullName) },
                                    variant = NexusButtonVariant.Primary,
                                    icon = Icons.Default.Refresh,
                                    modifier = Modifier.weight(1f),
                                    testTag = "analyzing_try_again_button"
                                ) {
                                    Text("Try again")
                                }

                                NexusButton(
                                    onClick = onPickAnotherRepo,
                                    variant = NexusButtonVariant.Secondary,
                                    modifier = Modifier.weight(1f),
                                    testTag = "analyzing_pick_another_button"
                                ) {
                                    Text("Pick another repo")
                                }
                            }
                        }

                        is Load.Ready -> {
                            // When ready, AppViewModel already called replace(Screen.RepoFound).
                            // Display transient progress while transition executes.
                            CircularProgressIndicator(
                                color = NexusTokens.Accent,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
