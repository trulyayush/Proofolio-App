package com.example.data

import com.example.data.api.*

data class AdminStudentItem(
    val id: String,
    val name: String,
    val university: String,
    val githubUsername: String,
    val score: Int,
    val status: String,
    val feedback: String,
    val strengths: List<String>,
    val improvements: List<String>
)

data class RecruiterCandidateItem(
    val id: String,
    val name: String,
    val roleApplied: String,
    val company: String,
    val matchScore: Int,
    val verifiedSkills: Map<String, Int>,
    val status: String
)

object DemoData {
    val report = ReportResponse(
        student = ReportStudent("demo_id", "Judge Demo Student", "Demo University", "demo_user"),
        repo = ReportRepo("demo_user/demo-project"),
        status = "done",
        overall = 85,
        skills = mapOf("backend" to 80, "frontend" to 90, "data" to 70, "ai_ml" to 60, "devops" to 85, "security" to 95),
        claimedVsVerified = listOf(
            ClaimedVsVerified("Kotlin", "backend", true, "strong"),
            ClaimedVsVerified("Jetpack Compose", "frontend", true, "strong"),
            ClaimedVsVerified("Spring Boot", "backend", true, "partial")
        ),
        evidence = listOf(
            EvidenceItem(
                questionId = "q1",
                repoFullName = "demo_user/demo-project",
                file = "src/main/java/Auth.kt",
                lines = "10-20",
                snippet = "val auth = JwtAuthService()",
                snippetStartLine = 10,
                question = "How do you handle secure token rotation in distributed microservices?",
                answerExcerpt = "We implement sliding window JWT refresh tokens with Redis persistence and secure httpOnly cookies.",
                evidenceQuote = "Excellent architectural understanding of token security and expiry handling.",
                covered = listOf("security", "backend"),
                missed = emptyList(),
                score = 9,
                skill = "security",
                mode = "text",
                pasted = false,
                durationS = 45,
                hadFollowUp = false
            )
        ),
        buildHistory = BuildHistory(
            commits = 48,
            activeDays = 14,
            spanDays = 45,
            authors = 1,
            yourCommitShare = 1.0,
            medianLinesPerCommit = 18
        ),
        hackathons = listOf(
            HackathonItem(
                hackathonId = "h1",
                event = "Global AI Hackathon 2025",
                year = 2025,
                result = "winner",
                teamSize = 3,
                repoFullName = "demo_user/ai-copilot",
                verified = true,
                vivaScore = 88,
                yourCommitShare = 0.95
            ),
            HackathonItem(
                hackathonId = "h2",
                event = "Open Source Sprint",
                year = 2024,
                result = "finalist",
                teamSize = 2,
                repoFullName = "demo_user/oss-core",
                verified = true,
                vivaScore = 82,
                yourCommitShare = 0.85
            )
        ),
        vivaInProgress = null,
        matches = listOf(
            MatchItem("m1", "Senior Android Engineer", "TechVenture Labs", 95, "Exceptional Jetpack Compose & Architecture proficiency", false),
            MatchItem("m2", "Backend Systems Engineer", "CloudScale Inc", 90, "Strong distributed systems and security background", true)
        )
    )

    val listings = ListingsResponse(
        listOf(
            Listing("m1", "Senior Android Engineer", "TechVenture Labs", "Build cutting-edge mobile apps using Jetpack Compose and Kotlin coroutines.", mapOf("frontend" to 0.8, "backend" to 0.5), listOf("kotlin", "compose", "android")),
            Listing("m2", "Backend Systems Engineer", "CloudScale Inc", "Design and scale high-throughput microservices in Kotlin and Spring.", mapOf("backend" to 0.9, "security" to 0.8), listOf("kotlin", "spring", "security", "docker"))
        )
    )

    val adminStudents = listOf(
        AdminStudentItem("demo_id", "Judge Demo Student", "Demo University", "demo_user", 85, "SHARED_WITH_RECRUITER", "Strong technical depth across backend and security. Excellent viva responses.", listOf("Security architecture", "Compose state management"), listOf("AI/ML optimization")),
        AdminStudentItem("s2", "Sarah Chen", "Stanford University", "schen_dev", 92, "UNDER_ADMIN_REVIEW", "Outstanding algorithmic problem solving and clean repository structure.", listOf("System design", "Clean code"), listOf("DevOps integration")),
        AdminStudentItem("s3", "Alex Rivera", "MIT", "arivera_code", 78, "INCOMPLETE", "Interview was skipped for 2 questions. Needs follow-up review.", listOf("Frontend UI"), listOf("Security protocols", "Test coverage"))
    )

    val recruiterCandidates = listOf(
        RecruiterCandidateItem("demo_id", "Judge Demo Student", "Senior Android Engineer", "TechVenture Labs", 95, mapOf("backend" to 80, "frontend" to 90, "security" to 95), "APPROVED"),
        RecruiterCandidateItem("s2", "Sarah Chen", "Senior Android Engineer", "TechVenture Labs", 92, mapOf("backend" to 95, "frontend" to 85, "security" to 90), "SHORTLISTED")
    )
}
