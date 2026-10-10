package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Real Student Account Credential for persistent authentication and session management.
 * Role is strictly "STUDENT" to prevent privilege escalation to Admin or Recruiter.
 */
@Entity(
    tableName = "student_accounts",
    indices = [Index(value = ["email"], unique = true)]
)
data class StudentAccountEntity(
    @PrimaryKey val id: String, // UUID
    val fullName: String,
    val email: String,
    val passwordHash: String, // SHA-256 hashed password
    val role: String = "STUDENT",
    val isEmailVerified: Boolean = false,
    val verificationCode: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
