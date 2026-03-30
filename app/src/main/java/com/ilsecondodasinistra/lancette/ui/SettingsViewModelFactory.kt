package com.ilsecondodasinistra.lancette.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ilsecondodasinistra.lancette.usecase.GetSettingsUseCase
import com.ilsecondodasinistra.lancette.usecase.GetTimeChangesUseCase
import com.ilsecondodasinistra.lancette.usecase.SetSettingsUseCase

class SettingsViewModelFactory(
    private val application: Application,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val setSettingsUseCase: SetSettingsUseCase,
    private val getTimeChangesUseCase: GetTimeChangesUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(
                application,
                getSettingsUseCase,
                setSettingsUseCase,
                getTimeChangesUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
