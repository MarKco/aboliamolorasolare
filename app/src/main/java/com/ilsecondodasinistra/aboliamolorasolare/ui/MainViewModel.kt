package com.ilsecondodasinistra.aboliamolorasolare.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilsecondodasinistra.aboliamolorasolare.TimeChangeResult
import com.ilsecondodasinistra.aboliamolorasolare.model.NotificationSetting
import com.ilsecondodasinistra.aboliamolorasolare.model.TimeChangeEventId
import com.ilsecondodasinistra.aboliamolorasolare.usecase.GetNotificationSettingsUseCase
import com.ilsecondodasinistra.aboliamolorasolare.usecase.GetSettingsUseCase
import com.ilsecondodasinistra.aboliamolorasolare.usecase.GetTimeChangesUseCase
import com.ilsecondodasinistra.aboliamolorasolare.usecase.RemoveNotificationSettingUseCase
import com.ilsecondodasinistra.aboliamolorasolare.usecase.SetNotificationSettingUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainViewModel(
    private val getTimeChangesUseCase: GetTimeChangesUseCase,
    private val getNotificationSettingsUseCase: GetNotificationSettingsUseCase,
    private val setNotificationSettingUseCase: SetNotificationSettingUseCase,
    private val removeNotificationSettingUseCase: RemoveNotificationSettingUseCase,
    private val getSettingsUseCase: GetSettingsUseCase
) : ViewModel() {
    private val _timeChanges = MutableStateFlow<TimeChangeResult?>(null)
    val timeChanges: StateFlow<TimeChangeResult?> = _timeChanges

    private val _notificationSettings = MutableStateFlow<List<NotificationSetting>>(emptyList())
    val notificationSettings: StateFlow<List<NotificationSetting>> = _notificationSettings

    private val _x = MutableStateFlow<Int?>(null)
    val x: StateFlow<Int?> = _x
    private val _y = MutableStateFlow<Int?>(null)
    val y: StateFlow<Int?> = _y

    fun load(baseDate: LocalDate = LocalDate.now(java.time.Clock.systemDefaultZone())) {
        _timeChanges.value = getTimeChangesUseCase.execute(baseDate)
        viewModelScope.launch {
            _notificationSettings.value = getNotificationSettingsUseCase.execute()
            _x.value = getSettingsUseCase.getX()
            _y.value = getSettingsUseCase.getY()
        }
    }

    fun setNotification(setting: NotificationSetting) {
        viewModelScope.launch {
            setNotificationSettingUseCase.execute(setting)
            _notificationSettings.value = getNotificationSettingsUseCase.execute()
        }
    }

    fun removeNotification(eventId: TimeChangeEventId) {
        viewModelScope.launch {
            removeNotificationSettingUseCase.execute(eventId)
            _notificationSettings.value = getNotificationSettingsUseCase.execute()
        }
    }
}


