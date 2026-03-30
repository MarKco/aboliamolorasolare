package com.ilsecondodasinistra.lancette.ui

import com.ilsecondodasinistra.lancette.repository.InMemorySettingsRepository
import com.ilsecondodasinistra.lancette.usecase.GetSettingsUseCase
import com.ilsecondodasinistra.lancette.usecase.SetSettingsUseCase
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
class SettingsViewModelTest {
    private lateinit var viewModel: SettingsViewModel
    private lateinit var repo: InMemorySettingsRepository
    private val testDispatcher = StandardTestDispatcher(TestCoroutineScheduler())
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repo = InMemorySettingsRepository()
        viewModel = SettingsViewModel(
            GetSettingsUseCase(repo),
            SetSettingsUseCase(repo)
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `load exposes default X and Y`() = testScope.runTest {
        viewModel.load()
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(5, viewModel.x.value)
        assertEquals(10, viewModel.y.value)
    }

    @Test
    fun `setX and setY update values`() = testScope.runTest {
        viewModel.load()
        viewModel.setX(7)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(7, viewModel.x.value)
        viewModel.setY(12)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(12, viewModel.y.value)
    }
}
