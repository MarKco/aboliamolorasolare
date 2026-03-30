package com.ilsecondodasinistra.lancette.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ilsecondodasinistra.lancette.R
import com.ilsecondodasinistra.lancette.TimeChangeDirection
import com.ilsecondodasinistra.lancette.TimeChangeEvent
import com.ilsecondodasinistra.lancette.TimeChangeResult
import com.ilsecondodasinistra.lancette.TimeChangeType
import com.ilsecondodasinistra.lancette.ui.theme.LancetteTheme
import java.util.Calendar

@Composable
fun CurrentStateSection(result: TimeChangeResult) {
    val now = Calendar.getInstance()
    val current = (result.previous + result.next).lastOrNull { !it.date.after(now) }
    val state = current?.type?.displayName() ?: stringResource(R.string.unknown)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                stringResource(R.string.current_state_label), 
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                state, 
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Preview(showBackground = true, name = "Summer Time")
@Composable
fun CurrentStateSectionSummerPreview() {
    val dummyCal = Calendar.getInstance().apply { set(2024, Calendar.APRIL, 1) }
    val dummyResult = TimeChangeResult(
        previous = listOf(TimeChangeEvent(dummyCal, TimeChangeType.LEGALE, TimeChangeDirection.AVANTI)),
        next = listOf()
    )
    LancetteTheme {
        CurrentStateSection(dummyResult)
    }
}

@Preview(showBackground = true, name = "Winter Time")
@Composable
fun CurrentStateSectionWinterPreview() {
    val dummyCal = Calendar.getInstance().apply { set(2024, Calendar.NOVEMBER, 1) }
    val dummyResult = TimeChangeResult(
        previous = listOf(TimeChangeEvent(dummyCal, TimeChangeType.SOLARE, TimeChangeDirection.INDIETRO)),
        next = listOf()
    )
    LancetteTheme {
        CurrentStateSection(dummyResult)
    }
}

@Preview(showBackground = true, name = "Dark Mode Summer", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun CurrentStateSectionDarkPreview() {
    val dummyCal = Calendar.getInstance().apply { set(2024, Calendar.APRIL, 1) }
    val dummyResult = TimeChangeResult(
        previous = listOf(TimeChangeEvent(dummyCal, TimeChangeType.LEGALE, TimeChangeDirection.AVANTI)),
        next = listOf()
    )
    LancetteTheme {
        CurrentStateSection(dummyResult)
    }
}
