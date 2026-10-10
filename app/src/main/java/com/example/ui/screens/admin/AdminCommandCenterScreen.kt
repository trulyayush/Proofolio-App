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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material.icons.filled.VerifiedUser
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AssessmentReportEntity
import com.example.data.model.HackathonEntity
import com.example.data.model.InterviewQuestionEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.RecruiterEntity
import com.example.data.model.StudentEntity
import com.example.ui.components.InteractiveRadarChart
import com.example.ui.components.NexusBadge
import com.example.ui.components.NexusButton
import com.example.ui.components.NexusButtonVariant
import com.example.ui.components.NexusCard
import com.example.ui.components.NexusEmptyState
import com.example.ui.components.NexusHeader
import com.example.ui.components.NexusSearchBar
import com.example.ui.components.NexusSegmentedTabs
import com.example.ui.components.NexusTextField
import com.example.ui.components.ScoreBadge
import com.example.ui.components.SkillChip
import com.example.ui.components.StatusBadge
import com.example.ui.theme.NexusTokens

@Composable
fun AdminCommandCenterScreen(
    students: List<StudentEntity>,
    projects: List<ProjectEntity>,
    reports: List<AssessmentReportEntity>,
    hackathons: List<HackathonEntity>,
    recruiters: List<RecruiterEntity>,
    questions: List<InterviewQuestionEntity>,
    onReleaseFeedback: (reportId: String, remarks: String) -> Unit,
    onForwardToRecruiter: (recruiterId: String, studentId: String, projectId: String, notes: String) -> Unit,
    onCreateRecruiter: (name: String, company: String, position: String, email: String, onResult: (Result<RecruiterEntity>) -> Unit) -> Unit,
    onOpenCampaigns: () -> Unit,
    onSwitchRole: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedSkillFilter by remember { mutableStateOf("ALL") }
    var minCgpaFilter by remember { mutableStateOf(0.0) }

    var inspectingStudent by remember { mutableStateOf<StudentEntity?>(null) }
    var showShareFeedbackDialog by remember { mutableStateOf<AssessmentReportEntity?>(null) }
    var showForwardRecruiterDialog by remember { mutableStateOf<Pair<StudentEntity, ProjectEntity>?>(null) }
    var showCreateRecruiterDialog by remember { mutableStateOf(false) }

    val tabs = listOf("Candidates & Auditing", "Recruiters (${recruiters.size})", "Campaigns")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NexusTokens.Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("admin_command_center_screen")
    ) {
        // Desktop-Optimized Governance Top Bar
        NexusHeader(
            title = "Admin Command Center",
            subtitle = "Placement Governance & Rubric Auditing",
            badgeText = "System Auditor",
            actions = {
                NexusButton(
                    onClick = onOpenCampaigns,
                    variant = NexusButtonVariant.Secondary,
                    icon = Icons.Default.Campaign,
                    testTag = "admin_campaign_button"
                ) {
                    Text("Campaigns")
                }

                IconButton(
                    onClick = onSwitchRole,
                    modifier = Modifier.testTag("admin_switch_role_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SwitchAccount,
                        contentDescription = "Switch Role",
                        tint = NexusTokens.Foreground
                    )
                }
            }
        )

        // Segmented Tabs with Quiet Confidence
        Box(modifier = Modifier.padding(horizontal = NexusTokens.Spacing.md, vertical = NexusTokens.Spacing.xs)) {
            NexusSegmentedTabs(
                tabs = tabs,
                selectedIndex = selectedTab,
                onSelectTab = { idx ->
                    if (idx == 2) onOpenCampaigns() else selectedTab = idx
                }
            )
        }

        // Main Tab Content
        when (selectedTab) {
            0 -> {
                if (inspectingStudent != null) {
                    val targetStudent = inspectingStudent!!
                    val studentProjects = projects.filter { it.studentId == targetStudent.id }
                    val studentHackathons = hackathons.filter { it.studentId == targetStudent.id }

                    CandidateInspectionView(
                        student = targetStudent,
                        projects = studentProjects,
                        reports = reports,
                        hackathons = studentHackathons,
                        questions = questions,
                        onBack = { inspectingStudent = null },
                        onShareFeedback = { report -> showShareFeedbackDialog = report },
                        onForwardRecruiter = { proj -> showForwardRecruiterDialog = Pair(targetStudent, proj) }
                    )
                } else {
                    CandidateRosterView(
                        students = students,
                        projects = projects,
                        reports = reports,
                        searchQuery = searchQuery,
                        onSearchChange = { searchQuery = it },
                        selectedSkillFilter = selectedSkillFilter,
                        onSkillFilterChange = { selectedSkillFilter = it },
                        minCgpaFilter = minCgpaFilter,
                        onCgpaFilterChange = { minCgpaFilter = it },
                        onInspectStudent = { inspectingStudent = it }
                    )
                }
            }

            1 -> {
                RecruiterManagementView(
                    recruiters = recruiters,
                    onCreateRecruiterClick = { showCreateRecruiterDialog = true }
                )
            }
        }
    }

    // Share Feedback Dialog
    if (showShareFeedbackDialog != null) {
        val rep = showShareFeedbackDialog!!
        var adminRemarksInput by remember { mutableStateOf("Technical evaluation approved. Candidate demonstrates high architectural clarity.") }

        AlertDialog(
            onDismissRequest = { showShareFeedbackDialog = null },
            containerColor = NexusTokens.Surface,
            shape = RoundedCornerShape(NexusTokens.Radius.lg),
            title = {
                Text(
                    text = "Release Assessment Feedback to Student",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = NexusTokens.Foreground
                )
            },
            text = {
                Column {
                    Text(
                        text = "Releasing this assessment will make the complete 8-dimension spider-web radar chart and recommendations visible in the student's dashboard.",
                        style = MaterialTheme.typography.bodySmall,
                        color = NexusTokens.ForegroundSecondary
                    )
                    Spacer(modifier = Modifier.height(NexusTokens.Spacing.sm))
                    NexusTextField(
                        value = adminRemarksInput,
                        onValueChange = { adminRemarksInput = it },
                        label = "Administrator Review Remarks",
                        singleLine = false,
                        minLines = 3,
                        maxLines = 5,
                        testTag = "admin_remarks_input"
                    )
                }
            },
            confirmButton = {
                NexusButton(
                    onClick = {
                        onReleaseFeedback(rep.id, adminRemarksInput)
                        showShareFeedbackDialog = null
                    },
                    variant = NexusButtonVariant.Primary,
                    testTag = "confirm_release_feedback_button"
                ) {
                    Text("Release to Student")
                }
            },
            dismissButton = {
                NexusButton(
                    onClick = { showShareFeedbackDialog = null },
                    variant = NexusButtonVariant.Outline
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Forward to Recruiter Dialog
    if (showForwardRecruiterDialog != null) {
        val (student, proj) = showForwardRecruiterDialog!!
        var selectedRecruiterId by remember { mutableStateOf(recruiters.firstOrNull()?.id ?: "") }
        var forwardNotes by remember { mutableStateOf("Candidate demonstrates strong distributed systems and Kotlin performance architecture.") }

        AlertDialog(
            onDismissRequest = { showForwardRecruiterDialog = null },
            containerColor = NexusTokens.Surface,
            shape = RoundedCornerShape(NexusTokens.Radius.lg),
            title = {
                Text(
                    text = "Forward Candidate to Recruiter",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = NexusTokens.Foreground
                )
            },
            text = {
                Column {
                    Text(
                        text = "Forward ${student.fullName} for '${proj.title}'. The recruiter will receive authorized portfolio and approved reports only. Raw interview transcripts remain private.",
                        style = MaterialTheme.typography.bodySmall,
                        color = NexusTokens.ForegroundSecondary
                    )
                    Spacer(modifier = Modifier.height(NexusTokens.Spacing.sm))

                    Text(
                        text = "Select Recruiter Target:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = NexusTokens.Foreground
                    )
                    Spacer(modifier = Modifier.height(NexusTokens.Spacing.xxs))
                    recruiters.forEach { r ->
                        val isSelected = selectedRecruiterId == r.id
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clickable { selectedRecruiterId = r.id },
                            color = if (isSelected) NexusTokens.AccentSubtle else NexusTokens.SurfaceSecondary,
                            shape = RoundedCornerShape(NexusTokens.Radius.sm),
                            border = BorderStroke(1.dp, if (isSelected) NexusTokens.Accent else NexusTokens.Border)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(if (isSelected) NexusTokens.Accent else NexusTokens.Border, RoundedCornerShape(2.dp))
                                )
                                Spacer(modifier = Modifier.width(NexusTokens.Spacing.xs))
                                Column {
                                    Text(
                                        text = "${r.fullName} • ${r.companyName}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                        color = NexusTokens.Foreground
                                    )
                                    Text(
                                        text = r.position,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = NexusTokens.ForegroundSecondary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(NexusTokens.Spacing.sm))

                    NexusTextField(
                        value = forwardNotes,
                        onValueChange = { forwardNotes = it },
                        label = "Admin Recommendation Note",
                        singleLine = false,
                        minLines = 2,
                        maxLines = 4,
                        testTag = "forward_notes_input"
                    )
                }
            },
            confirmButton = {
                NexusButton(
                    onClick = {
                        if (selectedRecruiterId.isNotBlank()) {
                            onForwardToRecruiter(selectedRecruiterId, student.id, proj.id, forwardNotes)
                            showForwardRecruiterDialog = null
                        }
                    },
                    variant = NexusButtonVariant.Primary,
                    testTag = "confirm_forward_recruiter_button"
                ) {
                    Text("Forward Profile")
                }
            },
            dismissButton = {
                NexusButton(
                    onClick = { showForwardRecruiterDialog = null },
                    variant = NexusButtonVariant.Outline
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Create Recruiter Account Dialog
    if (showCreateRecruiterDialog) {
        var recName by remember { mutableStateOf("") }
        var recCompany by remember { mutableStateOf("") }
        var recPosition by remember { mutableStateOf("") }
        var recEmail by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        var isCreating by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { if (!isCreating) showCreateRecruiterDialog = false },
            containerColor = NexusTokens.Surface,
            shape = RoundedCornerShape(NexusTokens.Radius.lg),
            title = {
                Text(
                    text = "Provision Recruiter Account",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = NexusTokens.Foreground
                )
            },
            text = {
                Column {
                    Text(
                        text = "Only Administrators can provision recruiter accounts. A secure temporary credential will be generated.",
                        style = MaterialTheme.typography.bodySmall,
                        color = NexusTokens.ForegroundSecondary
                    )
                    Spacer(modifier = Modifier.height(NexusTokens.Spacing.sm))

                    NexusTextField(
                        value = recName,
                        onValueChange = { recName = it },
                        label = "Recruiter Full Name",
                        testTag = "recruiter_name_input"
                    )
                    Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))
                    NexusTextField(
                        value = recCompany,
                        onValueChange = { recCompany = it },
                        label = "Company Name",
                        testTag = "recruiter_company_input"
                    )
                    Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))
                    NexusTextField(
                        value = recPosition,
                        onValueChange = { recPosition = it },
                        label = "Position / Title",
                        testTag = "recruiter_position_input"
                    )
                    Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))
                    NexusTextField(
                        value = recEmail,
                        onValueChange = { recEmail = it },
                        label = "Corporate Email",
                        errorText = errorMessage,
                        testTag = "recruiter_email_input"
                    )
                }
            },
            confirmButton = {
                NexusButton(
                    onClick = {
                        if (recName.isBlank() || recCompany.isBlank() || recEmail.isBlank()) {
                            errorMessage = "All fields are required."
                            return@NexusButton
                        }
                        isCreating = true
                        onCreateRecruiter(recName, recCompany, recPosition, recEmail) { res ->
                            isCreating = false
                            res.onSuccess {
                                showCreateRecruiterDialog = false
                            }.onFailure {
                                errorMessage = it.message ?: "Failed to provision account."
                            }
                        }
                    },
                    isLoading = isCreating,
                    variant = NexusButtonVariant.Primary,
                    testTag = "confirm_create_recruiter_button"
                ) {
                    Text("Provision Account")
                }
            },
            dismissButton = {
                if (!isCreating) {
                    NexusButton(
                        onClick = { showCreateRecruiterDialog = false },
                        variant = NexusButtonVariant.Outline
                    ) {
                        Text("Cancel")
                    }
                }
            }
        )
    }
}

@Composable
private fun CandidateRosterView(
    students: List<StudentEntity>,
    projects: List<ProjectEntity>,
    reports: List<AssessmentReportEntity>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedSkillFilter: String,
    onSkillFilterChange: (String) -> Unit,
    minCgpaFilter: Double,
    onCgpaFilterChange: (Double) -> Unit,
    onInspectStudent: (StudentEntity) -> Unit
) {
    val filteredStudents = students.filter { s ->
        val matchesSearch = s.fullName.contains(searchQuery, ignoreCase = true) ||
                s.college.contains(searchQuery, ignoreCase = true) ||
                s.technicalSkills.contains(searchQuery, ignoreCase = true)

        val matchesSkill = selectedSkillFilter == "ALL" || s.technicalSkills.contains(selectedSkillFilter, ignoreCase = true)
        val matchesCgpa = s.cgpa >= minCgpaFilter
        matchesSearch && matchesSkill && matchesCgpa
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(NexusTokens.Spacing.md),
        verticalArrangement = Arrangement.spacedBy(NexusTokens.Spacing.sm)
    ) {
        // Executive Intelligence Overview
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminMetricPill(
                    label = "CANDIDATES",
                    value = "${students.size}",
                    modifier = Modifier.weight(1f)
                )
                AdminMetricPill(
                    label = "REPOSITORIES",
                    value = "${projects.size}",
                    modifier = Modifier.weight(1f)
                )
                AdminMetricPill(
                    label = "ASSESSMENTS",
                    value = "${reports.size}",
                    modifier = Modifier.weight(1f)
                )
                val avgScore = if (reports.isNotEmpty()) (reports.map { it.overallScore }.average()).toInt() else 85
                AdminMetricPill(
                    label = "AVG SCORE",
                    value = "$avgScore",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Search & Filters Header
        item {
            NexusCard {
                NexusSearchBar(
                    query = searchQuery,
                    onQueryChange = onSearchChange,
                    placeholder = "Search candidates by name, university, or stack...",
                    testTag = "admin_candidate_search_input"
                )

                Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(NexusTokens.Spacing.xs),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Stack:",
                        style = MaterialTheme.typography.labelSmall,
                        color = NexusTokens.ForegroundSecondary
                    )

                    listOf("ALL", "Kotlin", "Go", "Python", "Rust").forEach { skill ->
                        val isSel = selectedSkillFilter == skill
                        Surface(
                            color = if (isSel) NexusTokens.Accent else NexusTokens.SurfaceSecondary,
                            shape = RoundedCornerShape(NexusTokens.Radius.sm),
                            border = BorderStroke(1.dp, if (isSel) NexusTokens.Accent else NexusTokens.Border),
                            modifier = Modifier.clickable { onSkillFilterChange(skill) }
                        ) {
                            Text(
                                text = skill,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSel) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSel) NexusTokens.OnAccent else NexusTokens.Foreground,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        if (filteredStudents.isEmpty()) {
            item {
                NexusEmptyState(
                    title = "No candidates match criteria",
                    description = "Try adjusting the search query or technology filter.",
                    icon = Icons.Default.Group
                )
            }
        } else {
            // Candidates List
            items(filteredStudents, key = { it.id }) { student ->
                val studentProjects = projects.filter { it.studentId == student.id }
                val highestScore = reports.filter { it.studentId == student.id }.maxOfOrNull { it.overallScore }

                NexusCard(
                    modifier = Modifier
                        .testTag("admin_candidate_row_${student.id}")
                        .clickable { onInspectStudent(student) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = student.fullName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = NexusTokens.Foreground
                                )
                                Spacer(modifier = Modifier.width(NexusTokens.Spacing.xs))
                                Surface(
                                    color = NexusTokens.SurfaceSecondary,
                                    shape = RoundedCornerShape(NexusTokens.Radius.sm),
                                    border = BorderStroke(1.dp, NexusTokens.Border)
                                ) {
                                    Text(
                                        text = "CGPA ${student.cgpa}",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = NexusTokens.Accent,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = "${student.college} • ${student.branch}",
                                style = MaterialTheme.typography.bodySmall,
                                color = NexusTokens.ForegroundSecondary
                            )

                            Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))

                            Row(horizontalArrangement = Arrangement.spacedBy(NexusTokens.Spacing.xxs)) {
                                student.technicalSkills.split(",").take(4).forEach {
                                    SkillChip(skill = it)
                                }
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            if (highestScore != null) {
                                ScoreBadge(score = highestScore)
                            } else {
                                NexusBadge(
                                    label = "${studentProjects.size} Projects",
                                    statusType = "NEUTRAL"
                                )
                            }

                            Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Inspect Audit",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = NexusTokens.Accent
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
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
private fun CandidateInspectionView(
    student: StudentEntity,
    projects: List<ProjectEntity>,
    reports: List<AssessmentReportEntity>,
    hackathons: List<HackathonEntity>,
    questions: List<InterviewQuestionEntity>,
    onBack: () -> Unit,
    onShareFeedback: (AssessmentReportEntity) -> Unit,
    onForwardRecruiter: (ProjectEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(NexusTokens.Spacing.md),
        verticalArrangement = Arrangement.spacedBy(NexusTokens.Spacing.md)
    ) {
        // Back Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = NexusTokens.Foreground
                    )
                }
                Spacer(modifier = Modifier.width(NexusTokens.Spacing.xs))
                Column {
                    Text(
                        text = "Full Candidate Audit Dossier",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = NexusTokens.Foreground
                    )
                    Text(
                        text = "${student.fullName} • ${student.email}",
                        style = MaterialTheme.typography.bodySmall,
                        color = NexusTokens.ForegroundSecondary
                    )
                }
            }
        }

        // Student Overview Card
        item {
            NexusCard {
                Text(
                    text = "Academic & Technical Profile",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = NexusTokens.Foreground
                )
                Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))
                Text(
                    text = "University: ${student.college} • ${student.branch}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NexusTokens.Foreground
                )
                Text(
                    text = "CGPA: ${student.cgpa} • Year: ${student.academicYear}",
                    style = MaterialTheme.typography.bodySmall,
                    color = NexusTokens.ForegroundSecondary
                )
                Text(
                    text = "GitHub: ${student.githubProfileUrl}",
                    style = MaterialTheme.typography.bodySmall,
                    color = NexusTokens.Accent
                )
                Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))
                Text(
                    text = "Stack: ${student.technicalSkills}",
                    style = MaterialTheme.typography.labelSmall,
                    color = NexusTokens.ForegroundSecondary
                )
                Spacer(modifier = Modifier.height(NexusTokens.Spacing.xxs))
                Text(
                    text = "Resume Summary: ${student.resumeSummary}",
                    style = MaterialTheme.typography.bodySmall,
                    color = NexusTokens.Foreground
                )
            }
        }

        // Submitted Projects & Assessment Reports
        item {
            Text(
                text = "Submitted Projects & Rubric Evaluations (${projects.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = NexusTokens.Foreground
            )
        }

        items(projects) { proj ->
            val rep = reports.find { it.projectId == proj.id }
            val projQuestions = questions.filter { it.projectId == proj.id }

            NexusCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = proj.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = NexusTokens.Foreground
                        )
                        Text(
                            text = proj.githubRepoUrl,
                            style = MaterialTheme.typography.bodySmall,
                            color = NexusTokens.Accent
                        )
                    }
                    StatusBadge(status = proj.status)
                }

                Spacer(modifier = Modifier.height(NexusTokens.Spacing.sm))

                if (rep != null) {
                    // Assessment summary
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Rubric Score: ",
                                style = MaterialTheme.typography.bodyMedium,
                                color = NexusTokens.ForegroundSecondary
                            )
                            ScoreBadge(score = rep.overallScore)
                        }

                        if (rep.feedbackSharedWithStudent) {
                            NexusBadge(
                                label = "Feedback Released",
                                statusType = "SUCCESS"
                            )
                        } else {
                            NexusButton(
                                onClick = { onShareFeedback(rep) },
                                variant = NexusButtonVariant.Secondary,
                                icon = Icons.Default.Share,
                                testTag = "admin_share_feedback_btn_${rep.id}"
                            ) {
                                Text("Release to Student")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(NexusTokens.Spacing.sm))

                    // Embedded Interactive Radar Chart
                    InteractiveRadarChart(report = rep)

                    Spacer(modifier = Modifier.height(NexusTokens.Spacing.sm))

                    Surface(
                        color = NexusTokens.SurfaceSecondary,
                        shape = RoundedCornerShape(NexusTokens.Radius.md),
                        border = BorderStroke(1.dp, NexusTokens.Border),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Admin Integrity Observations:",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = NexusTokens.Foreground
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = rep.integrityObservations,
                                style = MaterialTheme.typography.bodySmall,
                                color = NexusTokens.ForegroundSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(NexusTokens.Spacing.sm))

                    // Forward to Recruiter Action
                    NexusButton(
                        onClick = { onForwardRecruiter(proj) },
                        variant = NexusButtonVariant.Primary,
                        icon = Icons.Default.Share,
                        testTag = "admin_forward_recruiter_btn_${proj.id}",
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Forward Profile to Recruiter")
                    }
                } else {
                    Text(
                        text = "No assessment completed yet for this project. Status: ${proj.status}",
                        style = MaterialTheme.typography.bodySmall,
                        color = NexusTokens.ForegroundSecondary
                    )
                }

                // Raw Interview QA Inspection (Admin only!)
                if (projQuestions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(NexusTokens.Spacing.md))
                    Text(
                        text = "Candidate Raw Interview Q&A Responses (${projQuestions.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = NexusTokens.Foreground
                    )
                    Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))
                    projQuestions.forEachIndexed { idx, q ->
                        Surface(
                            color = NexusTokens.SurfaceSecondary,
                            shape = RoundedCornerShape(NexusTokens.Radius.md),
                            border = BorderStroke(1.dp, NexusTokens.Border),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Q${idx + 1} (${q.category}): ${q.questionText}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = NexusTokens.Foreground
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Answer: ${if (q.studentAnswer.isNotBlank()) q.studentAnswer else "[No response recorded]"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NexusTokens.ForegroundSecondary
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
private fun RecruiterManagementView(
    recruiters: List<RecruiterEntity>,
    onCreateRecruiterClick: () -> Unit
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
                        text = "Provisioned Recruiter Portals",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = NexusTokens.Foreground
                    )
                    Text(
                        text = "Accounts strictly authorized by Administrator governance.",
                        style = MaterialTheme.typography.bodySmall,
                        color = NexusTokens.ForegroundSecondary
                    )
                }

                NexusButton(
                    onClick = onCreateRecruiterClick,
                    variant = NexusButtonVariant.Primary,
                    icon = Icons.Default.PersonAdd,
                    testTag = "admin_provision_recruiter_btn"
                ) {
                    Text("Provision")
                }
            }
        }

        if (recruiters.isEmpty()) {
            item {
                NexusEmptyState(
                    title = "No Recruiter Accounts",
                    description = "Provision corporate recruiters to enable candidate forwarding.",
                    icon = Icons.Default.Group,
                    actionButtonText = "Provision Recruiter",
                    onActionClick = onCreateRecruiterClick
                )
            }
        } else {
            items(recruiters) { rec ->
                NexusCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = rec.fullName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = NexusTokens.Foreground
                            )
                            Text(
                                text = "${rec.companyName} • ${rec.position}",
                                style = MaterialTheme.typography.bodySmall,
                                color = NexusTokens.Accent,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        NexusBadge(
                            label = if (rec.permanentPasswordEstablished) "Active" else "Temporary Credential",
                            statusType = if (rec.permanentPasswordEstablished) "SUCCESS" else "WARNING"
                        )
                    }

                    Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))

                    Text(
                        text = "Corporate Email: ${rec.email}",
                        style = MaterialTheme.typography.bodySmall,
                        color = NexusTokens.ForegroundSecondary
                    )

                    if (!rec.permanentPasswordEstablished) {
                        Spacer(modifier = Modifier.height(NexusTokens.Spacing.xxs))
                        Text(
                            text = "Temporary Credential: ${rec.temporaryPassword}",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexusTokens.WarningText,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminMetricPill(
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
