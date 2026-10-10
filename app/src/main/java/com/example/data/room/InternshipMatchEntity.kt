package com.example.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "internship_matches")
data class InternshipMatchEntity(
    @PrimaryKey
    val id: String,
    val studentEmail: String,
    val title: String,
    val company: String,
    val description: String,
    val matchScore: Int,
    val requirements: String,
    val applied: Boolean = false
)
