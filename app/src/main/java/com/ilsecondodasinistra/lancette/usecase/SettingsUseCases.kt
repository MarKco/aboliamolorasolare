package com.ilsecondodasinistra.lancette.usecase

import com.ilsecondodasinistra.lancette.repository.SettingsRepository

class GetSettingsUseCase(private val repo: SettingsRepository) {
    suspend fun getX(): Int = repo.getX()
    suspend fun getY(): Int = repo.getY()
    suspend fun isGlobalNotificationsEnabled(): Boolean = repo.isGlobalNotificationsEnabled()
    suspend fun isNotifyXEnabled(): Boolean = repo.isNotifyXEnabled()
    suspend fun isNotifyYEnabled(): Boolean = repo.isNotifyYEnabled()
    suspend fun isNotifyZEnabled(): Boolean = repo.isNotifyZEnabled()
    suspend fun isFastNotificationsEnabled(): Boolean = repo.isFastNotificationsEnabled()
    
    // Per convenienza nel MainViewModel lasciamo questo qui
    suspend fun setGlobalNotificationsEnabled(enabled: Boolean) = repo.setGlobalNotificationsEnabled(enabled)
}

class SetSettingsUseCase(private val repo: SettingsRepository) {
    suspend fun setX(x: Int) = repo.setX(x)
    suspend fun setY(y: Int) = repo.setY(y)
    suspend fun setGlobalNotificationsEnabled(enabled: Boolean) = repo.setGlobalNotificationsEnabled(enabled)
    suspend fun setNotifyXEnabled(enabled: Boolean) = repo.setNotifyXEnabled(enabled)
    suspend fun setNotifyYEnabled(enabled: Boolean) = repo.setNotifyYEnabled(enabled)
    suspend fun setNotifyZEnabled(enabled: Boolean) = repo.setNotifyZEnabled(enabled)
    suspend fun setFastNotificationsEnabled(enabled: Boolean) = repo.setFastNotificationsEnabled(enabled)
}
