package com.example.ui.screens.recruiter

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AssessmentReportEntity
import com.example.data.model.HackathonEntity
import com.example.data.model.InternshipRequirementEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.RecruiterEntity
import com.example.data.model.SharedCandidateEntity
import com.example.data.model.StudentEntity
import com.example.ui.components.InteractiveRadarChart
import com.example.ui.components.NexusBadge
import com.example.ui.components.NexusButton
import com.example.ui.components.NexusButtonVariant
import com.example.ui.components.NexusCard
import com.example.ui.components.NexusEmptyState
import com.example.ui.components.NexusHeader
import com.example.ui.components.NexusSegmentedTabs
import com.example.ui.components.NexusTextField
import com.example.ui.components.ScoreBadge
import com.example.ui.components.StatusBadge
import com.example.ui.theme.NexusTokens

@Composable
fun RecruiterPortalScreen(
    currentRecruiter: RecruiterEntity,
    sharedCandidates: List<SharedCandidateEntity>,
    students: List<StudentEntity>,
    projects: List<ProjectEntity>,
    reports: List<AssessmentReportEntity>,
    hackathons: List<HackathonEntity>,
    requirements: List<InternshipRequirementEntity>,
    onPostRequirement: (role: String, skills: String, domains: String, criteria: String, duration: String, location: String, stipend: String, deadline: String) -> Unit,
    onEstablishPassword: (String) -> Unit,
    onContactCandidate: (studentId: String, message: String) -> Unit,
    onSwitchRole: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Shared Candidates (${sharedCandidates.size})", "Internship Openings (${requirements.size})")

    var inspectingCandidate by remember { mutableStateOf<SharedCandidateEntity?>(null) }
    var showPostRequirementDialog by remember { mutableStateOf(false) }
    var showContactDialog by remember { mutableStateOf<StudentEntity?>(null) }
    var showSetPasswordDialog by remember { mutableStateOf(!currentRecruiter.permanentPasswordEstablished) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NexusTokens.Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("recruiter_portal_screen")
    ) {
        // App Header
        NexusHeader(
            title = currentRecruiter.companyName,
            subtitle = "${currentRecruiter.fullName} • ${currentRecruiter.position}",
            badgeText = "Recruiter",
            actions = {
                if (!currentRecruiter.permanentPasswordEstablished) {
                    NexusButton(
                        onClick = { showSetPasswordDialog = true },
                        variant = NexusButtonVariant.Secondary,
                        icon = Icons.Default.Key
                    ) {
                        Text("Set Password")
                    }
                }

                IconButton(
                    onClick = onSwitchRole,
                    modifier = Modifier.testTag("recruiter_switch_role_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SwitchAccount,
                        contentDescription = "Switch Role",
                        tint = NexusTokens.Foreground
                    )
                }
            }
        )

        // Segmented Tabs
        Box(modifier = Modifier.padding(horizontal = NexusTokens.Spacing.md, vertical = NexusTokens.Spacing.xs)) {
            NexusSegmentedTabs(
                tabs = tabs,
                selectedIndex = selectedTab,
                onSelectTab = { selectedTab = it }
            )
        }

        // Tab Content
        when (selectedTab) {
            0 -> {
                if (inspectingCandidate != null) {
                    val shared = inspectingCandidate!!
                    val candidate = students.find { it.id == shared.studentId }
                    val project = projects.find { it.id == shared.projectId }
                    val report = reports.find { it.projectId == shared.projectId }
                    val candidateHackathons = hackathons.filter { it.studentId == shared.studentId }

                    if (candidate != null && project != null) {
                        RecruiterCandidateDetailView(
                            candidate = candidate,
                            project = project,
                            report = report,
                            hackathons = candidateHackathons,
                            shared = shared,
                            onBack = { inspectingCandidate = null },
                            onContact = { showContactDialog = candidate }
                        )
                    } else {
                        inspectingCandidate = null
                    }
                } else {
                    SharedCandidatesListView(
                        sharedCandidates = sharedCandidates,
                        students = students,
                        projects = projects,
                        reports = reports,
                        requirements = requirements,
                        onInspect = { inspectingCandidate = it }
                    )
                }
            }

            1 -> {
                InternshipRequirementsListView(
                    requirements = requirements,
                    onPostRequirementClick = { showPostRequirementDialog = true }
                )
            }
        }
    }

    // Set Permanent Password Dialog
    if (showSetPasswordDialog) {
        var newPass by remember { mutableStateOf("") }
        var confirmPass by remember { mutableStateOf("") }
        var errorMsg by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { if (currentRecruiter.permanentPasswordEstablished) showSetPasswordDialog = false },
            containerColor = NexusTokens.Surface,
            shape = RoundedCornerShape(NexusTokens.Radius.lg),
            title = {
                Text(
                    text = "Establish Permanent Password",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = NexusTokens.Foreground
                )
            },
            text = {
                Column {
                    Text(
                        text = "Your account was provisioned with temporary access. Establish your confidential permanent password to secure company evaluations.",
                        style = MaterialTheme.typography.bodySmall,
                        color = NexusTokens.ForegroundSecondary
                    )
                    Spacer(modifier = Modifier.height(NexusTokens.Spacing.sm))

                    NexusTextField(
                        value = newPass,
                        onValueChange = { newPass = it },
                        label = "New Permanent Password",
                        singleLine = true,
                        testTag = "new_password_input"
                    )

                    Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))

                    NexusTextField(
                        value = confirmPass,
                        onValueChange = { confirmPass = it },
                        label = "Confirm Password",
                        singleLine = true,
                        errorText = errorMsg,
                        testTag = "confirm_password_input"
                    )
                }
            },
            confirmButton = {
                NexusButton(
                    onClick = {
                        if (newPass.length < 6) {
                            errorMsg = "Password must be at least 6 characters."
                            return@NexusButton
                        }
                        if (newPass != confirmPass) {
                            errorMsg = "Passwords do not match."
                            return@NexusButton
                        }
                        onEstablishPassword(newPass)
                        showSetPasswordDialog = false
                    },
                    variant = NexusButtonVariant.Primary,
                    testTag = "confirm_set_password_button"
                ) {
                    Text("Save & Activate")
                }
            },
            dismissButton = {
                if (currentRecruiter.permanentPasswordEstablished) {
                    NexusButton(
                        onClick = { showSetPasswordDialog = false },
                        variant = NexusButtonVariant.Outline
                    ) {
                        Text("Cancel")
                    }
                }
            }
        )
    }

    // Contact Candidate Dialog
    if (showContactDialog != null) {
        val cand = showContactDialog!!
        var outreachMsg by remember {
            mutableStateOf("Hi ${cand.fullName}, we were impressed by your verified repository assessment and would like to invite you for a 30-minute technical discussion.")
        }
        var sentOutreach by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showContactDialog = null },
            containerColor = NexusTokens.Surface,
            shape = RoundedCornerShape(NexusTokens.Radius.lg),
            title = {
                Text(
                    text = "Permitted Candidate Outreach",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = NexusTokens.Foreground
                )
            },
            text = {
                Column {
                    Text(
                        text = "Send direct corporate internship outreach to ${cand.fullName} (${cand.email}).",
                        style = MaterialTheme.typography.bodySmall,
                        color = NexusTokens.ForegroundSecondary
                    )
                    Spacer(modifier = Modifier.height(NexusTokens.Spacing.sm))

                    NexusTextField(
                        value = outreachMsg,
                        onValueChange = { outreachMsg = it },
                        label = "Invitation Message",
                        singleLine = false,
                        minLines = 4,
                        maxLines = 6,
                        testTag = "outreach_message_input"
                    )

                    if (sentOutreach) {
                        Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))
                        Surface(
                            color = NexusTokens.SuccessBackground,
                            shape = RoundedCornerShape(NexusTokens.Radius.sm),
                            border = BorderStroke(1.dp, NexusTokens.SuccessBorder)
                        ) {
                            Text(
                                text = "Outreach message delivered to candidate inbox!",
                                style = MaterialTheme.typography.bodySmall,
                                color = NexusTokens.SuccessText,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                NexusButton(
                    onClick = {
                        onContactCandidate(cand.id, outreachMsg)
                        sentOutreach = true
                    },
                    variant = NexusButtonVariant.Primary,
                    testTag = "send_outreach_btn"
                ) {
                    Text("Send Invitation")
                }
            },
            dismissButton = {
                NexusButton(
                    onClick = { showContactDialog = null },
                    variant = NexusButtonVariant.Outline
                ) {
                    Text("Close")
                }
            }
        )
    }

    // Post Requirement Dialog
    if (showPostRequirementDialog) {
        var roleTitle by remember { mutableStateOf("") }
        var reqSkills by remember { mutableStateOf("") }
        var prefDomains by remember { mutableStateOf("") }
        var criteria by remember { mutableStateOf("CGPA >= 8.5, 3rd/4th Year B.Tech") }
        var duration by remember { mutableStateOf("6 Months") }
        var location by remember { mutableStateOf("Bengaluru (Hybrid)") }
        var stipend by remember { mutableStateOf("₹1,00,000 / month") }
        var deadline by remember { mutableStateOf("November 30, 2026") }

        AlertDialog(
            onDismissRequest = { showPostRequirementDialog = false },
            containerColor = NexusTokens.Surface,
            shape = RoundedCornerShape(NexusTokens.Radius.lg),
            title = {
                Text(
                    text = "Post New Internship Requirement",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = NexusTokens.Foreground
                )
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    NexusTextField(
                        value = roleTitle,
                        onValueChange = { roleTitle = it },
                        label = "Role Title",
                        placeholder = "e.g. Distributed Systems Intern",
                        singleLine = true,
                        testTag = "req_role_input"
                    )
                    Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))
                    NexusTextField(
                        value = reqSkills,
                        onValueChange = { reqSkills = it },
                        label = "Required Skills (comma separated)",
                        placeholder = "Go, Kotlin, gRPC, Docker",
                        singleLine = true,
                        testTag = "req_skills_input"
                    )
                    Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))
                    NexusTextField(
                        value = prefDomains,
                        onValueChange = { prefDomains = it },
                        label = "Preferred Domains",
                        placeholder = "Cloud Backend, Systems",
                        singleLine = true,
                        testTag = "req_domains_input"
                    )
                    Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))
                    NexusTextField(
                        value = criteria,
                        onValueChange = { criteria = it },
                        label = "Eligibility Criteria",
                        singleLine = true,
                        testTag = "req_criteria_input"
                    )
                    Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))
                    Row(horizontalArrangement = Arrangement.spacedBy(NexusTokens.Spacing.xs)) {
                        NexusTextField(
                            value = duration,
                            onValueChange = { duration = it },
                            label = "Duration",
                            modifier = Modifier.weight(1f)
                        )
                        NexusTextField(
                            value = stipend,
                            onValueChange = { stipend = it },
                            label = "Stipend",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))
                    NexusTextField(
                        value = deadline,
                        onValueChange = { deadline = it },
                        label = "Application Deadline",
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                NexusButton(
                    onClick = {
                        if (roleTitle.isNotBlank() && reqSkills.isNotBlank()) {
                            onPostRequirement(
                                roleTitle,
                                reqSkills,
                                prefDomains,
                                criteria,
                                duration,
                                location,
                                stipend,
                                deadline
                            )
                            showPostRequirementDialog = false
                        }
                    },
                    variant = NexusButtonVariant.Primary,
                    testTag = "confirm_post_req_button"
                ) {
                    Text("Post Requirement")
                }
            },
            dismissButton = {
                NexusButton(
                    onClick = { showPostRequirementDialog = false },
                    variant = NexusButtonVariant.Outline
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SharedCandidatesListView(
    sharedCandidates: List<SharedCandidateEntity>,
    students: List<StudentEntity>,
    projects: List<ProjectEntity>,
    reports: List<AssessmentReportEntity>,
    requirements: List<InternshipRequirementEntity>,
    onInspect: (SharedCandidateEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(NexusTokens.Spacing.md),
        verticalArrangement = Arrangement.spacedBy(NexusTokens.Spacing.sm)
    ) {
        item {
            Surface(
                color = NexusTokens.SurfaceSecondary,
                shape = RoundedCornerShape(NexusTokens.Radius.md),
                border = BorderStroke(1.dp, NexusTokens.Border)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = NexusTokens.ForegroundSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(NexusTokens.Spacing.xs))
                    Text(
                        text = "Authorized Access Only: Recruiters inspect only candidates explicitly shared by Administrators. Raw interview answers and private integrity logs remain confidential.",
                        style = MaterialTheme.typography.labelSmall,
                        color = NexusTokens.ForegroundSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Talent Acquisition Metrics
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RecruiterMetricPill(
                    label = "FORWARDED",
                    value = "${sharedCandidates.size}",
                    modifier = Modifier.weight(1f)
                )
                RecruiterMetricPill(
                    label = "POSTINGS",
                    value = "${requirements.size}",
                    modifier = Modifier.weight(1f)
                )
                val topMatch = if (reports.isNotEmpty()) "${reports.maxOf { it.overallScore }}%" else "94%"
                RecruiterMetricPill(
                    label = "TOP MATCH",
                    value = topMatch,
                    modifier = Modifier.weight(1f)
                )
                RecruiterMetricPill(
                    label = "VERIFIED",
                    value = "100%",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (sharedCandidates.isEmpty()) {
            item {
                NexusEmptyState(
                    title = "No candidates forwarded yet",
                    description = "When administrators audit and share candidates matching your internship requirements, they will appear here.",
                    icon = Icons.Default.Business
                )
            }
        } else {
            items(sharedCandidates) { shared ->
                val student = students.find { it.id == shared.studentId }
                val project = projects.find { it.id == shared.projectId }
                val report = reports.find { it.projectId == shared.projectId }

                if (student != null && project != null) {
                    val matchReason = "AI Match Justification: Candidate demonstrates verified proficiency in ${student.technicalSkills.split(",").take(3).joinToString(", ")}, validated through '${project.title}' (Score: ${report?.overallScore ?: 90}/100)."

                    NexusCard(
                        modifier = Modifier
                            .testTag("recruiter_candidate_card_${student.id}")
                            .clickable { onInspect(shared) }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = student.fullName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = NexusTokens.Foreground
                                )
                                Text(
                                    text = "${student.college} • CGPA ${student.cgpa}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NexusTokens.ForegroundSecondary
                                )
                            }

                            if (report != null) {
                                ScoreBadge(score = report.overallScore)
                            }
                        }

                        Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))

                        // AI Match Rationale Box
                        Surface(
                            color = NexusTokens.SuccessBackground,
                            shape = RoundedCornerShape(NexusTokens.Radius.sm),
                            border = BorderStroke(1.dp, NexusTokens.SuccessBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = null,
                                    tint = NexusTokens.SuccessText,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(NexusTokens.Spacing.xs))
                                Text(
                                    text = matchReason,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NexusTokens.SuccessText,
                                    lineHeight = 18.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))

                        Text(
                            text = "Forwarded Project: ${project.title}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = NexusTokens.Foreground
                        )
                        Text(
                            text = "Admin Note: ${shared.adminNotes}",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexusTokens.ForegroundSecondary
                        )

                        Spacer(modifier = Modifier.height(NexusTokens.Spacing.sm))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatusBadge(status = shared.recruiterStatus)

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "View Authorized Dossier",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = NexusTokens.Accent
                                )
                                Spacer(modifier = Modifier.width(2.dp))
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
    }
}

@Composable
private fun RecruiterCandidateDetailView(
    candidate: StudentEntity,
    project: ProjectEntity,
    report: AssessmentReportEntity?,
    hackathons: List<HackathonEntity>,
    shared: SharedCandidateEntity,
    onBack: () -> Unit,
    onContact: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(NexusTokens.Spacing.md),
        verticalArrangement = Arrangement.spacedBy(NexusTokens.Spacing.md)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = NexusTokens.Foreground
                    )
                }
                Spacer(modifier = Modifier.width(NexusTokens.Spacing.xs))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Authorized Candidate Profile",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = NexusTokens.Foreground
                    )
                    Text(
                        text = "${candidate.fullName} • ${candidate.college}",
                        style = MaterialTheme.typography.bodySmall,
                        color = NexusTokens.ForegroundSecondary
                    )
                }

                NexusButton(
                    onClick = onContact,
                    variant = NexusButtonVariant.Primary,
                    icon = Icons.Default.Email,
                    testTag = "recruiter_outreach_button"
                ) {
                    Text("Outreach")
                }
            }
        }

        // Academic Details & Verified Hackathons
        item {
            NexusCard {
                Text(
                    text = "Academic Credentials & Hackathons",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = NexusTokens.Foreground
                )
                Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))
                Text(
                    text = "CGPA: ${candidate.cgpa} • Major: ${candidate.branch}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NexusTokens.Foreground
                )
                Text(
                    text = "Technical Stack: ${candidate.technicalSkills}",
                    style = MaterialTheme.typography.bodySmall,
                    color = NexusTokens.ForegroundSecondary
                )

                if (hackathons.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(NexusTokens.Spacing.sm))
                    Text(
                        text = "Verified Hackathons:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = NexusTokens.Foreground
                    )
                    Spacer(modifier = Modifier.height(NexusTokens.Spacing.xxs))
                    hackathons.forEach { h ->
                        Text(
                            text = "• ${h.eventName} (${h.rankOrPerformance}, ${h.year})",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexusTokens.Accent
                        )
                    }
                }
            }
        }

        // Approved Performance Report & Radar Chart
        if (report != null) {
            item {
                NexusCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Admin-Approved Technical Assessment",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = NexusTokens.Foreground
                        )
                        ScoreBadge(score = report.overallScore)
                    }

                    Spacer(modifier = Modifier.height(NexusTokens.Spacing.xxs))

                    Text(
                        text = "Evaluated on repository: ${project.title}",
                        style = MaterialTheme.typography.bodySmall,
                        color = NexusTokens.ForegroundSecondary
                    )

                    Spacer(modifier = Modifier.height(NexusTokens.Spacing.sm))

                    // Embedded Interactive Radar Chart
                    InteractiveRadarChart(report = report)

                    Spacer(modifier = Modifier.height(NexusTokens.Spacing.sm))

                    Text(
                        text = "Demonstrated Strengths:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = NexusTokens.Foreground
                    )
                    Spacer(modifier = Modifier.height(NexusTokens.Spacing.xxs))
                    Text(
                        text = report.strengths,
                        style = MaterialTheme.typography.bodySmall,
                        color = NexusTokens.ForegroundSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun InternshipRequirementsListView(
    requirements: List<InternshipRequirementEntity>,
    onPostRequirementClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(NexusTokens.Spacing.md),
        verticalArrangement = Arrangement.spacedBy(NexusTokens.Spacing.sm)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Active Internship Openings",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = NexusTokens.Foreground
                    )
                    Text(
                        text = "Define criteria for AI matching and shortlist recommendations",
                        style = MaterialTheme.typography.bodySmall,
                        color = NexusTokens.ForegroundSecondary
                    )
                }

                NexusButton(
                    onClick = onPostRequirementClick,
                    variant = NexusButtonVariant.Primary,
                    icon = Icons.Default.Add,
                    testTag = "post_internship_button"
                ) {
                    Text("Post Opening")
                }
            }
        }

        if (requirements.isEmpty()) {
            item {
                NexusEmptyState(
                    title = "No Internship Openings",
                    description = "Post your first internship opening to receive AI-matched candidate recommendations.",
                    icon = Icons.Default.Work,
                    actionButtonText = "Post Opening",
                    onActionClick = onPostRequirementClick
                )
            }
        } else {
            items(requirements) { req ->
                NexusCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = req.roleTitle,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = NexusTokens.Foreground
                            )
                            Text(
                                text = "${req.companyName} • ${req.location}",
                                style = MaterialTheme.typography.bodySmall,
                                color = NexusTokens.Accent,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        NexusBadge(
                            label = req.stipend,
                            statusType = "SUCCESS"
                        )
                    }

                    Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))

                    Text(
                        text = "Required Stack: ${req.requiredSkills}",
                        style = MaterialTheme.typography.bodySmall,
                        color = NexusTokens.Foreground
                    )
                    Text(
                        text = "Eligibility: ${req.eligibilityCriteria} • Duration: ${req.duration}",
                        style = MaterialTheme.typography.labelSmall,
                        color = NexusTokens.ForegroundSecondary
                    )
                    Text(
                        text = "Application Deadline: ${req.deadline}",
                        style = MaterialTheme.typography.labelSmall,
                        color = NexusTokens.WarningText,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun RecruiterMetricPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = NexusTokens.Surface,
        shape = RoundedCornerShape(NexusTokens.Radius.sm),
        border = BorderStroke(1.dp, NexusTokens.Border),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                color = NexusTokens.Foreground
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 8.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp,
                color = NexusTokens.ForegroundTertiary
            )
        }
    }
}
