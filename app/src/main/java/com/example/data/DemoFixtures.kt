package com.example.data

enum class DemoMode {
    GUIDED, FREE
}

enum class DemoScreen {
    WELCOME,
    STUDENT_PROFILE,
    REPOSITORY,
    INTERVIEW,
    ASSESSMENT,
    ADMIN_DASHBOARD,
    FEEDBACK,
    RECRUITER_DASHBOARD
}

data class DemoStudent(
    val id: String,
    val name: String,
    val university: String,
    val githubUsername: String,
    val email: String,
    val skills: List<String>,
    val cgpa: Double,
    val interests: List<String>
)

data class DemoHackathon(
    val hackathonId: String,
    val studentId: String,
    val event: String,
    val year: Int,
    val result: String,
    val teamSize: Int,
    val repoFullName: String,
    val verified: Boolean,
    val vivaScore: Int,
    val commitShare: Double
)

data class DemoProject(
    val repoFullName: String,
    val studentId: String,
    val name: String,
    val description: String,
    val language: String,
    val stars: Int,
    val commits: Int,
    val activeDays: Int,
    val spanDays: Int,
    val commitShare: Double
)

data class DemoQuestion(
    val questionId: String,
    val repoFullName: String,
    val file: String,
    val lines: String,
    val snippet: String,
    val question: String,
    val defaultAnswer: String,
    val skill: String,
    val mode: String
)

data class DemoInterviewSession(
    val sessionId: String,
    val studentId: String,
    val repoFullName: String,
    val questions: List<DemoQuestion>,
    val status: String
)

data class DemoClaimedVsVerified(
    val skillOrTech: String,
    val category: String,
    val verified: Boolean,
    val confidence: String
)

data class DemoEvidence(
    val questionId: String,
    val file: String,
    val lines: String,
    val snippet: String,
    val question: String,
    val answerExcerpt: String,
    val evidenceQuote: String,
    val score: Int,
    val skill: String
)

data class DemoAssessment(
    val assessmentId: String,
    val studentId: String,
    val repoFullName: String,
    val overallScore: Int,
    val status: String,
    val skills: Map<String, Int>,
    val claimedVsVerified: List<DemoClaimedVsVerified>,
    val evidence: List<DemoEvidence>,
    val strengths: List<String>,
    val improvements: List<String>
)

data class DemoInternship(
    val id: String,
    val title: String,
    val company: String,
    val description: String,
    val skillWeights: Map<String, Double>,
    val requiredTags: List<String>
)

data class DemoCandidateMatch(
    val matchId: String,
    val studentId: String,
    val internshipId: String,
    val matchScore: Int,
    val rationale: String,
    val shortlisted: Boolean
)

data class DemoAdmin(
    val adminId: String,
    val name: String,
    val email: String
)

data class DemoRecruiter(
    val recruiterId: String,
    val name: String,
    val company: String,
    val associatedInternshipId: String
)

data class DemoFeedback(
    val feedbackId: String,
    val assessmentId: String,
    val studentId: String,
    val summary: String,
    val strengths: List<String>,
    val improvements: List<String>,
    val releasedToStudent: Boolean
)

data class DemoInterviewState(
    val sessionId: String?,
    val currentQuestionIndex: Int,
    val answers: Map<String, String>,
    val skippedQuestionIds: List<String>
)

data class DemoAdminState(
    val searchQuery: String,
    val activeFilters: Map<String, List<String>>,
    val shortlistedStudentIds: List<String>,
    val releasedFeedbackIds: List<String>
)

data class DemoFeedbackState(
    val selectedAssessmentId: String?,
    val visibleToStudent: Boolean
)

data class DemoState(
    val sessionId: String,
    val mode: String = "demo",
    val tourMode: DemoMode,
    val currentScreen: DemoScreen,
    val currentStep: Int,
    val selectedStudentId: String?,
    val selectedProjectId: String?,
    val selectedInternshipId: String?,
    val selectedRecruiterId: String?,
    val interview: DemoInterviewState,
    val admin: DemoAdminState,
    val feedback: DemoFeedbackState
)

object DemoFixtures {
    val students = listOf(
        DemoStudent(
            id = "student_aarav",
            name = "Aarav Mehta",
            university = "IIT Bombay",
            githubUsername = "aarav_ml",
            email = "aarav.demo@proofolio.ai",
            skills = listOf("Python", "PyTorch", "Model Evaluation", "FastAPI", "Docker"),
            cgpa = 9.2,
            interests = listOf("AI/ML Research", "LLM Optimization")
        ),
        DemoStudent(
            id = "student_mira",
            name = "Mira Nair",
            university = "BITS Pilani",
            githubUsername = "mira_fullstack",
            email = "mira.demo@proofolio.ai",
            skills = listOf("Kotlin", "Jetpack Compose", "Spring Boot", "REST API", "PostgreSQL"),
            cgpa = 8.9,
            interests = listOf("Mobile Architecture", "Cloud Infrastructure")
        ),
        DemoStudent(
            id = "student_rohan",
            name = "Rohan Shah",
            university = "IIIT Hyderabad",
            githubUsername = "rohan_data",
            email = "rohan.demo@proofolio.ai",
            skills = listOf("Python", "Pandas", "Scikit-Learn", "SQL", "Streamlit"),
            cgpa = 8.6,
            interests = listOf("Data Science", "Predictive Analytics")
        )
    )

    val projects = listOf(
        DemoProject(
            repoFullName = "aarav_ml/neural-prune",
            studentId = "student_aarav",
            name = "NeuralPrune",
            description = "Structured pruning toolkit for large transformer models with minimal accuracy loss.",
            language = "Python",
            stars = 340,
            commits = 120,
            activeDays = 30,
            spanDays = 90,
            commitShare = 0.92
        ),
        DemoProject(
            repoFullName = "mira_fullstack/proofolio-mobile",
            studentId = "student_mira",
            name = "Proofolio Mobile Client",
            description = "Jetpack Compose client application for verified student portfolio showcase.",
            language = "Kotlin",
            stars = 185,
            commits = 95,
            activeDays = 22,
            spanDays = 60,
            commitShare = 0.88
        ),
        DemoProject(
            repoFullName = "rohan_data/market-pulse",
            studentId = "student_rohan",
            name = "MarketPulse Analytics",
            description = "Real-time stock sentiment analysis pipeline using transformer embeddings.",
            language = "Python",
            stars = 92,
            commits = 45,
            activeDays = 15,
            spanDays = 40,
            commitShare = 0.95
        )
    )

    val hackathons = listOf(
        DemoHackathon(
            hackathonId = "h_aarav",
            studentId = "student_aarav",
            event = "Global AI Hackathon 2025",
            year = 2025,
            result = "winner",
            teamSize = 3,
            repoFullName = "aarav_ml/neural-prune",
            verified = true,
            vivaScore = 94,
            commitShare = 0.90
        ),
        DemoHackathon(
            hackathonId = "h_mira",
            studentId = "student_mira",
            event = "Android Dev Sprint",
            year = 2024,
            result = "finalist",
            teamSize = 2,
            repoFullName = "mira_fullstack/proofolio-mobile",
            verified = true,
            vivaScore = 89,
            commitShare = 0.85
        )
    )

    val questions = listOf(
        DemoQuestion(
            questionId = "q_aarav_1",
            repoFullName = "aarav_ml/neural-prune",
            file = "prune/core.py",
            lines = "45-62",
            snippet = "def compute_hessian_trace(model, dataloader):\n    # Gradient second-order approximation\n    pass",
            question = "How do you handle numerical stability when computing second-order Hessian traces for large weights?",
            defaultAnswer = "We add diagonal damping to the Fisher information matrix and use FP32 accumulation during trace estimation.",
            skill = "Python",
            mode = "text"
        ),
        DemoQuestion(
            questionId = "q_mira_1",
            repoFullName = "mira_fullstack/proofolio-mobile",
            file = "ui/Navigation.kt",
            lines = "12-30",
            snippet = "class AppViewModel : AndroidViewModel() {\n    val currentScreen = MutableStateFlow<Screen>(Screen.Welcome)\n}",
            question = "Explain how state preservation is handled across configuration changes in Jetpack Compose viewmodels.",
            defaultAnswer = "We utilize StateFlow collectors with collectAsStateWithLifecycle and ViewModel scoping to survive activity recreation.",
            skill = "Kotlin",
            mode = "text"
        )
    )

    val interviewSessions = listOf(
        DemoInterviewSession(
            sessionId = "session_aarav",
            studentId = "student_aarav",
            repoFullName = "aarav_ml/neural-prune",
            questions = questions.filter { it.repoFullName == "aarav_ml/neural-prune" },
            status = "completed"
        ),
        DemoInterviewSession(
            sessionId = "session_mira",
            studentId = "student_mira",
            repoFullName = "mira_fullstack/proofolio-mobile",
            questions = questions.filter { it.repoFullName == "mira_fullstack/proofolio-mobile" },
            status = "pending"
        )
    )

    val assessments = listOf(
        DemoAssessment(
            assessmentId = "assessment_aarav",
            studentId = "student_aarav",
            repoFullName = "aarav_ml/neural-prune",
            overallScore = 92,
            status = "done",
            skills = mapOf("Python" to 95, "PyTorch" to 90, "Model Evaluation" to 92, "FastAPI" to 85, "Docker" to 88),
            claimedVsVerified = listOf(
                DemoClaimedVsVerified("Python", "Language", true, "strong"),
                DemoClaimedVsVerified("PyTorch", "AI/ML", true, "strong"),
                DemoClaimedVsVerified("Docker", "DevOps", true, "partial")
            ),
            evidence = listOf(
                DemoEvidence(
                    questionId = "q_aarav_1",
                    file = "prune/core.py",
                    lines = "45-62",
                    snippet = "def compute_hessian_trace(model, dataloader): ...",
                    question = "How do you handle numerical stability when computing second-order Hessian traces?",
                    answerExcerpt = "We add diagonal damping to the Fisher information matrix...",
                    evidenceQuote = "Demonstrates rigorous mathematical understanding and robust implementation.",
                    score = 9,
                    skill = "Python"
                )
            ),
            strengths = listOf("Exceptional deep learning architecture mastery", "Clean algorithmic implementation"),
            improvements = listOf("Add automated unit tests for edge-case tensor shapes")
        ),
        DemoAssessment(
            assessmentId = "assessment_mira",
            studentId = "student_mira",
            repoFullName = "mira_fullstack/proofolio-mobile",
            overallScore = 89,
            status = "done",
            skills = mapOf("Kotlin" to 92, "Jetpack Compose" to 94, "Spring Boot" to 85, "REST API" to 88, "PostgreSQL" to 82),
            claimedVsVerified = listOf(
                DemoClaimedVsVerified("Kotlin", "Language", true, "strong"),
                DemoClaimedVsVerified("Jetpack Compose", "UI", true, "strong")
            ),
            evidence = emptyList(),
            strengths = listOf("Outstanding UI responsiveness and clean Compose hierarchy", "Solid state management"),
            improvements = listOf("Expand offline caching layers")
        )
    )

    val internships = listOf(
        DemoInternship(
            id = "internship_ai",
            title = "AI Research Intern",
            company = "Apex AI Labs",
            description = "Optimize transformer inference pipelines and evaluate quantization techniques.",
            skillWeights = mapOf("Python" to 0.4, "PyTorch" to 0.4, "Model Evaluation" to 0.2),
            requiredTags = listOf("Python", "PyTorch", "AI/ML")
        ),
        DemoInternship(
            id = "internship_mobile",
            title = "Senior Android Engineering Intern",
            company = "TechVenture Labs",
            description = "Build high-performance Android applications with Kotlin and Jetpack Compose.",
            skillWeights = mapOf("Kotlin" to 0.5, "Jetpack Compose" to 0.4, "REST API" to 0.1),
            requiredTags = listOf("Kotlin", "Compose", "Android")
        )
    )

    val candidateMatches = listOf(
        DemoCandidateMatch(
            matchId = "match_aarav_ai",
            studentId = "student_aarav",
            internshipId = "internship_ai",
            matchScore = 95,
            rationale = "Exceptional expertise in PyTorch model pruning and tensor optimization.",
            shortlisted = true
        ),
        DemoCandidateMatch(
            matchId = "match_mira_mobile",
            studentId = "student_mira",
            internshipId = "internship_mobile",
            matchScore = 93,
            rationale = "Strong mastery of Jetpack Compose state architecture and Kotlin coroutines.",
            shortlisted = true
        ),
        DemoCandidateMatch(
            matchId = "match_rohan_ai",
            studentId = "student_rohan",
            internshipId = "internship_ai",
            matchScore = 76,
            rationale = "Good data analytics background but requires deeper PyTorch fundamentals.",
            shortlisted = false
        )
    )

    val admin = DemoAdmin(
        adminId = "admin_demo",
        name = "Chief Demo Judge",
        email = "judge@proofolio.ai"
    )

    val recruiter = DemoRecruiter(
        recruiterId = "recruiter_demo",
        name = "Sarah TechRecruit",
        company = "TechVenture Labs",
        associatedInternshipId = "internship_mobile"
    )

    val feedback = DemoFeedback(
        feedbackId = "feedback_aarav",
        assessmentId = "assessment_aarav",
        studentId = "student_aarav",
        summary = "Demonstrated world-class code quality and deep theoretical grasp during AI interview.",
        strengths = listOf("Rigorous tensor computation logic", "Clear and concise explanation of Hessian damping"),
        improvements = listOf("Include unit test coverage metrics"),
        releasedToStudent = true
    )

    fun createInitialState(tourMode: DemoMode = DemoMode.GUIDED): DemoState {
        return DemoState(
            sessionId = "demo_session_${System.currentTimeMillis()}",
            tourMode = tourMode,
            currentScreen = if (tourMode == DemoMode.GUIDED) DemoScreen.STUDENT_PROFILE else DemoScreen.ADMIN_DASHBOARD,
            currentStep = 1,
            selectedStudentId = students.first().id,
            selectedProjectId = projects.first().repoFullName,
            selectedInternshipId = internships.first().id,
            selectedRecruiterId = recruiter.recruiterId,
            interview = DemoInterviewState(
                sessionId = interviewSessions.first().sessionId,
                currentQuestionIndex = 0,
                answers = emptyMap(),
                skippedQuestionIds = emptyList()
            ),
            admin = DemoAdminState(
                searchQuery = "",
                activeFilters = emptyMap(),
                shortlistedStudentIds = candidateMatches.filter { it.shortlisted }.map { it.studentId },
                releasedFeedbackIds = listOf(feedback.feedbackId)
            ),
            feedback = DemoFeedbackState(
                selectedAssessmentId = assessments.first().assessmentId,
                visibleToStudent = true
            )
        )
    }
}
