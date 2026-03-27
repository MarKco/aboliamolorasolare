package com.ilsecondodasinistra.aboliamolorasolare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ilsecondodasinistra.aboliamolorasolare.ui.theme.AboliamoLoraSolareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AboliamoLoraSolareTheme {
                // Manual ViewModel creation (replace with DI in production)
                val mainViewModel = com.ilsecondodasinistra.aboliamolorasolare.ui.MainViewModel(
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
                        com.ilsecondodasinistra.aboliamolorasolare.repository.InMemorySettingsRepository()
                    )
                )
                val settingsViewModel = com.ilsecondodasinistra.aboliamolorasolare.ui.SettingsViewModel(
                    com.ilsecondodasinistra.aboliamolorasolare.usecase.GetSettingsUseCase(
                        com.ilsecondodasinistra.aboliamolorasolare.repository.InMemorySettingsRepository()
                    ),
                    com.ilsecondodasinistra.aboliamolorasolare.usecase.SetSettingsUseCase(
                        com.ilsecondodasinistra.aboliamolorasolare.repository.InMemorySettingsRepository()
                    )
                )
                com.ilsecondodasinistra.aboliamolorasolare.ui.AppNavGraph(
                    mainViewModel = mainViewModel,
                    settingsViewModel = settingsViewModel
                )
            }
        }
    }
}
