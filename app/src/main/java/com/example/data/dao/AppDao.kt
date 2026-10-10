package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    // --- Students ---
    @Query("SELECT * FROM students WHERE id = :studentId LIMIT 1")
    fun getStudentFlow(studentId: String): Flow<StudentEntity?>

    @Query("SELECT * FROM students WHERE id = :studentId LIMIT 1")
    suspend fun getStudentById(studentId: String): StudentEntity?

    @Query("SELECT * FROM students WHERE email = :email LIMIT 1")
    suspend fun getStudentByEmail(email: String): StudentEntity?

    @Query("SELECT * FROM students ORDER BY fullName ASC")
    fun getAllStudentsFlow(): Flow<List<StudentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity)

    @Update
    suspend fun updateStudent(student: StudentEntity)

    // --- Hackathons ---
    @Query("SELECT * FROM hackathons WHERE studentId = :studentId ORDER BY year DESC")
    fun getHackathonsForStudentFlow(studentId: String): Flow<List<HackathonEntity>>

    @Query("SELECT * FROM hackathons WHERE studentId = :studentId")
    suspend fun getHackathonsForStudent(studentId: String): List<HackathonEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHackathon(hackathon: HackathonEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHackathons(hackathons: List<HackathonEntity>)

    // --- Projects ---
    @Query("SELECT * FROM projects WHERE studentId = :studentId ORDER BY submittedAt DESC")
    fun getProjectsForStudentFlow(studentId: String): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :projectId LIMIT 1")
    fun getProjectFlow(projectId: String): Flow<ProjectEntity?>

    @Query("SELECT * FROM projects WHERE id = :projectId LIMIT 1")
    suspend fun getProjectById(projectId: String): ProjectEntity?

    @Query("SELECT * FROM projects WHERE normalizedTitle = :normalizedTitle LIMIT 1")
    suspend fun getProjectByNormalizedTitle(normalizedTitle: String): ProjectEntity?

    @Query("SELECT * FROM projects ORDER BY submittedAt DESC")
    fun getAllProjectsFlow(): Flow<List<ProjectEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertProject(project: ProjectEntity)

    @Update
    suspend fun updateProject(project: ProjectEntity)

    // --- Interview Questions ---
    @Query("SELECT * FROM interview_questions WHERE projectId = :projectId ORDER BY questionIndex ASC")
    fun getQuestionsForProjectFlow(projectId: String): Flow<List<InterviewQuestionEntity>>

    @Query("SELECT * FROM interview_questions WHERE projectId = :projectId ORDER BY questionIndex ASC")
    suspend fun getQuestionsForProject(projectId: String): List<InterviewQuestionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInterviewQuestions(questions: List<InterviewQuestionEntity>)

    @Update
    suspend fun updateInterviewQuestion(question: InterviewQuestionEntity)

    // --- Assessment Reports ---
    @Query("SELECT * FROM assessment_reports WHERE projectId = :projectId LIMIT 1")
    fun getReportForProjectFlow(projectId: String): Flow<AssessmentReportEntity?>

    @Query("SELECT * FROM assessment_reports WHERE projectId = :projectId LIMIT 1")
    suspend fun getReportForProject(projectId: String): AssessmentReportEntity?

    @Query("SELECT * FROM assessment_reports ORDER BY evaluatedAt DESC")
    fun getAllReportsFlow(): Flow<List<AssessmentReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssessmentReport(report: AssessmentReportEntity)

    @Update
    suspend fun updateAssessmentReport(report: AssessmentReportEntity)

    @Query("UPDATE assessment_reports SET feedbackSharedWithStudent = :shared, adminRemarks = :remarks, feedbackSharedAt = :sharedAt WHERE id = :reportId")
    suspend fun shareFeedbackWithStudent(reportId: String, shared: Boolean, remarks: String, sharedAt: Long)

    // --- Connections ---
    @Query("SELECT * FROM connections WHERE requesterStudentId = :studentId OR targetStudentId = :studentId")
    fun getConnectionsForStudentFlow(studentId: String): Flow<List<ConnectionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConnection(connection: ConnectionEntity)

    @Query("UPDATE connections SET status = :status WHERE id = :connectionId")
    suspend fun updateConnectionStatus(connectionId: String, status: String)

    // --- Recruiters ---
    @Query("SELECT * FROM recruiters ORDER BY createdAt DESC")
    fun getAllRecruitersFlow(): Flow<List<RecruiterEntity>>

    @Query("SELECT * FROM recruiters WHERE id = :recruiterId LIMIT 1")
    suspend fun getRecruiterById(recruiterId: String): RecruiterEntity?

    @Query("SELECT * FROM recruiters WHERE email = :email LIMIT 1")
    suspend fun getRecruiterByEmail(email: String): RecruiterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecruiter(recruiter: RecruiterEntity)

    @Query("UPDATE recruiters SET permanentPasswordEstablished = 1 WHERE id = :recruiterId")
    suspend fun markPasswordEstablished(recruiterId: String)

    // --- Internship Requirements ---
    @Query("SELECT * FROM internship_requirements ORDER BY postedAt DESC")
    fun getAllRequirementsFlow(): Flow<List<InternshipRequirementEntity>>

    @Query("SELECT * FROM internship_requirements WHERE recruiterId = :recruiterId ORDER BY postedAt DESC")
    fun getRequirementsForRecruiterFlow(recruiterId: String): Flow<List<InternshipRequirementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequirement(req: InternshipRequirementEntity)

    // --- Shared Candidates ---
    @Query("SELECT * FROM shared_candidates WHERE recruiterId = :recruiterId ORDER BY sharedAt DESC")
    fun getSharedCandidatesForRecruiterFlow(recruiterId: String): Flow<List<SharedCandidateEntity>>

    @Query("SELECT * FROM shared_candidates ORDER BY sharedAt DESC")
    fun getAllSharedCandidatesFlow(): Flow<List<SharedCandidateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSharedCandidate(candidate: SharedCandidateEntity)

    @Query("UPDATE shared_candidates SET recruiterStatus = :status WHERE id = :candidateId")
    suspend fun updateSharedCandidateStatus(candidateId: String, status: String)

    // --- Campaigns ---
    @Query("SELECT * FROM campaign_messages ORDER BY sentAt DESC")
    fun getAllCampaignMessagesFlow(): Flow<List<CampaignMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCampaignMessage(message: CampaignMessageEntity)

    // --- Notifications ---
    @Query("SELECT * FROM notifications WHERE recipientRole = :role AND (recipientId = :recipientId OR recipientId = 'ALL') ORDER BY timestamp DESC")
    fun getNotificationsFlow(role: String, recipientId: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationRead(id: String)
}
