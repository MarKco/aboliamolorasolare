package com.ilsecondodasinistra.aboliamolorasolare.usecase

import com.ilsecondodasinistra.aboliamolorasolare.TimeChangeCalculator
import com.ilsecondodasinistra.aboliamolorasolare.TimeChangeResult
import java.time.LocalDate

class GetTimeChangesUseCase(private val calculator: TimeChangeCalculator) {
    fun execute(baseDate: LocalDate): TimeChangeResult = calculator.getTimeChanges(baseDate)
}

