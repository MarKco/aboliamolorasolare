package com.ilsecondodasinistra.aboliamolorasolare.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ilsecondodasinistra.aboliamolorasolare.usecase.GetSettingsUseCase
import com.ilsecondodasinistra.aboliamolorasolare.usecase.SetSettingsUseCase

class SettingsViewModelFactory(
    private val application: Application,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val setSettingsUseCase: SetSettingsUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(
                application,
                getSettingsUseCase,
                setSettingsUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
