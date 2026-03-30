package com.ilsecondodasinistra.lancette.ui

import com.ilsecondodasinistra.lancette.TimeChangeCalculator
import com.ilsecondodasinistra.lancette.model.NotificationSetting
import com.ilsecondodasinistra.lancette.model.TimeChangeEventId
import com.ilsecondodasinistra.lancette.repository.InMemoryNotificationRepository
import com.ilsecondodasinistra.lancette.repository.InMemorySettingsRepository
import com.ilsecondodasinistra.lancette.usecase.GetNotificationSettingsUseCase
import com.ilsecondodasinistra.lancette.usecase.GetSettingsUseCase
import com.ilsecondodasinistra.lancette.usecase.GetTimeChangesUseCase
import com.ilsecondodasinistra.lancette.usecase.RemoveNotificationSettingUseCase
import com.ilsecondodasinistra.lancette.usecase.SetNotificationSettingUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
    private lateinit var viewModel: MainViewModel
    private lateinit var notificationRepo: InMemoryNotificationRepository
    private lateinit var settingsRepo: InMemorySettingsRepository
    private val testDispatcher = StandardTestDispatcher(TestCoroutineScheduler())
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        notificationRepo = InMemoryNotificationRepository()
        settingsRepo = InMemorySettingsRepository()
        viewModel = MainViewModel(
            GetTimeChangesUseCase(TimeChangeCalculator()),
            GetNotificationSettingsUseCase(notificationRepo),
            SetNotificationSettingUseCase(notificationRepo),
            RemoveNotificationSettingUseCase(notificationRepo),
            GetSettingsUseCase(settingsRepo)
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load exposes correct time changes and settings`() = testScope.runTest {
        val baseDate = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.YEAR, 2026)
            set(java.util.Calendar.MONTH, 2) // Marzo: 0-based
            set(java.util.Calendar.DAY_OF_MONTH, 27)
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        viewModel.load(baseDate)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(5, viewModel.x.value)
        assertEquals(10, viewModel.y.value)
        assertEquals(4, viewModel.timeChanges.value?.next?.size)
        assertEquals(4, viewModel.timeChanges.value?.previous?.size)
    }

    @Test
    fun `set and remove notification updates state`() = testScope.runTest {
        val baseDate = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.YEAR, 2026)
            set(java.util.Calendar.MONTH, 2) // Marzo: 0-based
            set(java.util.Calendar.DAY_OF_MONTH, 27)
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        viewModel.load(baseDate)
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
        viewModel.setNotification(setting)
        // Simulate coroutine completion
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(1, viewModel.notificationSettings.value.size)
        viewModel.removeNotification(eventId)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(0, viewModel.notificationSettings.value.size)
    }
}
