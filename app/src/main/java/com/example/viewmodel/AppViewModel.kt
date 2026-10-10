package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AnswerRecorder
import com.example.audio.RecordedAnswer
import com.example.data.SessionStore
import com.example.data.api.ApiClient
import com.example.data.api.ApiResult
import com.example.data.api.AnalyzeRequest
import com.example.data.api.AnalyzeResponse
import com.example.data.api.ApplyRequest
import com.example.data.api.ApplyResponse
import com.example.data.api.HackathonCreateRequest
import com.example.data.api.HackathonItem
import com.example.data.api.HealthResponse
import com.example.data.api.Listing
import com.example.data.api.ListingsResponse
import com.example.data.api.OkResponse
import com.example.data.api.ReportResponse
import com.example.data.api.ReposResponse
import com.example.data.api.StudentCreateRequest
import com.example.data.api.StudentCreatedResponse
import com.example.viva.VivaController
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class Screen {
    object Welcome : Screen()
    object Onboarding : Screen()
    object Dashboard : Screen()
    object RepoPicker : Screen()
    data class Analyzing(val repoFullName: String) : Screen()
    object RepoFound : Screen()
    object Viva : Screen()
    object VivaComplete : Screen()
    object Report : Screen()
}

sealed class Load<out T> {
    object Idle : Load<Nothing>()
    object Loading : Load<Nothing>()
    data class Ready<out T>(val value: T) : Load<T>()
    data class Failed(val error: ApiResult.Err) : Load<Nothing>()
}

class AppViewModel(application: Application) : AndroidViewModel(application) {
    val sessionStore = SessionStore(application)
    val vivaController = VivaController()
    val recorder = AnswerRecorder(application)

    private val backStack = mutableListOf<Screen>()

    private val _currentScreen = MutableStateFlow<Screen>(
        if (sessionStore.studentId != null) Screen.Dashboard else Screen.Welcome
    )
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _health = MutableStateFlow<Load<HealthResponse>>(Load.Idle)
    val health: StateFlow<Load<HealthResponse>> = _health.asStateFlow()

    // Onboarding State
    private val _onboardingState = MutableStateFlow<Load<StudentCreatedResponse>>(Load.Idle)
    val onboardingState: StateFlow<Load<StudentCreatedResponse>> = _onboardingState.asStateFlow()

    // Dashboard / Report State
    private val _reportState = MutableStateFlow<Load<ReportResponse>>(Load.Idle)
    val reportState: StateFlow<Load<ReportResponse>> = _reportState.asStateFlow()

    // Listings State
    private val _listingsState = MutableStateFlow<Load<ListingsResponse>>(Load.Idle)
    val listingsState: StateFlow<Load<ListingsResponse>> = _listingsState.asStateFlow()

    // Applying to listing state
    private val _applyingListingIds = MutableStateFlow<Set<String>>(emptySet())
    val applyingListingIds: StateFlow<Set<String>> = _applyingListingIds.asStateFlow()

    // Repos State
    private val _reposState = MutableStateFlow<Load<ReposResponse>>(Load.Idle)
    val reposState: StateFlow<Load<ReposResponse>> = _reposState.asStateFlow()

    // Analyze State
    var lastAnalysis: AnalyzeResponse? = null
        private set
    private val _analyzeState = MutableStateFlow<Load<AnalyzeResponse>>(Load.Idle)
    val analyzeState: StateFlow<Load<AnalyzeResponse>> = _analyzeState.asStateFlow()
    private var analyzeJob: Job? = null

    // Resume Viva error state on Dashboard
    private val _resumeVivaError = MutableStateFlow<String?>(null)
    val resumeVivaError: StateFlow<String?> = _resumeVivaError.asStateFlow()

    // Viva Answer Input State across configuration changes
    private val _vivaRecordedAnswer = MutableStateFlow<RecordedAnswer?>(null)
    val vivaRecordedAnswer: StateFlow<RecordedAnswer?> = _vivaRecordedAnswer.asStateFlow()

    private val _vivaTypedText = MutableStateFlow("")
    val vivaTypedText: StateFlow<String> = _vivaTypedText.asStateFlow()

    private val _vivaPasted = MutableStateFlow(false)
    val vivaPasted: StateFlow<Boolean> = _vivaPasted.asStateFlow()

    private var questionAppearedAtElapsedS: Long = 0L

    fun notifyQuestionAppeared() {
        questionAppearedAtElapsedS = android.os.SystemClock.elapsedRealtime() / 1000L
        _vivaRecordedAnswer.value = null
        _vivaTypedText.value = ""
        _vivaPasted.value = false
        recorder.discard()
    }

    fun setRecordedAnswer(recorded: RecordedAnswer?) {
        _vivaRecordedAnswer.value = recorded
    }

    fun onTypedTextChanged(newText: String) {
        val limited = if (newText.length > 6000) newText.take(6000) else newText
        if (limited.length - _vivaTypedText.value.length >= 25) {
            _vivaPasted.value = true
        }
        _vivaTypedText.value = limited
    }

    fun submitVoiceAnswer() {
        val rec = _vivaRecordedAnswer.value ?: return
        viewModelScope.launch {
            vivaController.submit(
                mode = "voice",
                audioBase64 = rec.audioBase64,
                audioMime = rec.mime,
                durationS = rec.seconds
            )
        }
    }

    fun submitTextAnswer() {
        val text = _vivaTypedText.value.trim()
        if (text.isBlank()) return
        val nowS = android.os.SystemClock.elapsedRealtime() / 1000L
        val diffS = (nowS - questionAppearedAtElapsedS).coerceIn(0L, 3600L).toInt()
        viewModelScope.launch {
            vivaController.submit(
                mode = "text",
                answerText = text,
                durationS = diffS,
                pasted = _vivaPasted.value
            )
        }
    }

    fun retryVivaSubmit() {
        viewModelScope.launch {
            vivaController.retry()
        }
    }

    init {
        checkHealth()
    }

    fun loadRepos() {
        val username = sessionStore.githubUsername ?: return
        viewModelScope.launch {
            _reposState.value = Load.Loading
            when (val res = ApiClient.call { listRepos(username) }) {
                is ApiResult.Ok -> _reposState.value = Load.Ready(res.value)
                is ApiResult.Err -> _reposState.value = Load.Failed(res)
            }
        }
    }

    fun startAnalyze(repoFullName: String) {
        if (analyzeJob?.isActive == true) return
        val studentId = sessionStore.studentId ?: return
        analyzeJob = viewModelScope.launch {
            _analyzeState.value = Load.Loading
            when (val res = ApiClient.call { analyze(AnalyzeRequest(studentId = studentId, repoFullName = repoFullName)) }) {
                is ApiResult.Ok -> {
                    val analysis = res.value
                    lastAnalysis = analysis
                    sessionStore.sessionId = analysis.sessionId
                    sessionStore.timeLimitS = analysis.timeLimitS
                    vivaController.startFrom(analysis)
                    _analyzeState.value = Load.Ready(analysis)
                    replace(Screen.RepoFound)
                }
                is ApiResult.Err -> {
                    _analyzeState.value = Load.Failed(res)
                }
            }
        }
    }

    fun cancelAnalyze() {
        analyzeJob?.cancel()
        analyzeJob = null
        _analyzeState.value = Load.Idle
    }

    fun resumeViva(sessionId: String) {
        viewModelScope.launch {
            _resumeVivaError.value = null
            vivaController.resume(sessionId, sessionStore.timeLimitS)
            val err = vivaController.state.value.error
            if (err != null) {
                _resumeVivaError.value = err.message
            } else {
                navigate(Screen.Viva)
            }
        }
    }

    fun clearResumeVivaError() {
        _resumeVivaError.value = null
    }

    fun createStudent(
        request: StudentCreateRequest,
        onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _onboardingState.value = Load.Loading
            when (val res = ApiClient.call { createStudent(request) }) {
                is ApiResult.Ok -> {
                    sessionStore.studentId = res.value.studentId
                    sessionStore.studentName = request.name
                    sessionStore.githubUsername = res.value.githubUsername
                    _onboardingState.value = Load.Ready(res.value)
                    onSuccess()
                    navigate(Screen.Dashboard, clearStack = true)
                }
                is ApiResult.Err -> {
                    _onboardingState.value = Load.Failed(res)
                }
            }
        }
    }

    fun clearOnboardingError() {
        _onboardingState.value = Load.Idle
    }

    fun loadReport() {
        val studentId = sessionStore.studentId ?: return
        viewModelScope.launch {
            _reportState.value = Load.Loading
            when (val res = ApiClient.call { report(studentId) }) {
                is ApiResult.Ok -> {
                    _reportState.value = Load.Ready(res.value)
                }
                is ApiResult.Err -> {
                    _reportState.value = Load.Failed(res)
                }
            }
        }
    }

    fun loadListings() {
        viewModelScope.launch {
            _listingsState.value = Load.Loading
            when (val res = ApiClient.call { listings() }) {
                is ApiResult.Ok -> {
                    _listingsState.value = Load.Ready(res.value)
                }
                is ApiResult.Err -> {
                    _listingsState.value = Load.Failed(res)
                }
            }
        }
    }

    suspend fun applyWithConsent(listingId: String): ApiResult<ApplyResponse> {
        val studentId = sessionStore.studentId ?: return ApiResult.Err("NO_STUDENT", "No student profile found", null)
        _applyingListingIds.value = _applyingListingIds.value + listingId
        return try {
            val res = ApiClient.call {
                apply(ApplyRequest(studentId = studentId, listingId = listingId, shareConsent = true))
            }
            if (res is ApiResult.Ok || (res is ApiResult.Err && res.code == "ALREADY_APPLIED")) {
                loadReport()
            }
            res
        } finally {
            _applyingListingIds.value = _applyingListingIds.value - listingId
        }
    }

    fun applyToListing(listingId: String) {
        val studentId = sessionStore.studentId ?: return
        if (_applyingListingIds.value.contains(listingId)) return
        viewModelScope.launch {
            _applyingListingIds.value = _applyingListingIds.value + listingId
            when (val res = ApiClient.call {
                apply(ApplyRequest(studentId = studentId, listingId = listingId, shareConsent = true))
            }) {
                is ApiResult.Ok -> {
                    val current = _reportState.value
                    if (current is Load.Ready) {
                        val updatedMatches = current.value.matches.map {
                            if (it.listingId == listingId) it.copy(applied = true) else it
                        }
                        _reportState.value = Load.Ready(current.value.copy(matches = updatedMatches))
                    }
                }
                is ApiResult.Err -> {
                    // Button re-enabled; error captured if needed
                }
            }
            _applyingListingIds.value = _applyingListingIds.value - listingId
        }
    }

    suspend fun addHackathon(
        event: String,
        year: Int,
        result: String,
        teamSize: Int,
        repoFullName: String
    ): ApiResult<HackathonItem> {
        val studentId = sessionStore.studentId ?: return ApiResult.Err("NO_STUDENT", "No student profile found", null)
        val res = ApiClient.call {
            addHackathon(
                studentId = studentId,
                body = HackathonCreateRequest(
                    event = event,
                    year = year,
                    result = result,
                    teamSize = teamSize,
                    repoFullName = repoFullName
                )
            )
        }
        if (res is ApiResult.Ok) {
            loadReport()
        }
        return res
    }

    suspend fun deleteHackathon(hackathonId: String): ApiResult<OkResponse> {
        val studentId = sessionStore.studentId ?: return ApiResult.Err("NO_STUDENT", "No student profile found", null)
        val res = ApiClient.call {
            deleteHackathon(studentId = studentId, hackathonId = hackathonId)
        }
        if (res is ApiResult.Ok) {
            loadReport()
        }
        return res
    }

    fun signOut() {
        sessionStore.clear()
        _reportState.value = Load.Idle
        _onboardingState.value = Load.Idle
        navigate(Screen.Welcome, clearStack = true)
    }

    fun checkHealth() {
        viewModelScope.launch {
            _health.value = Load.Loading
            when (val r = ApiClient.call { health() }) {
                is ApiResult.Ok -> _health.value = Load.Ready(r.value)
                is ApiResult.Err -> _health.value = Load.Failed(r)
            }
        }
    }

    fun retryHealth() {
        checkHealth()
    }

    fun navigate(to: Screen, clearStack: Boolean = false) {
        if (clearStack) {
            backStack.clear()
        } else {
            backStack.add(_currentScreen.value)
        }
        _currentScreen.value = to
    }

    fun replace(to: Screen) {
        _currentScreen.value = to
    }

    fun back(): Boolean {
        if (backStack.isNotEmpty()) {
            _currentScreen.value = backStack.removeAt(backStack.size - 1)
            return true
        }
        return false
    }

    override fun onCleared() {
        super.onCleared()
        recorder.discard()
    }
}
