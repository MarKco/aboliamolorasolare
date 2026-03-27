package com.ilsecondodasinistra.aboliamolorasolare.repository

import com.ilsecondodasinistra.aboliamolorasolare.model.NotificationSetting
import com.ilsecondodasinistra.aboliamolorasolare.model.TimeChangeEventId

interface NotificationRepository {
    suspend fun getNotificationSettings(): List<NotificationSetting>
    suspend fun setNotificationSetting(setting: NotificationSetting)
    suspend fun removeNotificationSetting(eventId: TimeChangeEventId)
}

