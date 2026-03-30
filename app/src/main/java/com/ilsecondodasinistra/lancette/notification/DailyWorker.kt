package com.ilsecondodasinistra.lancette.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class DailyWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        // Ricalcola tutto. Se il cambio dell'ora è passato, schedulerà quello successivo.
        AlarmSchedulerHelper(context).rescheduleAll()
        return Result.success()
    }
}
