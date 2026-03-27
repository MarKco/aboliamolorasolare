package com.ilsecondodasinistra.aboliamolorasolare.model
import java.util.Calendar

/**
 * Identificatore univoco per ogni evento di cambio ora (data + tipo)
 */
data class TimeChangeEventId(val date: Calendar, val type: String)

/**
 * Impostazione di notifica per un evento di cambio ora
 */
data class NotificationSetting(
    val eventId: TimeChangeEventId,
    val notifyX: Boolean = false,
    val notifyY: Boolean = false
)
