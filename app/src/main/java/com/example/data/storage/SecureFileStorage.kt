package com.example.data.storage

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

/**
 * Manages private, sandboxed file storage for resumes and certificates.
 * Files are kept strictly in private application storage (context.filesDir)
 * ensuring only authenticated sessions on device can access them, with zero public exposure.
 */
class SecureFileStorage(private val context: Context) {

    private val resumesDir = File(context.filesDir, "resumes").apply { if (!exists()) mkdirs() }
    private val certificatesDir = File(context.filesDir, "certificates").apply { if (!exists()) mkdirs() }

    /**
     * Saves raw bytes or text content securely as a private file.
     */
    suspend fun saveResumeFile(
        studentId: String,
        originalFileName: String,
        content: ByteArray
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val extension = if (originalFileName.contains(".")) {
                originalFileName.substringAfterLast(".").lowercase()
            } else "pdf"

            if (extension !in listOf("pdf", "txt", "doc", "docx")) {
                return@withContext Result.failure(IllegalArgumentException("Unsupported file type .$extension. Allowed: PDF, TXT, DOC, DOCX"))
            }

            if (content.size > 10 * 1024 * 1024) {
                return@withContext Result.failure(IllegalArgumentException("File exceeds maximum allowed size of 10MB."))
            }

            val sanitizedId = studentId.replace(Regex("[^a-zA-Z0-9_-]"), "_")
            val targetFile = File(resumesDir, "resume_${sanitizedId}_${System.currentTimeMillis()}.$extension")
            FileOutputStream(targetFile).use { it.write(content) }
            Result.success(targetFile.absolutePath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveCertificateFile(
        studentId: String,
        hackathonId: String,
        originalFileName: String,
        content: ByteArray
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val extension = if (originalFileName.contains(".")) {
                originalFileName.substringAfterLast(".").lowercase()
            } else "pdf"

            if (content.size > 10 * 1024 * 1024) {
                return@withContext Result.failure(IllegalArgumentException("File exceeds maximum allowed size of 10MB."))
            }

            val sanitizedId = studentId.replace(Regex("[^a-zA-Z0-9_-]"), "_")
            val sanitizedHackId = hackathonId.replace(Regex("[^a-zA-Z0-9_-]"), "_")
            val targetFile = File(certificatesDir, "cert_${sanitizedId}_${sanitizedHackId}.$extension")
            FileOutputStream(targetFile).use { it.write(content) }
            Result.success(targetFile.absolutePath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun readResumeContentAsText(filePath: String): String = withContext(Dispatchers.IO) {
        try {
            val file = File(filePath)
            if (file.exists() && file.isFile) {
                file.readText()
            } else ""
        } catch (e: Exception) {
            ""
        }
    }

    fun fileExists(filePath: String): Boolean {
        if (filePath.isBlank()) return false
        val file = File(filePath)
        return file.exists() && file.isFile
    }

    fun getFileName(filePath: String): String {
        if (filePath.isBlank()) return ""
        return File(filePath).name
    }
}
