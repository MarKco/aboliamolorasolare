package com.ilsecondodasinistra.aboliamolorasolare.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ilsecondodasinistra.aboliamolorasolare.notification.AlarmSchedulerHelper
import com.ilsecondodasinistra.aboliamolorasolare.usecase.GetSettingsUseCase
import com.ilsecondodasinistra.aboliamolorasolare.usecase.GetTimeChangesUseCase
import com.ilsecondodasinistra.aboliamolorasolare.usecase.SetSettingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SettingsViewModel(
    application: Application,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val setSettingsUseCase: SetSettingsUseCase,
    private val getTimeChangesUseCase: GetTimeChangesUseCase
) : AndroidViewModel(application) {

    private val _x = MutableStateFlow<Int?>(null)
    val x: StateFlow<Int?> = _x
    private val _y = MutableStateFlow<Int?>(null)
    val y: StateFlow<Int?> = _y
    
    private val _isNotifyXEnabled = MutableStateFlow(true)
    val isNotifyXEnabled: StateFlow<Boolean> = _isNotifyXEnabled
    
    private val _isNotifyYEnabled = MutableStateFlow(true)
    val isNotifyYEnabled: StateFlow<Boolean> = _isNotifyYEnabled
    
    private val _isNotifyZEnabled = MutableStateFlow(true)
    val isNotifyZEnabled: StateFlow<Boolean> = _isNotifyZEnabled

    private val _isFastNotificationsEnabled = MutableStateFlow(false)
    val isFastNotificationsEnabled: StateFlow<Boolean> = _isFastNotificationsEnabled

    private val _nextScheduledX = MutableStateFlow<String?>(null)
    val nextScheduledX: StateFlow<String?> = _nextScheduledX
    private val _nextScheduledY = MutableStateFlow<String?>(null)
    val nextScheduledY: StateFlow<String?> = _nextScheduledY
    private val _nextScheduledZ = MutableStateFlow<String?>(null)
    val nextScheduledZ: StateFlow<String?> = _nextScheduledZ

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())

    fun load() {
        viewModelScope.launch {
            _x.value = getSettingsUseCase.getX()
            _y.value = getSettingsUseCase.getY()
            _isNotifyXEnabled.value = getSettingsUseCase.isNotifyXEnabled()
            _isNotifyYEnabled.value = getSettingsUseCase.isNotifyYEnabled()
            _isNotifyZEnabled.value = getSettingsUseCase.isNotifyZEnabled()
            _isFastNotificationsEnabled.value = getSettingsUseCase.isFastNotificationsEnabled()
            updateScheduledTimes()
        }
    }

    fun setX(x: Int) {
        _x.value = x
        viewModelScope.launch {
            setSettingsUseCase.setX(x)
            reschedule()
        }
    }

    fun setY(y: Int) {
        _y.value = y
        viewModelScope.launch {
            setSettingsUseCase.setY(y)
            reschedule()
        }
    }
    
    fun setNotifyXEnabled(enabled: Boolean) {
        _isNotifyXEnabled.value = enabled
        viewModelScope.launch {
            setSettingsUseCase.setNotifyXEnabled(enabled)
            reschedule()
        }
    }
    
    fun setNotifyYEnabled(enabled: Boolean) {
        _isNotifyYEnabled.value = enabled
        viewModelScope.launch {
            setSettingsUseCase.setNotifyYEnabled(enabled)
            reschedule()
        }
    }
    
    fun setNotifyZEnabled(enabled: Boolean) {
        _isNotifyZEnabled.value = enabled
        viewModelScope.launch {
            setSettingsUseCase.setNotifyZEnabled(enabled)
            reschedule()
        }
    }

    fun setFastNotificationsEnabled(enabled: Boolean) {
        _isFastNotificationsEnabled.value = enabled
        viewModelScope.launch {
            setSettingsUseCase.setFastNotificationsEnabled(enabled)
            reschedule()
        }
    }

    private suspend fun updateScheduledTimes() {
        val nextEvent = getTimeChangesUseCase.execute(Calendar.getInstance()).next.firstOrNull() ?: return
        val isFast = _isFastNotificationsEnabled.value
        val now = System.currentTimeMillis()

        if (_isNotifyXEnabled.value) {
            val trigger = if (isFast) now + 5000L else calculateTrigger(nextEvent.date, -(_x.value ?: 5))
            _nextScheduledX.value = dateFormat.format(trigger)
        } else {
            _nextScheduledX.value = null
        }

        if (_isNotifyYEnabled.value) {
            val trigger = if (isFast) now + 10000L else calculateTrigger(nextEvent.date, -(_y.value ?: 10))
            _nextScheduledY.value = dateFormat.format(trigger)
        } else {
            _nextScheduledY.value = null
        }

        if (_isNotifyZEnabled.value) {
            val trigger = if (isFast) now + 15000L else calculateTrigger(nextEvent.date, 0)
            _nextScheduledZ.value = dateFormat.format(trigger)
        } else {
            _nextScheduledZ.value = null
        }
    }

    private fun calculateTrigger(cal: Calendar, offset: Int): Long {
        return (cal.clone() as Calendar).apply { 
            add(Calendar.DAY_OF_MONTH, offset)
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
    
    private suspend fun reschedule() {
        AlarmSchedulerHelper(getApplication()).rescheduleAll()
        updateScheduledTimes()
    }
}
