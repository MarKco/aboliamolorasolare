package com.ilsecondodasinistra.aboliamolorasolare

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import com.ilsecondodasinistra.aboliamolorasolare.notification.NotificationScheduler
import com.ilsecondodasinistra.aboliamolorasolare.ui.MainViewModel
import com.ilsecondodasinistra.aboliamolorasolare.ui.SettingsViewModel
import com.ilsecondodasinistra.aboliamolorasolare.ui.theme.AboliamoLoraSolareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AboliamoLoraSolareTheme {
                val settingsRepository = com.ilsecondodasinistra.aboliamolorasolare.repository.InMemorySettingsRepository()
                val mainViewModelFactory = com.ilsecondodasinistra.aboliamolorasolare.ui.MainViewModelFactory(
                    com.ilsecondodasinistra.aboliamolorasolare.usecase.GetTimeChangesUseCase(TimeChangeCalculator()),
                    com.ilsecondodasinistra.aboliamolorasolare.usecase.GetNotificationSettingsUseCase(
                        com.ilsecondodasinistra.aboliamolorasolare.repository.InMemoryNotificationRepository()
                    ),
                    com.ilsecondodasinistra.aboliamolorasolare.usecase.SetNotificationSettingUseCase(
                        com.ilsecondodasinistra.aboliamolorasolare.repository.InMemoryNotificationRepository()
                    ),
                    com.ilsecondodasinistra.aboliamolorasolare.usecase.RemoveNotificationSettingUseCase(
                        com.ilsecondodasinistra.aboliamolorasolare.repository.InMemoryNotificationRepository()
                    ),
                    com.ilsecondodasinistra.aboliamolorasolare.usecase.GetSettingsUseCase(
                        settingsRepository
                    ),
                    notificationScheduler = NotificationScheduler(this)
                )
                val mainViewModel = androidx.lifecycle.viewmodel.compose.viewModel<MainViewModel>(
                    factory = mainViewModelFactory
                )
                val settingsViewModelFactory = com.ilsecondodasinistra.aboliamolorasolare.ui.SettingsViewModelFactory(
                    com.ilsecondodasinistra.aboliamolorasolare.usecase.GetSettingsUseCase(
                        settingsRepository
                    ),
                    com.ilsecondodasinistra.aboliamolorasolare.usecase.SetSettingsUseCase(
                        settingsRepository
                    )
                )
                val settingsViewModel = androidx.lifecycle.viewmodel.compose.viewModel<SettingsViewModel>(
                    factory = settingsViewModelFactory
                )

                // Permesso notifiche Android 13+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val permission = Manifest.permission.POST_NOTIFICATIONS
                    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted -> }
                    LaunchedEffect(Unit) {
                        if (ContextCompat.checkSelfPermission(this@MainActivity, permission) != PackageManager.PERMISSION_GRANTED) {
                            launcher.launch(permission)
                        }
                    }
                }

                com.ilsecondodasinistra.aboliamolorasolare.ui.AppNavGraph(
                    mainViewModel = mainViewModel,
                    settingsViewModel = settingsViewModel
                )
            }
        }
    }
}
