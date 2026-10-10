package com.example.ui.screens.student

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.api.RepoSummary
import com.example.data.api.ReposResponse
import com.example.data.api.VivaInProgress
import com.example.ui.components.NexusButton
import com.example.ui.components.NexusButtonVariant
import com.example.ui.components.NexusCard
import com.example.ui.components.NexusTextField
import com.example.ui.theme.NexusTokens
import com.example.viewmodel.Load

private val TEAM_REPO_REGEX = Regex("^[A-Za-z0-9-]{1,39}/[A-Za-z0-9._-]{1,100}$")

fun normalizeTeamRepoInput(raw: String): String {
    var s = raw.trim()
    val prefixes = listOf("https://github.com/", "http://github.com/", "github.com/", "@")
    for (p in prefixes) {
        if (s.startsWith(p, ignoreCase = true)) {
            s = s.substring(p.length)
            break
        }
    }
    s = s.trim('/')
    val parts = s.split("/")
    return if (parts.size >= 2) {
        "${parts[0]}/${parts[1]}"
    } else {
        s
    }
}

@Composable
fun RepoPickerScreen(
    reposState: Load<ReposResponse>,
    githubUsername: String?,
    vivaInProgress: VivaInProgress?,
    onLoadRepos: () -> Unit,
    onSelectRepo: (repoFullName: String) -> Unit,
    onBack: () -> Unit
) {
    LaunchedEffect(Unit) {
        onLoadRepos()
    }

    var selectedRepoForDialog by remember { mutableStateOf<RepoSummary?>(null) }
    var showTeamRepoDialog by remember { mutableStateOf(false) }
    var teamRepoInput by remember { mutableStateOf("") }
    var teamRepoError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NexusTokens.Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("repo_picker_screen")
    ) {
        // App bar
        Surface(
            color = NexusTokens.Surface,
            border = BorderStroke(1.dp, NexusTokens.Border)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("repo_picker_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = NexusTokens.Foreground
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = "Pick a repository",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NexusTokens.Foreground
                        )
                        if (githubUsername != null) {
                            Text(
                                text = "@$githubUsername",
                                style = MaterialTheme.typography.bodySmall,
                                color = NexusTokens.ForegroundSecondary
                            )
                        }
                    }
                }

                IconButton(
                    onClick = onLoadRepos,
                    modifier = Modifier.testTag("repo_picker_refresh_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh repos",
                        tint = NexusTokens.ForegroundSecondary
                    )
                }
            }
        }

        // Body
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when (reposState) {
                is Load.Loading, is Load.Idle -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = NexusTokens.Accent,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Loading public repositories…",
                                style = MaterialTheme.typography.bodyMedium,
                                color = NexusTokens.ForegroundSecondary
                            )
                        }
                    }
                }

                is Load.Failed -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        NexusCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 500.dp),
                            backgroundColor = NexusTokens.Surface,
                            borderColor = NexusTokens.Border
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = NexusTokens.ErrorText,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = reposState.error.message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = NexusTokens.Foreground,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(18.dp))
                                NexusButton(
                                    onClick = onLoadRepos,
                                    variant = NexusButtonVariant.Primary,
                                    testTag = "repo_picker_retry_button"
                                ) {
                                    Text("Retry")
                                }
                            }
                        }
                    }
                }

                is Load.Ready -> {
                    val repos = reposState.value.repos

                    if (repos.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            NexusCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .widthIn(max = 500.dp),
                                backgroundColor = NexusTokens.Surface,
                                borderColor = NexusTokens.Border
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Code,
                                        contentDescription = null,
                                        tint = NexusTokens.ForegroundTertiary,
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "No public repos found on @${githubUsername ?: "you"}. Push a project to GitHub, then tap Refresh.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = NexusTokens.ForegroundSecondary,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(18.dp))
                                    NexusButton(
                                        onClick = onLoadRepos,
                                        variant = NexusButtonVariant.Secondary
                                    ) {
                                        Text("Refresh")
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    TextButton(
                                        onClick = { showTeamRepoDialog = true },
                                        modifier = Modifier.testTag("repo_picker_team_repo_button")
                                    ) {
                                        Text(
                                            text = "Verify a team repo instead",
                                            color = NexusTokens.Accent
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            items(repos, key = { it.fullName }) { repo ->
                                val repoName = repo.fullName.substringAfterLast("/")
                                val lang = repo.language ?: "Code"
                                val updatedDate = repo.pushedAt?.take(10) ?: "—"

                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                        .clickable { selectedRepoForDialog = repo }
                                        .testTag("repo_item_${repo.fullName}"),
                                    color = NexusTokens.Surface,
                                    shape = RoundedCornerShape(NexusTokens.Radius.md),
                                    border = BorderStroke(1.dp, NexusTokens.Border)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = repoName,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = NexusTokens.Foreground
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = repo.description ?: "No description",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (repo.description != null) NexusTokens.ForegroundSecondary else NexusTokens.ForegroundTertiary,
                                            maxLines = 2
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            text = "$lang · ★ ${repo.stars} · Updated $updatedDate",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = NexusTokens.ForegroundTertiary
                                        )
                                    }
                                }
                            }

                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    TextButton(
                                        onClick = { showTeamRepoDialog = true },
                                        modifier = Modifier.testTag("repo_picker_team_repo_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Group,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = NexusTokens.Accent
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Verify a team repo instead",
                                            color = NexusTokens.Accent,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog for picking a repository
    selectedRepoForDialog?.let { repo ->
        val repoName = repo.fullName.substringAfterLast("/")
        AlertDialog(
            onDismissRequest = { selectedRepoForDialog = null },
            title = {
                Text(
                    text = "Verify $repoName?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "We'll read the code and ask you 5 questions about it. Answer by speaking or typing, in any language. Each question has 90 seconds.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NexusTokens.Foreground
                    )
                    if (vivaInProgress != null) {
                        val inProgressRepoName = vivaInProgress.repoFullName?.substringAfterLast("/") ?: "repository"
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Your unfinished viva on $inProgressRepoName will stay unfinished.",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexusTokens.ForegroundSecondary
                        )
                    }
                }
            },
            confirmButton = {
                NexusButton(
                    onClick = {
                        val fullName = repo.fullName
                        selectedRepoForDialog = null
                        onSelectRepo(fullName)
                    },
                    variant = NexusButtonVariant.Primary,
                    testTag = "repo_confirm_start_button"
                ) {
                    Text("Start")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedRepoForDialog = null }) {
                    Text("Cancel", color = NexusTokens.ForegroundSecondary)
                }
            },
            containerColor = NexusTokens.Surface
        )
    }

    // Dialog for verifying a team repo
    if (showTeamRepoDialog) {
        AlertDialog(
            onDismissRequest = {
                showTeamRepoDialog = false
                teamRepoError = null
            },
            title = {
                Text(
                    text = "Verify a team repo",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    NexusTextField(
                        value = teamRepoInput,
                        onValueChange = {
                            teamRepoInput = it
                            teamRepoError = null
                        },
                        label = "owner/repo",
                        placeholder = "owner/repo or https://github.com/owner/repo",
                        helperText = "A teammate's or organisation's repo you have commits in.",
                        errorText = teamRepoError,
                        singleLine = true,
                        testTag = "team_repo_input"
                    )
                }
            },
            confirmButton = {
                NexusButton(
                    onClick = {
                        val normalized = normalizeTeamRepoInput(teamRepoInput)
                        if (TEAM_REPO_REGEX.matches(normalized)) {
                            showTeamRepoDialog = false
                            teamRepoError = null
                            onSelectRepo(normalized)
                        } else {
                            teamRepoError = "That doesn't look like an owner/repo path."
                        }
                    },
                    variant = NexusButtonVariant.Primary,
                    testTag = "repo_team_dialog_start_button"
                ) {
                    Text("Start")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showTeamRepoDialog = false
                        teamRepoError = null
                    }
                ) {
                    Text("Cancel", color = NexusTokens.ForegroundSecondary)
                }
            },
            containerColor = NexusTokens.Surface
        )
    }
}
