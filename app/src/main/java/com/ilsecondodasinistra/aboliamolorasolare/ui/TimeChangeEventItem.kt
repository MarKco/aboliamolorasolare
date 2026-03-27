package com.ilsecondodasinistra.aboliamolorasolare.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ilsecondodasinistra.aboliamolorasolare.R
import com.ilsecondodasinistra.aboliamolorasolare.TimeChangeDirection
import com.ilsecondodasinistra.aboliamolorasolare.TimeChangeEvent
import com.ilsecondodasinistra.aboliamolorasolare.TimeChangeType
import com.ilsecondodasinistra.aboliamolorasolare.ui.theme.AboliamoLoraSolareTheme
import java.util.Calendar

@Composable
fun TimeChangeEventItem(
    event: TimeChangeEvent,
    notifyX: Boolean,
    notifyY: Boolean,
    x: Int?,
    y: Int?,
    onToggleX: (Boolean) -> Unit,
    onToggleY: (Boolean) -> Unit
) {
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
            Text(stringResource(R.string.move_hands_label, event.direction.displayName()), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            
            if (x != null && y != null) {
                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth().height(IntrinsicSize.Max), Arrangement.spacedBy(8.dp)) {
                    NotificationButton(Modifier.weight(1f).fillMaxHeight(), notifyX, x, onToggleX)
                    NotificationButton(Modifier.weight(1f).fillMaxHeight(), notifyY, y, onToggleY)
                }
            }
        }
    }
}

@Composable
fun NotificationButton(modifier: Modifier, isActive: Boolean, days: Int, onToggle: (Boolean) -> Unit) {
    // Il segreto per non rompere il Ripple in Compose è USARE LO STESSO NODO. 
    // Se scambiamo FilledTonalButton e OutlinedButton con un if/else, il nodo viene distrutto
    // e l'animazione del ripple muore all'istante.
    // Usando un singolo `Button` e animando i suoi colori/bordi, il ripple si completerà sempre.
    
    val containerColor by animateColorAsState(
        targetValue = if (isActive) Color.Transparent else MaterialTheme.colorScheme.secondaryContainer,
        label = "containerColor"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSecondaryContainer,
        label = "contentColor"
    )
    val border = if (isActive) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null

    Button(
        modifier = modifier,
        onClick = { onToggle(!isActive) },
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        border = border,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 12.dp)
    ) {
        Text(
            text = stringResource(if (isActive) R.string.remove_notification_btn else R.string.notify_days_before_btn, days),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TimeChangeEventItemPreview() {
    val dummyCal = Calendar.getInstance().apply { set(2026, Calendar.APRIL, 1) }
    AboliamoLoraSolareTheme {
        TimeChangeEventItem(TimeChangeEvent(dummyCal, TimeChangeType.LEGALE, TimeChangeDirection.AVANTI), false, true, 5, 10, {}, {})
    }
}
