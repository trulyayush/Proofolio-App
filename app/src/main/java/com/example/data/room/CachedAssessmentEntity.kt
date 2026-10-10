package com.example.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_assessments")
data class CachedAssessmentEntity(
    @PrimaryKey
    val studentEmail: String,
    val reportJson: String,
    val cachedAt: Long = System.currentTimeMillis()
)
