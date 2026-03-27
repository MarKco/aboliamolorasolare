package com.ilsecondodasinistra.aboliamolorasolare.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ilsecondodasinistra.aboliamolorasolare.TimeChangeEvent
import com.ilsecondodasinistra.aboliamolorasolare.TimeChangeResult
import com.ilsecondodasinistra.aboliamolorasolare.model.NotificationSetting
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onNavigateToSettings: () -> Unit
) {
    val timeChanges by viewModel.timeChanges.collectAsState()
    val notificationSettings by viewModel.notificationSettings.collectAsState()
    val x by viewModel.x.collectAsState()
    val y by viewModel.y.collectAsState()
    var showSnackbar by remember { mutableStateOf<String?>(null) }

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
        snackbarHost = {
            SnackbarHost(hostState = remember { SnackbarHostState() })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            timeChanges?.let { changes ->
                CurrentStateSection(changes)
                Spacer(Modifier.height(16.dp))
                Text("Prossimi cambi dell'ora", style = MaterialTheme.typography.titleMedium)
                LazyColumn {
                    items(changes.next) { event ->
                        TimeChangeEventItem(
                            event = event,
                            notificationSetting = notificationSettings.find { it.eventId.date == event.date && it.eventId.type == event.type.name },
                            x = x,
                            y = y,
                            onSetNotification = { setting -> viewModel.setNotification(setting) },
                            onRemoveNotification = { id -> viewModel.removeNotification(id) }
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text("Ultimi cambi dell'ora", style = MaterialTheme.typography.titleMedium)
                LazyColumn {
                    items(changes.previous) { event ->
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
    if (showSnackbar != null) {
        Snackbar(
            action = {
                TextButton(onClick = { showSnackbar = null }) { Text("OK") }
            },
            modifier = Modifier.padding(8.dp)
        ) { Text(showSnackbar ?: "") }
    }
}

@Composable
fun CurrentStateSection(result: TimeChangeResult) {
    val now = LocalDate.now()
    val current = (result.previous + result.next).lastOrNull { !it.date.isAfter(now) }
    val state = when (current?.type) {
        com.ilsecondodasinistra.aboliamolorasolare.TimeChangeType.LEGALE -> "Ora legale"
        com.ilsecondodasinistra.aboliamolorasolare.TimeChangeType.SOLARE -> "Ora solare"
        else -> "-"
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Stato attuale:", style = MaterialTheme.typography.labelMedium)
            Text(state, style = MaterialTheme.typography.headlineMedium)
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
            Text("${event.date} - ${event.type}", style = MaterialTheme.typography.bodyLarge)
            Text("Spostare le lancette: ${event.direction}", style = MaterialTheme.typography.bodyMedium)
            if (x != null && y != null) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    if (notificationSetting == null || !notificationSetting.notifyX) {
                        Button(onClick = {
                            onSetNotification(
                                NotificationSetting(
                                    eventId = com.ilsecondodasinistra.aboliamolorasolare.model.TimeChangeEventId(event.date, event.type.name),
                                    notifyX = true
                                )
                            )
                        }) { Text("Notifica $x giorni prima") }
                    } else {
                        OutlinedButton(onClick = {
                            onRemoveNotification(com.ilsecondodasinistra.aboliamolorasolare.model.TimeChangeEventId(event.date, event.type.name))
                        }) { Text("Annulla notifica X") }
                    }
                    Spacer(Modifier.width(8.dp))
                    if (notificationSetting == null || !notificationSetting.notifyY) {
                        Button(onClick = {
                            onSetNotification(
                                NotificationSetting(
                                    eventId = com.ilsecondodasinistra.aboliamolorasolare.model.TimeChangeEventId(event.date, event.type.name),
                                    notifyY = true
                                )
                            )
                        }) { Text("Notifica $y giorni prima") }
                    } else {
                        OutlinedButton(onClick = {
                            onRemoveNotification(com.ilsecondodasinistra.aboliamolorasolare.model.TimeChangeEventId(event.date, event.type.name))
                        }) { Text("Annulla notifica Y") }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    // Dummy preview, replace with real preview logic if needed
    val dummyResult = TimeChangeResult(
        previous = listOf(),
        next = listOf(
            TimeChangeEvent(LocalDate.now().plusDays(5), com.ilsecondodasinistra.aboliamolorasolare.TimeChangeType.LEGALE, com.ilsecondodasinistra.aboliamolorasolare.TimeChangeDirection.AVANTI)
        )
    )
    MaterialTheme {
        CurrentStateSection(dummyResult)
    }
}

