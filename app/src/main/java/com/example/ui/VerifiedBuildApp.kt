package com.example.ui

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.NexusButton
import com.example.ui.components.NexusButtonVariant
import com.example.ui.components.NexusCard
import com.example.ui.screens.DemoWelcomeScreen
import com.example.ui.screens.GetStartedScreen
import com.example.ui.screens.PrivacyPolicyScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TermsConditionsScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.screens.student.AnalyzingScreen
import com.example.ui.screens.student.RepoFoundScreen
import com.example.ui.screens.student.RepoPickerScreen
import com.example.ui.screens.student.ReportScreen
import com.example.ui.screens.student.StudentDashboardScreen
import com.example.ui.screens.student.StudentOnboardingScreen
import com.example.ui.screens.student.StudentRegistrationScreen
import com.example.ui.screens.student.VivaCompleteScreen
import com.example.ui.screens.student.VivaScreen
import com.example.ui.theme.NexusTokens
import com.example.viewmodel.AppViewModel
import com.example.viewmodel.Load
import com.example.viewmodel.Screen

@Composable
fun VerifiedBuildApp(viewModel: AppViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val health by viewModel.health.collectAsStateWithLifecycle()
    val context = LocalContext.current

    BackHandler(enabled = true) {
        if (currentScreen is Screen.DemoWelcome) {
            viewModel.setShowExitDemoDialog(true)
        } else if (!viewModel.back()) {
            if (viewModel.isDemo) {
                viewModel.setShowExitDemoDialog(true)
            } else {
                (context as? Activity)?.finish()
            }
        }
    }

    when (val screen = currentScreen) {
        is Screen.Welcome -> {
            GetStartedScreen(
                onStudentClick = {
                    if (viewModel.sessionStore.studentId != null) {
                        viewModel.navigate(Screen.Dashboard, clearStack = true)
                        viewModel.loadReport()
                    } else {
                        viewModel.navigate(Screen.StudentRegistration)
                    }
                },
                onAdminClick = {
                    viewModel.startDemo()
                    viewModel.updateDemoView(AppViewModel.DemoView.ADMIN)
                },
                onRecruiterClick = {
                    viewModel.startDemo()
                    viewModel.updateDemoView(AppViewModel.DemoView.RECRUITER)
                },
                onExploreDemoClick = {
                    viewModel.startDemoIntro()
                },
                onPrivacyPolicyClick = {
                    viewModel.navigate(Screen.PrivacyPolicy)
                },
                onTermsClick = {
                    viewModel.navigate(Screen.TermsConditions)
                }
            )
        }

        is Screen.StudentRegistration -> {
            val registrationError by viewModel.registrationError.collectAsStateWithLifecycle()
            StudentRegistrationScreen(
                onRegister = { name, email, pass ->
                    viewModel.registerStudent(name, email, pass)
                },
                onLogin = { email, pass ->
                    viewModel.loginStudent(email, pass)
                },
                onBack = { viewModel.back() },
                onPrivacyPolicyClick = { viewModel.navigate(Screen.PrivacyPolicy) },
                onTermsClick = { viewModel.navigate(Screen.TermsConditions) },
                registrationError = registrationError
            )
        }

        is Screen.DemoWelcome -> {
            val showExitDemoDialog by viewModel.showExitDemoDialog.collectAsStateWithLifecycle()
            DemoWelcomeScreen(
                onStartGuidedTour = { viewModel.startGuidedTour() },
                onExploreFreely = { viewModel.exploreFreely() },
                onExitDemo = { viewModel.setShowExitDemoDialog(true) }
            )

            if (showExitDemoDialog) {
                AlertDialog(
                    onDismissRequest = { viewModel.setShowExitDemoDialog(false) },
                    title = { Text("Exit Demo Mode?") },
                    text = { Text("You will return to the Get Started screen. Your demo progress will be cleared. No real account or platform data will be affected.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.setShowExitDemoDialog(false)
                                viewModel.exitDemo()
                            },
                            modifier = Modifier.testTag("confirm_exit_demo_button")
                        ) {
                            Text("Exit Demo")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { viewModel.setShowExitDemoDialog(false) },
                            modifier = Modifier.testTag("continue_demo_button")
                        ) {
                            Text("Continue Demo")
                        }
                    }
                )
            }
        }

        is Screen.Onboarding -> {
            val onboardingState by viewModel.onboardingState.collectAsStateWithLifecycle()
            StudentOnboardingScreen(
                state = onboardingState,
                onSubmit = { request -> viewModel.createStudent(request) },
                onSuccess = { viewModel.navigate(Screen.Dashboard, clearStack = true) },
                onBack = { viewModel.back() },
                onClearError = { viewModel.clearOnboardingError() },
                sessionStore = viewModel.sessionStore,
                onPrivacyPolicyClick = { viewModel.navigate(Screen.PrivacyPolicy) },
                onTermsClick = { viewModel.navigate(Screen.TermsConditions) }
            )
        }

        is Screen.PrivacyPolicy -> {
            PrivacyPolicyScreen(
                onBack = { viewModel.back() }
            )
        }

        is Screen.TermsConditions -> {
            TermsConditionsScreen(
                onBack = { viewModel.back() }
            )
        }

        is Screen.Settings -> {
            val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
            SettingsScreen(
                isDarkMode = isDarkMode,
                onToggleDarkMode = { enabled -> viewModel.toggleDarkMode(enabled) },
                onBack = { viewModel.back() },
                onPrivacyPolicyClick = { viewModel.navigate(Screen.PrivacyPolicy) },
                onTermsClick = { viewModel.navigate(Screen.TermsConditions) }
            )
        }

        is Screen.Dashboard -> {
            val reportState by viewModel.reportState.collectAsStateWithLifecycle()
            val applyingIds by viewModel.applyingListingIds.collectAsStateWithLifecycle()
            val resumeVivaError by viewModel.resumeVivaError.collectAsStateWithLifecycle()
            val reposState by viewModel.reposState.collectAsStateWithLifecycle()
            val showExitDemoDialog by viewModel.showExitDemoDialog.collectAsStateWithLifecycle()
            val showResetDemoDialog by viewModel.showResetDemoDialog.collectAsStateWithLifecycle()
            val demoState by viewModel.demoState.collectAsStateWithLifecycle()

            StudentDashboardScreen(
                reportState = reportState,
                sessionStore = viewModel.sessionStore,
                applyingIds = applyingIds,
                reposState = reposState,
                isDemo = viewModel.isDemo,
                demoView = viewModel.demoView,
                demoState = demoState,
                onSetDemoView = { view -> viewModel.updateDemoView(view) },
                onNavigateDemo = { screen, step -> viewModel.navigateDemo(screen, step) },
                onNextDemoStep = { viewModel.nextDemoStep() },
                onPrevDemoStep = { viewModel.prevDemoStep() },
                onSelectStudent = { studentId -> viewModel.selectStudent(studentId) },
                onSelectProject = { repo -> viewModel.selectProject(repo) },
                onAnswerQuestion = { qId, ans -> viewModel.answerQuestion(qId, ans) },
                onSkipQuestion = { qId -> viewModel.skipQuestion(qId) },
                onToggleShortlist = { sId -> viewModel.toggleShortlist(sId) },
                onShareFeedback = { aId, vis -> viewModel.shareDemoFeedback(aId, vis) },
                onUpdateFilters = { filters -> viewModel.updateDemoFilters(filters) },
                onLoadRepos = { viewModel.loadRepos() },
                resumeVivaError = resumeVivaError,
                onClearResumeVivaError = { viewModel.clearResumeVivaError() },
                onRefresh = { viewModel.loadReport() },
                onVerifyRepo = { viewModel.navigate(Screen.RepoPicker) },
                onVerifyHackathonRepo = { repoFullName -> viewModel.navigate(Screen.Analyzing(repoFullName)) },
                onContinueViva = { sessionId -> viewModel.resumeViva(sessionId) },
                onViewReport = { viewModel.navigate(Screen.Report) },
                onApply = { listingId -> viewModel.applyToListing(listingId) },
                onAddHackathon = { event, year, result, teamSize, repoFullName ->
                    viewModel.addHackathon(event, year, result, teamSize, repoFullName)
                },
                onDeleteHackathon = { hackathonId ->
                    viewModel.deleteHackathon(hackathonId)
                },
                onSignOut = { viewModel.signOut() },
                onSettingsClick = { viewModel.navigate(Screen.Settings) },
                onExitDemo = { viewModel.setShowExitDemoDialog(true) },
                onResetDemo = { viewModel.setShowResetDemoDialog(true) }
            )

            if (showExitDemoDialog) {
                AlertDialog(
                    onDismissRequest = { viewModel.setShowExitDemoDialog(false) },
                    title = { Text("Exit Demo Mode?") },
                    text = { Text("You will return to the Get Started screen. Your demo progress will be cleared. No real account or platform data will be affected.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.setShowExitDemoDialog(false)
                                viewModel.exitDemo()
                            },
                            modifier = Modifier.testTag("confirm_exit_demo_button")
                        ) {
                            Text("Exit Demo")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { viewModel.setShowExitDemoDialog(false) },
                            modifier = Modifier.testTag("continue_demo_button")
                        ) {
                            Text("Continue Demo")
                        }
                    }
                )
            }

            if (showResetDemoDialog) {
                AlertDialog(
                    onDismissRequest = { viewModel.setShowResetDemoDialog(false) },
                    title = { Text("Reset Demo Progress?") },
                    text = { Text("This will restore the original sample data and initial demo progress.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.setShowResetDemoDialog(false)
                                viewModel.resetDemo()
                            }
                        ) {
                            Text("Reset Demo")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { viewModel.setShowResetDemoDialog(false) }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }

        is Screen.RepoPicker -> {
            val reposState by viewModel.reposState.collectAsStateWithLifecycle()
            val reportState by viewModel.reportState.collectAsStateWithLifecycle()
            val vivaInProgress = (reportState as? Load.Ready)?.value?.vivaInProgress
            RepoPickerScreen(
                reposState = reposState,
                githubUsername = viewModel.sessionStore.githubUsername,
                vivaInProgress = vivaInProgress,
                onLoadRepos = { viewModel.loadRepos() },
                onSelectRepo = { repoFullName -> viewModel.navigate(Screen.Analyzing(repoFullName)) },
                onBack = { viewModel.back() }
            )
        }

        is Screen.Analyzing -> {
            val analyzeState by viewModel.analyzeState.collectAsStateWithLifecycle()
            AnalyzingScreen(
                repoFullName = screen.repoFullName,
                analyzeState = analyzeState,
                onStartAnalyze = { repoFullName -> viewModel.startAnalyze(repoFullName) },
                onCancelAnalyze = { viewModel.cancelAnalyze() },
                onPickAnotherRepo = {
                    viewModel.cancelAnalyze()
                    viewModel.back()
                },
                onBack = { viewModel.back() }
            )
        }

        is Screen.RepoFound -> {
            RepoFoundScreen(
                analysis = viewModel.lastAnalysis,
                onStartQuestion1 = { viewModel.replace(Screen.Viva) },
                onBack = { viewModel.navigate(Screen.Dashboard, clearStack = true) }
            )
        }

        is Screen.Viva -> {
            VivaScreen(
                viewModel = viewModel,
                onDone = { viewModel.replace(Screen.VivaComplete) },
                onLeave = { viewModel.navigate(Screen.Dashboard, clearStack = true) }
            )
        }

        is Screen.VivaComplete -> {
            VivaCompleteScreen(
                viewModel = viewModel,
                onSeeReport = { viewModel.navigate(Screen.Report) },
                onBackToDashboard = { viewModel.navigate(Screen.Dashboard, clearStack = true) }
            )
        }

        is Screen.Report -> {
            ReportScreen(
                viewModel = viewModel,
                onBack = { viewModel.back() },
                onVerifyRepo = { repoFullName -> viewModel.navigate(Screen.Analyzing(repoFullName)) },
                onContinueViva = { sessionId -> viewModel.resumeViva(sessionId) }
            )
        }
    }
}
