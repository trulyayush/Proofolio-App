package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.RoleSelectionScreen
import com.example.ui.screens.admin.AdminCampaignScreen
import com.example.ui.screens.admin.AdminCommandCenterScreen
import com.example.ui.screens.recruiter.RecruiterPortalScreen
import com.example.ui.screens.student.AiInterviewScreen
import com.example.ui.screens.student.AssessmentReportScreen
import com.example.ui.screens.student.ProjectDetailScreen
import com.example.ui.screens.student.StudentAuthScreen
import com.example.ui.screens.student.StudentDashboardScreen
import com.example.ui.screens.student.StudentOnboardingScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NexusTokens
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.ScreenDestination

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = NexusTokens.Background
                ) {
                    NexusInternApp()
                }
            }
        }
    }
}

@Composable
fun NexusInternApp(viewModel: MainViewModel = viewModel()) {
    val destination by viewModel.currentDestination.collectAsStateWithLifecycle()

    val currentStudent by viewModel.currentStudent.collectAsStateWithLifecycle()
    val allStudents by viewModel.allStudents.collectAsStateWithLifecycle()
    val studentProjects by viewModel.studentProjects.collectAsStateWithLifecycle()
    val allProjects by viewModel.allProjects.collectAsStateWithLifecycle()
    val studentHackathons by viewModel.studentHackathons.collectAsStateWithLifecycle()
    val studentConnections by viewModel.studentConnections.collectAsStateWithLifecycle()
    val allReports by viewModel.allReports.collectAsStateWithLifecycle()
    val allRecruiters by viewModel.allRecruiters.collectAsStateWithLifecycle()
    val allRequirements by viewModel.allRequirements.collectAsStateWithLifecycle()
    val sharedCandidates by viewModel.sharedCandidates.collectAsStateWithLifecycle()
    val campaignHistory by viewModel.campaignHistory.collectAsStateWithLifecycle()
    val notifications by viewModel.studentNotifications.collectAsStateWithLifecycle()
    val interviewQuestions by viewModel.activeInterviewQuestions.collectAsStateWithLifecycle()

    val isListening by viewModel.isListening.collectAsStateWithLifecycle()
    val transcriptionText by viewModel.transcriptionText.collectAsStateWithLifecycle()
    val speechError by viewModel.speechError.collectAsStateWithLifecycle()

    when (val dest = destination) {
        is ScreenDestination.RoleSelect -> {
            RoleSelectionScreen(
                onSelectRole = { role -> viewModel.selectRole(role) }
            )
        }

        is ScreenDestination.StudentAuth -> {
            BackHandler { viewModel.navigateBack() }
            StudentAuthScreen(
                onLogin = { email, pass, onRes -> viewModel.loginStudent(email, pass, onRes) },
                onSignUp = { name, email, pass, onRes -> viewModel.registerStudent(name, email, pass, onRes) },
                onResetPassword = { email, newPass, onRes -> viewModel.resetStudentPassword(email, newPass, onRes) },
                onBack = { viewModel.navigateBack() }
            )
        }

        is ScreenDestination.StudentOnboarding -> {
            BackHandler { viewModel.navigateBack() }
            StudentOnboardingScreen(
                initialStudent = currentStudent,
                onSaveProfile = { student, hacks -> viewModel.saveStudentProfile(student, hacks) },
                onSaveProgress = { student, hacks, step -> viewModel.saveOnboardingProgress(student, hacks, step) },
                onVerifyEmail = { email, code, onRes -> viewModel.verifyStudentEmail(email, code, onRes) },
                onUploadResumeFile = { fileName, content, onRes -> viewModel.uploadResumeFile(fileName, content, onRes) },
                onUploadCertificateFile = { hackId, fileName, content, onRes -> viewModel.uploadCertificateFile(hackId, fileName, content, onRes) },
                onCancel = { viewModel.navigateBack() }
            )
        }

        is ScreenDestination.StudentDashboard -> {
            BackHandler { viewModel.navigateTo(ScreenDestination.RoleSelect) }
            currentStudent?.let { student ->
                StudentDashboardScreen(
                    student = student,
                    projects = studentProjects,
                    hackathons = studentHackathons,
                    connections = studentConnections,
                    allStudents = allStudents,
                    notifications = notifications,
                    onOpenProject = { pId -> viewModel.openProjectDetail(pId) },
                    onEditProfile = { viewModel.navigateTo(ScreenDestination.StudentOnboarding) },
                    onSubmitNewProject = { title, repoUrl, onRes ->
                        viewModel.submitNewProject(title, repoUrl, onRes)
                    },
                    onAcceptConnection = { cId -> viewModel.acceptConnection(cId) },
                    onSendConnection = { sId -> viewModel.sendConnection(sId) },
                    onSwitchRole = { viewModel.navigateTo(ScreenDestination.RoleSelect) },
                    onLogout = { viewModel.logoutStudent() }
                )
            }
        }

        is ScreenDestination.ProjectDetail -> {
            BackHandler { viewModel.navigateBack() }
            val project = allProjects.find { it.id == dest.projectId }
            val report = allReports.find { it.projectId == dest.projectId }
            if (project != null) {
                ProjectDetailScreen(
                    project = project,
                    questions = interviewQuestions,
                    report = report,
                    onBack = { viewModel.navigateBack() },
                    onStartInterview = { viewModel.openAiInterview(dest.projectId) },
                    onViewReport = { viewModel.openAssessmentReport(dest.projectId) }
                )
            }
        }

        is ScreenDestination.AiInterview -> {
            BackHandler { viewModel.navigateBack() }
            val project = allProjects.find { it.id == dest.projectId }
            if (project != null) {
                AiInterviewScreen(
                    project = project,
                    questions = interviewQuestions,
                    isListening = isListening,
                    transcriptionText = transcriptionText,
                    speechError = speechError,
                    onStartSpeech = { init -> viewModel.startListening(init) },
                    onStopSpeech = { viewModel.stopListening() },
                    onSaveAnswer = { qId, ans, isVoice -> viewModel.saveInterviewAnswer(qId, ans, isVoice) },
                    onSubmitInterview = { viewModel.submitInterview(dest.projectId) },
                    onBack = { viewModel.navigateBack() }
                )
            }
        }

        is ScreenDestination.AssessmentReport -> {
            BackHandler { viewModel.navigateBack() }
            val project = allProjects.find { it.id == dest.projectId }
            val report = allReports.find { it.projectId == dest.projectId }
            if (project != null && report != null) {
                AssessmentReportScreen(
                    project = project,
                    report = report,
                    onBack = { viewModel.navigateBack() }
                )
            }
        }

        is ScreenDestination.AdminCommandCenter -> {
            BackHandler { viewModel.navigateTo(ScreenDestination.RoleSelect) }
            AdminCommandCenterScreen(
                students = allStudents,
                projects = allProjects,
                reports = allReports,
                hackathons = studentHackathons,
                recruiters = allRecruiters,
                questions = interviewQuestions,
                onReleaseFeedback = { rId, remarks -> viewModel.releaseFeedbackToStudent(rId, remarks) },
                onForwardToRecruiter = { rId, sId, pId, notes -> viewModel.forwardToRecruiter(rId, sId, pId, notes) },
                onCreateRecruiter = { name, comp, pos, email, onRes ->
                    viewModel.createRecruiterAccount(name, comp, pos, email, onRes)
                },
                onOpenCampaigns = { viewModel.navigateTo(ScreenDestination.AdminCampaignCenter) },
                onSwitchRole = { viewModel.navigateTo(ScreenDestination.RoleSelect) }
            )
        }

        is ScreenDestination.AdminCampaignCenter -> {
            BackHandler { viewModel.navigateBack() }
            AdminCampaignScreen(
                students = allStudents,
                campaignHistory = campaignHistory,
                onSendCampaign = { subj, body, flt, cnt, ch, tmpl ->
                    viewModel.sendAdminCampaign(subj, body, flt, cnt, ch, tmpl)
                },
                onBack = { viewModel.navigateBack() }
            )
        }

        is ScreenDestination.RecruiterPortal -> {
            BackHandler { viewModel.navigateTo(ScreenDestination.RoleSelect) }
            val rec = allRecruiters.firstOrNull() ?: com.example.data.model.RecruiterEntity(
                id = "recruiter_1",
                fullName = "Vikram Sengupta",
                companyName = "Google Cloud",
                position = "Engineering Director",
                email = "vikram.cloud@google.com",
                temporaryPassword = "TEMP-GC-8849",
                permanentPasswordEstablished = true
            )
            val filteredShared = sharedCandidates.filter { it.recruiterId == rec.id }

            RecruiterPortalScreen(
                currentRecruiter = rec,
                sharedCandidates = filteredShared,
                students = allStudents,
                projects = allProjects,
                reports = allReports,
                hackathons = studentHackathons,
                requirements = allRequirements,
                onPostRequirement = { role, skills, domains, crit, dur, loc, stip, dead ->
                    viewModel.postInternshipRequirement(role, skills, domains, crit, dur, loc, stip, dead)
                },
                onEstablishPassword = { pass -> viewModel.establishRecruiterPassword(rec.id, pass) },
                onContactCandidate = { sId, msg -> viewModel.sendRecruiterOutreach(sId, msg) },
                onSwitchRole = { viewModel.navigateTo(ScreenDestination.RoleSelect) }
            )
        }
    }
}
