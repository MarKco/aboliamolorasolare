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
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import com.ilsecondodasinistra.aboliamolorasolare.notification.NotificationScheduler
import com.ilsecondodasinistra.aboliamolorasolare.repository.SharedPreferencesNotificationRepository
import com.ilsecondodasinistra.aboliamolorasolare.repository.SharedPreferencesSettingsRepository
import com.ilsecondodasinistra.aboliamolorasolare.ui.MainViewModel
import com.ilsecondodasinistra.aboliamolorasolare.ui.SettingsViewModel
import com.ilsecondodasinistra.aboliamolorasolare.ui.theme.AboliamoLoraSolareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AboliamoLoraSolareTheme {
                // I repository ora sono persistenti tramite SharedPreferences
                val settingsRepository = remember { SharedPreferencesSettingsRepository(applicationContext) }
                val notificationRepository = remember { SharedPreferencesNotificationRepository(applicationContext) }
                
                val mainViewModelFactory = remember(settingsRepository, notificationRepository) {
                    com.ilsecondodasinistra.aboliamolorasolare.ui.MainViewModelFactory(
                        application = application,
                        getTimeChangesUseCase = com.ilsecondodasinistra.aboliamolorasolare.usecase.GetTimeChangesUseCase(TimeChangeCalculator()),
                        getNotificationSettingsUseCase = com.ilsecondodasinistra.aboliamolorasolare.usecase.GetNotificationSettingsUseCase(
                            notificationRepository
                        ),
                        setNotificationSettingUseCase = com.ilsecondodasinistra.aboliamolorasolare.usecase.SetNotificationSettingUseCase(
                            notificationRepository
                        ),
                        removeNotificationSettingUseCase = com.ilsecondodasinistra.aboliamolorasolare.usecase.RemoveNotificationSettingUseCase(
                            notificationRepository
                        ),
                        getSettingsUseCase = com.ilsecondodasinistra.aboliamolorasolare.usecase.GetSettingsUseCase(
                            settingsRepository
                        ),
                        notificationScheduler = NotificationScheduler(this@MainActivity)
                    )
                }
                
                val mainViewModel = androidx.lifecycle.viewmodel.compose.viewModel<MainViewModel>(
                    factory = mainViewModelFactory
                )
                
                val settingsViewModelFactory = remember(settingsRepository) {
                    com.ilsecondodasinistra.aboliamolorasolare.ui.SettingsViewModelFactory(
                        com.ilsecondodasinistra.aboliamolorasolare.usecase.GetSettingsUseCase(
                            settingsRepository
                        ),
                        com.ilsecondodasinistra.aboliamolorasolare.usecase.SetSettingsUseCase(
                            settingsRepository
                        )
                    )
                }

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
