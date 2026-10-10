package com.example.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val email: String,
    val name: String,
    val passwordHash: String,
    val college: String,
    val githubUsername: String,
    val createdAt: Long = System.currentTimeMillis()
)
