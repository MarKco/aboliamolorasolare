package com.ilsecondodasinistra.lancette.usecase

import com.ilsecondodasinistra.lancette.TimeChangeCalculator
import com.ilsecondodasinistra.lancette.TimeChangeResult
import java.util.Calendar

class GetTimeChangesUseCase(private val calculator: TimeChangeCalculator) {
    fun execute(baseDate: Calendar): TimeChangeResult = calculator.getTimeChanges(baseDate)
}

