package com.example.tasklocker

import android.content.Context
import android.content.SharedPreferences

object TaskManager {
    private const val PREF_NAME = "task_locker_prefs"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun isAppLocked(context: Context, packageName: String): Boolean {
        // Örnek: com.instagram.android uygulaması taskId: 1'e bağlı
        if (packageName == "com.instagram.android") {
            return !getPrefs(context).getBoolean("task_1_completed", false)
        }
        return false
    }

    fun markTaskCompleted(context: Context, taskId: Int) {
        getPrefs(context).edit().putBoolean("task_${taskId}_completed", true).apply()
    }

    fun resetDailyTasks(context: Context) {
        getPrefs(context).edit().clear().apply()
    }
}
