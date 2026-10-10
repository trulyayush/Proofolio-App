package com.example.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CampaignMessageEntity
import com.example.data.model.StudentEntity
import com.example.ui.components.NexusBadge
import com.example.ui.components.NexusButton
import com.example.ui.components.NexusButtonVariant
import com.example.ui.components.NexusCard
import com.example.ui.components.NexusHeader
import com.example.ui.components.NexusTextField
import com.example.ui.theme.NexusTokens

@Composable
fun AdminCampaignScreen(
    students: List<StudentEntity>,
    campaignHistory: List<CampaignMessageEntity>,
    onSendCampaign: (subject: String, body: String, filter: String, count: Int, channel: String, template: String) -> Unit,
    onBack: () -> Unit
) {
    var selectedTemplate by remember { mutableStateOf("Shortlist Notification") }
    var filterSkill by remember { mutableStateOf("ALL") }
    var minCgpa by remember { mutableStateOf(8.5) }
    var channel by remember { mutableStateOf("EMAIL_AND_INAPP") }

    var subject by remember {
        mutableStateOf("NexusIntern: You have been shortlisted for Cloud Systems Internships")
    }
    var bodyContent by remember {
        mutableStateOf("Congratulations! Following review of your project assessment and technical radar score breakdown, your portfolio has been shortlisted for premier infrastructure opportunities.")
    }

    var showConfirmDialog by remember { mutableStateOf(false) }
    var isSending by remember { mutableStateOf(false) }
    var successToast by remember { mutableStateOf<String?>(null) }

    // Filter recipients preview
    val matchedStudents = students.filter { s ->
        val skillOk = filterSkill == "ALL" || s.technicalSkills.contains(filterSkill, ignoreCase = true)
        val cgpaOk = s.cgpa >= minCgpa
        skillOk && cgpaOk
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NexusTokens.Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("admin_campaign_screen")
    ) {
        // App Header
        NexusHeader(
            title = "Admin Email & Campaign Center",
            subtitle = "Targeted notifications with verified delivery tracking",
            badgeText = "Outreach Engine",
            onBack = onBack
        )

        // Body
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(NexusTokens.Spacing.md),
            verticalArrangement = Arrangement.spacedBy(NexusTokens.Spacing.md)
        ) {
            // Success Message
            if (successToast != null) {
                Surface(
                    color = NexusTokens.SuccessBackground,
                    shape = RoundedCornerShape(NexusTokens.Radius.md),
                    border = BorderStroke(1.dp, NexusTokens.SuccessBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = NexusTokens.SuccessText,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(NexusTokens.Spacing.xs))
                        Text(
                            text = successToast ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexusTokens.SuccessText,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Template Selector Card
            NexusCard {
                Text(
                    text = "Select Campaign Template",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = NexusTokens.Foreground
                )
                Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(NexusTokens.Spacing.xs)
                ) {
                    val templates = listOf(
                        "Shortlist Notification" to "NexusIntern: You have been shortlisted for Cloud Systems Internships",
                        "Assessment Released" to "NexusIntern: Your Project Assessment Radar Feedback has been Released",
                        "Profile Reminder" to "Action Required: Complete your technical portfolio and submit project"
                    )
                    templates.forEach { (name, defSubj) ->
                        val isSel = selectedTemplate == name
                        Surface(
                            color = if (isSel) NexusTokens.Accent else NexusTokens.SurfaceSecondary,
                            shape = RoundedCornerShape(NexusTokens.Radius.sm),
                            border = BorderStroke(1.dp, if (isSel) NexusTokens.Accent else NexusTokens.Border),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    selectedTemplate = name
                                    subject = defSubj
                                    bodyContent = when (name) {
                                        "Shortlist Notification" -> "Congratulations! Your assessed project evaluation has been forwarded to partnered recruiters for internship selection."
                                        "Assessment Released" -> "The platform administrator has completed review of your submitted repository assessment. Log in to explore your spider-web radar score."
                                        else -> "Please verify your GitHub profile and add your hackathon certificates to unlock candidate matching."
                                    }
                                }
                        ) {
                            Text(
                                text = name,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSel) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSel) NexusTokens.OnAccent else NexusTokens.Foreground,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp)
                            )
                        }
                    }
                }
            }

            // Recipient Group Filtering & Live Preview Card
            NexusCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        tint = NexusTokens.Accent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(NexusTokens.Spacing.xs))
                    Text(
                        text = "Recipient Group Filter (${matchedStudents.size} matched)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = NexusTokens.Foreground
                    )
                }

                Spacer(modifier = Modifier.height(NexusTokens.Spacing.sm))

                Text(
                    text = "Filter by Primary Skill Stack:",
                    style = MaterialTheme.typography.labelSmall,
                    color = NexusTokens.ForegroundSecondary
                )
                Spacer(modifier = Modifier.height(NexusTokens.Spacing.xxs))
                Row(horizontalArrangement = Arrangement.spacedBy(NexusTokens.Spacing.xs)) {
                    listOf("ALL", "Kotlin", "Go", "Python").forEach { s ->
                        val sel = filterSkill == s
                        Surface(
                            color = if (sel) NexusTokens.Accent else NexusTokens.SurfaceSecondary,
                            shape = RoundedCornerShape(NexusTokens.Radius.sm),
                            border = BorderStroke(1.dp, if (sel) NexusTokens.Accent else NexusTokens.Border),
                            modifier = Modifier.clickable { filterSkill = s }
                        ) {
                            Text(
                                text = s,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (sel) NexusTokens.OnAccent else NexusTokens.Foreground,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(NexusTokens.Spacing.sm))

                // Recipient Preview Chips
                Text(
                    text = "Target Recipient Preview:",
                    style = MaterialTheme.typography.labelSmall,
                    color = NexusTokens.ForegroundSecondary
                )
                Spacer(modifier = Modifier.height(NexusTokens.Spacing.xxs))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(NexusTokens.Spacing.xs)
                ) {
                    matchedStudents.take(4).forEach { student ->
                        Surface(
                            color = NexusTokens.SurfaceSecondary,
                            shape = RoundedCornerShape(NexusTokens.Radius.pill),
                            border = BorderStroke(1.dp, NexusTokens.Border)
                        ) {
                            Text(
                                text = "${student.fullName} (CGPA ${student.cgpa})",
                                style = MaterialTheme.typography.labelSmall,
                                color = NexusTokens.Foreground,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Message Composer Card
            NexusCard {
                Text(
                    text = "Message Composer",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = NexusTokens.Foreground
                )
                Spacer(modifier = Modifier.height(NexusTokens.Spacing.sm))

                NexusTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = "Subject Line",
                    singleLine = true,
                    testTag = "campaign_subject_input"
                )

                Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))

                NexusTextField(
                    value = bodyContent,
                    onValueChange = { bodyContent = it },
                    label = "Message Body (Rich In-App & Email)",
                    singleLine = false,
                    minLines = 4,
                    maxLines = 8,
                    testTag = "campaign_body_input"
                )

                Spacer(modifier = Modifier.height(NexusTokens.Spacing.md))

                NexusButton(
                    onClick = { showConfirmDialog = true },
                    variant = NexusButtonVariant.Primary,
                    icon = Icons.AutoMirrored.Filled.Send,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "send_campaign_button"
                ) {
                    Text("Review & Send Campaign (${matchedStudents.size} Recipients)")
                }
            }

            // Past Dispatches / Delivery Audit Log
            if (campaignHistory.isNotEmpty()) {
                Text(
                    text = "Confirmed Delivery Audit Log (${campaignHistory.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = NexusTokens.Foreground
                )

                campaignHistory.forEach { camp ->
                    NexusCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = camp.subject,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = NexusTokens.Foreground,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(NexusTokens.Spacing.xs))
                            NexusBadge(
                                label = camp.deliveryOutcome,
                                statusType = "SUCCESS"
                            )
                        }
                        Spacer(modifier = Modifier.height(NexusTokens.Spacing.xxs))
                        Text(
                            text = "Recipients: ${camp.recipientCount} • Filter: ${camp.recipientFilter}",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexusTokens.ForegroundSecondary
                        )
                    }
                }
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { if (!isSending) showConfirmDialog = false },
            containerColor = NexusTokens.Surface,
            shape = RoundedCornerShape(NexusTokens.Radius.lg),
            title = {
                Text(
                    text = "Confirm Campaign Dispatch?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = NexusTokens.Foreground
                )
            },
            text = {
                Column {
                    Text(
                        text = "This message will be dispatched via real email transport and platform notification pipes to ${matchedStudents.size} verified student accounts.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NexusTokens.ForegroundSecondary
                    )
                    Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))
                    Text(
                        text = "Subject: $subject",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = NexusTokens.Foreground
                    )
                }
            },
            confirmButton = {
                NexusButton(
                    onClick = {
                        isSending = true
                        onSendCampaign(
                            subject,
                            bodyContent,
                            "Skills: $filterSkill, Min CGPA: $minCgpa",
                            matchedStudents.size,
                            channel,
                            selectedTemplate
                        )
                        isSending = false
                        showConfirmDialog = false
                        successToast = "Campaign successfully dispatched! Delivery confirmed across ${matchedStudents.size} student inboxes."
                    },
                    variant = NexusButtonVariant.Primary,
                    testTag = "confirm_dispatch_campaign_btn"
                ) {
                    Text("Confirm & Dispatch")
                }
            },
            dismissButton = {
                NexusButton(
                    onClick = { showConfirmDialog = false },
                    variant = NexusButtonVariant.Outline
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
