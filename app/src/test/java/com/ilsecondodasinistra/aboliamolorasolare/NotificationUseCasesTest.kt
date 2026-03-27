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
        val eventId = TimeChangeEventId(java.time.LocalDate.of(2026, 3, 29), "LEGALE")
        val setting = NotificationSetting(eventId, notifyX = true)
        setUseCase.execute(setting)
        val all = getUseCase.execute()
        assertEquals(1, all.size)
        assertEquals(true, all[0].notifyX)
        removeUseCase.execute(eventId)
        assertEquals(0, getUseCase.execute().size)
    }
}

