package com.ilsecondodasinistra.lancette

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
        // Normalizziamo la baseDate per il confronto sicuro
        val normalizedBaseDate = (baseDate.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        val baseYear = baseDate.get(Calendar.YEAR)
        val years = (baseYear - 3)..(baseYear + 3) // range bilanciato per prendere almeno 5 passati e 5 futuri
        val allEvents = years.flatMap { year ->
            listOf(
                // Ultima domenica di marzo: ora legale (avanti)
                TimeChangeEvent(
                    date = lastSundayOfMonth(year, Calendar.MARCH),
                    type = TimeChangeType.LEGALE,
                    direction = TimeChangeDirection.AVANTI
                ),
                // Ultima domenica di ottobre: ora solare (indietro)
                TimeChangeEvent(
                    date = lastSundayOfMonth(year, Calendar.OCTOBER),
                    type = TimeChangeType.SOLARE,
                    direction = TimeChangeDirection.INDIETRO
                )
            )
        }.sortedBy { it.date.timeInMillis }

        val previous = allEvents.filter { it.date.before(normalizedBaseDate) }.takeLast(5)
        val next = allEvents.filter { !it.date.before(normalizedBaseDate) }.take(5)
        return TimeChangeResult(previous = previous, next = next)
    }

    private fun lastSundayOfMonth(year: Int, month: Int): Calendar {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        // Reset orario per garantire l'uguaglianza dei Calendar
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            cal.add(Calendar.DAY_OF_MONTH, -1)
        }
        return cal
    }
}