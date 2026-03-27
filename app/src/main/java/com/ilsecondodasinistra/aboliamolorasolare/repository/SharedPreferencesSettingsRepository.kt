package com.ilsecondodasinistra.aboliamolorasolare.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SharedPreferencesSettingsRepository(context: Context) : SettingsRepository {
    private val prefs: SharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    override suspend fun getX(): Int = prefs.getInt("x_days", 5)
    override suspend fun getY(): Int = prefs.getInt("y_days", 10)
    
    override suspend fun setX(x: Int) {
        prefs.edit { putInt("x_days", x) }
    }
    
    override suspend fun setY(y: Int) {
        prefs.edit { putInt("y_days", y) }
    }

    override suspend fun isGlobalNotificationsEnabled(): Boolean = prefs.getBoolean("global_enabled", false)
    override suspend fun setGlobalNotificationsEnabled(enabled: Boolean) {
        prefs.edit { putBoolean("global_enabled", enabled) }
    }

    override suspend fun isNotifyXEnabled(): Boolean = prefs.getBoolean("notify_x_enabled", true)
    override suspend fun setNotifyXEnabled(enabled: Boolean) {
        prefs.edit { putBoolean("notify_x_enabled", enabled) }
    }

    override suspend fun isNotifyYEnabled(): Boolean = prefs.getBoolean("notify_y_enabled", true)
    override suspend fun setNotifyYEnabled(enabled: Boolean) {
        prefs.edit { putBoolean("notify_y_enabled", enabled) }
    }

    override suspend fun isNotifyZEnabled(): Boolean = prefs.getBoolean("notify_z_enabled", true)
    override suspend fun setNotifyZEnabled(enabled: Boolean) {
        prefs.edit { putBoolean("notify_z_enabled", enabled) }
    }

    override suspend fun isFastNotificationsEnabled(): Boolean = prefs.getBoolean("fast_notifications_enabled", false)
    override suspend fun setFastNotificationsEnabled(enabled: Boolean) {
        prefs.edit { putBoolean("fast_notifications_enabled", enabled) }
    }
}
