package com.example.ui.screens.student

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.api.ReportResponse
import com.example.ui.components.NexusButton
import com.example.ui.components.NexusButtonVariant
import com.example.ui.components.NexusCard
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.AccentEmeraldLight
import com.example.ui.theme.NexusTokens
import com.example.viewmodel.AppViewModel
import com.example.viewmodel.Load

@Composable
fun VivaCompleteScreen(
    viewModel: AppViewModel,
    onSeeReport: () -> Unit,
    onBackToDashboard: () -> Unit
) {
    BackHandler {
        onBackToDashboard()
    }

    LaunchedEffect(Unit) {
        // When it opens:
        // set store.sessionId = null;
        viewModel.sessionStore.sessionId = null
        // reload reportState;
        viewModel.loadReport()
    }

    val reportState by viewModel.reportState.collectAsStateWithLifecycle()
    val lastAnalysis = viewModel.lastAnalysis
    val vivaRepoFullName = lastAnalysis?.repo?.fullName

    val readyReport: ReportResponse? = (reportState as? Load.Ready)?.value

    // Check if a report.hackathons item has the same repoFullName as the viva and verified is true
    val matchedHackathon = readyReport?.hackathons?.firstOrNull {
        it.repoFullName.equals(vivaRepoFullName, ignoreCase = true) && it.verified
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NexusTokens.Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .testTag("viva_complete_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Success Icon
        Surface(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            color = AccentEmeraldLight,
            border = BorderStroke(2.dp, AccentEmerald.copy(alpha = 0.3f))
        ) {
            Icon(
                imageVector = Icons.Default.Verified,
                contentDescription = null,
                tint = AccentEmerald,
                modifier = Modifier
                    .padding(20.dp)
                    .size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Viva complete",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = NexusTokens.Foreground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your report is ready.",
            style = MaterialTheme.typography.bodyLarge,
            color = NexusTokens.ForegroundSecondary,
            textAlign = TextAlign.Center
        )

        // Highlighted Hackathon Verification Card if matched
        if (matchedHackathon != null) {
            Spacer(modifier = Modifier.height(24.dp))

            val resultDisplay = matchedHackathon.result.replaceFirstChar { it.uppercase() }
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = AccentEmeraldLight,
                shape = RoundedCornerShape(NexusTokens.Radius.md),
                border = BorderStroke(1.5.dp, AccentEmerald.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = AccentEmerald,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Hackathon Verified",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = AccentEmerald
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "This viva also verified: $resultDisplay at ${matchedHackathon.event} ${matchedHackathon.year}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = NexusTokens.Foreground
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Actions
        NexusButton(
            onClick = onSeeReport,
            variant = NexusButtonVariant.Primary,
            trailingIcon = Icons.AutoMirrored.Filled.ArrowForward,
            modifier = Modifier.fillMaxWidth(),
            testTag = "see_my_report_button"
        ) {
            Text("See my report")
        }

        Spacer(modifier = Modifier.height(12.dp))

        NexusButton(
            onClick = onBackToDashboard,
            variant = NexusButtonVariant.Outline,
            modifier = Modifier.fillMaxWidth(),
            testTag = "back_to_dashboard_button"
        ) {
            Text("Back to dashboard")
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
