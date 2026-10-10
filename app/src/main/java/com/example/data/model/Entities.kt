package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// --- 1. Student Profile ---
@Entity(
    tableName = "students",
    indices = [Index(value = ["email"], unique = true)]
)
data class StudentEntity(
    @PrimaryKey val id: String, // e.g. "student_1" or UUID
    val fullName: String,
    val email: String,
    val isEmailVerified: Boolean = false,
    val college: String,
    val branch: String,
    val academicYear: String,
    val cgpa: Double,
    val technicalSkills: String, // comma separated
    val areasOfInterest: String, // comma separated
    val preferredDomains: String, // comma separated
    val githubProfileUrl: String,
    val resumeSummary: String, // editable extracted bullet points
    val experienceSummary: String,
    val bio: String = "",
    val isOnboardingComplete: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)

// --- 2. Hackathon Record ---
@Entity(tableName = "hackathons")
data class HackathonEntity(
    @PrimaryKey val id: String,
    val studentId: String,
    val eventName: String,
    val rankOrPerformance: String, // e.g. "1st Place Winner", "Top 10 Finalist"
    val year: String,
    val certificateTitle: String,
    val verified: Boolean = true
)

// --- 3. Student Project (Enforcing Title Uniqueness) ---
@Entity(
    tableName = "projects",
    indices = [
        Index(value = ["normalizedTitle"], unique = true),
        Index(value = ["studentId"])
    ]
)
data class ProjectEntity(
    @PrimaryKey val id: String,
    val studentId: String,
    val title: String,
    val normalizedTitle: String, // normalized for uniqueness check
    val githubRepoUrl: String,
    val languages: String, // e.g. "Kotlin: 65%, Python: 25%, SQL: 10%"
    val repoDescription: String,
    val readmeSnippet: String,
    val fileStructureSummary: String,
    val starsCount: Int = 0,
    val defaultBranch: String = "main",
    val status: String, // "SUBMITTED", "INTERVIEW_PENDING", "EVALUATION_COMPLETED", "UNDER_ADMIN_REVIEW", "SHARED_WITH_RECRUITER"
    val submittedAt: Long = System.currentTimeMillis(),
    val lastEvaluatedAt: Long? = null
)

// --- 4. Interview Question & Responses ---
@Entity(
    tableName = "interview_questions",
    indices = [Index(value = ["projectId", "questionIndex"])]
)
data class InterviewQuestionEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val studentId: String,
    val questionIndex: Int,
    val questionText: String,
    val category: String, // "Architecture", "Algorithms", "Debugging", "Design Decisions", "Edge Cases"
    val difficulty: String, // "Standard", "Intermediate", "Advanced"
    val rationale: String,
    val studentAnswer: String = "",
    val isVoiceTranscription: Boolean = false,
    val isAnswered: Boolean = false,
    val answeredAt: Long? = null
)

// --- 5. Assessment Report (Strict Rubric Dimensions) ---
@Entity(
    tableName = "assessment_reports",
    indices = [Index(value = ["projectId"], unique = true)]
)
data class AssessmentReportEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val studentId: String,
    val technicalUnderstandingScore: Int, // 0-100
    val implementationKnowledgeScore: Int, // 0-100
    val problemSolvingScore: Int, // 0-100
    val architectureScore: Int, // 0-100
    val algorithmsScore: Int, // 0-100
    val debuggingEdgeCasesScore: Int, // 0-100
    val explanationAbilityScore: Int, // 0-100
    val communicationClarityScore: Int, // 0-100
    val overallScore: Int, // Average or weighted
    val strengths: String, // Bullet points or JSON
    val weaknesses: String, // Bullet points or JSON
    val recommendations: String, // Practical improvements
    val integrityObservations: String, // Distinguishing evidence from uncertain signals
    val feedbackSharedWithStudent: Boolean = false, // Controlled by Admin!
    val adminRemarks: String = "",
    val evaluatedAt: Long = System.currentTimeMillis(),
    val feedbackSharedAt: Long? = null
)

// --- 6. Mutual Connection ---
@Entity(
    tableName = "connections",
    indices = [Index(value = ["requesterStudentId", "targetStudentId"], unique = true)]
)
data class ConnectionEntity(
    @PrimaryKey val id: String,
    val requesterStudentId: String,
    val targetStudentId: String,
    val status: String, // "PENDING", "ACCEPTED", "DECLINED"
    val createdAt: Long = System.currentTimeMillis()
)

// --- 7. Recruiter Account ---
@Entity(
    tableName = "recruiters",
    indices = [Index(value = ["email"], unique = true)]
)
data class RecruiterEntity(
    @PrimaryKey val id: String,
    val fullName: String,
    val companyName: String,
    val position: String,
    val email: String,
    val temporaryPassword: String,
    val permanentPasswordEstablished: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

// --- 8. Internship Requirements Posted by Recruiters ---
@Entity(tableName = "internship_requirements")
data class InternshipRequirementEntity(
    @PrimaryKey val id: String,
    val recruiterId: String,
    val companyName: String,
    val roleTitle: String,
    val requiredSkills: String, // comma separated
    val preferredDomains: String,
    val eligibilityCriteria: String, // e.g. "CGPA >= 8.0, 3rd/4th Year"
    val duration: String, // e.g. "6 Months"
    val location: String, // e.g. "Remote / Bengaluru"
    val stipend: String, // e.g. "₹45,000 / month"
    val deadline: String,
    val postedAt: Long = System.currentTimeMillis()
)

// --- 9. Shared Candidate (Forwarded by Admin to Recruiter) ---
@Entity(
    tableName = "shared_candidates",
    indices = [Index(value = ["recruiterId", "studentId", "projectId"], unique = true)]
)
data class SharedCandidateEntity(
    @PrimaryKey val id: String,
    val recruiterId: String,
    val studentId: String,
    val projectId: String,
    val adminNotes: String = "",
    val sharedAt: Long = System.currentTimeMillis(),
    val recruiterStatus: String = "UNDER_REVIEW" // "UNDER_REVIEW", "SHORTLISTED", "INTERVIEW_OFFERED", "ARCHIVED"
)

// --- 10. Admin Message / Campaign ---
@Entity(tableName = "campaign_messages")
data class CampaignMessageEntity(
    @PrimaryKey val id: String,
    val subject: String,
    val body: String,
    val recipientFilter: String, // e.g. "CGPA >= 8.5 & Skills: Android"
    val recipientCount: Int,
    val channel: String, // "EMAIL", "IN_APP"
    val templateName: String = "Custom",
    val deliveryOutcome: String, // "DELIVERED", "QUEUED", "PENDING_CONFIRMATION"
    val sentAt: Long = System.currentTimeMillis()
)

// --- 11. In-App Notification ---
@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val recipientRole: String, // "STUDENT", "ADMIN", "RECRUITER"
    val recipientId: String,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
