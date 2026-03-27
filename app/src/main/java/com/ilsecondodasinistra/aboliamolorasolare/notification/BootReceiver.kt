package com.ilsecondodasinistra.aboliamolorasolare.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ilsecondodasinistra.aboliamolorasolare.R
import com.ilsecondodasinistra.aboliamolorasolare.TimeChangeCalculator
import com.ilsecondodasinistra.aboliamolorasolare.TimeChangeType
import com.ilsecondodasinistra.aboliamolorasolare.repository.SharedPreferencesNotificationRepository
import com.ilsecondodasinistra.aboliamolorasolare.repository.SharedPreferencesSettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            // Riprogrammiamo tutte le notifiche salvate
            val settingsRepo = SharedPreferencesSettingsRepository(context)
            val notificationRepo = SharedPreferencesNotificationRepository(context)
            val scheduler = NotificationScheduler(context)
            val calculator = TimeChangeCalculator()

            CoroutineScope(Dispatchers.IO).launch {
                val xVal = settingsRepo.getX()
                val yVal = settingsRepo.getY()
                val settings = notificationRepo.getNotificationSettings()
                
                // Per ricalcolare i giorni e i testi serve la lista degli eventi completi
                val allEvents = calculator.getTimeChanges(Calendar.getInstance()).let { it.previous + it.next }

                for (setting in settings) {
                    val event = allEvents.find { it.date.timeInMillis == setting.eventId.dateMillis && it.type.name == setting.eventId.type }
                    if (event != null) {
                        val eventTypeName = if (event.type == TimeChangeType.LEGALE) 
                            context.getString(R.string.summer_time) 
                        else 
                            context.getString(R.string.winter_time)

                        if (setting.notifyX) {
                            val trigger = calculateTrigger(event.date, -xVal)
                            if (trigger > System.currentTimeMillis()) {
                                scheduler.scheduleNotification("${setting.eventId.dateMillis}_${setting.eventId.type}_X", trigger, 
                                    context.getString(R.string.notification_title, xVal), 
                                    context.getString(R.string.notification_message, xVal, eventTypeName))
                            }
                        }
                        if (setting.notifyY) {
                            val trigger = calculateTrigger(event.date, -yVal)
                            if (trigger > System.currentTimeMillis()) {
                                scheduler.scheduleNotification("${setting.eventId.dateMillis}_${setting.eventId.type}_Y", trigger, 
                                    context.getString(R.string.notification_title, yVal), 
                                    context.getString(R.string.notification_message, yVal, eventTypeName))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun calculateTrigger(cal: Calendar, offset: Int): Long {
        return (cal.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, offset); set(Calendar.HOUR_OF_DAY, 9) }.timeInMillis
    }
}
