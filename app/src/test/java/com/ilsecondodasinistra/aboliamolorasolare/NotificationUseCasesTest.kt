package com.ilsecondodasinistra.aboliamolorasolare.usecase

import com.ilsecondodasinistra.aboliamolorasolare.model.NotificationSetting
import com.ilsecondodasinistra.aboliamolorasolare.model.TimeChangeEventId
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class NotificationUseCasesTest {
    @Test
    fun `aggiunta e rimozione notifica`() = runBlocking {
        val repo = com.ilsecondodasinistra.aboliamolorasolare.repository.InMemoryNotificationRepository()
        val setUseCase = SetNotificationSettingUseCase(repo)
        val getUseCase = GetNotificationSettingsUseCase(repo)
        val removeUseCase = RemoveNotificationSettingUseCase(repo)
        val eventCal = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.YEAR, 2026)
            set(java.util.Calendar.MONTH, 2) // Marzo: 0-based
            set(java.util.Calendar.DAY_OF_MONTH, 29)
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        val eventId = TimeChangeEventId(eventCal, "LEGALE")
        val setting = NotificationSetting(eventId, notifyX = true)
        setUseCase.execute(setting)
        val all = getUseCase.execute()
        assertEquals(1, all.size)
        assertEquals(true, all[0].notifyX)
        removeUseCase.execute(eventId)
        assertEquals(0, getUseCase.execute().size)
    }
}
