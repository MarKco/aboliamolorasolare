package com.ilsecondodasinistra.aboliamolorasolare.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ilsecondodasinistra.aboliamolorasolare.TimeChangeEvent
import com.ilsecondodasinistra.aboliamolorasolare.TimeChangeResult
import com.ilsecondodasinistra.aboliamolorasolare.model.NotificationSetting
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onNavigateToSettings: () -> Unit
) {
    val context = LocalContext.current
    val timeChanges by viewModel.timeChanges.collectAsState()
    val notificationSettings by viewModel.notificationSettings.collectAsState()
    val x by viewModel.x.collectAsState()
    val y by viewModel.y.collectAsState()

    LaunchedEffect(Unit) { viewModel.load() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Aboliamo l'ora solare") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Impostazioni")
                    }
                }
            )
        },
        bottomBar = {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ElevatedButton(
                        onClick = {
                            viewModel.activateAllNotifications()
                            Toast.makeText(context, "Tutte le notifiche attivate!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Text("attiva tutte")
                    }
                    ElevatedButton(
                        onClick = {
                            viewModel.deactivateAllNotifications()
                            Toast.makeText(context, "Tutte le notifiche disattivate!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text("disattiva tutte")
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp, start = 8.dp, end = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (timeChanges != null) {
                item {
                    CurrentStateSection(timeChanges!!)
                }
                item {
                    Text("Prossimi cambi dell'ora", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(horizontal = 8.dp))
                }
                items(timeChanges!!.next) { event ->
                    TimeChangeEventItem(
                        event = event,
                        notificationSetting = notificationSettings.find { it.eventId.date == event.date && it.eventId.type == event.type.name },
                        x = x,
                        y = y,
                        onSetNotification = { setting ->
                            viewModel.setNotification(setting)
                            Toast.makeText(context, "Notifica attivata!", Toast.LENGTH_SHORT).show()
                        },
                        onRemoveNotification = { id ->
                            viewModel.removeNotification(id)
                            Toast.makeText(context, "Notifica disattivata!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                item {
                    Text("Ultimi cambi dell'ora", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(horizontal = 8.dp))
                }
                items(timeChanges!!.previous) { event ->
                    TimeChangeEventItem(
                        event = event,
                        notificationSetting = null,
                        x = null,
                        y = null,
                        onSetNotification = {},
                        onRemoveNotification = {}
                    )
                }
            }
        }
    }
}

@Composable
fun TimeChangeEventItem(
    event: TimeChangeEvent,
    notificationSetting: NotificationSetting?,
    x: Int?,
    y: Int?,
    onSetNotification: (NotificationSetting) -> Unit,
    onRemoveNotification: (com.ilsecondodasinistra.aboliamolorasolare.model.TimeChangeEventId) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text("${event.date.formatAsDayMonthYear()} - ${event.type}", style = MaterialTheme.typography.bodyLarge)
            Text("Spostare le lancette: ${event.direction}", style = MaterialTheme.typography.bodyMedium)
            if (x != null && y != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (notificationSetting == null || !notificationSetting.notifyX) {
                        FilledTonalButton(
                            onClick = {
                                onSetNotification(
                                    NotificationSetting(
                                        eventId = com.ilsecondodasinistra.aboliamolorasolare.model.TimeChangeEventId(event.date, event.type.name),
                                        notifyX = true
                                    )
                                )
                            }
                        ) { Text("X ($x gg)") }
                    } else {
                        OutlinedButton(
                            onClick = {
                                if (notificationSetting.notifyY) {
                                    onSetNotification(notificationSetting.copy(notifyX = false))
                                } else {
                                    onRemoveNotification(com.ilsecondodasinistra.aboliamolorasolare.model.TimeChangeEventId(event.date, event.type.name))
                                }
                            },
                            border = ButtonDefaults.outlinedButtonBorder.copy(width = 2.dp)
                        ) { Text("Rimuovi X") }
                    }
                    Spacer(Modifier.width(8.dp))
                    if (notificationSetting == null || !notificationSetting.notifyY) {
                        FilledTonalButton(
                            onClick = {
                                onSetNotification(
                                    NotificationSetting(
                                        eventId = com.ilsecondodasinistra.aboliamolorasolare.model.TimeChangeEventId(event.date, event.type.name),
                                        notifyY = true
                                    )
                                )
                            }
                        ) { Text("Y ($y gg)") }
                    } else {
                        OutlinedButton(
                            onClick = {
                                if (notificationSetting.notifyX) {
                                    onSetNotification(notificationSetting.copy(notifyY = false))
                                } else {
                                    onRemoveNotification(com.ilsecondodasinistra.aboliamolorasolare.model.TimeChangeEventId(event.date, event.type.name))
                                }
                            },
                            border = ButtonDefaults.outlinedButtonBorder.copy(width = 2.dp)
                        ) { Text("Rimuovi Y") }
                    }
                }
            }
        }
    }
}

@Composable
fun CurrentStateSection(result: TimeChangeResult) {
    val now = Calendar.getInstance()
    val current = (result.previous + result.next).lastOrNull { !it.date.after(now) }
    val state = when (current?.type) {
        com.ilsecondodasinistra.aboliamolorasolare.TimeChangeType.LEGALE -> "Ora legale"
        com.ilsecondodasinistra.aboliamolorasolare.TimeChangeType.SOLARE -> "Ora solare"
        else -> "-"
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Stato attuale:", style = MaterialTheme.typography.labelMedium)
            Text(state, style = MaterialTheme.typography.headlineMedium)
        }
    }
}

fun Calendar.formatAsDayMonthYear(): String {
    val format = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
    return format.format(this.time)
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val dummyCal = Calendar.getInstance().apply { set(2026, Calendar.APRIL, 1) }
    val dummyResult = TimeChangeResult(
        previous = listOf(),
        next = listOf(
            TimeChangeEvent(dummyCal, com.ilsecondodasinistra.aboliamolorasolare.TimeChangeType.LEGALE, com.ilsecondodasinistra.aboliamolorasolare.TimeChangeDirection.AVANTI)
        )
    )
    MaterialTheme {
        CurrentStateSection(dummyResult)
    }
}
