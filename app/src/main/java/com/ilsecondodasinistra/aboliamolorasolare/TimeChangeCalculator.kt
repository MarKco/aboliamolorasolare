package com.ilsecondodasinistra.aboliamolorasolare

import java.util.Calendar

enum class TimeChangeType { LEGALE, SOLARE }
enum class TimeChangeDirection { AVANTI, INDIETRO }

data class TimeChangeEvent(
    val date: Calendar,
    val type: TimeChangeType,
    val direction: TimeChangeDirection
)

data class TimeChangeResult(
    val previous: List<TimeChangeEvent>,
    val next: List<TimeChangeEvent>
)

class TimeChangeCalculator {
    fun getTimeChanges(baseDate: Calendar): TimeChangeResult {
        val baseYear = baseDate.get(Calendar.YEAR)
        val years = (baseYear - 10)..(baseYear + 10)
        val allEvents = years.flatMap { year ->
            listOf(
                // Ultima domenica di marzo: ora legale (avanti)
                TimeChangeEvent(
                    date = lastSundayOfMonth(year, 2), // Marzo: month=2 (Calendar)
                    type = TimeChangeType.LEGALE,
                    direction = TimeChangeDirection.AVANTI
                ),
                // Ultima domenica di ottobre: ora solare (indietro)
                TimeChangeEvent(
                    date = lastSundayOfMonth(year, 9), // Ottobre: month=9 (Calendar)
                    type = TimeChangeType.SOLARE,
                    direction = TimeChangeDirection.INDIETRO
                )
            )
        }.sortedBy { it.date.timeInMillis }

        val previous = allEvents.filter { it.date.before(baseDate) }.takeLast(4)
        val next = allEvents.filter { !it.date.before(baseDate) }.take(4)
        return TimeChangeResult(previous = previous, next = next)
    }

    private fun lastSundayOfMonth(year: Int, month: Int): Calendar {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            cal.add(Calendar.DAY_OF_MONTH, -1)
        }
        return cal
    }
}
