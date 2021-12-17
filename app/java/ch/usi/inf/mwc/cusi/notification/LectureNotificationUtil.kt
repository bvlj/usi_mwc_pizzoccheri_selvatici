package ch.usi.inf.mwc.cusi.notification

import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object LectureNotificationUtil {
    private const val TAG = "LectureNotificationUtil"
    private const val DEBUG = true

    fun schedule(workManager: WorkManager) {
        Log.d(TAG, "Scheduling periodic requests")
        val periodicRequest =
            PeriodicWorkRequestBuilder<NotificationSchedulerWorker>(1, TimeUnit.DAYS)
                .build()
        workManager.enqueueUniquePeriodicWork(
            TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest
        )

        if (DEBUG) {
            Log.d(TAG, "Scheduling one–time request")
            workManager.enqueue(OneTimeWorkRequestBuilder<NotificationSchedulerWorker>().build())
        }
    }
}