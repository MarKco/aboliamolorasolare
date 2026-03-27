package com.ilsecondodasinistra.aboliamolorasolare.usecase

import com.ilsecondodasinistra.aboliamolorasolare.TimeChangeCalculator
import com.ilsecondodasinistra.aboliamolorasolare.TimeChangeResult
import java.util.Calendar

class GetTimeChangesUseCase(private val calculator: TimeChangeCalculator) {
    fun execute(baseDate: Calendar): TimeChangeResult = calculator.getTimeChanges(baseDate)
}

