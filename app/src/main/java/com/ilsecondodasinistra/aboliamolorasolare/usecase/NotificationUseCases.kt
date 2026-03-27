package com.ilsecondodasinistra.aboliamolorasolare.usecase

import com.ilsecondodasinistra.aboliamolorasolare.model.NotificationSetting
import com.ilsecondodasinistra.aboliamolorasolare.model.TimeChangeEventId
import com.ilsecondodasinistra.aboliamolorasolare.repository.NotificationRepository

class GetNotificationSettingsUseCase(private val repo: NotificationRepository) {
    suspend fun execute(): List<NotificationSetting> = repo.getNotificationSettings()
}

class SetNotificationSettingUseCase(private val repo: NotificationRepository) {
    suspend fun execute(setting: NotificationSetting) = repo.setNotificationSetting(setting)
}

class RemoveNotificationSettingUseCase(private val repo: NotificationRepository) {
    suspend fun execute(eventId: TimeChangeEventId) = repo.removeNotificationSetting(eventId)
}

