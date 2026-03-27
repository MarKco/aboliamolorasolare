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
}