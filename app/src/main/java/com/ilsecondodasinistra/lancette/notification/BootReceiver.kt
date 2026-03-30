package com.ilsecondodasinistra.lancette.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            // Riprogrammiamo tutte le notifiche salvate in un colpo solo
            val helper = AlarmSchedulerHelper(context)
            CoroutineScope(Dispatchers.IO).launch {
                helper.rescheduleAll()
            }
        }
    }
}
