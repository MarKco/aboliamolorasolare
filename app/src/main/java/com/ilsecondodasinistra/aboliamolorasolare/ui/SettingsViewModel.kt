package com.ilsecondodasinistra.aboliamolorasolare.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ilsecondodasinistra.aboliamolorasolare.notification.AlarmSchedulerHelper
import com.ilsecondodasinistra.aboliamolorasolare.usecase.GetSettingsUseCase
import com.ilsecondodasinistra.aboliamolorasolare.usecase.SetSettingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    application: Application,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val setSettingsUseCase: SetSettingsUseCase
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

    fun load() {
        viewModelScope.launch {
            _x.value = getSettingsUseCase.getX()
            _y.value = getSettingsUseCase.getY()
            _isNotifyXEnabled.value = getSettingsUseCase.isNotifyXEnabled()
            _isNotifyYEnabled.value = getSettingsUseCase.isNotifyYEnabled()
            _isNotifyZEnabled.value = getSettingsUseCase.isNotifyZEnabled()
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
    
    private suspend fun reschedule() {
        AlarmSchedulerHelper(getApplication()).rescheduleAll()
    }
}
