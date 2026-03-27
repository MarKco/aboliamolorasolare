package com.ilsecondodasinistra.aboliamolorasolare.model

/**
 * Identificatore univoco per ogni evento di cambio ora (timestamp del giorno a mezzanotte + tipo)
 * Usiamo Long per la data per garantire stabilità e facilità di confronto in Compose.
 */
data class TimeChangeEventId(val dateMillis: Long, val type: String)

/**
 * Impostazione di notifica per un evento di cambio ora
 */
data class NotificationSetting(
    val eventId: TimeChangeEventId,
    val notifyX: Boolean = false,
    val notifyY: Boolean = false
)
