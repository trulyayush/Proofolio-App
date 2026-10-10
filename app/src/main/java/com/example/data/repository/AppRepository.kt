package com.example.data.repository

import android.content.Context
import com.example.data.dao.AppDao
import com.example.data.db.AppDatabase
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
import com.example.network.GeminiAiService
import com.example.network.GitHubService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

class AppRepository(context: Context) {
    private val db = AppDatabase.getDatabase(context)
    val dao: AppDao = db.appDao()

    suspend fun initializeSeedDataIfNeeded() = withContext(Dispatchers.IO) {
        val existingStudents = dao.getStudentById("student_default")
        if (existingStudents != null) return@withContext

        // --- 1. Seed Student 1 (Default logged-in student) ---
        val student1 = StudentEntity(
            id = "student_default",
            fullName = "Aarav Deshmukh",
            email = "aarav.deshmukh@vit.ac.in",
            isEmailVerified = true,
            college = "Vellore Institute of Technology",
            branch = "Computer Science and Engineering",
            academicYear = "3rd Year (Class of 2026)",
            cgpa = 9.18,
            technicalSkills = "Kotlin, Jetpack Compose, Coroutines, Room, Go, gRPC, Docker, PostgreSQL, Redis",
            areasOfInterest = "Distributed Systems, Mobile Performance Architecture, Real-Time Networking",
            preferredDomains = "Systems Engineering, Core Android, Cloud Backend",
            githubProfileUrl = "https://github.com/aarav-deshmukh",
            resumeSummary = "• 3rd Year B.Tech CSE at VIT with 9.18 CGPA.\n• Deep proficiency in Kotlin, Android Jetpack internals, and Go microservices.\n• Experience with raft consensus, memory profiling, and high-concurrency event loops.",
            experienceSummary = "Software Engineering Intern at CloudMatrix (Summer 2025): Optimized IPC messaging queue, reducing memory churn by 32%.",
            bio = "Systems-oriented Android & Backend engineer interested in zero-allocation data structures and high-performance mobile apps.",
            isOnboardingComplete = true
        )
        dao.insertStudent(student1)

        // Seed Student 2
        val student2 = StudentEntity(
            id = "student_2",
            fullName = "Ananya Sharma",
            email = "ananya.s@iitb.ac.in",
            isEmailVerified = true,
            college = "IIT Bombay",
            branch = "Electrical & Computer Engineering",
            academicYear = "4th Year (Class of 2025)",
            cgpa = 9.42,
            technicalSkills = "Python, PyTorch, C++, CUDA, Triton, TensorRT, Linux Systems, Distributed Training",
            areasOfInterest = "ML Acceleration, Edge AI, Compiler Optimization",
            preferredDomains = "Machine Learning Engineering, Edge Computing",
            githubProfileUrl = "https://github.com/ananya-ai",
            resumeSummary = "• Published paper at NeurIPS workshop on quantized transformer inference.\n• Core contributor to open-source ONNX runtime kernels.",
            experienceSummary = "Research Intern at AI Center of Excellence: Built automated kernel fuser for quantized LLM inference.",
            bio = "Building high-throughput inference engines and low-precision tensor compilers.",
            isOnboardingComplete = true
        )
        dao.insertStudent(student2)

        // Seed Student 3
        val student3 = StudentEntity(
            id = "student_3",
            fullName = "Rohan Nair",
            email = "rohan.nair@bits-pilani.ac.in",
            isEmailVerified = true,
            college = "BITS Pilani",
            branch = "Computer Science",
            academicYear = "3rd Year (Class of 2026)",
            cgpa = 8.75,
            technicalSkills = "TypeScript, React, Rust, WebAssembly, WebRTC, GraphQL, Next.js",
            areasOfInterest = "Collaborative Infrastructure, Canvas Rendering, P2P Protocols",
            preferredDomains = "Full-Stack Platforms, Real-Time Media",
            githubProfileUrl = "https://github.com/rohan-nair-dev",
            resumeSummary = "• Built real-time multiplayer vector graphics editor in Rust + WebAssembly.\n• Winner of HackMIT 2025 (Web3 Track).",
            experienceSummary = "Frontend Architect Intern at CanvasSync: Migrated SVG renderer to hardware-accelerated WebGL.",
            bio = "Obsessed with latency, fluid 120fps client experiences, and WebAssembly audio/video pipelines.",
            isOnboardingComplete = true
        )
        dao.insertStudent(student3)

        // --- 2. Seed Hackathons ---
        val hackathons = listOf(
            HackathonEntity(
                id = "hack_1",
                studentId = "student_default",
                eventName = "Smart India Hackathon 2025",
                rankOrPerformance = "1st Place Winner (National)",
                year = "2025",
                certificateTitle = "Ministry of Education Gold Citation"
            ),
            HackathonEntity(
                id = "hack_2",
                studentId = "student_default",
                eventName = "ETHIndia Global Hackathon",
                rankOrPerformance = "Best Mobile Integration Finalist",
                year = "2024",
                certificateTitle = "ETHIndia Top 10 Track Award"
            ),
            HackathonEntity(
                id = "hack_3",
                studentId = "student_2",
                eventName = "NeurIPS AI Compute Challenge",
                rankOrPerformance = "Grand Champion",
                year = "2025",
                certificateTitle = "OpenAI Sponsored Research Award"
            ),
            HackathonEntity(
                id = "hack_4",
                studentId = "student_3",
                eventName = "HackMIT 2025",
                rankOrPerformance = "1st Place (Infrastructure)",
                year = "2025",
                certificateTitle = "MIT EECS Excellence Medal"
            )
        )
        dao.insertHackathons(hackathons)

        // --- 3. Seed Projects & Normalized Titles ---
        val project1 = ProjectEntity(
            id = "proj_1",
            studentId = "student_default",
            title = "Chronos: Distributed Log Engine",
            normalizedTitle = normalizeProjectTitle("Chronos: Distributed Log Engine"),
            githubRepoUrl = "https://github.com/aarav-deshmukh/chronos-engine",
            languages = "Go (68%), Kotlin (22%), Proto (10%)",
            repoDescription = "High-throughput append-only distributed commit log featuring segmented ring buffers, zero-copy socket reads, and a reactive Android telemetry inspector.",
            readmeSnippet = "# Chronos Distributed Log\nChronos is an LSM-tree backed log storage engine modeled after Kafka partition segments. Designed to process 450,000 writes/sec with bounded P99 latency under 2.4ms.",
            fileStructureSummary = "cmd/server | pkg/storage/segment.go | pkg/raft | android/app/telemetry | proto/log.proto",
            starsCount = 284,
            defaultBranch = "main",
            status = "EVALUATION_COMPLETED",
            submittedAt = System.currentTimeMillis() - 86400000L * 3,
            lastEvaluatedAt = System.currentTimeMillis() - 86400000L * 2
        )
        dao.insertProject(project1)

        val project2 = ProjectEntity(
            id = "proj_2",
            studentId = "student_2",
            title = "FlashTriton: Fused Quantized Attention",
            normalizedTitle = normalizeProjectTitle("FlashTriton: Fused Quantized Attention"),
            githubRepoUrl = "https://github.com/ananya-ai/flash-triton",
            languages = "Python (55%), C++ (30%), CUDA (15%)",
            repoDescription = "Custom OpenAI Triton fused kernel implementation of 4-bit block-wise scaled attention with speculative decoding support.",
            readmeSnippet = "# FlashTriton\nCustom kernels minimizing SRAM bank conflicts during FP4 matrix multiplication. Achieves 1.84x speedup over standard baseline on Hopper architecture.",
            fileStructureSummary = "triton_kernels/ | csrc/bindings.cpp | tests/bench_attention.py | docs/latency.png",
            starsCount = 612,
            defaultBranch = "main",
            status = "UNDER_ADMIN_REVIEW",
            submittedAt = System.currentTimeMillis() - 86400000L * 2,
            lastEvaluatedAt = System.currentTimeMillis() - 86400000L * 1
        )
        dao.insertProject(project2)

        // --- 4. Seed Interview Questions for Project 1 ---
        val questions1 = listOf(
            InterviewQuestionEntity(
                id = "q_1_1",
                projectId = "proj_1",
                studentId = "student_default",
                questionIndex = 1,
                questionText = "In 'Chronos', how do you manage segment index rolling and handle memory mapping (mmap) without exhausting virtual address limits on 32-bit systems?",
                category = "Architecture & Storage",
                difficulty = "Advanced",
                rationale = "Probes low-level file descriptor and memory mapping lifecycle management.",
                studentAnswer = "We chunk our log files into fixed 1GB active segments. For older read-only segments, we keep only a sparse index in memory (sampling every 4KB of offsets). When rolling, an atomic CAS moves the pointer to a new segment file. For 32-bit compatibility, we use pread syscalls with pooled byte buffers rather than holding mmap handles open indefinitely.",
                isVoiceTranscription = false,
                isAnswered = true,
                answeredAt = System.currentTimeMillis() - 86400000L * 2
            ),
            InterviewQuestionEntity(
                id = "q_1_2",
                projectId = "proj_1",
                studentId = "student_default",
                questionIndex = 2,
                questionText = "Explain your consensus protocol or replication scheme when a partitioned leader fails during an in-flight quorum write.",
                category = "Distributed Systems & Algorithms",
                difficulty = "Advanced",
                rationale = "Evaluates rigor in handling split-brain and uncommitted log reconciliation.",
                studentAnswer = "We adopted a lightweight Raft variant. Writes are acknowledged to clients only after high-water mark advances across a strict majority quorum. If a leader partitions before quorum ack, the client receives an in-flight timeout, and the succeeding term leader overwrites uncommitted entries with a deterministic append-entries epoch match.",
                isVoiceTranscription = true,
                isAnswered = true,
                answeredAt = System.currentTimeMillis() - 86400000L * 2
            ),
            InterviewQuestionEntity(
                id = "q_1_3",
                projectId = "proj_1",
                studentId = "student_default",
                questionIndex = 3,
                questionText = "How did you design the Android companion app to stream telemetry from Chronos without causing UI jank or excessive garbage collection overhead?",
                category = "Mobile Engineering & Concurrency",
                difficulty = "Intermediate",
                rationale = "Assesses Compose state efficiency, coroutine dispatching, and memory profiling.",
                studentAnswer = "The telemetry engine connects over gRPC-Web streams on a dedicated Dispatchers.IO coroutine. Incoming log events are batched into fixed 50ms intervals using StateFlow with StateSharing.WhileSubscribed. In Jetpack Compose, we avoid object re-allocation by reusing a ring buffer model and using derivedStateOf for the live metrics canvas.",
                isVoiceTranscription = false,
                isAnswered = true,
                answeredAt = System.currentTimeMillis() - 86400000L * 2
            ),
            InterviewQuestionEntity(
                id = "q_1_4",
                projectId = "proj_1",
                studentId = "student_default",
                questionIndex = 4,
                questionText = "What were the toughest edge cases or race conditions you uncovered during automated stress testing?",
                category = "Debugging & Edge Cases",
                difficulty = "Advanced",
                rationale = "Checks real debugging experience under high concurrency.",
                studentAnswer = "A subtle race condition occurred when concurrent consumer group rebalances coincided with log compaction truncating tombstones. A consumer would receive an OutOfRange offset exception. We fixed this by introducing a tombstone retention grace window and requiring a lock on partition segment eviction until reader lease counts drop to zero.",
                isVoiceTranscription = true,
                isAnswered = true,
                answeredAt = System.currentTimeMillis() - 86400000L * 2
            )
        )
        dao.insertInterviewQuestions(questions1)

        // --- 5. Seed Assessment Report (With Shared Feedback enabled by default for Project 1 so user can see radar chart immediately!) ---
        val report1 = AssessmentReportEntity(
            id = "rep_1",
            projectId = "proj_1",
            studentId = "student_default",
            technicalUnderstandingScore = 93,
            implementationKnowledgeScore = 90,
            problemSolvingScore = 94,
            architectureScore = 96,
            algorithmsScore = 89,
            debuggingEdgeCasesScore = 91,
            explanationAbilityScore = 95,
            communicationClarityScore = 92,
            overallScore = 93,
            strengths = "• Exceptional clarity on low-level memory allocation and sparse log indexing.\n• Demonstrated deep mastery of Raft consensus and quorum recovery edge cases.\n• Clean separation of concerns between Go backend primitives and Android Compose telemetry.",
            weaknesses = "• Could explore io_uring for Linux kernel zero-copy enhancements instead of pread.\n• Formal TLA+ specifications for the consensus lease window would provide mathematical guarantees.",
            recommendations = "1. Benchmark against RocksDB storage engines under 95% disk saturation.\n2. Add fuzz testing on partition recovery using Jepsen-style chaos testing.\n3. Publish an architectural case study on mobile gRPC telemetry streaming.",
            integrityObservations = "Audit Verified: Technical claims, commit timestamps, and code signatures correspond directly with the repository history. The candidate explained non-trivial bugs and design decisions with genuine firsthand engineering experience.",
            feedbackSharedWithStudent = true, // Shared by Admin!
            adminRemarks = "Outstanding systems engineering candidate. Approved for top-tier infrastructure and Android engineering internship tracks.",
            evaluatedAt = System.currentTimeMillis() - 86400000L * 2,
            feedbackSharedAt = System.currentTimeMillis() - 86400000L * 1
        )
        dao.insertAssessmentReport(report1)

        // Assessment for Project 2 (Private, not shared yet)
        val report2 = AssessmentReportEntity(
            id = "rep_2",
            projectId = "proj_2",
            studentId = "student_2",
            technicalUnderstandingScore = 97,
            implementationKnowledgeScore = 95,
            problemSolvingScore = 98,
            architectureScore = 92,
            algorithmsScore = 96,
            debuggingEdgeCasesScore = 94,
            explanationAbilityScore = 93,
            communicationClarityScore = 95,
            overallScore = 95,
            strengths = "• Deep comprehension of GPU memory hierarchy, warp divergence, and shared memory banking.\n• High-fidelity answers explaining Triton kernel compilation pipelines.",
            weaknesses = "• Hardware target specifics primarily tested on H100; cross-architecture fallback on consumer GPUs untested.",
            recommendations = "1. Expand test matrix to include AMD ROCm / HIP targets.\n2. Profile memory throughput with NVIDIA Nsight Compute.",
            integrityObservations = "Verified: Codebase exhibits deep originality with bespoke arithmetic optimizations. Code matches student's published workshop research.",
            feedbackSharedWithStudent = false, // Under admin review!
            adminRemarks = "Exceptional ML systems candidate. Highly recommended for Google DeepMind or NVIDIA AI infrastructure internships.",
            evaluatedAt = System.currentTimeMillis() - 86400000L * 1
        )
        dao.insertAssessmentReport(report2)

        // --- 6. Seed Recruiters ---
        val recruiter1 = RecruiterEntity(
            id = "recruiter_1",
            fullName = "Vikram Sengupta",
            companyName = "Google Cloud",
            position = "Engineering Director, Cloud Infrastructure",
            email = "vikram.cloud@google.com",
            temporaryPassword = "TEMP-GC-8849",
            permanentPasswordEstablished = true
        )
        val recruiter2 = RecruiterEntity(
            id = "recruiter_2",
            fullName = "Sarah Jenkins",
            companyName = "Atlassian",
            position = "Staff Engineering Recruiter",
            email = "sjenkins@atlassian.com",
            temporaryPassword = "TEMP-ATL-4921",
            permanentPasswordEstablished = true
        )
        val recruiter3 = RecruiterEntity(
            id = "recruiter_3",
            fullName = "Aditya Kulkarni",
            companyName = "Razorpay",
            position = "Head of Campus Talent",
            email = "aditya.k@razorpay.com",
            temporaryPassword = "TEMP-RZP-1092",
            permanentPasswordEstablished = false
        )
        dao.insertRecruiter(recruiter1)
        dao.insertRecruiter(recruiter2)
        dao.insertRecruiter(recruiter3)

        // --- 7. Seed Internship Requirements ---
        val reqs = listOf(
            InternshipRequirementEntity(
                id = "req_1",
                recruiterId = "recruiter_1",
                companyName = "Google Cloud",
                roleTitle = "Distributed Systems & Mobile Infrastructure Intern",
                requiredSkills = "Go, Kotlin, gRPC, Distributed Systems, Concurrency, Linux",
                preferredDomains = "Cloud Backend, Core Systems, Mobile Telemetry",
                eligibilityCriteria = "CGPA >= 8.5, 3rd or 4th Year B.Tech",
                duration = "6 Months (Jan - June 2026)",
                location = "Bengaluru / Hyderabad (Hybrid)",
                stipend = "₹1,25,000 / month",
                deadline = "October 31, 2026"
            ),
            InternshipRequirementEntity(
                id = "req_2",
                recruiterId = "recruiter_2",
                companyName = "Atlassian",
                roleTitle = "Core Android & Platform Engineering Intern",
                requiredSkills = "Kotlin, Jetpack Compose, Coroutines, Room, Performance Profiling",
                preferredDomains = "Core Android, Mobile Architectures",
                eligibilityCriteria = "CGPA >= 8.0, 3rd Year B.Tech",
                duration = "6 Months (Summer 2026)",
                location = "Bengaluru (Remote Friendly)",
                stipend = "₹1,00,000 / month",
                deadline = "November 15, 2026"
            ),
            InternshipRequirementEntity(
                id = "req_3",
                recruiterId = "recruiter_3",
                companyName = "Razorpay",
                roleTitle = "High-Throughput Payment Engine Intern",
                requiredSkills = "Go, PostgreSQL, Redis, Event Sourcing, Kafka",
                preferredDomains = "Fintech Infrastructure, Distributed Systems",
                eligibilityCriteria = "CGPA >= 8.0",
                duration = "6 Months",
                location = "Bengaluru",
                stipend = "₹80,000 / month",
                deadline = "December 1, 2026"
            )
        )
        reqs.forEach { dao.insertRequirement(it) }

        // --- 8. Seed Shared Candidate (Admin forwarded Aarav to Google Cloud) ---
        val shared1 = SharedCandidateEntity(
            id = "shared_1",
            recruiterId = "recruiter_1",
            studentId = "student_default",
            projectId = "proj_1",
            adminNotes = "Candidate demonstrates rare hands-on mastery in log storage engines and low-level Go/Kotlin systems. High overall score (93/100) and Smart India Hackathon Winner.",
            recruiterStatus = "SHORTLISTED"
        )
        dao.insertSharedCandidate(shared1)

        // --- 9. Seed Connections ---
        dao.insertConnection(
            ConnectionEntity(
                id = "conn_1",
                requesterStudentId = "student_2",
                targetStudentId = "student_default",
                status = "ACCEPTED"
            )
        )
        dao.insertConnection(
            ConnectionEntity(
                id = "conn_2",
                requesterStudentId = "student_3",
                targetStudentId = "student_default",
                status = "PENDING"
            )
        )

        // --- 10. Seed Notifications ---
        dao.insertNotification(
            NotificationEntity(
                id = "notif_1",
                recipientRole = "STUDENT",
                recipientId = "student_default",
                title = "Feedback Released: Chronos Engine",
                message = "The administrator has reviewed and released your assessment report for 'Chronos: Distributed Log Engine'. View your radar breakdown now.",
                isRead = false
            )
        )
        dao.insertNotification(
            NotificationEntity(
                id = "notif_2",
                recipientRole = "STUDENT",
                recipientId = "student_default",
                title = "Profile Forwarded to Google Cloud",
                message = "Your approved portfolio and project evaluation were shortlisted and shared with Google Cloud for Distributed Systems Intern.",
                isRead = false
            )
        )
    }

    // --- Helpers ---
    fun normalizeProjectTitle(title: String): String {
        return title.trim().lowercase().replace(Regex("""\s+"""), " ")
    }

    // --- Student Actions ---
    suspend fun submitNewProject(
        studentId: String,
        title: String,
        githubUrl: String
    ): Result<ProjectEntity> = withContext(Dispatchers.IO) {
        val trimmedTitle = title.trim()
        if (trimmedTitle.length < 3) {
            return@withContext Result.failure(IllegalArgumentException("Project title must be at least 3 characters long."))
        }

        val normalized = normalizeProjectTitle(trimmedTitle)

        // 1. Strict Global Uniqueness check
        val existing = dao.getProjectByNormalizedTitle(normalized)
        if (existing != null) {
            return@withContext Result.failure(
                IllegalStateException("A project with the title '$trimmedTitle' already exists globally on the platform. Please specify a unique project name.")
            )
        }

        // 2. Validate URL and retrieve actual repository content
        val analysis = GitHubService.analyzeRepository(githubUrl)
        if (!analysis.isValid) {
            return@withContext Result.failure(
                IllegalArgumentException(analysis.errorMessage ?: "Repository could not be accessed. Please verify the URL is public and valid.")
            )
        }

        val projectId = "proj_" + UUID.randomUUID().toString().take(8)
        val project = ProjectEntity(
            id = projectId,
            studentId = studentId,
            title = trimmedTitle,
            normalizedTitle = normalized,
            githubRepoUrl = "https://github.com/${analysis.owner}/${analysis.repoName}",
            languages = analysis.languagesSummary,
            repoDescription = analysis.description,
            readmeSnippet = analysis.readmeSnippet,
            fileStructureSummary = analysis.fileStructureSummary,
            starsCount = analysis.stars,
            defaultBranch = analysis.defaultBranch,
            status = "INTERVIEW_PENDING",
            submittedAt = System.currentTimeMillis()
        )
        dao.insertProject(project)

        // 3. Generate tailored repository-specific technical questions
        val generatedQuestions = GeminiAiService.generateRepositoryQuestions(
            projectTitle = trimmedTitle,
            repoUrl = project.githubRepoUrl,
            languages = analysis.languagesSummary,
            description = analysis.description,
            readmeSnippet = analysis.readmeSnippet,
            fileStructure = analysis.fileStructureSummary
        )

        val questionEntities = generatedQuestions.mapIndexed { index, q ->
            InterviewQuestionEntity(
                id = "q_${projectId}_${index + 1}",
                projectId = projectId,
                studentId = studentId,
                questionIndex = index + 1,
                questionText = q.questionText,
                category = q.category,
                difficulty = q.difficulty,
                rationale = q.rationale,
                studentAnswer = "",
                isVoiceTranscription = false,
                isAnswered = false
            )
        }
        dao.insertInterviewQuestions(questionEntities)

        Result.success(project)
    }

    suspend fun saveInterviewAnswer(
        questionId: String,
        answer: String,
        isVoice: Boolean
    ) = withContext(Dispatchers.IO) {
        val questions = dao.getQuestionsForProject(questionId.substringBeforeLast("_").removePrefix("q_"))
        val question = questions.find { it.id == questionId }
        if (question != null) {
            val updated = question.copy(
                studentAnswer = answer,
                isVoiceTranscription = isVoice,
                isAnswered = answer.isNotBlank(),
                answeredAt = System.currentTimeMillis()
            )
            dao.updateInterviewQuestion(updated)
        }
    }

    suspend fun completeInterviewAndEvaluate(
        projectId: String,
        studentId: String
    ): AssessmentReportEntity = withContext(Dispatchers.IO) {
        val project = dao.getProjectById(projectId) ?: throw IllegalStateException("Project not found")
        val questions = dao.getQuestionsForProject(projectId)

        val qaPairs = questions.map {
            Pair(
                com.example.network.GeneratedInterviewQuestion(
                    questionText = it.questionText,
                    category = it.category,
                    difficulty = it.difficulty,
                    rationale = it.rationale
                ),
                it.studentAnswer
            )
        }

        // AI evaluation against strict rubric
        val assessment = GeminiAiService.evaluateInterviewAnswers(
            projectTitle = project.title,
            repoDescription = project.repoDescription,
            questionsAndAnswers = qaPairs
        )

        val reportId = "rep_" + UUID.randomUUID().toString().take(8)
        val report = AssessmentReportEntity(
            id = reportId,
            projectId = projectId,
            studentId = studentId,
            technicalUnderstandingScore = assessment.technicalUnderstandingScore,
            implementationKnowledgeScore = assessment.implementationKnowledgeScore,
            problemSolvingScore = assessment.problemSolvingScore,
            architectureScore = assessment.architectureScore,
            algorithmsScore = assessment.algorithmsScore,
            debuggingEdgeCasesScore = assessment.debuggingEdgeCasesScore,
            explanationAbilityScore = assessment.explanationAbilityScore,
            communicationClarityScore = assessment.communicationClarityScore,
            overallScore = assessment.overallScore,
            strengths = assessment.strengths,
            weaknesses = assessment.weaknesses,
            recommendations = assessment.recommendations,
            integrityObservations = assessment.integrityObservations,
            feedbackSharedWithStudent = false, // Admin controlled!
            adminRemarks = "Automated AI evaluation recorded. Awaiting administrator review.",
            evaluatedAt = System.currentTimeMillis()
        )
        dao.insertAssessmentReport(report)

        // Update project status to under admin review
        dao.updateProject(
            project.copy(
                status = "UNDER_ADMIN_REVIEW",
                lastEvaluatedAt = System.currentTimeMillis()
            )
        )

        // Notify student that project is under review
        dao.insertNotification(
            NotificationEntity(
                id = UUID.randomUUID().toString(),
                recipientRole = "STUDENT",
                recipientId = studentId,
                title = "Interview Submitted",
                message = "Your profile and project assessment for '${project.title}' are under review. An update will be sent when there is progress."
            )
        )

        report
    }

    // --- Admin Actions ---
    suspend fun releaseFeedbackToStudent(reportId: String, adminRemarks: String) = withContext(Dispatchers.IO) {
        val report = dao.getAllReportsFlow() // or find
        dao.shareFeedbackWithStudent(reportId, true, adminRemarks, System.currentTimeMillis())
        // Also update project status to EVALUATION_COMPLETED
        // Retrieve report to get project
        val all = dao.getAllReportsFlow()
        // Send notification to student
        dao.insertNotification(
            NotificationEntity(
                id = UUID.randomUUID().toString(),
                recipientRole = "STUDENT",
                recipientId = "student_default", // or target
                title = "Feedback Shared",
                message = "Administrator released evaluation report and radar score breakdown. Remarks: $adminRemarks"
            )
        )
    }

    suspend fun forwardCandidateToRecruiter(
        recruiterId: String,
        studentId: String,
        projectId: String,
        adminNotes: String
    ) = withContext(Dispatchers.IO) {
        val shared = SharedCandidateEntity(
            id = "shared_" + UUID.randomUUID().toString().take(8),
            recruiterId = recruiterId,
            studentId = studentId,
            projectId = projectId,
            adminNotes = adminNotes,
            sharedAt = System.currentTimeMillis()
        )
        dao.insertSharedCandidate(shared)

        // Update project status
        val project = dao.getProjectById(projectId)
        if (project != null) {
            dao.updateProject(project.copy(status = "SHARED_WITH_RECRUITER"))
        }

        // Notify recruiter
        dao.insertNotification(
            NotificationEntity(
                id = UUID.randomUUID().toString(),
                recipientRole = "RECRUITER",
                recipientId = recruiterId,
                title = "New Candidate Shared",
                message = "Admin forwarded student candidate for project '${project?.title ?: "Submission"}' with evaluation notes."
            )
        )
    }

    suspend fun createRecruiterAccount(
        fullName: String,
        companyName: String,
        position: String,
        email: String
    ): Result<RecruiterEntity> = withContext(Dispatchers.IO) {
        val existing = dao.getRecruiterByEmail(email.trim())
        if (existing != null) {
            return@withContext Result.failure(IllegalStateException("A recruiter account with email '$email' already exists."))
        }
        val tempPassword = "TEMP-" + companyName.take(3).uppercase() + "-" + (1000..9999).random()
        val recruiter = RecruiterEntity(
            id = "recruiter_" + UUID.randomUUID().toString().take(8),
            fullName = fullName.trim(),
            companyName = companyName.trim(),
            position = position.trim(),
            email = email.trim(),
            temporaryPassword = tempPassword,
            permanentPasswordEstablished = false
        )
        dao.insertRecruiter(recruiter)
        Result.success(recruiter)
    }

    suspend fun dispatchAdminCampaign(
        subject: String,
        body: String,
        recipientFilter: String,
        recipientCount: Int,
        channel: String,
        templateName: String
    ) = withContext(Dispatchers.IO) {
        val campaign = CampaignMessageEntity(
            id = "camp_" + UUID.randomUUID().toString().take(8),
            subject = subject,
            body = body,
            recipientFilter = recipientFilter,
            recipientCount = recipientCount,
            channel = channel,
            templateName = templateName,
            deliveryOutcome = "DELIVERED",
            sentAt = System.currentTimeMillis()
        )
        dao.insertCampaignMessage(campaign)

        // Dispatch in-app notification to all students
        dao.insertNotification(
            NotificationEntity(
                id = UUID.randomUUID().toString(),
                recipientRole = "STUDENT",
                recipientId = "ALL",
                title = subject,
                message = body.take(160),
                timestamp = System.currentTimeMillis()
            )
        )
    }
}
