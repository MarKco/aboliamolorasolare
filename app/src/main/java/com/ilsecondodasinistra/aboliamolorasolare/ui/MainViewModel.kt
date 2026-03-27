package com.ilsecondodasinistra.aboliamolorasolare.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ilsecondodasinistra.aboliamolorasolare.BuildConfig
import com.ilsecondodasinistra.aboliamolorasolare.R
import com.ilsecondodasinistra.aboliamolorasolare.TimeChangeEvent
import com.ilsecondodasinistra.aboliamolorasolare.TimeChangeResult
import com.ilsecondodasinistra.aboliamolorasolare.TimeChangeType
import com.ilsecondodasinistra.aboliamolorasolare.model.NotificationSetting
import com.ilsecondodasinistra.aboliamolorasolare.model.TimeChangeEventId
import com.ilsecondodasinistra.aboliamolorasolare.notification.NotificationScheduler
import com.ilsecondodasinistra.aboliamolorasolare.usecase.GetNotificationSettingsUseCase
import com.ilsecondodasinistra.aboliamolorasolare.usecase.GetSettingsUseCase
import com.ilsecondodasinistra.aboliamolorasolare.usecase.GetTimeChangesUseCase
import com.ilsecondodasinistra.aboliamolorasolare.usecase.RemoveNotificationSettingUseCase
import com.ilsecondodasinistra.aboliamolorasolare.usecase.SetNotificationSettingUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class MainViewModel(
    application: Application,
    private val getTimeChangesUseCase: GetTimeChangesUseCase,
    private val getNotificationSettingsUseCase: GetNotificationSettingsUseCase,
    private val setNotificationSettingUseCase: SetNotificationSettingUseCase,
    private val removeNotificationSettingUseCase: RemoveNotificationSettingUseCase,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val notificationScheduler: NotificationScheduler? = null,
    private val timeChangeEventsProvider: (() -> List<TimeChangeEvent>)? = null 
) : AndroidViewModel(application) {
    private val _timeChanges = MutableStateFlow<TimeChangeResult?>(null)
    val timeChanges: StateFlow<TimeChangeResult?> = _timeChanges

    private val _notificationSettings = MutableStateFlow<List<NotificationSetting>>(emptyList())
    val notificationSettings: StateFlow<List<NotificationSetting>> = _notificationSettings

    private val _x = MutableStateFlow<Int?>(null)
    val x: StateFlow<Int?> = _x
    private val _y = MutableStateFlow<Int?>(null)
    val y: StateFlow<Int?> = _y

    fun load(baseDate: Calendar? = null) {
        val date = baseDate ?: Calendar.getInstance()
        _timeChanges.value = getTimeChangesUseCase.execute(date)
        viewModelScope.launch {
            _notificationSettings.value = getNotificationSettingsUseCase.execute()
            _x.value = getSettingsUseCase.getX()
            _y.value = getSettingsUseCase.getY()
        }
    }

    fun setNotification(setting: NotificationSetting) {
        // Aggiornamento ottimistico: emettiamo subito la lista modificata per la UI
        val newList = _notificationSettings.value.filter { it.eventId != setting.eventId }.toMutableList()
        newList.add(setting)
        _notificationSettings.value = newList

        viewModelScope.launch {
            setNotificationSettingUseCase.execute(setting)
            _notificationSettings.value = getNotificationSettingsUseCase.execute()
            
            notificationScheduler?.let { scheduler ->
                val event = findEventById(setting.eventId)
                val xVal = _x.value ?: 5
                val yVal = _y.value ?: 10
                if (event != null) {
                    val eventTypeName = if (event.type == TimeChangeType.LEGALE) 
                        getApplication<Application>().getString(R.string.summer_time) 
                    else 
                        getApplication<Application>().getString(R.string.winter_time)

                    if (setting.notifyX) {
                        val trigger = if (BuildConfig.DEBUG) System.currentTimeMillis() + 10000L else calculateTrigger(event.date, -xVal)
                        
                        // Solo se il trigger è nel futuro impostiamo la notifica, altrimenti l'AlarmManager farà scattare la notifica immediatamente
                        if (trigger > System.currentTimeMillis()) {
                            scheduler.scheduleNotification("${setting.eventId.dateMillis}_${setting.eventId.type}_X", trigger, 
                                getApplication<Application>().getString(if (BuildConfig.DEBUG) R.string.notification_debug_title else R.string.notification_title, xVal), 
                                getApplication<Application>().getString(if (BuildConfig.DEBUG) R.string.notification_debug_message else R.string.notification_message, xVal, eventTypeName))
                        }
                    }
                    if (setting.notifyY) {
                        val trigger = if (BuildConfig.DEBUG) System.currentTimeMillis() + 20000L else calculateTrigger(event.date, -yVal)
                        
                        if (trigger > System.currentTimeMillis()) {
                            scheduler.scheduleNotification("${setting.eventId.dateMillis}_${setting.eventId.type}_Y", trigger, 
                                getApplication<Application>().getString(if (BuildConfig.DEBUG) R.string.notification_debug_title else R.string.notification_title, yVal), 
                                getApplication<Application>().getString(if (BuildConfig.DEBUG) R.string.notification_debug_message else R.string.notification_message, yVal, eventTypeName))
                        }
                    }
                }
            }
        }
    }

    fun removeNotification(eventId: TimeChangeEventId) {
        _notificationSettings.value = _notificationSettings.value.filter { it.eventId != eventId }

        viewModelScope.launch {
            removeNotificationSettingUseCase.execute(eventId)
            _notificationSettings.value = getNotificationSettingsUseCase.execute()
            notificationScheduler?.let {
                it.cancelNotification("${eventId.dateMillis}_${eventId.type}_X")
                it.cancelNotification("${eventId.dateMillis}_${eventId.type}_Y")
            }
        }
    }

    private fun calculateTrigger(cal: Calendar, offset: Int): Long {
        return (cal.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, offset); set(Calendar.HOUR_OF_DAY, 9) }.timeInMillis
    }

    private fun findEventById(id: TimeChangeEventId): TimeChangeEvent? {
        val events = timeChangeEventsProvider?.invoke() ?: _timeChanges.value?.let { it.previous + it.next } ?: emptyList()
        return events.find { it.date.timeInMillis == id.dateMillis && it.type.name == id.type }
    }

    fun activateAllNotifications() {
        _timeChanges.value?.next?.forEach { event ->
            setNotification(NotificationSetting(TimeChangeEventId(event.date.timeInMillis, event.type.name), true, true))
        }
    }

    fun deactivateAllNotifications() {
        _timeChanges.value?.next?.forEach { event ->
            removeNotification(TimeChangeEventId(event.date.timeInMillis, event.type.name))
        }
    }
}
