package com.example.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserEntity::class, InternshipMatchEntity::class, AssessmentScoreEntity::class, CachedAssessmentEntity::class], version = 2, exportSchema = false)
abstract class ProofolioDatabase : RoomDatabase() {
    abstract fun proofolioDao(): ProofolioDao

    companion object {
        @Volatile
        private var INSTANCE: ProofolioDatabase? = null

        fun getDatabase(context: Context): ProofolioDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ProofolioDatabase::class.java,
                    "proofolio_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
