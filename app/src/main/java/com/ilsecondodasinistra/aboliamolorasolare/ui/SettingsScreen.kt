package com.ilsecondodasinistra.aboliamolorasolare.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ilsecondodasinistra.aboliamolorasolare.BuildConfig
import com.ilsecondodasinistra.aboliamolorasolare.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val x by viewModel.x.collectAsState()
    val y by viewModel.y.collectAsState()
    val isXEnabled by viewModel.isNotifyXEnabled.collectAsState()
    val isYEnabled by viewModel.isNotifyYEnabled.collectAsState()
    val isZEnabled by viewModel.isNotifyZEnabled.collectAsState()
    val isFastEnabled by viewModel.isFastNotificationsEnabled.collectAsState()
    
    val nextX by viewModel.nextScheduledX.collectAsState()
    val nextY by viewModel.nextScheduledY.collectAsState()
    val nextZ by viewModel.nextScheduledZ.collectAsState()
    
    var expandedX by remember { mutableStateOf(false) }
    var expandedY by remember { mutableStateOf(false) }
    val xOptions = (1..30).toList()
    val yOptions = (1..30).toList()

    LaunchedEffect(Unit) { viewModel.load() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_desc))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            
            // Impostazione Notifica X
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(stringResource(R.string.enable_x_notification), fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(R.string.notify_x_days_before_setting), style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.width(8.dp))
                        Box {
                            Button(onClick = { expandedX = true }, enabled = isXEnabled) {
                                Text(x?.toString() ?: "-")
                            }
                            DropdownMenu(expanded = expandedX, onDismissRequest = { expandedX = false }) {
                                xOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option.toString()) },
                                        onClick = {
                                            viewModel.setX(option)
                                            expandedX = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                Switch(
                    checked = isXEnabled,
                    onCheckedChange = { viewModel.setNotifyXEnabled(it) }
                )
            }
            
            HorizontalDivider()

            // Impostazione Notifica Y
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(stringResource(R.string.enable_y_notification), fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(R.string.notify_y_days_before_setting), style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.width(8.dp))
                        Box {
                            Button(onClick = { expandedY = true }, enabled = isYEnabled) {
                                Text(y?.toString() ?: "-")
                            }
                            DropdownMenu(expanded = expandedY, onDismissRequest = { expandedY = false }) {
                                yOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option.toString()) },
                                        onClick = {
                                            viewModel.setY(option)
                                            expandedY = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                Switch(
                    checked = isYEnabled,
                    onCheckedChange = { viewModel.setNotifyYEnabled(it) }
                )
            }

            HorizontalDivider()

            // Impostazione Notifica Z (Giorno stesso)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.notify_day_of_change_setting), fontWeight = FontWeight.Bold)
                Switch(
                    checked = isZEnabled,
                    onCheckedChange = { viewModel.setNotifyZEnabled(it) }
                )
            }

            if (BuildConfig.DEBUG) {
                HorizontalDivider()
                
                Text(
                    stringResource(R.string.dev_settings_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(stringResource(R.string.fast_notifications_label), modifier = Modifier.weight(1f))
                    Switch(
                        checked = isFastEnabled,
                        onCheckedChange = { viewModel.setFastNotificationsEnabled(it) }
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(stringResource(R.string.scheduled_notifications_title), fontWeight = FontWeight.Bold)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(stringResource(R.string.scheduled_notif_x, nextX ?: stringResource(R.string.not_scheduled)))
                    Text(stringResource(R.string.scheduled_notif_y, nextY ?: stringResource(R.string.not_scheduled)))
                    Text(stringResource(R.string.scheduled_notif_z, nextZ ?: stringResource(R.string.not_scheduled)))
                }
            }
        }
    }
}
