package com.ilsecondodasinistra.aboliamolorasolare.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ilsecondodasinistra.aboliamolorasolare.usecase.GetSettingsUseCase
import com.ilsecondodasinistra.aboliamolorasolare.usecase.SetSettingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val setSettingsUseCase: SetSettingsUseCase
) : ViewModel() {
    private val _x = MutableStateFlow<Int?>(null)
    val x: StateFlow<Int?> = _x
    private val _y = MutableStateFlow<Int?>(null)
    val y: StateFlow<Int?> = _y

    fun load() {
        viewModelScope.launch {
            _x.value = getSettingsUseCase.getX()
            _y.value = getSettingsUseCase.getY()
        }
    }

    fun setX(newX: Int) {
        viewModelScope.launch {
            setSettingsUseCase.setX(newX)
            load() // Forza il refresh dei valori dopo la scrittura
        }
    }

    fun setY(newY: Int) {
        viewModelScope.launch {
            setSettingsUseCase.setY(newY)
            load() // Forza il refresh dei valori dopo la scrittura
        }
    }
}

