package com.ilsecondodasinistra.aboliamolorasolare.repository

interface SettingsRepository {
    suspend fun getX(): Int
    suspend fun getY(): Int
    suspend fun setX(x: Int)
    suspend fun setY(y: Int)

    suspend fun isGlobalNotificationsEnabled(): Boolean
    suspend fun setGlobalNotificationsEnabled(enabled: Boolean)

    suspend fun isNotifyXEnabled(): Boolean
    suspend fun setNotifyXEnabled(enabled: Boolean)

    suspend fun isNotifyYEnabled(): Boolean
    suspend fun setNotifyYEnabled(enabled: Boolean)

    suspend fun isNotifyZEnabled(): Boolean
    suspend fun setNotifyZEnabled(enabled: Boolean)

    // Dev settings
    suspend fun isFastNotificationsEnabled(): Boolean
    suspend fun setFastNotificationsEnabled(enabled: Boolean)
}
