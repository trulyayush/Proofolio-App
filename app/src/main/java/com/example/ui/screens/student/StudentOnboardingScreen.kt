package com.example.ui.screens.student

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PickedResume
import com.example.data.SessionStore
import com.example.data.api.ApiResult
import com.example.data.api.StudentCreateRequest
import com.example.data.api.StudentCreatedResponse
import com.example.data.readResumePdf
import com.example.ui.components.Floating3DBackground
import com.example.ui.components.NexusButton
import com.example.ui.components.NexusButtonVariant
import com.example.ui.components.NexusCard
import com.example.ui.components.NexusTextField
import com.example.ui.theme.NexusTokens
import com.example.viewmodel.Load

// Fixed vocabularies as required by PROJECT_RULES.md
private val INTERESTS_OPTIONS = listOf(
    "ai" to "AI",
    "data" to "Data",
    "web" to "Web",
    "mobile" to "Mobile",
    "saas" to "SaaS",
    "fintech" to "FinTech",
    "edtech" to "EdTech",
    "hrtech" to "HR-Tech",
    "security" to "Security",
    "devtools" to "Dev tools",
    "growth" to "Growth",
    "healthcare" to "Healthcare"
)

private val SKILL_CHIPS_OPTIONS = listOf(
    "Python", "JavaScript", "TypeScript", "Java", "C++",
    "React", "Next.js", "HTML/CSS", "Flutter", "Android",
    "Node.js", "FastAPI", "Django", "Flask", "Spring",
    "SQL", "MongoDB", "Pandas", "Machine Learning", "Deep Learning",
    "LLMs/GenAI", "Docker", "Cloud (AWS/GCP/Azure)", "CI/CD", "Security"
)

private val GITHUB_USERNAME_REGEX = Regex("^[A-Za-z0-9](?:[A-Za-z0-9-]{0,38})$")
private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

fun normalizeGitHubUsername(raw: String): String {
    var s = raw.trim()
    val prefixes = listOf("https://github.com/", "http://github.com/", "github.com/", "@")
    for (p in prefixes) {
        if (s.startsWith(p, ignoreCase = true)) {
            s = s.substring(p.length)
            break
        }
    }
    s = s.trimEnd('/')
    val slashIdx = s.indexOf('/')
    if (slashIdx != -1) {
        s = s.substring(0, slashIdx)
    }
    return s.trim()
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StudentOnboardingScreen(
    state: Load<StudentCreatedResponse>,
    onSubmit: (StudentCreateRequest) -> Unit,
    onSuccess: () -> Unit,
    onBack: () -> Unit,
    onClearError: () -> Unit,
    sessionStore: SessionStore,
    onPrivacyPolicyClick: () -> Unit,
    onTermsClick: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Form inputs
    var fullName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var college by rememberSaveable { mutableStateOf("") }
    var githubInput by rememberSaveable { mutableStateOf("") }

    val selectedInterests = remember { mutableStateListOf<String>() }
    val selectedSkills = remember { mutableStateListOf<String>() }

    // Resume state
    var pickedResume by remember { mutableStateOf<PickedResume.Ok?>(null) }
    var resumeRejectMessage by remember { mutableStateOf<String?>(null) }

    // Validation errors
    var fullNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var collegeError by remember { mutableStateOf<String?>(null) }
    var githubError by remember { mutableStateOf<String?>(null) }
    var interestsError by remember { mutableStateOf<String?>(null) }
    var skillsError by remember { mutableStateOf<String?>(null) }
    var termsAccepted by rememberSaveable { mutableStateOf(false) }
    var termsError by remember { mutableStateOf<String?>(null) }

    val isSubmitting = state is Load.Loading
    val apiError = (state as? Load.Failed)?.error

    val resumeLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            when (val result = readResumePdf(context, uri)) {
                is PickedResume.Ok -> {
                    pickedResume = result
                    resumeRejectMessage = null
                    skillsError = null
                    onClearError()
                }
                is PickedResume.Rejected -> {
                    pickedResume = null
                    resumeRejectMessage = result.message
                }
            }
        }
    }

    fun validate(): Boolean {
        var valid = true

        val trimmedName = fullName.trim()
        if (trimmedName.isEmpty()) {
            fullNameError = "Full name is required."
            valid = false
        } else if (trimmedName.length > 100) {
            fullNameError = "Full name must be at most 100 characters."
            valid = false
        } else {
            fullNameError = null
        }

        val trimmedEmail = email.trim()
        if (trimmedEmail.isEmpty()) {
            emailError = "Email is required."
            valid = false
        } else if (trimmedEmail.length > 200) {
            emailError = "Email must be at most 200 characters."
            valid = false
        } else if (!EMAIL_REGEX.matches(trimmedEmail)) {
            emailError = "Please enter a valid email address (name@domain.tld)."
            valid = false
        } else {
            emailError = null
        }

        val trimmedCollege = college.trim()
        if (trimmedCollege.isEmpty()) {
            collegeError = "College is required."
            valid = false
        } else if (trimmedCollege.length > 150) {
            collegeError = "College must be at most 150 characters."
            valid = false
        } else {
            collegeError = null
        }

        val normalizedGh = normalizeGitHubUsername(githubInput)
        if (normalizedGh.isEmpty()) {
            githubError = "GitHub username is required."
            valid = false
        } else if (!GITHUB_USERNAME_REGEX.matches(normalizedGh)) {
            githubError = "That doesn't look like a GitHub username."
            valid = false
        } else {
            githubError = null
        }

        if (selectedInterests.isEmpty()) {
            interestsError = "Pick at least one interest."
            valid = false
        } else {
            interestsError = null
        }

        if (pickedResume == null && selectedSkills.isEmpty()) {
            skillsError = "Upload a resume or pick at least one skill."
            valid = false
        } else {
            skillsError = null
        }

        return valid
    }

    fun submitForm(withoutResume: Boolean = false) {
        if (withoutResume) {
            pickedResume = null
        }
        if (!validate()) return

        if (!termsAccepted) {
            termsError = "You must agree to the Terms & Conditions and Privacy Policy to register."
            return
        } else {
            termsError = null
        }

        onClearError()
        val normalizedGh = normalizeGitHubUsername(githubInput)
        val resumeToUse = if (withoutResume) null else pickedResume

        val request = StudentCreateRequest(
            name = fullName.trim(),
            email = email.trim(),
            college = college.trim(),
            githubUsername = normalizedGh,
            interests = selectedInterests.toList(),
            skills = if (selectedSkills.isNotEmpty()) selectedSkills.toList() else null,
            resumeBase64 = resumeToUse?.base64,
            resumeMime = if (resumeToUse != null) "application/pdf" else null
        )
        onSubmit(request)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NexusTokens.Background)
            .testTag("student_onboarding_screen")
    ) {
        Floating3DBackground(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 640.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    enabled = !isSubmitting,
                    modifier = Modifier.testTag("onboarding_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = NexusTokens.Foreground
                    )
                }

                Surface(
                    color = NexusTokens.Surface,
                    shape = RoundedCornerShape(NexusTokens.Radius.pill),
                    border = BorderStroke(1.dp, NexusTokens.Border)
                ) {
                    Text(
                        text = "Proofolio Profile",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = NexusTokens.ForegroundSecondary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.size(48.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Form Container Card
            NexusCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 640.dp),
                backgroundColor = NexusTokens.Surface,
                borderColor = NexusTokens.Border
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Create your profile",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = NexusTokens.Foreground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Connect your GitHub repository and skills to prepare for your code viva.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NexusTokens.ForegroundSecondary
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 1. Full Name
                    NexusTextField(
                        value = fullName,
                        onValueChange = {
                            if (it.length <= 100) {
                                fullName = it
                                if (fullNameError != null) fullNameError = null
                            }
                        },
                        label = "Full name *",
                        placeholder = "Your full name",
                        errorText = fullNameError,
                        enabled = !isSubmitting,
                        singleLine = true,
                        leadingIcon = Icons.Default.Person,
                        testTag = "onboarding_name_input"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 2. Email
                    NexusTextField(
                        value = email,
                        onValueChange = {
                            if (it.length <= 200) {
                                email = it
                                if (emailError != null) emailError = null
                            }
                        },
                        label = "Email address *",
                        placeholder = "student@university.edu",
                        errorText = emailError,
                        enabled = !isSubmitting,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        testTag = "onboarding_email_input"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3. College
                    NexusTextField(
                        value = college,
                        onValueChange = {
                            if (it.length <= 150) {
                                college = it
                                if (collegeError != null) collegeError = null
                            }
                        },
                        label = "College / University *",
                        placeholder = "Your university or college",
                        errorText = collegeError,
                        enabled = !isSubmitting,
                        singleLine = true,
                        leadingIcon = Icons.Default.School,
                        testTag = "onboarding_college_input"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 4. GitHub Username
                    NexusTextField(
                        value = githubInput,
                        onValueChange = {
                            githubInput = it
                            if (githubError != null) githubError = null
                        },
                        label = "GitHub username *",
                        placeholder = "username or https://github.com/username",
                        helperText = "We only read your public repos.",
                        errorText = githubError,
                        enabled = !isSubmitting,
                        singleLine = true,
                        testTag = "onboarding_github_input"
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 5. Interests
                    Text(
                        text = "Interests *",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = NexusTokens.Foreground
                    )
                    Text(
                        text = "Pick the technology areas you want to explore.",
                        style = MaterialTheme.typography.bodySmall,
                        color = NexusTokens.ForegroundSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        INTERESTS_OPTIONS.forEach { (id, label) ->
                            val isSelected = selectedInterests.contains(id)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (!isSubmitting) {
                                        if (isSelected) {
                                            selectedInterests.remove(id)
                                        } else {
                                            selectedInterests.add(id)
                                        }
                                        if (interestsError != null) interestsError = null
                                    }
                                },
                                enabled = !isSubmitting,
                                label = {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NexusTokens.AccentSubtle,
                                    selectedLabelColor = NexusTokens.Accent,
                                    containerColor = NexusTokens.SurfaceSecondary,
                                    labelColor = NexusTokens.Foreground
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = !isSubmitting,
                                    selected = isSelected,
                                    borderColor = if (isSelected) NexusTokens.Accent else NexusTokens.Border,
                                    selectedBorderColor = NexusTokens.Accent
                                )
                            )
                        }
                    }

                    if (interestsError != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = interestsError!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = NexusTokens.ErrorText
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // 6. Skills
                    Text(
                        text = "Your skills",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = NexusTokens.Foreground
                    )
                    Text(
                        text = "With a resume, we read your skills from it. Without one, we use the chips you pick.",
                        style = MaterialTheme.typography.bodySmall,
                        color = NexusTokens.ForegroundSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 6a. Upload resume
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(),
                        color = if (pickedResume != null) NexusTokens.Surface else NexusTokens.SurfaceSecondary,
                        shape = RoundedCornerShape(NexusTokens.Radius.md),
                        border = BorderStroke(
                            1.dp,
                            if (pickedResume != null) NexusTokens.Accent else NexusTokens.Border
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp)
                        ) {
                            if (pickedResume == null) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.UploadFile,
                                            contentDescription = null,
                                            tint = NexusTokens.Accent,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "Upload your resume (PDF, under 5 MB)",
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.Medium,
                                                color = NexusTokens.Foreground
                                            )
                                            Text(
                                                text = "PDF only, max 5 MB",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = NexusTokens.ForegroundTertiary
                                            )
                                        }
                                    }

                                    NexusButton(
                                        onClick = {
                                            resumeLauncher.launch(arrayOf("application/pdf"))
                                        },
                                        enabled = !isSubmitting,
                                        variant = NexusButtonVariant.Secondary,
                                        testTag = "onboarding_upload_resume_button"
                                    ) {
                                        Text("Choose PDF")
                                    }
                                }

                                if (resumeRejectMessage != null) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.ErrorOutline,
                                            contentDescription = null,
                                            tint = NexusTokens.ErrorText,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = resumeRejectMessage!!,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = NexusTokens.ErrorText
                                        )
                                    }
                                }
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = NexusTokens.SuccessText,
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = pickedResume!!.fileName,
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.SemiBold,
                                                color = NexusTokens.Foreground
                                            )
                                            Text(
                                                text = "Resume attached (PDF)",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = NexusTokens.SuccessText
                                            )
                                        }
                                    }

                                    NexusButton(
                                        onClick = {
                                            pickedResume = null
                                            resumeRejectMessage = null
                                        },
                                        enabled = !isSubmitting,
                                        variant = NexusButtonVariant.Outline,
                                        testTag = "onboarding_remove_resume_button"
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Remove")
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 6b. Skill chips
                    Text(
                        text = "Or choose skills directly:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color = NexusTokens.Foreground
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SKILL_CHIPS_OPTIONS.forEach { chipText ->
                            val isSelected = selectedSkills.contains(chipText)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (!isSubmitting) {
                                        if (isSelected) {
                                            selectedSkills.remove(chipText)
                                        } else {
                                            selectedSkills.add(chipText)
                                        }
                                        if (skillsError != null) skillsError = null
                                    }
                                },
                                enabled = !isSubmitting,
                                label = {
                                    Text(
                                        text = chipText,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NexusTokens.AccentSubtle,
                                    selectedLabelColor = NexusTokens.Accent,
                                    containerColor = NexusTokens.SurfaceSecondary,
                                    labelColor = NexusTokens.Foreground
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = !isSubmitting,
                                    selected = isSelected,
                                    borderColor = if (isSelected) NexusTokens.Accent else NexusTokens.Border,
                                    selectedBorderColor = NexusTokens.Accent
                                )
                            )
                        }
                    }

                    if (skillsError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = skillsError!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = NexusTokens.ErrorText
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Terms & Conditions checkbox and links
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { termsAccepted = !termsAccepted }
                            .testTag("onboarding_terms_checkbox_row"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        androidx.compose.material3.Checkbox(
                            checked = termsAccepted,
                            onCheckedChange = {
                                termsAccepted = it
                                if (it) termsError = null
                            },
                            modifier = Modifier.testTag("onboarding_terms_checkbox")
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "I agree to the ",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NexusTokens.ForegroundSecondary
                                )
                                Text(
                                    text = "Terms & Conditions",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = NexusTokens.Accent,
                                    modifier = Modifier
                                        .clickable(onClick = onTermsClick)
                                        .testTag("onboarding_terms_link")
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "and acknowledge the ",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NexusTokens.ForegroundSecondary
                                )
                                Text(
                                    text = "Privacy Policy",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = NexusTokens.Accent,
                                    modifier = Modifier
                                        .clickable(onClick = onPrivacyPolicyClick)
                                        .testTag("onboarding_privacy_link")
                                )
                            }
                        }
                    }

                    if (termsError != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = termsError!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = NexusTokens.ErrorText
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Submit button
                    val submittingText = if (pickedResume != null) {
                        "Checking your GitHub and reading your resume…"
                    } else {
                        "Checking your GitHub…"
                    }

                    NexusButton(
                        onClick = { submitForm() },
                        enabled = !isSubmitting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("onboarding_submit_button"),
                        variant = NexusButtonVariant.Primary
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                color = NexusTokens.OnAccent,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(submittingText)
                        } else {
                            Text("Create my profile")
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Error presentation under the button
                    if (apiError != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            color = NexusTokens.ErrorBackground,
                            shape = RoundedCornerShape(NexusTokens.Radius.sm),
                            border = BorderStroke(1.dp, NexusTokens.ErrorBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = apiError.message,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NexusTokens.ErrorText
                                )

                                // If code == "RESUME_UNREADABLE", offer "Continue without resume"
                                if (apiError.code == "RESUME_UNREADABLE") {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    NexusButton(
                                        onClick = {
                                            if (selectedSkills.isNotEmpty()) {
                                                submitForm(withoutResume = true)
                                            } else {
                                                skillsError = "Please pick at least one skill before continuing without a resume."
                                            }
                                        },
                                        variant = NexusButtonVariant.Outline,
                                        modifier = Modifier.testTag("onboarding_continue_without_resume_button")
                                    ) {
                                        Text("Continue without resume")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
