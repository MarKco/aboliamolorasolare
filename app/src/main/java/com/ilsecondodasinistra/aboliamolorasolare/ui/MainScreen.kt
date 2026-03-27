package com.ilsecondodasinistra.aboliamolorasolare.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
                title = { 
                    Text(
                        stringResource(R.string.app_name), 
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    ) 
                },
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
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val allActivatedMsg = stringResource(R.string.all_notifications_activated_msg)
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            viewModel.activateAllNotifications()
                            Toast.makeText(context, allActivatedMsg, Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                        Text(stringResource(R.string.activate_all_btn))
                    }
                    val allDeactivatedMsg = stringResource(R.string.all_notifications_deactivated_msg)
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            viewModel.deactivateAllNotifications()
                            Toast.makeText(context, allDeactivatedMsg, Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(stringResource(R.string.deactivate_all_btn))
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding),
            contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (timeChanges != null) {
                item {
                    CurrentStateSection(timeChanges!!)
                }
                
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.upcoming_changes_title), 
                        style = MaterialTheme.typography.titleLarge, 
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                
                items(timeChanges!!.next) { event ->
                    val setMsg = stringResource(R.string.notification_set_msg)
                    val removeMsg = stringResource(R.string.notification_removed_msg)
                    TimeChangeEventItem(
                        event = event,
                        notificationSetting = notificationSettings.find { it.eventId.date == event.date && it.eventId.type == event.type.name },
                        x = x,
                        y = y,
                        onSetNotification = { setting ->
                            viewModel.setNotification(setting)
                            Toast.makeText(context, setMsg, Toast.LENGTH_SHORT).show()
                        },
                        onRemoveNotification = { id ->
                            viewModel.removeNotification(id)
                            Toast.makeText(context, removeMsg, Toast.LENGTH_SHORT).show()
                        }
                    )
                }
                
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.recent_changes_title), 
                        style = MaterialTheme.typography.titleLarge, 
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
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
