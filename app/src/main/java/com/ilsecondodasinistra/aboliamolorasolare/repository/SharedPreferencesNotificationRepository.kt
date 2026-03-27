package com.ilsecondodasinistra.aboliamolorasolare.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.ilsecondodasinistra.aboliamolorasolare.model.NotificationSetting
import com.ilsecondodasinistra.aboliamolorasolare.model.TimeChangeEventId

class SharedPreferencesNotificationRepository(context: Context) : NotificationRepository {
    private val prefs: SharedPreferences = context.getSharedPreferences("notifications", Context.MODE_PRIVATE)

    override suspend fun getNotificationSettings(): List<NotificationSetting> {
        val xSet = prefs.getStringSet("x_notifications", emptySet()) ?: emptySet()
        val ySet = prefs.getStringSet("y_notifications", emptySet()) ?: emptySet()
        
        val allIds = xSet + ySet
        return allIds.map { idStr ->
            val parts = idStr.split("_")
            val dateMillis = parts[0].toLong()
            val type = parts[1]
            val eventId = TimeChangeEventId(dateMillis, type)
            NotificationSetting(
                eventId = eventId,
                notifyX = xSet.contains(idStr),
                notifyY = ySet.contains(idStr)
            )
        }
    }

    override suspend fun setNotificationSetting(setting: NotificationSetting) {
        val idStr = "${setting.eventId.dateMillis}_${setting.eventId.type}"
        val xSet = prefs.getStringSet("x_notifications", emptySet())?.toMutableSet() ?: mutableSetOf()
        val ySet = prefs.getStringSet("y_notifications", emptySet())?.toMutableSet() ?: mutableSetOf()
        
        if (setting.notifyX) xSet.add(idStr) else xSet.remove(idStr)
        if (setting.notifyY) ySet.add(idStr) else ySet.remove(idStr)
        
        prefs.edit {
            putStringSet("x_notifications", xSet)
            putStringSet("y_notifications", ySet)
        }
    }

    override suspend fun removeNotificationSetting(eventId: TimeChangeEventId) {
        val idStr = "${eventId.dateMillis}_${eventId.type}"
        val xSet = prefs.getStringSet("x_notifications", emptySet())?.toMutableSet() ?: mutableSetOf()
        val ySet = prefs.getStringSet("y_notifications", emptySet())?.toMutableSet() ?: mutableSetOf()
        
        xSet.remove(idStr)
        ySet.remove(idStr)
        
        prefs.edit {
            putStringSet("x_notifications", xSet)
            putStringSet("y_notifications", ySet)
        }
    }
}