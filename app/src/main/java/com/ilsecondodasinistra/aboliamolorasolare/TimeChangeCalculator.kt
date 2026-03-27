package com.ilsecondodasinistra.aboliamolorasolare

import java.time.LocalDate

enum class TimeChangeType { LEGALE, SOLARE }
enum class TimeChangeDirection { AVANTI, INDIETRO }

data class TimeChangeEvent(
    val date: LocalDate,
    val type: TimeChangeType,
    val direction: TimeChangeDirection
)

data class TimeChangeResult(
    val previous: List<TimeChangeEvent>,
    val next: List<TimeChangeEvent>
)

class TimeChangeCalculator {
    fun getTimeChanges(baseDate: LocalDate): TimeChangeResult {
        // Trova i cambi d'ora per un range di anni attorno alla data base
        val years = (baseDate.year - 10)..(baseDate.year + 10)
        val allEvents = years.flatMap { year ->
            listOf(
                // Ultima domenica di marzo: ora legale (avanti)
                TimeChangeEvent(
                    date = lastSundayOfMonth(year, 3),
                    type = TimeChangeType.LEGALE,
                    direction = TimeChangeDirection.AVANTI
                ),
                // Ultima domenica di ottobre: ora solare (indietro)
                TimeChangeEvent(
                    date = lastSundayOfMonth(year, 10),
                    type = TimeChangeType.SOLARE,
                    direction = TimeChangeDirection.INDIETRO
                )
            )
        }.sortedBy { it.date }

        val previous = allEvents.filter { it.date.isBefore(baseDate) }.takeLast(4)
        val next = allEvents.filter { !it.date.isBefore(baseDate) }.take(4)
        return TimeChangeResult(previous = previous, next = next)
    }

    private fun lastSundayOfMonth(year: Int, month: Int): LocalDate {
        // Trova l'ultimo giorno del mese
        var date = LocalDate.of(year, month, 1).withDayOfMonth(LocalDate.of(year, month, 1).lengthOfMonth())
        // Torna indietro fino a domenica
        while (date.dayOfWeek.value != 7) { // 7 = Sunday
            date = date.minusDays(1)
        }
        return date
    }
}
