package com.ilsecondodasinistra.aboliamolorasolare.ui

import com.ilsecondodasinistra.aboliamolorasolare.TimeChangeCalculator
import com.ilsecondodasinistra.aboliamolorasolare.model.NotificationSetting
import com.ilsecondodasinistra.aboliamolorasolare.model.TimeChangeEventId
import com.ilsecondodasinistra.aboliamolorasolare.repository.InMemoryNotificationRepository
import com.ilsecondodasinistra.aboliamolorasolare.repository.InMemorySettingsRepository
import com.ilsecondodasinistra.aboliamolorasolare.usecase.GetNotificationSettingsUseCase
import com.ilsecondodasinistra.aboliamolorasolare.usecase.GetSettingsUseCase
import com.ilsecondodasinistra.aboliamolorasolare.usecase.GetTimeChangesUseCase
import com.ilsecondodasinistra.aboliamolorasolare.usecase.RemoveNotificationSettingUseCase
import com.ilsecondodasinistra.aboliamolorasolare.usecase.SetNotificationSettingUseCase
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
import java.time.LocalDate

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
        viewModel.load(LocalDate.of(2026, 3, 27))
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(5, viewModel.x.value)
        assertEquals(10, viewModel.y.value)
        assertEquals(4, viewModel.timeChanges.value?.next?.size)
        assertEquals(4, viewModel.timeChanges.value?.previous?.size)
    }

    @Test
    fun `set and remove notification updates state`() = testScope.runTest {
        viewModel.load(LocalDate.of(2026, 3, 27))
        val eventId = TimeChangeEventId(LocalDate.of(2026, 3, 29), "LEGALE")
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
