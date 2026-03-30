package com.ilsecondodasinistra.lancette.ui

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
import androidx.compose.material.icons.filled.NotificationsOff
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ilsecondodasinistra.lancette.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onNavigateToSettings: () -> Unit
) {
    val timeChanges by viewModel.timeChanges.collectAsState()
    val isGlobalEnabled by viewModel.isGlobalNotificationsEnabled.collectAsState()

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
                Row(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(16.dp)) {
                    Button(
                        modifier = Modifier.fillMaxWidth(), 
                        shape = RoundedCornerShape(12.dp), 
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isGlobalEnabled) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                            contentColor = if (isGlobalEnabled) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary
                        ),
                        onClick = { viewModel.toggleGlobalNotifications() }
                    ) {
                        Icon(
                            if (isGlobalEnabled) Icons.Default.NotificationsOff else Icons.Default.Notifications,
                            contentDescription = null, 
                            Modifier.padding(end = 8.dp)
                        )
                        Text(stringResource(if (isGlobalEnabled) R.string.deactivate_all_btn else R.string.activate_all_btn))
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(padding), 
            contentPadding = PaddingValues(16.dp), 
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (timeChanges != null) {
                item { CurrentStateSection(timeChanges!!) }
                
                item { Text(stringResource(R.string.upcoming_changes_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
                items(timeChanges!!.next, key = { it.date.timeInMillis }) { event ->
                    TimeChangeEventItem(event = event)
                }
                
                item { Text(stringResource(R.string.recent_changes_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
                items(timeChanges!!.previous.sortedByDescending { it.date }, key = { it.date.timeInMillis }) { event ->
                    TimeChangeEventItem(event = event)
                }
            }
        }
    }
}
