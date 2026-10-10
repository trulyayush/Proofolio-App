package com.example.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assessment_scores")
data class AssessmentScoreEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val studentEmail: String,
    val overallScore: Int,
    val technicalScore: Int,
    val communicationScore: Int,
    val problemSolvingScore: Int,
    val summary: String,
    val timestamp: Long = System.currentTimeMillis()
)
