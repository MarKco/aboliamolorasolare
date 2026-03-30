package com.ilsecondodasinistra.lancette.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.ilsecondodasinistra.lancette.TimeChangeType
import com.ilsecondodasinistra.lancette.ui.theme.LancetteTheme
import java.util.Calendar

@Composable
fun TimeChangeEventItem(
    event: TimeChangeEvent
) {
    val isPast = event.date.before(Calendar.getInstance())
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text(event.date.formatAsDayMonthYear(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                val typeColor = if (event.type == TimeChangeType.LEGALE) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                Box(Modifier.background(typeColor, RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 4.dp)) {
                    Text(event.type.displayName(), style = MaterialTheme.typography.labelMedium, color = if (event.type == TimeChangeType.LEGALE) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(8.dp))
            
            val moveHandsLabel = if (isPast) R.string.move_hands_past_label else R.string.move_hands_label
            Text(stringResource(moveHandsLabel, event.direction.displayName()), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            
            val sleepMessage = if (event.direction == TimeChangeDirection.INDIETRO) {
                if (isPast) stringResource(R.string.gained_hour) else stringResource(R.string.gain_hour)
            } else {
                if (isPast) stringResource(R.string.lost_hour) else stringResource(R.string.lose_hour)
            }
                
            Text(sleepMessage, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TimeChangeEventItemPreview() {
    val dummyCal = Calendar.getInstance().apply { set(2026, Calendar.APRIL, 1) }
    LancetteTheme {
        TimeChangeEventItem(TimeChangeEvent(dummyCal, TimeChangeType.LEGALE, TimeChangeDirection.AVANTI))
    }
}
