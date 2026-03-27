package com.ilsecondodasinistra.aboliamolorasolare.ui

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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ilsecondodasinistra.aboliamolorasolare.R
import com.ilsecondodasinistra.aboliamolorasolare.TimeChangeDirection
import com.ilsecondodasinistra.aboliamolorasolare.TimeChangeEvent
import com.ilsecondodasinistra.aboliamolorasolare.TimeChangeType
import com.ilsecondodasinistra.aboliamolorasolare.model.NotificationSetting
import com.ilsecondodasinistra.aboliamolorasolare.model.TimeChangeEventId
import com.ilsecondodasinistra.aboliamolorasolare.ui.theme.AboliamoLoraSolareTheme
import java.util.Calendar

@Composable
fun TimeChangeEventItem(
    event: TimeChangeEvent,
    notificationSetting: NotificationSetting?,
    x: Int?,
    y: Int?,
    onSetNotification: (NotificationSetting) -> Unit,
    onRemoveNotification: (TimeChangeEventId) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = event.date.formatAsDayMonthYear(), 
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                val typeColor = if (event.type == TimeChangeType.LEGALE) 
                    MaterialTheme.colorScheme.secondary 
                else 
                    MaterialTheme.colorScheme.primary

                val onTypeColor = if (event.type == TimeChangeType.LEGALE) 
                    MaterialTheme.colorScheme.onSecondary 
                else 
                    MaterialTheme.colorScheme.onPrimary
                
                Box(
                    modifier = Modifier
                        .background(typeColor, shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = event.type.displayName(),
                        style = MaterialTheme.typography.labelMedium,
                        color = onTypeColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.move_hands_label, event.direction.displayName()), 
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (x != null && y != null) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Notifica X
                    if (notificationSetting == null || !notificationSetting.notifyX) {
                        FilledTonalButton(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onSetNotification(
                                    NotificationSetting(
                                        eventId = TimeChangeEventId(event.date, event.type.name),
                                        notifyX = true,
                                        notifyY = notificationSetting?.notifyY ?: false
                                    )
                                )
                            },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) { 
                            Text(
                                text = stringResource(R.string.notify_days_before_btn, x),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelLarge
                            ) 
                        }
                    } else {
                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                if (notificationSetting.notifyY) {
                                    onSetNotification(notificationSetting.copy(notifyX = false))
                                } else {
                                    onRemoveNotification(TimeChangeEventId(event.date, event.type.name))
                                }
                            },
                            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(width = 1.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) { 
                            Text(
                                text = stringResource(R.string.remove_notification_btn, x),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelLarge
                            ) 
                        }
                    }
                    
                    // Notifica Y
                    if (notificationSetting == null || !notificationSetting.notifyY) {
                        FilledTonalButton(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onSetNotification(
                                    NotificationSetting(
                                        eventId = TimeChangeEventId(event.date, event.type.name),
                                        notifyY = true,
                                        notifyX = notificationSetting?.notifyX ?: false
                                    )
                                )
                            },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) { 
                            Text(
                                text = stringResource(R.string.notify_days_before_btn, y),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelLarge
                            ) 
                        }
                    } else {
                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                if (notificationSetting.notifyX) {
                                    onSetNotification(notificationSetting.copy(notifyY = false))
                                } else {
                                    onRemoveNotification(TimeChangeEventId(event.date, event.type.name))
                                }
                            },
                            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(width = 1.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) { 
                            Text(
                                text = stringResource(R.string.remove_notification_btn, y),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelLarge
                            ) 
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Upcoming Summer Time - No Notification")
@Composable
fun TimeChangeEventItemPreview() {
    val dummyCal = Calendar.getInstance().apply { set(2026, Calendar.APRIL, 1) }
    AboliamoLoraSolareTheme {
        TimeChangeEventItem(
            event = TimeChangeEvent(dummyCal, TimeChangeType.LEGALE, TimeChangeDirection.AVANTI),
            notificationSetting = null,
            x = 5,
            y = 10,
            onSetNotification = {},
            onRemoveNotification = {}
        )
    }
}

@Preview(showBackground = true, name = "Upcoming Winter Time - One Notification Set")
@Composable
fun TimeChangeEventItemNotificationSetPreview() {
    val dummyCal = Calendar.getInstance().apply { set(2026, Calendar.NOVEMBER, 1) }
    val dummyEvent = TimeChangeEvent(dummyCal, TimeChangeType.SOLARE, TimeChangeDirection.INDIETRO)
    AboliamoLoraSolareTheme {
        TimeChangeEventItem(
            event = dummyEvent,
            notificationSetting = NotificationSetting(
                eventId = TimeChangeEventId(dummyEvent.date, dummyEvent.type.name),
                notifyX = true,
                notifyY = false
            ),
            x = 5,
            y = 10,
            onSetNotification = {},
            onRemoveNotification = {}
        )
    }
}

@Preview(showBackground = true, name = "Past Event")
@Composable
fun TimeChangeEventItemPastPreview() {
    val dummyCal = Calendar.getInstance().apply { set(2023, Calendar.APRIL, 1) }
    AboliamoLoraSolareTheme {
        TimeChangeEventItem(
            event = TimeChangeEvent(dummyCal, TimeChangeType.LEGALE, TimeChangeDirection.AVANTI),
            notificationSetting = null,
            x = null,
            y = null,
            onSetNotification = {},
            onRemoveNotification = {}
        )
    }
}

@Preview(showBackground = true, name = "Dark Mode", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TimeChangeEventItemDarkPreview() {
    val dummyCal = Calendar.getInstance().apply { set(2026, Calendar.APRIL, 1) }
    AboliamoLoraSolareTheme {
        TimeChangeEventItem(
            event = TimeChangeEvent(dummyCal, TimeChangeType.LEGALE, TimeChangeDirection.AVANTI),
            notificationSetting = null,
            x = 5,
            y = 10,
            onSetNotification = {},
            onRemoveNotification = {}
        )
    }
}
