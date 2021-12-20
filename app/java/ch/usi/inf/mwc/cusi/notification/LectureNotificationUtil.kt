package ch.usi.inf.mwc.cusi.notification

import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object LectureNotificationUtil {
    private const val TAG = "LectureNotificationUtil"

    fun schedule(workManager: WorkManager) {
        Log.d(TAG, "Scheduling periodic requests")
        // Schedule every day
        val periodicRequest =
            PeriodicWorkRequestBuilder<NotificationSchedulerWorker>(1, TimeUnit.DAYS)
                .build()
        workManager.enqueueUniquePeriodicWork(
            TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest
        )

        // Perform one now for today's schedule
        Log.d(TAG, "Scheduling one–time request")
        workManager.enqueue(OneTimeWorkRequestBuilder<NotificationSchedulerWorker>().build())
    }
}