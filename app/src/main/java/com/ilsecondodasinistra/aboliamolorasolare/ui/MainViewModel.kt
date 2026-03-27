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
    private val timeChangeEventsProvider: (() -> List<TimeChangeEvent>)? = null // per testabilità
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
        viewModelScope.launch {
            setNotificationSettingUseCase.execute(setting)
            _notificationSettings.value = getNotificationSettingsUseCase.execute()
            // Schedula la notifica reale se richiesto
            notificationScheduler?.let { scheduler ->
                val event = findEventById(setting.eventId)
                val xVal = _x.value ?: 5
                val yVal = _y.value ?: 10
                if (event != null) {
                    val now = System.currentTimeMillis()
                    val eventTypeName = if (event.type == TimeChangeType.LEGALE) 
                        getApplication<Application>().getString(R.string.summer_time) 
                    else 
                        getApplication<Application>().getString(R.string.winter_time)

                    if (setting.notifyX) {
                        val triggerMillis = if (BuildConfig.DEBUG) {
                            now + 10_000L
                        } else {
                            calculateTriggerMillis(
                                event.date.get(Calendar.YEAR),
                                event.date.get(Calendar.MONTH) + 1,
                                event.date.get(Calendar.DAY_OF_MONTH),
                                -xVal
                            )
                        }
                        
                        val title = if (BuildConfig.DEBUG) 
                            getApplication<Application>().getString(R.string.notification_debug_title, 10)
                        else 
                            getApplication<Application>().getString(R.string.notification_title, xVal)
                            
                        val message = if (BuildConfig.DEBUG)
                            getApplication<Application>().getString(R.string.notification_debug_message, 10, eventTypeName)
                        else
                            getApplication<Application>().getString(R.string.notification_message, xVal, eventTypeName)

                        scheduler.scheduleNotification(
                            eventId = "${setting.eventId.date}_${setting.eventId.type}_X",
                            triggerAtMillis = triggerMillis,
                            title = title,
                            message = message
                        )
                    }
                    if (setting.notifyY) {
                        val triggerMillis = if (BuildConfig.DEBUG) {
                            now + 20_000L
                        } else {
                            calculateTriggerMillis(
                                event.date.get(Calendar.YEAR),
                                event.date.get(Calendar.MONTH) + 1,
                                event.date.get(Calendar.DAY_OF_MONTH),
                                -yVal
                            )
                        }
                        
                        val title = if (BuildConfig.DEBUG)
                            getApplication<Application>().getString(R.string.notification_debug_title, 20)
                        else
                            getApplication<Application>().getString(R.string.notification_title, yVal)

                        val message = if (BuildConfig.DEBUG)
                            getApplication<Application>().getString(R.string.notification_debug_message, 20, eventTypeName)
                        else
                            getApplication<Application>().getString(R.string.notification_message, yVal, eventTypeName)

                        scheduler.scheduleNotification(
                            eventId = "${setting.eventId.date}_${setting.eventId.type}_Y",
                            triggerAtMillis = triggerMillis,
                            title = title,
                            message = message
                        )
                    }
                }
            }
        }
    }

    fun removeNotification(eventId: TimeChangeEventId) {
        viewModelScope.launch {
            removeNotificationSettingUseCase.execute(eventId)
            _notificationSettings.value = getNotificationSettingsUseCase.execute()
            // Cancella entrambe le notifiche X e Y
            notificationScheduler?.let { scheduler ->
                scheduler.cancelNotification("${eventId.date}_${eventId.type}_X")
                scheduler.cancelNotification("${eventId.date}_${eventId.type}_Y")
            }
        }
    }

    // Calcola i millis di trigger per la notifica (compatibile minSdk 24)
    private fun calculateTriggerMillis(year: Int, month: Int, day: Int, daysOffset: Int): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month - 1)
        cal.set(Calendar.DAY_OF_MONTH, day)
        cal.set(Calendar.HOUR_OF_DAY, 9)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.add(Calendar.DAY_OF_MONTH, daysOffset)
        return cal.timeInMillis
    }

    private fun findEventById(eventId: TimeChangeEventId): TimeChangeEvent? {
        val events = timeChangeEventsProvider?.invoke() ?: _timeChanges.value?.let { it.previous + it.next } ?: emptyList()
        return events.find {
            it.date.get(Calendar.YEAR) == eventId.date.get(Calendar.YEAR)
                    && it.date.get(Calendar.MONTH) == eventId.date.get(Calendar.MONTH)
                    && it.date.get(Calendar.DAY_OF_MONTH) == eventId.date.get(Calendar.DAY_OF_MONTH)
                    && it.type.name == eventId.type
        }
    }

    fun activateAllNotifications() {
        val events = timeChangeEventsProvider?.invoke() ?: _timeChanges.value?.next ?: emptyList()
        _x.value ?: 5
        _y.value ?: 10
        viewModelScope.launch {
            events.forEach { event ->
                setNotification(NotificationSetting(
                    eventId = TimeChangeEventId(event.date, event.type.name),
                    notifyX = true,
                    notifyY = true
                ))
            }
        }
    }

    fun deactivateAllNotifications() {
        val events = timeChangeEventsProvider?.invoke() ?: _timeChanges.value?.next ?: emptyList()
        viewModelScope.launch {
            events.forEach { event ->
                removeNotification(TimeChangeEventId(event.date, event.type.name))
            }
        }
    }
}
