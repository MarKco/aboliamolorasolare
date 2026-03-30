package com.ilsecondodasinistra.lancette.usecase

import com.ilsecondodasinistra.lancette.repository.InMemorySettingsRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class SettingsUseCasesTest {
    @Test
    fun `default e modifica valori X e Y`() = runBlocking {
        val repo = InMemorySettingsRepository()
        val getUseCase = GetSettingsUseCase(repo)
        val setUseCase = SetSettingsUseCase(repo)
        // Default
        assertEquals(5, getUseCase.getX())
        assertEquals(10, getUseCase.getY())
        // Modifica
        setUseCase.setX(7)
        setUseCase.setY(12)
        assertEquals(7, getUseCase.getX())
        assertEquals(12, getUseCase.getY())
    }
}

