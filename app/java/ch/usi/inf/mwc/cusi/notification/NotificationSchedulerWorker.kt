package ch.usi.inf.mwc.cusi.notification

import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import androidx.work.*
import ch.usi.inf.mwc.cusi.db.AppDatabase
import ch.usi.inf.mwc.cusi.preferences.Preferences
import java.time.*
import java.util.concurrent.TimeUnit

class NotificationSchedulerWorker(
    context: Context,
    parameters: WorkerParameters,
) : CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result {
        val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        if (!preferences.getBoolean(Preferences.KEY_NOTIFICATIONS, false)) {
            // Do not schedule notifications if feature is disabled
            return Result.success()
        }

        val db = AppDatabase.getInstance(applicationContext)
        val start = LocalDateTime.now()
        val end = LocalDate.now().atTime(23, 59, 59)
        val zone = ZoneId.systemDefault()

        Log.d(TAG, "Scheduling lecture notifications")

        val wm = WorkManager.getInstance(applicationContext)
        wm.cancelAllWorkByTag(TAG)

        db.lectures().selectAllEnrolledWithinDates(start, end).forEach { lecture ->
            val courseInfo = db.course().getCourseInfo(lecture.courseId)
            val startInEpochSeconds = lecture.start.atZone(zone).toEpochSecond()

            val delay = startInEpochSeconds -
                    start.atZone(zone).toEpochSecond() -
                    BEFORE_LECTURE

            Log.d(TAG, "Scheduling for $lecture in $delay seconds")

            val req = OneTimeWorkRequest.Builder(LectureWorker::class.java)
                .setConstraints(Constraints.NONE)
                .setInitialDelay(delay, TimeUnit.SECONDS)
                .setInputData(
                    Data.Builder()
                        .putString(LectureWorker.KEY_NAME, courseInfo?.name)
                        .putString(LectureWorker.KEY_ADDRESS, lecture.lectureLocation.address)
                        .putString(LectureWorker.KEY_ROOM, lecture.lectureLocation.room)
                        .putLong(LectureWorker.KEY_TIMESTAMP, startInEpochSeconds * 1000L)
                        .build()
                )
                .build()
            wm.enqueue(req)
        }

        return Result.success()
    }

    private companion object {
        const val TAG = "NotificationSchedulerWorker"

        // 30 mins in seconds
        const val BEFORE_LECTURE = 60L * 30L
    }
}