package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.AppDatabase
import com.example.data.model.AssessmentReportEntity
import com.example.data.model.ProjectEntity
import com.example.data.model.StudentEntity
import com.example.data.repository.AppRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class NexusInternPlatformTest {

    private lateinit var context: Context
    private lateinit var repository: AppRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        AppDatabase.closeAndResetForTesting()
        repository = AppRepository(context)
    }

    @After
    fun tearDown() {
        AppDatabase.closeAndResetForTesting()
    }

    @Test
    fun testSeedDataInitialization() = runBlocking {
        repository.initializeSeedDataIfNeeded()

        val student = repository.dao.getStudentById("student_default")
        assertNotNull(student)
        assertEquals("Aarav Deshmukh", student?.fullName)
        assertTrue(student?.isEmailVerified == true)

        val projects = repository.dao.getProjectsForStudentFlow("student_default").first()
        assertTrue(projects.isNotEmpty())
        assertEquals("Chronos: Distributed Log Engine", projects.first().title)
    }

    @Test
    fun testProjectTitleNormalizationAndUniqueness() = runBlocking {
        repository.initializeSeedDataIfNeeded()

        val normalized = repository.normalizeProjectTitle("   CHRONOS:   distributed   log   ENGINE  ")
        assertEquals("chronos: distributed log engine", normalized)

        // Attempt to submit project with duplicate normalized title
        val duplicateResult = repository.submitNewProject(
            studentId = "student_default",
            title = "Chronos: Distributed Log Engine",
            githubUrl = "https://github.com/aarav-deshmukh/chronos-engine"
        )

        assertTrue(duplicateResult.isFailure)
        assertTrue(
            duplicateResult.exceptionOrNull()?.message?.contains("already exists globally", ignoreCase = true) == true
        )
    }

    @Test
    fun testAdminReleaseFeedbackToStudent() = runBlocking {
        repository.initializeSeedDataIfNeeded()

        val reportId = "rep_2"
        val initialReport = repository.dao.getAllReportsFlow().first().find { it.id == reportId }
        assertNotNull(initialReport)
        assertFalse(initialReport!!.feedbackSharedWithStudent)

        // Admin releases feedback
        repository.releaseFeedbackToStudent(reportId, "Approved after committee audit.")

        val updatedReport = repository.dao.getAllReportsFlow().first().find { it.id == reportId }
        assertTrue(updatedReport!!.feedbackSharedWithStudent)
        assertEquals("Approved after committee audit.", updatedReport.adminRemarks)
    }

    @Test
    fun testRecruiterProvisioning() = runBlocking {
        val result = repository.createRecruiterAccount(
            fullName = "Anand Mehra",
            companyName = "Microsoft",
            position = "Campus Talent Lead",
            email = "anand.m@microsoft.com"
        )

        assertTrue(result.isSuccess)
        val recruiter = result.getOrNull()
        assertNotNull(recruiter)
        assertEquals("Microsoft", recruiter?.companyName)
        assertTrue(recruiter?.temporaryPassword?.startsWith("TEMP-") == true)
        assertFalse(recruiter!!.permanentPasswordEstablished)
    }
}
