package com.ilsecondodasinistra.aboliamolorasolare.repository

import com.ilsecondodasinistra.aboliamolorasolare.model.NotificationSetting
import com.ilsecondodasinistra.aboliamolorasolare.model.TimeChangeEventId

/**
 * Implementazione fake in memoria per TDD/test
 */
class InMemoryNotificationRepository : NotificationRepository {
    private val settings = mutableListOf<NotificationSetting>()
    override suspend fun getNotificationSettings(): List<NotificationSetting> = settings.toList()
    override suspend fun setNotificationSetting(setting: NotificationSetting) {
        settings.removeAll { it.eventId == setting.eventId }
        settings.add(setting)
    }
    override suspend fun removeNotificationSetting(eventId: TimeChangeEventId) {
        settings.removeAll { it.eventId == eventId }
    }
}

