package com.ilsecondodasinistra.lancette.repository

/**
 * Implementazione fake in memoria per TDD/test
 */
class InMemorySettingsRepository : SettingsRepository {
    private var x: Int = 5
    private var y: Int = 10
    
    private var globalNotificationsEnabled: Boolean = true
    private var notifyXEnabled: Boolean = true
    private var notifyYEnabled: Boolean = true
    private var notifyZEnabled: Boolean = true
    private var fastNotificationsEnabled: Boolean = false

    override suspend fun getX(): Int = x
    override suspend fun getY(): Int = y
    override suspend fun setX(x: Int) { this.x = x }
    override suspend fun setY(y: Int) { this.y = y }

    override suspend fun isGlobalNotificationsEnabled(): Boolean = globalNotificationsEnabled
    override suspend fun setGlobalNotificationsEnabled(enabled: Boolean) { this.globalNotificationsEnabled = enabled }

    override suspend fun isNotifyXEnabled(): Boolean = notifyXEnabled
    override suspend fun setNotifyXEnabled(enabled: Boolean) { this.notifyXEnabled = enabled }

    override suspend fun isNotifyYEnabled(): Boolean = notifyYEnabled
    override suspend fun setNotifyYEnabled(enabled: Boolean) { this.notifyYEnabled = enabled }

    override suspend fun isNotifyZEnabled(): Boolean = notifyZEnabled
    override suspend fun setNotifyZEnabled(enabled: Boolean) { this.notifyZEnabled = enabled }

    override suspend fun isFastNotificationsEnabled(): Boolean = fastNotificationsEnabled
    override suspend fun setFastNotificationsEnabled(enabled: Boolean) { this.fastNotificationsEnabled = enabled }
}
