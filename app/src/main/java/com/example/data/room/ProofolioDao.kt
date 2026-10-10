package com.example.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProofolioDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUser(email: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM internship_matches WHERE studentEmail = :studentEmail")
    fun getInternshipMatches(studentEmail: String): Flow<List<InternshipMatchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInternshipMatches(matches: List<InternshipMatchEntity>)

    @Query("UPDATE internship_matches SET applied = :applied WHERE id = :matchId")
    suspend fun updateMatchApplicationStatus(matchId: String, applied: Boolean)

    @Query("SELECT * FROM assessment_scores WHERE studentEmail = :studentEmail ORDER BY timestamp DESC")
    fun getAssessmentScores(studentEmail: String): Flow<List<AssessmentScoreEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssessmentScore(score: AssessmentScoreEntity)

    @Query("SELECT * FROM cached_assessments WHERE studentEmail = :email LIMIT 1")
    suspend fun getCachedAssessment(email: String): CachedAssessmentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedAssessment(assessment: CachedAssessmentEntity)
}
