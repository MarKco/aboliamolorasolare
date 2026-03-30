package com.ilsecondodasinistra.lancette.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ilsecondodasinistra.lancette.TimeChangeResult
import com.ilsecondodasinistra.lancette.notification.AlarmSchedulerHelper
import com.ilsecondodasinistra.lancette.usecase.GetSettingsUseCase
import com.ilsecondodasinistra.lancette.usecase.GetTimeChangesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class MainViewModel(
    application: Application,
    private val getTimeChangesUseCase: GetTimeChangesUseCase,
    private val getSettingsUseCase: GetSettingsUseCase
) : AndroidViewModel(application) {

    private val _timeChanges = MutableStateFlow<TimeChangeResult?>(null)
    val timeChanges: StateFlow<TimeChangeResult?> = _timeChanges

    private val _isGlobalNotificationsEnabled = MutableStateFlow(false)
    val isGlobalNotificationsEnabled: StateFlow<Boolean> = _isGlobalNotificationsEnabled

    fun load() {
        _timeChanges.value = getTimeChangesUseCase.execute(Calendar.getInstance())
        viewModelScope.launch {
            _isGlobalNotificationsEnabled.value = getSettingsUseCase.isGlobalNotificationsEnabled()
        }
    }

    fun toggleGlobalNotifications() {
        val newState = !_isGlobalNotificationsEnabled.value
        _isGlobalNotificationsEnabled.value = newState
        
        viewModelScope.launch {
            getSettingsUseCase.setGlobalNotificationsEnabled(newState)
            // Schedula o cancella gli allarmi tramite l'helper
            AlarmSchedulerHelper(getApplication()).rescheduleAll()
        }
    }
}
