package com.ilsecondodasinistra.aboliamolorasolare

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class TimeChangeCalculatorTest {
    @Test
    fun `calcola i prossimi 4 cambi dell'ora a partire da una data qualsiasi`() {
        val calculator = TimeChangeCalculator()
        val baseDate = LocalDate.of(2026, 3, 27) // Data attuale
        val events = calculator.getTimeChanges(baseDate)
        // Verifica che i primi 4 eventi siano i prossimi cambi
        assertEquals(4, events.next.size)
        // Primo evento: 29 marzo 2026 (ultima domenica di marzo)
        assertEquals(LocalDate.of(2026, 3, 29), events.next[0].date)
        assertEquals(TimeChangeType.LEGALE, events.next[0].type)
        assertEquals(TimeChangeDirection.AVANTI, events.next[0].direction)
        // Secondo evento: 25 ottobre 2026 (ultima domenica di ottobre)
        assertEquals(LocalDate.of(2026, 10, 25), events.next[1].date)
        assertEquals(TimeChangeType.SOLARE, events.next[1].type)
        assertEquals(TimeChangeDirection.INDIETRO, events.next[1].direction)
        // Terzo evento: 28 marzo 2027
        assertEquals(LocalDate.of(2027, 3, 28), events.next[2].date)
        // Quarto evento: 31 ottobre 2027
        assertEquals(LocalDate.of(2027, 10, 31), events.next[3].date)
    }

    @Test
    fun `calcola gli ultimi 4 cambi dell'ora rispetto a una data`() {
        val calculator = TimeChangeCalculator()
        val baseDate = LocalDate.of(2026, 3, 27)
        val events = calculator.getTimeChanges(baseDate)
        // Verifica che gli ultimi 4 eventi siano corretti
        assertEquals(4, events.previous.size)
        // Ultimo evento: 26 ottobre 2025
        assertEquals(LocalDate.of(2025, 10, 26), events.previous[3].date)
        // Penultimo evento: 30 marzo 2025
        assertEquals(LocalDate.of(2025, 3, 30), events.previous[2].date)
        // ...
    }

    @Test
    fun `verifica edge case per anni bisestili`() {
        val calculator = TimeChangeCalculator()
        val baseDate = LocalDate.of(2024, 3, 25)
        val events = calculator.getTimeChanges(baseDate)
        // Primo evento: 31 marzo 2024 (ultima domenica di marzo)
        assertEquals(LocalDate.of(2024, 3, 31), events.next[0].date)
        // Secondo evento: 27 ottobre 2024
        assertEquals(LocalDate.of(2024, 10, 27), events.next[1].date)
    }
}

