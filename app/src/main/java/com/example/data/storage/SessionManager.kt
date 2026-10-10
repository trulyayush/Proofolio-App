package com.example.data.storage

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages persistent authenticated session state for the Student role.
 * Persists session across application restarts and device reboots.
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("nexus_intern_auth_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ACTIVE_STUDENT_ID = "active_student_id"
        private const val KEY_ACTIVE_STUDENT_EMAIL = "active_student_email"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    fun saveActiveSession(studentId: String, email: String) {
        prefs.edit()
            .putString(KEY_ACTIVE_STUDENT_ID, studentId)
            .putString(KEY_ACTIVE_STUDENT_EMAIL, email)
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .apply()
    }

    fun getActiveStudentId(): String? {
        return if (isLoggedIn()) {
            prefs.getString(KEY_ACTIVE_STUDENT_ID, null)
        } else null
    }

    fun getActiveStudentEmail(): String? {
        return if (isLoggedIn()) {
            prefs.getString(KEY_ACTIVE_STUDENT_EMAIL, null)
        } else null
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false) && !prefs.getString(KEY_ACTIVE_STUDENT_ID, null).isNullOrBlank()
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
