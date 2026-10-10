package com.example.ui.screens.student

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConnectionEntity
import com.example.data.model.HackathonEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.StudentEntity
import com.example.ui.components.NexusBadge
import com.example.ui.components.NexusButton
import com.example.ui.components.NexusButtonVariant
import com.example.ui.components.NexusCard
import com.example.ui.components.NexusEmptyState
import com.example.ui.components.NexusSegmentedTabs
import com.example.ui.components.NexusSkillChip
import com.example.ui.components.NexusTextField
import com.example.ui.components.ScoreBadge
import com.example.ui.components.StatusBadge
import com.example.ui.theme.NexusTokens

/**
 * Student Dashboard Screen:
 * Redesigned as a curated, high-impact technical engineering portfolio.
 * Features an architectural dossier masthead, technical stats grid,
 * elegant code repository cards, and structured achievement timelines.
 */
@Composable
fun StudentDashboardScreen(
    student: StudentEntity,
    projects: List<ProjectEntity>,
    hackathons: List<HackathonEntity>,
    connections: List<ConnectionEntity>,
    allStudents: List<StudentEntity>,
    notifications: List<NotificationEntity>,
    onOpenProject: (String) -> Unit,
    onEditProfile: () -> Unit,
    onSubmitNewProject: (title: String, repoUrl: String, onComplete: (Result<ProjectEntity>) -> Unit) -> Unit,
    onAcceptConnection: (String) -> Unit,
    onSendConnection: (String) -> Unit,
    onSwitchRole: () -> Unit,
    onLogout: () -> Unit = onSwitchRole
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showAddProjectDialog by remember { mutableStateOf(false) }

    val tabs = listOf(
        "Projects (${projects.size})",
        "Portfolio",
        "Network (${connections.size})",
        "Updates (${notifications.size})"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NexusTokens.Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("student_dashboard_screen")
    ) {
        // 1. Curated Technical Portfolio Masthead
        Surface(
            color = NexusTokens.Surface,
            border = BorderStroke(1.dp, NexusTokens.Border)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // Top Identification Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Architectural Monogram Box with Verified Status
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Surface(
                                shape = RoundedCornerShape(NexusTokens.Radius.md),
                                color = NexusTokens.SurfaceSecondary,
                                border = BorderStroke(1.5.dp, NexusTokens.BorderStrong),
                                modifier = Modifier.size(52.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = student.fullName.take(1).uppercase(),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = NexusTokens.Foreground
                                    )
                                }
                            }
                            if (student.isEmailVerified) {
                                Surface(
                                    shape = CircleShape,
                                    color = NexusTokens.Accent,
                                    border = BorderStroke(1.5.dp, NexusTokens.Surface),
                                    modifier = Modifier.size(16.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.Verified,
                                            contentDescription = "Verified Identity",
                                            tint = Color.White,
                                            modifier = Modifier.size(10.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = student.fullName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                                    fontWeight = FontWeight.Bold,
                                    color = NexusTokens.Foreground
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = NexusTokens.AccentSubtle,
                                    shape = RoundedCornerShape(NexusTokens.Radius.pill),
                                    border = BorderStroke(1.dp, NexusTokens.Accent.copy(alpha = 0.2f))
                                ) {
                                    Text(
                                        text = "NX-PORTFOLIO",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 9.sp,
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

                            Text(
                                text = "Year ${student.academicYear} • CGPA ${student.cgpa} / 10.0",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = NexusTokens.Foreground
                            )
                        }
                    }

                    // Header Action Buttons
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            shape = RoundedCornerShape(NexusTokens.Radius.sm),
                            color = NexusTokens.SurfaceSecondary,
                            border = BorderStroke(1.dp, NexusTokens.Border)
                        ) {
                            IconButton(
                                onClick = onEditProfile,
                                modifier = Modifier
                                    .size(34.dp)
                                    .testTag("edit_profile_button")
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Edit Profile",
                                    tint = NexusTokens.ForegroundSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(NexusTokens.Radius.sm),
                            color = NexusTokens.SurfaceSecondary,
                            border = BorderStroke(1.dp, NexusTokens.Border)
                        ) {
                            IconButton(
                                onClick = onLogout,
                                modifier = Modifier
                                    .size(34.dp)
                                    .testTag("logout_student_button")
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ExitToApp,
                                    contentDescription = "Log Out",
                                    tint = NexusTokens.Foreground,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(NexusTokens.Radius.sm),
                            color = NexusTokens.SurfaceSecondary,
                            border = BorderStroke(1.dp, NexusTokens.Border)
                        ) {
                            IconButton(
                                onClick = onSwitchRole,
                                modifier = Modifier
                                    .size(34.dp)
                                    .testTag("switch_role_button")
                            ) {
                                Icon(
                                    Icons.Default.SwitchAccount,
                                    contentDescription = "Switch Portal",
                                    tint = NexusTokens.Foreground,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                if (student.bio.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = NexusTokens.SurfaceSecondary.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(NexusTokens.Radius.sm),
                        border = BorderStroke(1.dp, NexusTokens.Border)
                    ) {
                        Text(
                            text = "“${student.bio}”",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            ),
                            color = NexusTokens.ForegroundSecondary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Architectural Metrics Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PortfolioMetricPill(
                        label = "REPOSITORIES",
                        value = "${projects.size}",
                        modifier = Modifier.weight(1f)
                    )
                    PortfolioMetricPill(
                        label = "ASSESSMENTS",
                        value = "${projects.count { it.status == "COMPLETED" }}",
                        modifier = Modifier.weight(1f)
                    )
                    PortfolioMetricPill(
                        label = "HACKATHONS",
                        value = "${hackathons.size}",
                        modifier = Modifier.weight(1f)
                    )
                    PortfolioMetricPill(
                        label = "NETWORK",
                        value = "${connections.count { it.status == "ACCEPTED" }}",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Segmented Navigation Tabs
                NexusSegmentedTabs(
                    tabs = tabs,
                    selectedIndex = selectedTabIndex,
                    onSelectTab = { selectedTabIndex = it }
                )
            }
        }

        // Tab Content
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTabIndex) {
                0 -> {
                    ProjectsTabContent(
                        projects = projects,
                        onOpenProject = onOpenProject,
                        onAddProjectClick = { showAddProjectDialog = true }
                    )
                }
                1 -> {
                    PortfolioTabContent(student = student, hackathons = hackathons)
                }
                2 -> {
                    ConnectionsTabContent(
                        currentStudentId = student.id,
                        connections = connections,
                        allStudents = allStudents,
                        onAccept = onAcceptConnection,
                        onSend = onSendConnection
                    )
                }
                3 -> {
                    NotificationsTabContent(notifications = notifications)
                }
            }
        }
    }

    // Add Project Modal Dialog
    if (showAddProjectDialog) {
        AddProjectDialog(
            onDismiss = { showAddProjectDialog = false },
            onSubmit = { title, repoUrl, onResult ->
                onSubmitNewProject(title, repoUrl) { result ->
                    onResult(result)
                    if (result.isSuccess) {
                        showAddProjectDialog = false
                    }
                }
            }
        )
    }
}

@Composable
private fun PortfolioMetricPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = NexusTokens.SurfaceSecondary,
        shape = RoundedCornerShape(NexusTokens.Radius.sm),
        border = BorderStroke(1.dp, NexusTokens.Border),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 7.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
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

@Composable
private fun ProjectsTabContent(
    projects: List<ProjectEntity>,
    onOpenProject: (String) -> Unit,
    onAddProjectClick: () -> Unit
) {
    if (projects.isEmpty()) {
        NexusEmptyState(
            title = "No Engineering Projects Submitted",
            description = "Submit a public GitHub repository to initiate architectural code parsing, project-specific AI interview sessions, and verified radar evaluations.",
            icon = Icons.Default.Code,
            actionButtonText = "Submit First Project",
            onActionClick = onAddProjectClick
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Auditable Submissions Notice
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
                            Icons.Default.Lock,
                            contentDescription = null,
                            tint = NexusTokens.ForegroundSecondary,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "IMMUTABLE AUDIT LOG: Submitted repositories and AI evaluation dossiers are permanently recorded to guarantee credibility for recruiters.",
                            style = MaterialTheme.typography.labelSmall,
                            color = NexusTokens.ForegroundSecondary,
                            lineHeight = 15.sp,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Projects List with Redesigned Technical Cards
            items(projects, key = { it.id }) { proj ->
                ArchitecturalProjectCard(
                    project = proj,
                    onClick = { onOpenProject(proj.id) }
                )
            }

            // Add Project Action
            item {
                Spacer(modifier = Modifier.height(4.dp))
                NexusButton(
                    onClick = onAddProjectClick,
                    variant = NexusButtonVariant.Outline,
                    icon = Icons.Default.Add,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "add_project_button_list"
                ) {
                    Text("Submit Another Repository")
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ArchitecturalProjectCard(
    project: ProjectEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("project_card_${project.id}")
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(NexusTokens.Radius.lg),
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = NexusTokens.Surface),
        border = BorderStroke(1.dp, NexusTokens.Border),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Top Row: Title + Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = project.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NexusTokens.Foreground
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Monospace Repo Link Tag
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Terminal,
                            contentDescription = null,
                            tint = NexusTokens.Accent,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = project.githubRepoUrl.removePrefix("https://"),
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = NexusTokens.Accent,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                StatusBadge(status = project.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Repo Description
            Text(
                text = project.repoDescription,
                style = MaterialTheme.typography.bodySmall,
                color = NexusTokens.ForegroundSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tech Stack & Action Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Languages Tags
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    project.languages.split(",").take(3).forEach { lang ->
                        Surface(
                            color = NexusTokens.SurfaceSecondary,
                            shape = RoundedCornerShape(NexusTokens.Radius.sm),
                            border = BorderStroke(1.dp, NexusTokens.Border)
                        ) {
                            Text(
                                text = lang.trim(),
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = NexusTokens.ForegroundSecondary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Interactive Link Indicator
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = when (project.status) {
                            "COMPLETED" -> "View Dossier"
                            "READY_FOR_INTERVIEW" -> "Begin Assessment"
                            else -> "Inspect Details"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = NexusTokens.Accent
                    )
                    Spacer(modifier = Modifier.width(4.dp))
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

@Composable
private fun PortfolioTabContent(
    student: StudentEntity,
    hackathons: List<HackathonEntity>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Technical Competencies Matrix
        item {
            NexusCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Technical Competencies Matrix",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = NexusTokens.Foreground
                    )
                    Surface(
                        color = NexusTokens.SuccessBackground,
                        shape = RoundedCornerShape(NexusTokens.Radius.pill),
                        border = BorderStroke(1.dp, NexusTokens.SuccessBorder)
                    ) {
                        Text(
                            text = "Code-Extracted",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = NexusTokens.SuccessText,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    student.technicalSkills.split(",").take(6).forEach {
                        NexusSkillChip(skill = it.trim())
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "TARGET DOMAINS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    letterSpacing = 0.8.sp,
                    color = NexusTokens.ForegroundTertiary
                )
                Text(
                    text = student.preferredDomains,
                    style = MaterialTheme.typography.bodySmall,
                    color = NexusTokens.Foreground
                )
            }
        }

        // 2. Academic Credentials Dossier
        item {
            NexusCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.School,
                        contentDescription = null,
                        tint = NexusTokens.Accent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Academic Dossier & Institution",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = NexusTokens.Foreground
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Institution",
                            style = MaterialTheme.typography.labelSmall,
                            color = NexusTokens.ForegroundTertiary
                        )
                        Text(
                            text = student.college,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = NexusTokens.Foreground
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Cumulative GPA",
                            style = MaterialTheme.typography.labelSmall,
                            color = NexusTokens.ForegroundTertiary
                        )
                        Text(
                            text = "${student.cgpa} / 10.0",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = NexusTokens.Accent
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Department: ${student.branch} • Graduating Class Year ${student.academicYear}",
                    style = MaterialTheme.typography.bodySmall,
                    color = NexusTokens.ForegroundSecondary
                )
            }
        }

        // 3. Hackathon Citations & Competitive Coding
        item {
            NexusCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = NexusTokens.WarningText,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Hackathons & Competitive Honors",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = NexusTokens.Foreground
                        )
                    }

                    Text(
                        text = "${hackathons.size} Citations",
                        style = MaterialTheme.typography.labelSmall,
                        color = NexusTokens.ForegroundTertiary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (hackathons.isEmpty()) {
                    Text(
                        text = "No hackathon citations registered.",
                        style = MaterialTheme.typography.bodySmall,
                        color = NexusTokens.ForegroundTertiary
                    )
                } else {
                    hackathons.forEach { hack ->
                        Surface(
                            color = NexusTokens.SurfaceSecondary,
                            shape = RoundedCornerShape(NexusTokens.Radius.md),
                            border = BorderStroke(1.dp, NexusTokens.Border),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = hack.eventName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = NexusTokens.Foreground
                                    )
                                    Surface(
                                        color = NexusTokens.WarningBackground,
                                        shape = RoundedCornerShape(NexusTokens.Radius.pill),
                                        border = BorderStroke(1.dp, NexusTokens.WarningBorder)
                                    ) {
                                        Text(
                                            text = hack.rankOrPerformance,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 10.sp,
                                            color = NexusTokens.WarningText,
                                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Citation: ${hack.certificateTitle} (Year ${hack.year})",
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
private fun ConnectionsTabContent(
    currentStudentId: String,
    connections: List<ConnectionEntity>,
    allStudents: List<StudentEntity>,
    onAccept: (String) -> Unit,
    onSend: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Privacy Safeguard Header
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
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = NexusTokens.ForegroundSecondary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ZERO-LEAK PRIVACY: Engineering peers can exchange project links, but NEVER access private raw AI assessment transcripts or recruiter notes.",
                        style = MaterialTheme.typography.labelSmall,
                        color = NexusTokens.ForegroundSecondary,
                        lineHeight = 15.sp,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Active / Pending Connections
        item {
            Text(
                text = "Mutual Connections & Inbound Requests",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = NexusTokens.Foreground
            )
        }

        if (connections.isEmpty()) {
            item {
                Text(
                    text = "No active connection requests yet.",
                    style = MaterialTheme.typography.bodySmall,
                    color = NexusTokens.ForegroundTertiary
                )
            }
        } else {
            items(connections) { conn ->
                val otherId = if (conn.requesterStudentId == currentStudentId) conn.targetStudentId else conn.requesterStudentId
                val otherStudent = allStudents.find { it.id == otherId }

                NexusCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = otherStudent?.fullName ?: "Candidate Peer",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = NexusTokens.Foreground
                            )
                            Text(
                                text = "${otherStudent?.college ?: "University"} • ${otherStudent?.branch ?: ""}",
                                style = MaterialTheme.typography.bodySmall,
                                color = NexusTokens.ForegroundSecondary
                            )
                        }

                        if (conn.status == "PENDING" && conn.targetStudentId == currentStudentId) {
                            NexusButton(
                                onClick = { onAccept(conn.id) },
                                variant = NexusButtonVariant.Primary
                            ) {
                                Text("Accept")
                            }
                        } else {
                            StatusBadge(status = conn.status)
                        }
                    }
                }
            }
        }

        // Discover Peers
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Discover Engineering Peers",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = NexusTokens.Foreground
            )
        }

        val peers = allStudents.filter { it.id != currentStudentId && connections.none { c -> c.requesterStudentId == it.id || c.targetStudentId == it.id } }
        if (peers.isEmpty()) {
            item {
                Text(
                    text = "All campus peers connected.",
                    style = MaterialTheme.typography.bodySmall,
                    color = NexusTokens.ForegroundTertiary
                )
            }
        } else {
            items(peers) { peer ->
                NexusCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = peer.fullName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = NexusTokens.Foreground
                            )
                            Text(
                                text = "${peer.college} • ${peer.branch} • CGPA ${peer.cgpa}",
                                style = MaterialTheme.typography.bodySmall,
                                color = NexusTokens.ForegroundSecondary
                            )
                        }

                        NexusButton(
                            onClick = { onSend(peer.id) },
                            variant = NexusButtonVariant.Outline,
                            icon = Icons.Default.PersonAdd
                        ) {
                            Text("Connect")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationsTabContent(notifications: List<NotificationEntity>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (notifications.isEmpty()) {
            item {
                NexusEmptyState(
                    title = "All Caught Up",
                    description = "No pending updates. Autonomous evaluation notices and interview schedule notifications will appear here.",
                    icon = Icons.Default.Notifications
                )
            }
        } else {
            items(notifications) { notif ->
                NexusCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Surface(
                            color = NexusTokens.AccentSubtle,
                            shape = CircleShape,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = NexusTokens.Accent,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = notif.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = NexusTokens.Foreground
                            )
                            Text(
                                text = notif.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = NexusTokens.ForegroundSecondary,
                                modifier = Modifier.padding(top = 2.dp),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddProjectDialog(
    onDismiss: () -> Unit,
    onSubmit: (title: String, repoUrl: String, onResult: (Result<ProjectEntity>) -> Unit) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var repoUrl by remember { mutableStateOf("https://github.com/") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        containerColor = NexusTokens.Surface,
        title = {
            Text(
                text = "Submit Repository Project",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = NexusTokens.Foreground
            )
        },
        text = {
            Column {
                Text(
                    text = "Submit an accessible public GitHub repository for automated codebase extraction and repository-specific AI technical interview generation.",
                    style = MaterialTheme.typography.bodySmall,
                    color = NexusTokens.ForegroundSecondary,
                    modifier = Modifier.padding(bottom = 14.dp)
                )

                NexusTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        errorMessage = null
                    },
                    label = "Project Title",
                    placeholder = "e.g. Distributed Consensus Engine",
                    testTag = "new_project_title_input"
                )

                Spacer(modifier = Modifier.height(10.dp))

                NexusTextField(
                    value = repoUrl,
                    onValueChange = {
                        repoUrl = it
                        errorMessage = null
                    },
                    label = "GitHub Repository URL",
                    placeholder = "https://github.com/username/repository",
                    testTag = "new_project_url_input"
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = NexusTokens.ErrorBackground,
                        shape = RoundedCornerShape(NexusTokens.Radius.md),
                        border = BorderStroke(1.dp, NexusTokens.ErrorBorder)
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexusTokens.ErrorText,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                if (isLoading) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = NexusTokens.Accent
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "Auditing repository via GitHub API...",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexusTokens.ForegroundSecondary
                        )
                    }
                }
            }
        },
        confirmButton = {
            NexusButton(
                onClick = {
                    if (title.isBlank() || repoUrl.isBlank()) {
                        errorMessage = "Please enter both project title and GitHub URL."
                        return@NexusButton
                    }
                    isLoading = true
                    errorMessage = null
                    onSubmit(title, repoUrl) { result ->
                        isLoading = false
                        result.onFailure {
                            errorMessage = it.message ?: "Failed to validate repository."
                        }
                    }
                },
                enabled = !isLoading,
                isLoading = isLoading,
                variant = NexusButtonVariant.Primary,
                testTag = "submit_project_confirm_button"
            ) {
                Text("Analyze & Submit")
            }
        },
        dismissButton = {
            if (!isLoading) {
                NexusButton(
                    onClick = onDismiss,
                    variant = NexusButtonVariant.Outline
                ) {
                    Text("Cancel")
                }
            }
        }
    )
}
