package com.ilsecondodasinistra.aboliamolorasolare.notification

import android.content.Context
import com.ilsecondodasinistra.aboliamolorasolare.BuildConfig
import com.ilsecondodasinistra.aboliamolorasolare.R
import com.ilsecondodasinistra.aboliamolorasolare.TimeChangeCalculator
import com.ilsecondodasinistra.aboliamolorasolare.TimeChangeType
import com.ilsecondodasinistra.aboliamolorasolare.repository.SharedPreferencesSettingsRepository
import java.util.Calendar

class AlarmSchedulerHelper(private val context: Context) {
    
    suspend fun rescheduleAll() {
        val settingsRepo = SharedPreferencesSettingsRepository(context)
        val isGlobalEnabled = settingsRepo.isGlobalNotificationsEnabled()
        
        val scheduler = NotificationScheduler(context)
        
        // Per prima cosa disattiviamo e annulliamo qualsiasi allarme in coda.
        scheduler.cancelNotification("NEXT_X")
        scheduler.cancelNotification("NEXT_Y")
        scheduler.cancelNotification("NEXT_Z")
        
        // Se le notifiche globali sono disattivate, non c'è altro da fare.
        if (!isGlobalEnabled) return
        
        // Se sono attivate, leggiamo i settings individuali.
        val xDays = settingsRepo.getX()
        val yDays = settingsRepo.getY()
        val isXEnabled = settingsRepo.isNotifyXEnabled()
        val isYEnabled = settingsRepo.isNotifyYEnabled()
        val isZEnabled = settingsRepo.isNotifyZEnabled()
        
        // Calcoliamo il *prossimo* cambio dell'ora.
        val calculator = TimeChangeCalculator()
        val nextEvent = calculator.getTimeChanges(Calendar.getInstance()).next.firstOrNull() ?: return
        
        val eventTypeName = if (nextEvent.type == TimeChangeType.LEGALE) 
            context.getString(R.string.summer_time) 
        else 
            context.getString(R.string.winter_time)
            
        // Schedulazione Notifica X
        if (isXEnabled) {
            val secondsDelay = 10
            val trigger = if (BuildConfig.DEBUG) System.currentTimeMillis() + secondsDelay * 1000L else calculateTrigger(nextEvent.date, -xDays)
            
            val title = if (BuildConfig.DEBUG) 
                context.getString(R.string.notification_debug_title, secondsDelay)
            else 
                context.getString(R.string.notification_title, xDays)
            
            val message = if (BuildConfig.DEBUG)
                context.getString(R.string.notification_debug_message, secondsDelay, eventTypeName)
            else
                context.getString(R.string.notification_message, xDays, eventTypeName)

            if (trigger > System.currentTimeMillis()) {
                scheduler.scheduleNotification("NEXT_X", trigger, title, message)
            }
        }
        
        // Schedulazione Notifica Y
        if (isYEnabled) {
            val secondsDelay = 20
            val trigger = if (BuildConfig.DEBUG) System.currentTimeMillis() + secondsDelay * 1000L else calculateTrigger(nextEvent.date, -yDays)
            
            val title = if (BuildConfig.DEBUG) 
                context.getString(R.string.notification_debug_title, secondsDelay)
            else 
                context.getString(R.string.notification_title, yDays)
            
            val message = if (BuildConfig.DEBUG)
                context.getString(R.string.notification_debug_message, secondsDelay, eventTypeName)
            else
                context.getString(R.string.notification_message, yDays, eventTypeName)

            if (trigger > System.currentTimeMillis()) {
                scheduler.scheduleNotification("NEXT_Y", trigger, title, message)
            }
        }
        
        // Schedulazione Notifica Z (la mattina del cambio dell'ora)
        if (isZEnabled) {
            val secondsDelay = 30
            val trigger = if (BuildConfig.DEBUG) System.currentTimeMillis() + secondsDelay * 1000L else calculateTrigger(nextEvent.date, 0)
            
            val title = if (BuildConfig.DEBUG) 
                context.getString(R.string.notification_debug_title, secondsDelay)
            else 
                context.getString(R.string.notification_title_today)
            
            val message = if (BuildConfig.DEBUG)
                context.getString(R.string.notification_debug_message, secondsDelay, eventTypeName)
            else
                context.getString(R.string.notification_message_today, eventTypeName)

            if (trigger > System.currentTimeMillis()) {
                scheduler.scheduleNotification("NEXT_Z", trigger, title, message)
            }
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
}
