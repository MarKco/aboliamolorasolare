package com.ilsecondodasinistra.aboliamolorasolare.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ilsecondodasinistra.aboliamolorasolare.R
import com.ilsecondodasinistra.aboliamolorasolare.model.NotificationSetting
import com.ilsecondodasinistra.aboliamolorasolare.model.TimeChangeEventId

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
                title = { Text(stringResource(R.string.app_name), fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings_desc))
                    }
                }
            )
        },
        bottomBar = {
            Surface(modifier = Modifier.fillMaxWidth().shadow(8.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 8.dp) {
                Row(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    val allActivatedMsg = stringResource(R.string.all_notifications_activated_msg)
                    Button(modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), onClick = { 
                        viewModel.activateAllNotifications() 
                        Toast.makeText(context, allActivatedMsg, Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.Notifications, null, Modifier.padding(end = 8.dp))
                        Text(stringResource(R.string.activate_all_btn))
                    }
                    val allDeactivatedMsg = stringResource(R.string.all_notifications_deactivated_msg)
                    Button(modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary, contentColor = MaterialTheme.colorScheme.onSecondary), onClick = { 
                        viewModel.deactivateAllNotifications() 
                        Toast.makeText(context, allDeactivatedMsg, Toast.LENGTH_SHORT).show()
                    }) {
                        Text(stringResource(R.string.deactivate_all_btn))
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            if (timeChanges != null) {
                item { CurrentStateSection(timeChanges!!) }
                item { Text(stringResource(R.string.upcoming_changes_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
                items(timeChanges!!.next, key = { it.date.timeInMillis }) { event ->
                    val setting = notificationSettings.find { it.eventId.dateMillis == event.date.timeInMillis && it.eventId.type == event.type.name }
                    val setMsg = stringResource(R.string.notification_set_msg)
                    val removeMsg = stringResource(R.string.notification_removed_msg)

                    TimeChangeEventItem(
                        event = event,
                        notifyX = setting?.notifyX ?: false,
                        notifyY = setting?.notifyY ?: false,
                        x = x,
                        y = y,
                        onToggleX = { active ->
                            val id = TimeChangeEventId(event.date.timeInMillis, event.type.name)
                            if (active) {
                                viewModel.setNotification(NotificationSetting(id, notifyX = true, notifyY = setting?.notifyY ?: false))
                                Toast.makeText(context, setMsg, Toast.LENGTH_SHORT).show()
                            } else {
                                if (setting?.notifyY == true) {
                                    viewModel.setNotification(setting.copy(notifyX = false))
                                } else {
                                    viewModel.removeNotification(id)
                                }
                                Toast.makeText(context, removeMsg, Toast.LENGTH_SHORT).show()
                            }
                        },
                        onToggleY = { active ->
                            val id = TimeChangeEventId(event.date.timeInMillis, event.type.name)
                            if (active) {
                                viewModel.setNotification(NotificationSetting(id, notifyY = true, notifyX = setting?.notifyX ?: false))
                                Toast.makeText(context, setMsg, Toast.LENGTH_SHORT).show()
                            } else {
                                if (setting?.notifyX == true) {
                                    viewModel.setNotification(setting.copy(notifyY = false))
                                } else {
                                    viewModel.removeNotification(id)
                                }
                                Toast.makeText(context, removeMsg, Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
                item { Text(stringResource(R.string.recent_changes_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
                items(timeChanges!!.previous, key = { it.date.timeInMillis }) { event ->
                    TimeChangeEventItem(event, false, false, null, null, {}, {})
                }
            }
        }
    }
}
