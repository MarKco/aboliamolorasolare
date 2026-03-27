package com.ilsecondodasinistra.aboliamolorasolare.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ilsecondodasinistra.aboliamolorasolare.notification.NotificationScheduler
import com.ilsecondodasinistra.aboliamolorasolare.usecase.GetNotificationSettingsUseCase
import com.ilsecondodasinistra.aboliamolorasolare.usecase.GetSettingsUseCase
import com.ilsecondodasinistra.aboliamolorasolare.usecase.GetTimeChangesUseCase
import com.ilsecondodasinistra.aboliamolorasolare.usecase.RemoveNotificationSettingUseCase
import com.ilsecondodasinistra.aboliamolorasolare.usecase.SetNotificationSettingUseCase

class MainViewModelFactory(
    private val getTimeChangesUseCase: GetTimeChangesUseCase,
    private val getNotificationSettingsUseCase: GetNotificationSettingsUseCase,
    private val setNotificationSettingUseCase: SetNotificationSettingUseCase,
    private val removeNotificationSettingUseCase: RemoveNotificationSettingUseCase,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val notificationScheduler: NotificationScheduler? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(
                getTimeChangesUseCase,
                getNotificationSettingsUseCase,
                setNotificationSettingUseCase,
                removeNotificationSettingUseCase,
                getSettingsUseCase,
                notificationScheduler
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

