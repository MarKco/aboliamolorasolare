package com.ilsecondodasinistra.lancette.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ilsecondodasinistra.lancette.R
import com.ilsecondodasinistra.lancette.TimeChangeDirection
import com.ilsecondodasinistra.lancette.TimeChangeType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun TimeChangeType.displayName(): String {
    return when (this) {
        TimeChangeType.LEGALE -> stringResource(R.string.summer_time)
        TimeChangeType.SOLARE -> stringResource(R.string.winter_time)
    }
}

@Composable
fun TimeChangeDirection.displayName(): String {
    return when (this) {
        TimeChangeDirection.AVANTI -> stringResource(R.string.forward)
        TimeChangeDirection.INDIETRO -> stringResource(R.string.backward)
    }
}

fun Calendar.formatAsDayMonthYear(): String {
    val format = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
    return format.format(this.time)
}
