package com.example.ui.screens.student

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.api.ApiResult
import com.example.data.api.HackathonItem
import com.example.data.api.ReposResponse
import com.example.ui.components.NexusButton
import com.example.ui.components.NexusButtonVariant
import com.example.ui.components.NexusTextField
import com.example.ui.theme.NexusTokens
import com.example.viewmodel.Load
import kotlinx.coroutines.launch
import java.util.Calendar

private val TEAM_REPO_REGEX = Regex("^[A-Za-z0-9-]{1,39}/[A-Za-z0-9._-]{1,100}$")

private enum class RepoOption {
    MY_REPO,
    TEAM_REPO
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHackathonSheet(
    reposState: Load<ReposResponse>,
    onLoadRepos: () -> Unit,
    onSaveHackathon: suspend (
        event: String,
        year: Int,
        result: String,
        teamSize: Int,
        repoFullName: String
    ) -> ApiResult<HackathonItem>,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val currentYear = remember { Calendar.getInstance().get(Calendar.YEAR) }

    var eventName by remember { mutableStateOf("") }
    var yearText by remember { mutableStateOf(currentYear.toString()) }
    var resultChoice by remember { mutableStateOf("winner") } // "winner", "finalist", "participant"
    var teamSize by remember { mutableIntStateOf(1) } // 1..20

    var repoOption by remember { mutableStateOf(RepoOption.MY_REPO) }
    var selectedMyRepo by remember { mutableStateOf<String?>(null) }
    var isRepoDropdownExpanded by remember { mutableStateOf(false) }

    var teamRepoInput by remember { mutableStateOf("") }
    var teamRepoError by remember { mutableStateOf<String?>(null) }

    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Load user's repos if needed
    LaunchedEffect(Unit) {
        if (reposState is Load.Idle) {
            onLoadRepos()
        }
    }

    // Auto-select first repo if available and none selected yet
    LaunchedEffect(reposState) {
        if (reposState is Load.Ready && selectedMyRepo == null && reposState.value.repos.isNotEmpty()) {
            selectedMyRepo = reposState.value.repos.first().fullName
        }
    }

    // Form validation
    val isEventValid by remember {
        derivedStateOf {
            eventName.trim().isNotEmpty() && eventName.trim().length <= 120
        }
    }

    val isYearValid by remember {
        derivedStateOf {
            val y = yearText.trim().toIntOrNull()
            y != null && y in 2000..2100
        }
    }

    val chosenRepoFullName by remember {
        derivedStateOf {
            when (repoOption) {
                RepoOption.MY_REPO -> selectedMyRepo
                RepoOption.TEAM_REPO -> {
                    val normalized = normalizeTeamRepoInput(teamRepoInput)
                    if (TEAM_REPO_REGEX.matches(normalized)) normalized else null
                }
            }
        }
    }

    val isFormValid by remember {
        derivedStateOf {
            isEventValid && isYearValid && !chosenRepoFullName.isNullOrBlank()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = NexusTokens.Surface,
        dragHandle = null,
        modifier = Modifier
            .navigationBarsPadding()
            .imePadding()
            .testTag("add_hackathon_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = NexusTokens.Accent,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Add a hackathon",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = NexusTokens.Foreground
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("add_hackathon_close_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = NexusTokens.ForegroundSecondary
                    )
                }
            }

            Text(
                text = "Add a hackathon you built for. You can verify it with a viva on its repo to prove your contribution.",
                style = MaterialTheme.typography.bodySmall,
                color = NexusTokens.ForegroundSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Error banner if any
            if (errorMessage != null) {
                Surface(
                    color = NexusTokens.ErrorBackground,
                    shape = RoundedCornerShape(NexusTokens.Radius.sm),
                    border = BorderStroke(1.dp, NexusTokens.ErrorBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = NexusTokens.ErrorText,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorMessage ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexusTokens.ErrorText
                        )
                    }
                }
            }

            // 1. Event Name
            NexusTextField(
                value = eventName,
                onValueChange = {
                    if (it.length <= 120) {
                        eventName = it
                        errorMessage = null
                    }
                },
                label = "Event name",
                placeholder = "e.g. Smart City Hackathon",
                helperText = "${eventName.length}/120 characters",
                singleLine = true,
                testTag = "hackathon_event_name_input"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Year
            NexusTextField(
                value = yearText,
                onValueChange = { input ->
                    val filtered = input.filter { it.isDigit() }.take(4)
                    yearText = filtered
                    errorMessage = null
                },
                label = "Year",
                placeholder = currentYear.toString(),
                helperText = "Between 2000 and 2100",
                errorText = if (yearText.isNotEmpty() && !isYearValid) "Must be between 2000 and 2100" else null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                testTag = "hackathon_year_input"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Result (Winner / Finalist / Participant)
            Text(
                text = "Result",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = NexusTokens.Foreground
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NexusTokens.SurfaceSecondary, RoundedCornerShape(NexusTokens.Radius.md))
                    .border(1.dp, NexusTokens.Border, RoundedCornerShape(NexusTokens.Radius.md))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(
                    "winner" to "Winner",
                    "finalist" to "Finalist",
                    "participant" to "Participant"
                ).forEach { (value, label) ->
                    val isSelected = resultChoice == value
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(NexusTokens.Radius.sm))
                            .background(if (isSelected) NexusTokens.Accent else androidx.compose.ui.graphics.Color.Transparent)
                            .clickable {
                                resultChoice = value
                                errorMessage = null
                            }
                            .padding(vertical = 8.dp)
                            .testTag("hackathon_result_$value"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) NexusTokens.OnAccent else NexusTokens.ForegroundSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 4. Team size stepper (1 to 20, default 1)
            Text(
                text = "Team size",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = NexusTokens.Foreground
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (teamSize == 1) "1 member (Solo project)" else "$teamSize members",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NexusTokens.ForegroundSecondary
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { if (teamSize > 1) teamSize-- },
                        enabled = teamSize > 1,
                        modifier = Modifier
                            .size(36.dp)
                            .border(
                                1.dp,
                                if (teamSize > 1) NexusTokens.Border else NexusTokens.Border.copy(alpha = 0.4f),
                                CircleShape
                            )
                            .testTag("hackathon_team_size_minus")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease team size",
                            tint = if (teamSize > 1) NexusTokens.Foreground else NexusTokens.ForegroundTertiary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = "$teamSize",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NexusTokens.Foreground,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    IconButton(
                        onClick = { if (teamSize < 20) teamSize++ },
                        enabled = teamSize < 20,
                        modifier = Modifier
                            .size(36.dp)
                            .border(
                                1.dp,
                                if (teamSize < 20) NexusTokens.Border else NexusTokens.Border.copy(alpha = 0.4f),
                                CircleShape
                            )
                            .testTag("hackathon_team_size_plus")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase team size",
                            tint = if (teamSize < 20) NexusTokens.Foreground else NexusTokens.ForegroundTertiary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 5. Project repository
            Text(
                text = "Project repository",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = NexusTokens.Foreground
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Radio/Segmented option for repo origin
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NexusTokens.SurfaceSecondary, RoundedCornerShape(NexusTokens.Radius.md))
                    .border(1.dp, NexusTokens.Border, RoundedCornerShape(NexusTokens.Radius.md))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(NexusTokens.Radius.sm))
                        .background(if (repoOption == RepoOption.MY_REPO) NexusTokens.Accent else androidx.compose.ui.graphics.Color.Transparent)
                        .clickable {
                            repoOption = RepoOption.MY_REPO
                            errorMessage = null
                        }
                        .padding(vertical = 8.dp)
                        .testTag("hackathon_repo_option_my_repo"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "One of my repos",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (repoOption == RepoOption.MY_REPO) FontWeight.Bold else FontWeight.Medium,
                        color = if (repoOption == RepoOption.MY_REPO) NexusTokens.OnAccent else NexusTokens.ForegroundSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(NexusTokens.Radius.sm))
                        .background(if (repoOption == RepoOption.TEAM_REPO) NexusTokens.Accent else androidx.compose.ui.graphics.Color.Transparent)
                        .clickable {
                            repoOption = RepoOption.TEAM_REPO
                            errorMessage = null
                        }
                        .padding(vertical = 8.dp)
                        .testTag("hackathon_repo_option_team_repo"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Team or org repo",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (repoOption == RepoOption.TEAM_REPO) FontWeight.Bold else FontWeight.Medium,
                        color = if (repoOption == RepoOption.TEAM_REPO) NexusTokens.OnAccent else NexusTokens.ForegroundSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (repoOption) {
                RepoOption.MY_REPO -> {
                    when (reposState) {
                        is Load.Loading -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = NexusTokens.Accent
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Loading your repositories…",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NexusTokens.ForegroundSecondary
                                )
                            }
                        }

                        is Load.Failed -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = reposState.error.message,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NexusTokens.ErrorText,
                                    modifier = Modifier.weight(1f)
                                )
                                NexusButton(
                                    onClick = onLoadRepos,
                                    variant = NexusButtonVariant.Outline
                                ) {
                                    Text("Retry")
                                }
                            }
                        }

                        is Load.Ready -> {
                            val repos = reposState.value.repos
                            if (repos.isEmpty()) {
                                Text(
                                    text = "No public repositories found for your GitHub account.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NexusTokens.ForegroundSecondary
                                )
                            } else {
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    Surface(
                                        color = NexusTokens.Surface,
                                        shape = RoundedCornerShape(NexusTokens.Radius.md),
                                        border = BorderStroke(1.dp, NexusTokens.Border),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { isRepoDropdownExpanded = true }
                                            .testTag("hackathon_repo_dropdown")
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 14.dp, vertical = 12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = selectedMyRepo ?: "Select a repository",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = if (selectedMyRepo != null) NexusTokens.Foreground else NexusTokens.ForegroundTertiary
                                            )
                                            Icon(
                                                imageVector = Icons.Default.ArrowDropDown,
                                                contentDescription = "Expand",
                                                tint = NexusTokens.ForegroundSecondary
                                            )
                                        }
                                    }

                                    DropdownMenu(
                                        expanded = isRepoDropdownExpanded,
                                        onDismissRequest = { isRepoDropdownExpanded = false },
                                        modifier = Modifier.background(NexusTokens.Surface)
                                    ) {
                                        repos.forEach { repo ->
                                            DropdownMenuItem(
                                                text = {
                                                    Column {
                                                        Text(
                                                            text = repo.fullName,
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            color = NexusTokens.Foreground
                                                        )
                                                        if (!repo.language.isNullOrBlank()) {
                                                            Text(
                                                                text = repo.language,
                                                                style = MaterialTheme.typography.labelSmall,
                                                                color = NexusTokens.ForegroundSecondary
                                                            )
                                                        }
                                                    }
                                                },
                                                onClick = {
                                                    selectedMyRepo = repo.fullName
                                                    isRepoDropdownExpanded = false
                                                    errorMessage = null
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        is Load.Idle -> {
                            Text(
                                text = "Tap to load repositories.",
                                style = MaterialTheme.typography.bodySmall,
                                color = NexusTokens.ForegroundSecondary
                            )
                        }
                    }
                }

                RepoOption.TEAM_REPO -> {
                    NexusTextField(
                        value = teamRepoInput,
                        onValueChange = {
                            teamRepoInput = it
                            teamRepoError = null
                            errorMessage = null
                        },
                        label = "owner/repo",
                        placeholder = "owner/repo or https://github.com/owner/repo",
                        helperText = "A teammate's or organisation's repo you have commits in.",
                        errorText = teamRepoError,
                        singleLine = true,
                        testTag = "hackathon_team_repo_input"
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Save & Cancel actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                NexusButton(
                    onClick = onDismiss,
                    variant = NexusButtonVariant.Outline,
                    modifier = Modifier.weight(1f),
                    testTag = "hackathon_cancel_button"
                ) {
                    Text("Cancel")
                }

                NexusButton(
                    onClick = {
                        val repoFullName = chosenRepoFullName
                        if (repoFullName == null) {
                            if (repoOption == RepoOption.TEAM_REPO) {
                                teamRepoError = "That doesn't look like an owner/repo path."
                            }
                            return@NexusButton
                        }

                        val yearVal = yearText.trim().toIntOrNull() ?: currentYear
                        isSaving = true
                        errorMessage = null

                        scope.launch {
                            val res = onSaveHackathon(
                                eventName.trim(),
                                yearVal,
                                resultChoice,
                                teamSize,
                                repoFullName
                            )
                            isSaving = false
                            when (res) {
                                is ApiResult.Ok -> {
                                    onDismiss()
                                }
                                is ApiResult.Err -> {
                                    errorMessage = res.message
                                }
                            }
                        }
                    },
                    enabled = isFormValid && !isSaving,
                    variant = NexusButtonVariant.Primary,
                    modifier = Modifier.weight(1f),
                    testTag = "hackathon_save_button"
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            color = NexusTokens.OnAccent,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Saving…")
                    } else {
                        Text("Save")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
