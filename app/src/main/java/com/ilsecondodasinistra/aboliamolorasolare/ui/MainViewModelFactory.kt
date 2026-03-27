package com.ilsecondodasinistra.aboliamolorasolare.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ilsecondodasinistra.aboliamolorasolare.usecase.GetSettingsUseCase
import com.ilsecondodasinistra.aboliamolorasolare.usecase.GetTimeChangesUseCase

class MainViewModelFactory(
    private val application: Application,
    private val getTimeChangesUseCase: GetTimeChangesUseCase,
    private val getSettingsUseCase: GetSettingsUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(
                application,
                getTimeChangesUseCase,
                getSettingsUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
