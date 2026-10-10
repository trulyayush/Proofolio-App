package com.example.ui.screens.student

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HackathonEntity
import com.example.data.model.StudentEntity
import com.example.network.GeminiAiService
import com.example.ui.components.NexusButton
import com.example.ui.components.NexusButtonVariant
import com.example.ui.components.NexusCard
import com.example.ui.components.NexusHeader
import com.example.ui.components.NexusTextField
import com.example.ui.components.NexusUploadArea
import com.example.ui.theme.NexusTokens
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun StudentOnboardingScreen(
    initialStudent: StudentEntity?,
    onSaveProfile: (StudentEntity, List<HackathonEntity>) -> Unit,
    onCancel: () -> Unit
) {
    var step by remember { mutableIntStateOf(1) }
    val scope = rememberCoroutineScope()

    // Form State
    var fullName by remember { mutableStateOf(initialStudent?.fullName ?: "") }
    var email by remember { mutableStateOf(initialStudent?.email ?: "") }
    var verificationCode by remember { mutableStateOf("") }
    var isEmailVerified by remember { mutableStateOf(initialStudent?.isEmailVerified ?: false) }
    var verificationSent by remember { mutableStateOf(false) }

    // Resume State
    var resumeFileName by remember { mutableStateOf<String?>("Aarav_Deshmukh_Resume.pdf") }
    var resumeRawText by remember {
        mutableStateOf(
            """
            Aarav Deshmukh
            Email: aarav.deshmukh@vit.ac.in
            College: Vellore Institute of Technology
            Branch: Computer Science and Engineering
            Academic Year: 3rd Year (Class of 2026)
            CGPA: 9.18
            Technical Skills: Kotlin, Jetpack Compose, Coroutines, Room, Go, gRPC, Docker, PostgreSQL, Redis
            Experience: Software Engineering Intern at CloudMatrix (Summer 2025)
            Projects: Chronos: Distributed Log Engine; Raft Consensus KV Store
            """.trimIndent()
        )
    }
    var isExtractingResume by remember { mutableStateOf(false) }

    var college by remember { mutableStateOf(initialStudent?.college ?: "Vellore Institute of Technology") }
    var branch by remember { mutableStateOf(initialStudent?.branch ?: "Computer Science and Engineering") }
    var academicYear by remember { mutableStateOf(initialStudent?.academicYear ?: "3rd Year (Class of 2026)") }
    var cgpaText by remember { mutableStateOf(initialStudent?.cgpa?.toString() ?: "9.18") }
    var technicalSkills by remember { mutableStateOf(initialStudent?.technicalSkills ?: "Kotlin, Jetpack Compose, Coroutines, Room, Go, gRPC") }
    var experienceSummary by remember { mutableStateOf(initialStudent?.experienceSummary ?: "Software Engineering Intern at CloudMatrix (Summer 2025)") }
    var resumeProjects by remember { mutableStateOf(initialStudent?.resumeSummary ?: "Chronos: Distributed Log Engine in Go; Raft Consensus KV Store") }

    // GitHub
    var githubProfileUrl by remember { mutableStateOf(initialStudent?.githubProfileUrl ?: "https://github.com/aarav-deshmukh") }

    // Areas of Interest & Preferred Domains
    var areasOfInterest by remember { mutableStateOf(initialStudent?.areasOfInterest ?: "Distributed Systems, Mobile Performance Architecture, Real-Time Systems") }
    var preferredDomains by remember { mutableStateOf(initialStudent?.preferredDomains ?: "Systems Engineering, Core Android, Cloud Infrastructure") }
    var bio by remember { mutableStateOf(initialStudent?.bio ?: "Systems-oriented Android & Backend engineer interested in zero-allocation data structures.") }

    // Hackathons (Multiple entries allowed)
    val hackathons = remember {
        mutableStateListOf(
            HackathonEntity(
                id = "hack_init_1",
                studentId = initialStudent?.id ?: "student_default",
                eventName = "Smart India Hackathon 2025",
                rankOrPerformance = "1st Place Winner (National)",
                year = "2025",
                certificateTitle = "Ministry of Education Gold Citation"
            )
        )
    }

    var newHackEvent by remember { mutableStateOf("") }
    var newHackRank by remember { mutableStateOf("") }
    var newHackYear by remember { mutableStateOf("") }
    var newHackCert by remember { mutableStateOf("") }

    val totalSteps = 6

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NexusTokens.Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("student_onboarding_screen")
    ) {
        // Top Header
        NexusHeader(
            title = "Student Registration",
            subtitle = "Step $step of $totalSteps",
            onBack = { if (step > 1) step-- else onCancel() }
        )

        LinearProgressIndicator(
            progress = { step.toFloat() / totalSteps },
            modifier = Modifier
                .fillMaxWidth()
                .height(2.5.dp),
            color = NexusTokens.Accent,
            trackColor = NexusTokens.Border
        )

        // Main Scrollable Form Area with sensible max width
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 640.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = NexusTokens.Spacing.lg, vertical = NexusTokens.Spacing.md)
            ) {
                when (step) {
                    1 -> {
                        // STEP 1: Name and Email Verification
                        Text(
                            text = "Identity & Email Verification",
                            style = MaterialTheme.typography.headlineMedium,
                            color = NexusTokens.Foreground
                        )
                        Text(
                            text = "Enter your full name and official academic email address to begin your verified portfolio.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NexusTokens.ForegroundSecondary,
                            modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
                        )

                        NexusTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = "Full Name",
                            placeholder = "e.g. Aarav Deshmukh",
                            testTag = "full_name_input"
                        )

                        Spacer(modifier = Modifier.height(NexusTokens.Spacing.sm))

                        NexusTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                isEmailVerified = false
                            },
                            label = "Academic or Professional Email",
                            placeholder = "aarav.deshmukh@vit.ac.in",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            testTag = "email_input"
                        )

                        Spacer(modifier = Modifier.height(NexusTokens.Spacing.md))

                        if (!isEmailVerified) {
                            NexusCard(
                                backgroundColor = NexusTokens.Surface,
                                borderColor = NexusTokens.Border
                            ) {
                                Text(
                                    text = "Email Verification",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = NexusTokens.Foreground
                                )
                                Text(
                                    text = "A 6-digit confirmation code will be dispatched to verify your student email.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NexusTokens.ForegroundSecondary,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )

                                if (verificationSent) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    NexusTextField(
                                        value = verificationCode,
                                        onValueChange = { verificationCode = it },
                                        label = "Verification Code (Enter 749210)",
                                        placeholder = "749210",
                                        testTag = "verification_code_input"
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))
                                    NexusButton(
                                        onClick = {
                                            if (verificationCode.isNotBlank()) {
                                                isEmailVerified = true
                                            }
                                        },
                                        variant = NexusButtonVariant.Primary,
                                        testTag = "confirm_verify_button"
                                    ) {
                                        Text("Verify Email Address")
                                    }
                                } else {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    NexusButton(
                                        onClick = {
                                            verificationSent = true
                                            verificationCode = "749210"
                                        },
                                        variant = NexusButtonVariant.Secondary,
                                        testTag = "send_verify_code_button"
                                    ) {
                                        Text("Send Verification Code")
                                    }
                                }
                            }
                        } else {
                            Surface(
                                color = NexusTokens.SuccessBackground,
                                shape = RoundedCornerShape(NexusTokens.Radius.md),
                                border = BorderStroke(1.dp, NexusTokens.SuccessBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NexusTokens.SuccessText, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Email address verified successfully",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium,
                                        color = NexusTokens.SuccessText
                                    )
                                }
                            }
                        }
                    }

                    2 -> {
                        // STEP 2: Upload Resume & Editable Extracted Bullet Points
                        Text(
                            text = "Resume Analysis & Parsing",
                            style = MaterialTheme.typography.headlineMedium,
                            color = NexusTokens.Foreground
                        )
                        Text(
                            text = "Upload your resume file or paste text. Information is extracted into editable bullet points below. Missing items are never invented.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NexusTokens.ForegroundSecondary,
                            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                        )

                        // Reusable file upload area with guidance, progress & removal
                        NexusUploadArea(
                            fileName = resumeFileName,
                            fileSizeInfo = "PDF or TXT up to 5 MB • Text parsing enabled",
                            progress = if (isExtractingResume) 0.65f else null,
                            onUploadClick = {
                                if (resumeFileName == null) resumeFileName = "Uploaded_Resume.pdf"
                            },
                            onRemoveClick = { resumeFileName = null }
                        )

                        Spacer(modifier = Modifier.height(NexusTokens.Spacing.sm))

                        NexusTextField(
                            value = resumeRawText,
                            onValueChange = { resumeRawText = it },
                            label = "Resume Raw Text Content",
                            placeholder = "Paste resume content here...",
                            minLines = 4,
                            maxLines = 7,
                            testTag = "resume_text_input"
                        )

                        Spacer(modifier = Modifier.height(NexusTokens.Spacing.sm))

                        NexusButton(
                            onClick = {
                                scope.launch {
                                    isExtractingResume = true
                                    val data = GeminiAiService.extractResume(resumeRawText)
                                    if (data.fullName.isNotBlank()) fullName = data.fullName
                                    if (data.email.isNotBlank()) email = data.email
                                    if (data.college.isNotBlank()) college = data.college
                                    if (data.branch.isNotBlank()) branch = data.branch
                                    if (data.academicYear.isNotBlank()) academicYear = data.academicYear
                                    if (data.cgpa > 0.0) cgpaText = data.cgpa.toString()
                                    if (data.skills.isNotBlank()) technicalSkills = data.skills
                                    if (data.experience.isNotBlank()) experienceSummary = data.experience
                                    if (data.projects.isNotBlank()) resumeProjects = data.projects
                                    isExtractingResume = false
                                }
                            },
                            isLoading = isExtractingResume,
                            variant = NexusButtonVariant.Secondary,
                            icon = Icons.Default.UploadFile,
                            testTag = "extract_resume_button"
                        ) {
                            Text("Extract Resume Fields")
                        }

                        Spacer(modifier = Modifier.height(NexusTokens.Spacing.lg))

                        Text(
                            text = "Editable Extracted Information:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = NexusTokens.Foreground
                        )

                        Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))

                        NexusTextField(
                            value = college,
                            onValueChange = { college = it },
                            label = "College / University",
                            testTag = "college_input"
                        )
                        Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))
                        Row(horizontalArrangement = Arrangement.spacedBy(NexusTokens.Spacing.xs)) {
                            NexusTextField(
                                value = branch,
                                onValueChange = { branch = it },
                                label = "Branch / Major",
                                modifier = Modifier.weight(1.5f),
                                testTag = "branch_input"
                            )
                            NexusTextField(
                                value = cgpaText,
                                onValueChange = { cgpaText = it },
                                label = "CGPA",
                                modifier = Modifier.weight(1f),
                                testTag = "cgpa_input"
                            )
                        }
                        Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))
                        NexusTextField(
                            value = academicYear,
                            onValueChange = { academicYear = it },
                            label = "Academic Year",
                            testTag = "academic_year_input"
                        )
                        Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))
                        NexusTextField(
                            value = experienceSummary,
                            onValueChange = { experienceSummary = it },
                            label = "Prior Experience / Internships",
                            testTag = "experience_input"
                        )
                        Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))
                        NexusTextField(
                            value = resumeProjects,
                            onValueChange = { resumeProjects = it },
                            label = "Notable Projects Summary",
                            testTag = "projects_summary_input"
                        )
                    }

                    3 -> {
                        // STEP 3: GitHub Profile URL
                        Text(
                            text = "GitHub Developer Identity",
                            style = MaterialTheme.typography.headlineMedium,
                            color = NexusTokens.Foreground
                        )
                        Text(
                            text = "Link your public GitHub profile to authenticate your open-source contributions and codebase submissions.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NexusTokens.ForegroundSecondary,
                            modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
                        )

                        NexusTextField(
                            value = githubProfileUrl,
                            onValueChange = { githubProfileUrl = it },
                            label = "GitHub Profile URL",
                            placeholder = "https://github.com/username",
                            testTag = "github_profile_url_input"
                        )

                        Spacer(modifier = Modifier.height(NexusTokens.Spacing.md))

                        NexusCard(
                            backgroundColor = NexusTokens.Surface,
                            borderColor = NexusTokens.Border
                        ) {
                            Text(
                                text = "Repository Verification Protocol",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = NexusTokens.Foreground
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "• Only accessible public repositories can be evaluated.\n• Code structure, READMEs, and implementation files are audited directly via the GitHub API.\n• Inaccessible repositories will not be analyzed.",
                                style = MaterialTheme.typography.bodySmall,
                                color = NexusTokens.ForegroundSecondary,
                                lineHeight = 20.sp
                            )
                        }
                    }

                    4 -> {
                        // STEP 4: Technical Skills, Interests, Preferred Domains
                        Text(
                            text = "Technical Competencies & Domains",
                            style = MaterialTheme.typography.headlineMedium,
                            color = NexusTokens.Foreground
                        )
                        Text(
                            text = "Define your core language proficiencies, engineering interests, and target internship specializations.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NexusTokens.ForegroundSecondary,
                            modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
                        )

                        NexusTextField(
                            value = technicalSkills,
                            onValueChange = { technicalSkills = it },
                            label = "Technical Skills (comma separated)",
                            placeholder = "Kotlin, Jetpack Compose, Go, Docker, PostgreSQL",
                            testTag = "skills_input"
                        )

                        Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))

                        NexusTextField(
                            value = areasOfInterest,
                            onValueChange = { areasOfInterest = it },
                            label = "Areas of Technical Interest",
                            placeholder = "Distributed Systems, Mobile Architectures, Zero-Copy Buffers",
                            testTag = "interests_input"
                        )

                        Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))

                        NexusTextField(
                            value = preferredDomains,
                            onValueChange = { preferredDomains = it },
                            label = "Preferred Internship Domains",
                            placeholder = "Core Android Engineering, Cloud Infrastructure",
                            testTag = "domains_input"
                        )

                        Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))

                        NexusTextField(
                            value = bio,
                            onValueChange = { bio = it },
                            label = "Engineering Focus Statement",
                            placeholder = "Brief technical summary...",
                            minLines = 2,
                            maxLines = 4,
                            testTag = "bio_input"
                        )
                    }

                    5 -> {
                        // STEP 5: Hackathons & Certificates (Multiple Entries)
                        Text(
                            text = "Hackathon Achievements & Certificates",
                            style = MaterialTheme.typography.headlineMedium,
                            color = NexusTokens.Foreground
                        )
                        Text(
                            text = "Add verified hackathon participations, national/global rankings, and certificate references.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NexusTokens.ForegroundSecondary,
                            modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
                        )

                        // Current Hackathons List
                        hackathons.forEachIndexed { index, hack ->
                            NexusCard(
                                modifier = Modifier.padding(bottom = NexusTokens.Spacing.xs),
                                backgroundColor = NexusTokens.Surface,
                                borderColor = NexusTokens.Border
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = hack.eventName,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = NexusTokens.Foreground
                                        )
                                        Text(
                                            text = "${hack.rankOrPerformance} • ${hack.year}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = NexusTokens.Accent,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "Certificate: ${hack.certificateTitle}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = NexusTokens.ForegroundSecondary
                                        )
                                    }

                                    IconButton(onClick = { hackathons.removeAt(index) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = NexusTokens.ForegroundTertiary, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))

                        // Add New Hackathon Form
                        NexusCard(
                            backgroundColor = NexusTokens.SurfaceSecondary,
                            borderColor = NexusTokens.Border
                        ) {
                            Text(
                                text = "Add Hackathon Entry",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = NexusTokens.Foreground
                            )
                            Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))

                            NexusTextField(
                                value = newHackEvent,
                                onValueChange = { newHackEvent = it },
                                label = "Event Name",
                                placeholder = "e.g. Smart India Hackathon",
                                testTag = "hackathon_event_input"
                            )

                            Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))

                            Row(horizontalArrangement = Arrangement.spacedBy(NexusTokens.Spacing.xs)) {
                                NexusTextField(
                                    value = newHackRank,
                                    onValueChange = { newHackRank = it },
                                    label = "Rank / Performance",
                                    placeholder = "1st Place Winner",
                                    modifier = Modifier.weight(1.5f),
                                    testTag = "hackathon_rank_input"
                                )
                                NexusTextField(
                                    value = newHackYear,
                                    onValueChange = { newHackYear = it },
                                    label = "Year",
                                    placeholder = "2025",
                                    modifier = Modifier.weight(1f),
                                    testTag = "hackathon_year_input"
                                )
                            }

                            Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))

                            NexusTextField(
                                value = newHackCert,
                                onValueChange = { newHackCert = it },
                                label = "Certificate Upload / Reference Title",
                                placeholder = "Ministry of Education Citation",
                                testTag = "hackathon_cert_input"
                            )

                            Spacer(modifier = Modifier.height(NexusTokens.Spacing.sm))

                            NexusButton(
                                onClick = {
                                    if (newHackEvent.isNotBlank()) {
                                        hackathons.add(
                                            HackathonEntity(
                                                id = "hack_" + UUID.randomUUID().toString().take(6),
                                                studentId = initialStudent?.id ?: "student_default",
                                                eventName = newHackEvent.trim(),
                                                rankOrPerformance = if (newHackRank.isBlank()) "Participant" else newHackRank.trim(),
                                                year = if (newHackYear.isBlank()) "2025" else newHackYear.trim(),
                                                certificateTitle = if (newHackCert.isBlank()) "Verified Participation" else newHackCert.trim()
                                            )
                                        )
                                        newHackEvent = ""
                                        newHackRank = ""
                                        newHackYear = ""
                                        newHackCert = ""
                                    }
                                },
                                variant = NexusButtonVariant.Secondary,
                                icon = Icons.Default.Add,
                                testTag = "add_hackathon_button"
                            ) {
                                Text("Add Entry")
                            }
                        }
                    }

                    6 -> {
                        // STEP 6: Review & Confirm Profile
                        Text(
                            text = "Review & Finalize Portfolio",
                            style = MaterialTheme.typography.headlineMedium,
                            color = NexusTokens.Foreground
                        )
                        Text(
                            text = "Confirm your engineering portfolio. After confirmation, you can submit projects for AI assessment.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NexusTokens.ForegroundSecondary,
                            modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
                        )

                        NexusCard(
                            backgroundColor = NexusTokens.Surface,
                            borderColor = NexusTokens.Border
                        ) {
                            Text(
                                text = fullName.ifBlank { "Candidate" },
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = NexusTokens.Foreground
                            )
                            Text(
                                text = email.ifBlank { "candidate@example.edu" },
                                style = MaterialTheme.typography.bodySmall,
                                color = NexusTokens.Accent
                            )

                            Spacer(modifier = Modifier.height(NexusTokens.Spacing.sm))

                            Text(
                                text = "$college • $branch",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = NexusTokens.Foreground
                            )
                            Text(
                                text = "CGPA: $cgpaText • $academicYear",
                                style = MaterialTheme.typography.bodySmall,
                                color = NexusTokens.ForegroundSecondary
                            )

                            Spacer(modifier = Modifier.height(NexusTokens.Spacing.sm))

                            Text(
                                text = "Technical Stack",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = NexusTokens.Foreground
                            )
                            Text(
                                text = technicalSkills,
                                style = MaterialTheme.typography.bodySmall,
                                color = NexusTokens.ForegroundSecondary
                            )

                            Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))

                            Text(
                                text = "GitHub Profile",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = NexusTokens.Foreground
                            )
                            Text(
                                text = githubProfileUrl,
                                style = MaterialTheme.typography.bodySmall,
                                color = NexusTokens.Accent
                            )

                            Spacer(modifier = Modifier.height(NexusTokens.Spacing.xs))

                            Text(
                                text = "Hackathon Achievements (${hackathons.size})",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = NexusTokens.Foreground
                            )
                            hackathons.forEach { h ->
                                Text(
                                    text = "• ${h.eventName} (${h.rankOrPerformance}, ${h.year})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NexusTokens.ForegroundSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom Action Bar
        Surface(
            color = NexusTokens.Surface,
            border = BorderStroke(1.dp, NexusTokens.Border)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (step > 1) {
                    NexusButton(
                        onClick = { step-- },
                        variant = NexusButtonVariant.Outline,
                        testTag = "onboarding_prev_button"
                    ) {
                        Text("Previous")
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                if (step < totalSteps) {
                    NexusButton(
                        onClick = { step++ },
                        variant = NexusButtonVariant.Primary,
                        trailingIcon = Icons.AutoMirrored.Filled.ArrowForward,
                        testTag = "onboarding_next_button"
                    ) {
                        Text("Next Step")
                    }
                } else {
                    NexusButton(
                        onClick = {
                            val studentId = initialStudent?.id ?: "student_default"
                            val parsedCgpa = cgpaText.toDoubleOrNull() ?: 8.5
                            val updatedStudent = StudentEntity(
                                id = studentId,
                                fullName = fullName.ifBlank { "Candidate" },
                                email = email.ifBlank { "candidate@example.edu" },
                                isEmailVerified = isEmailVerified,
                                college = college,
                                branch = branch,
                                academicYear = academicYear,
                                cgpa = parsedCgpa,
                                technicalSkills = technicalSkills,
                                areasOfInterest = areasOfInterest,
                                preferredDomains = preferredDomains,
                                githubProfileUrl = githubProfileUrl,
                                resumeSummary = resumeProjects,
                                experienceSummary = experienceSummary,
                                bio = bio,
                                isOnboardingComplete = true,
                                updatedAt = System.currentTimeMillis()
                            )
                            onSaveProfile(updatedStudent, hackathons)
                        },
                        variant = NexusButtonVariant.Primary,
                        icon = Icons.Default.Check,
                        testTag = "confirm_profile_button"
                    ) {
                        Text("Confirm Profile")
                    }
                }
            }
        }
    }
}
