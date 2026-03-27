package com.ilsecondodasinistra.aboliamolorasolare.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit
) {
    val x by viewModel.x.collectAsState()
    val y by viewModel.y.collectAsState()
    var expandedX by remember { mutableStateOf(false) }
    var expandedY by remember { mutableStateOf(false) }
    val xOptions = (1..30).toList()
    val yOptions = (1..30).toList()

    LaunchedEffect(Unit) { viewModel.load() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Impostazioni") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Indietro")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text("Notifica X giorni prima:")
            Box {
                Button(onClick = { expandedX = true }) {
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
            Text("Notifica Y giorni prima:")
            Box {
                Button(onClick = { expandedY = true }) {
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
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    MaterialTheme {
        Column(Modifier.padding(16.dp)) {
            Text("Notifica X giorni prima:")
            Button(onClick = {}) { Text("5") }
            Text("Notifica Y giorni prima:")
            Button(onClick = {}) { Text("10") }
        }
    }
}

