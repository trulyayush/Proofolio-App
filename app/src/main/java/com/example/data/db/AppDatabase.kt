package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.AppDao
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
import com.example.data.model.StudentAccountEntity
import com.example.data.model.StudentEntity

@Database(
    entities = [
        StudentAccountEntity::class,
        StudentEntity::class,
        HackathonEntity::class,
        ProjectEntity::class,
        InterviewQuestionEntity::class,
        AssessmentReportEntity::class,
        ConnectionEntity::class,
        RecruiterEntity::class,
        InternshipRequirementEntity::class,
        SharedCandidateEntity::class,
        CampaignMessageEntity::class,
        NotificationEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nexus_intern_platform.db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun closeAndResetForTesting() {
            INSTANCE?.close()
            INSTANCE = null
        }
    }
}
