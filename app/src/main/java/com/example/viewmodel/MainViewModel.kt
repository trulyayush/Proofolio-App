package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AssessmentReportEntity
import com.example.data.model.CampaignMessageEntity
import com.example.data.model.ConnectionEntity
import com.example.data.model.HackathonEntity
import com.example.data.model.InternshipRequirementEntity
import com.example.data.model.InterviewQuestionEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.RecruiterEntity
import com.example.data.model.SharedCandidateEntity
import com.example.data.model.StudentEntity
import com.example.data.repository.AppRepository
import com.example.speech.SpeechRecognitionHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

sealed class ScreenDestination {
    object RoleSelect : ScreenDestination()
    object StudentAuth : ScreenDestination()
    object StudentOnboarding : ScreenDestination()
    object StudentDashboard : ScreenDestination()
    data class ProjectDetail(val projectId: String) : ScreenDestination()
    data class AiInterview(val projectId: String) : ScreenDestination()
    data class AssessmentReport(val projectId: String) : ScreenDestination()
    object AdminCommandCenter : ScreenDestination()
    object AdminCampaignCenter : ScreenDestination()
    object RecruiterPortal : ScreenDestination()
}

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(application: Application) : AndroidViewModel(application) {
    val repository = AppRepository(application)
    private val speechHelper = SpeechRecognitionHelper(application)

    val isListening: StateFlow<Boolean> = speechHelper.isListening
    val transcriptionText: StateFlow<String> = speechHelper.transcription
    val speechError: StateFlow<String?> = speechHelper.errorMessage

    private val _currentDestination = MutableStateFlow<ScreenDestination>(ScreenDestination.RoleSelect)
    val currentDestination: StateFlow<ScreenDestination> = _currentDestination.asStateFlow()

    private val _navigationStack = mutableListOf<ScreenDestination>()

    // Active authenticated student ID (persisted across app restarts)
    private val _activeStudentId = MutableStateFlow<String?>(repository.sessionManager.getActiveStudentId())
    val activeStudentId: StateFlow<String?> = _activeStudentId.asStateFlow()

    val allStudents: StateFlow<List<StudentEntity>> = repository.dao.getAllStudentsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dynamically react to active student ID
    val currentStudent: StateFlow<StudentEntity?> = _activeStudentId
        .flatMapLatest { id ->
            if (id != null) repository.dao.getStudentFlow(id) else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val studentProjects: StateFlow<List<ProjectEntity>> = _activeStudentId
        .flatMapLatest { id ->
            if (id != null) repository.dao.getProjectsForStudentFlow(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProjects: StateFlow<List<ProjectEntity>> = repository.dao.getAllProjectsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studentHackathons: StateFlow<List<HackathonEntity>> = _activeStudentId
        .flatMapLatest { id ->
            if (id != null) repository.dao.getHackathonsForStudentFlow(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studentConnections: StateFlow<List<ConnectionEntity>> = _activeStudentId
        .flatMapLatest { id ->
            if (id != null) repository.dao.getConnectionsForStudentFlow(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allReports: StateFlow<List<AssessmentReportEntity>> = repository.dao.getAllReportsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRecruiters: StateFlow<List<RecruiterEntity>> = repository.dao.getAllRecruitersFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRequirements: StateFlow<List<InternshipRequirementEntity>> = repository.dao.getAllRequirementsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sharedCandidates: StateFlow<List<SharedCandidateEntity>> = repository.dao.getAllSharedCandidatesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val campaignHistory: StateFlow<List<CampaignMessageEntity>> = repository.dao.getAllCampaignMessagesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studentNotifications: StateFlow<List<NotificationEntity>> = _activeStudentId
        .flatMapLatest { id ->
            if (id != null) repository.dao.getNotificationsFlow("STUDENT", id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeInterviewQuestions = MutableStateFlow<List<InterviewQuestionEntity>>(emptyList())
    val activeInterviewQuestions: StateFlow<List<InterviewQuestionEntity>> = _activeInterviewQuestions.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeSeedDataIfNeeded()

            // Check if student session is already active
            val loggedInId = repository.sessionManager.getActiveStudentId()
            if (loggedInId != null) {
                _activeStudentId.value = loggedInId
            }
        }
    }

    // Navigation
    fun navigateTo(dest: ScreenDestination) {
        _navigationStack.add(_currentDestination.value)
        _currentDestination.value = dest
    }

    fun navigateBack() {
        if (_navigationStack.isNotEmpty()) {
            _currentDestination.value = _navigationStack.removeAt(_navigationStack.size - 1)
        } else {
            _currentDestination.value = ScreenDestination.RoleSelect
        }
    }

    fun selectRole(role: String) {
        when (role) {
            "STUDENT" -> {
                val loggedInId = _activeStudentId.value
                if (loggedInId == null) {
                    // Route to Real Student Login/Register
                    navigateTo(ScreenDestination.StudentAuth)
                } else {
                    viewModelScope.launch {
                        val student = repository.dao.getStudentById(loggedInId)
                        if (student == null || !student.isOnboardingComplete) {
                            navigateTo(ScreenDestination.StudentOnboarding)
                        } else {
                            navigateTo(ScreenDestination.StudentDashboard)
                        }
                    }
                }
            }
            "ADMIN" -> navigateTo(ScreenDestination.AdminCommandCenter)
            "RECRUITER" -> navigateTo(ScreenDestination.RecruiterPortal)
            else -> navigateTo(ScreenDestination.RoleSelect)
        }
    }

    // --- Student Authentication Operations ---

    fun loginStudent(email: String, password: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = repository.loginStudent(email, password)
            result.onSuccess { account ->
                _activeStudentId.value = account.id
                val student = repository.dao.getStudentById(account.id)
                if (student == null || !student.isOnboardingComplete) {
                    _currentDestination.value = ScreenDestination.StudentOnboarding
                } else {
                    _currentDestination.value = ScreenDestination.StudentDashboard
                }
                onResult(Result.success(Unit))
            }.onFailure { err ->
                onResult(Result.failure(err))
            }
        }
    }

    fun registerStudent(fullName: String, email: String, password: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = repository.registerStudent(fullName, email, password)
            result.onSuccess { account ->
                _activeStudentId.value = account.id
                _currentDestination.value = ScreenDestination.StudentOnboarding
                onResult(Result.success(Unit))
            }.onFailure { err ->
                onResult(Result.failure(err))
            }
        }
    }

    fun logoutStudent() {
        repository.logoutStudent()
        _activeStudentId.value = null
        _navigationStack.clear()
        _currentDestination.value = ScreenDestination.RoleSelect
    }

    fun verifyStudentEmail(email: String, code: String, onResult: (Result<Boolean>) -> Unit) {
        viewModelScope.launch {
            val res = repository.verifyStudentEmail(email, code)
            onResult(res)
        }
    }

    fun resetStudentPassword(email: String, newPass: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val res = repository.resetStudentPassword(email, newPass)
            res.onSuccess { onResult(Result.success(Unit)) }
                .onFailure { onResult(Result.failure(it)) }
        }
    }

    fun uploadResumeFile(fileName: String, content: ByteArray, onResult: (Result<String>) -> Unit) {
        viewModelScope.launch {
            val studentId = _activeStudentId.value ?: "temp_student"
            val res = repository.secureFileStorage.saveResumeFile(studentId, fileName, content)
            onResult(res)
        }
    }

    fun uploadCertificateFile(hackathonId: String, fileName: String, content: ByteArray, onResult: (Result<String>) -> Unit) {
        viewModelScope.launch {
            val studentId = _activeStudentId.value ?: "temp_student"
            val res = repository.secureFileStorage.saveCertificateFile(studentId, hackathonId, fileName, content)
            onResult(res)
        }
    }

    fun saveOnboardingProgress(student: StudentEntity, hackathons: List<HackathonEntity>, step: Int) {
        viewModelScope.launch {
            repository.dao.insertStudent(student.copy(onboardingStep = step))
            if (hackathons.isNotEmpty()) {
                repository.dao.insertHackathons(hackathons)
            }
        }
    }

    fun saveStudentProfile(student: StudentEntity, hackathons: List<HackathonEntity>) {
        viewModelScope.launch {
            repository.dao.insertStudent(student.copy(isOnboardingComplete = true, onboardingStep = 7))
            repository.dao.deleteHackathonsForStudent(student.id)
            if (hackathons.isNotEmpty()) {
                repository.dao.insertHackathons(hackathons)
            }
            _currentDestination.value = ScreenDestination.StudentDashboard
        }
    }

    // Projects & Interviews
    fun openProjectDetail(projectId: String) {
        loadProjectInterviewQuestions(projectId)
        navigateTo(ScreenDestination.ProjectDetail(projectId))
    }

    fun openAiInterview(projectId: String) {
        loadProjectInterviewQuestions(projectId)
        navigateTo(ScreenDestination.AiInterview(projectId))
    }

    fun openAssessmentReport(projectId: String) {
        navigateTo(ScreenDestination.AssessmentReport(projectId))
    }

    fun loadProjectInterviewQuestions(projectId: String) {
        viewModelScope.launch {
            val qList = repository.dao.getQuestionsForProject(projectId)
            _activeInterviewQuestions.value = qList
        }
    }

    // Speech controls
    fun startListening(initial: String = "") {
        speechHelper.startListening(initial)
    }

    fun stopListening() {
        speechHelper.stopListening()
    }

    fun submitNewProject(title: String, repoUrl: String, onResult: (Result<ProjectEntity>) -> Unit) {
        viewModelScope.launch {
            val studentId = _activeStudentId.value ?: "student_default"
            val result = repository.submitNewProject(studentId, title, repoUrl)
            onResult(result)
            result.onSuccess { proj ->
                openProjectDetail(proj.id)
            }
        }
    }

    fun saveInterviewAnswer(questionId: String, answer: String, isVoice: Boolean) {
        viewModelScope.launch {
            repository.saveInterviewAnswer(questionId, answer, isVoice)
            _activeInterviewQuestions.value = _activeInterviewQuestions.value.map {
                if (it.id == questionId) it.copy(studentAnswer = answer, isVoiceTranscription = isVoice, isAnswered = answer.isNotBlank())
                else it
            }
        }
    }

    fun submitInterview(projectId: String) {
        viewModelScope.launch {
            val studentId = _activeStudentId.value ?: "student_default"
            repository.completeInterviewAndEvaluate(projectId, studentId)
            loadProjectInterviewQuestions(projectId)
            _currentDestination.value = ScreenDestination.ProjectDetail(projectId)
        }
    }

    // Admin Actions
    fun releaseFeedbackToStudent(reportId: String, remarks: String) {
        viewModelScope.launch {
            repository.releaseFeedbackToStudent(reportId, remarks)
        }
    }

    fun forwardToRecruiter(recruiterId: String, studentId: String, projectId: String, notes: String) {
        viewModelScope.launch {
            repository.forwardCandidateToRecruiter(recruiterId, studentId, projectId, notes)
        }
    }

    fun createRecruiterAccount(name: String, company: String, position: String, email: String, onResult: (Result<RecruiterEntity>) -> Unit) {
        viewModelScope.launch {
            val res = repository.createRecruiterAccount(name, company, position, email)
            onResult(res)
        }
    }

    fun sendAdminCampaign(subject: String, body: String, filter: String, count: Int, channel: String, template: String) {
        viewModelScope.launch {
            repository.dispatchAdminCampaign(subject, body, filter, count, channel, template)
        }
    }

    // Recruiter Actions
    fun postInternshipRequirement(
        role: String,
        skills: String,
        domains: String,
        criteria: String,
        duration: String,
        location: String,
        stipend: String,
        deadline: String
    ) {
        viewModelScope.launch {
            val recruiter = allRecruiters.value.firstOrNull()
            val req = InternshipRequirementEntity(
                id = "req_" + UUID.randomUUID().toString().take(8),
                recruiterId = recruiter?.id ?: "recruiter_1",
                companyName = recruiter?.companyName ?: "Google Cloud",
                roleTitle = role,
                requiredSkills = skills,
                preferredDomains = domains,
                eligibilityCriteria = criteria,
                duration = duration,
                location = location,
                stipend = stipend,
                deadline = deadline
            )
            repository.dao.insertRequirement(req)
        }
    }

    fun establishRecruiterPassword(recruiterId: String, pass: String) {
        viewModelScope.launch {
            repository.dao.markPasswordEstablished(recruiterId)
        }
    }

    fun sendRecruiterOutreach(studentId: String, message: String) {
        viewModelScope.launch {
            val recruiter = allRecruiters.value.firstOrNull()
            repository.dao.insertNotification(
                NotificationEntity(
                    id = UUID.randomUUID().toString(),
                    recipientRole = "STUDENT",
                    recipientId = studentId,
                    title = "Direct Outreach from ${recruiter?.companyName ?: "Partner Company"}",
                    message = message
                )
            )
        }
    }

    // Connections
    fun acceptConnection(connectionId: String) {
        viewModelScope.launch {
            repository.dao.updateConnectionStatus(connectionId, "ACCEPTED")
        }
    }

    fun sendConnection(targetStudentId: String) {
        viewModelScope.launch {
            val currentId = _activeStudentId.value ?: "student_default"
            repository.dao.insertConnection(
                ConnectionEntity(
                    id = "conn_" + UUID.randomUUID().toString().take(8),
                    requesterStudentId = currentId,
                    targetStudentId = targetStudentId,
                    status = "PENDING"
                )
            )
        }
    }
}
