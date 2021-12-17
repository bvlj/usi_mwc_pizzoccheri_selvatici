package ch.usi.inf.mwc.cusi.notification

import android.content.Context
import android.util.Log
import androidx.work.*
import ch.usi.inf.mwc.cusi.db.AppDatabase
import java.time.*
import java.util.concurrent.TimeUnit

class NotificationSchedulerWorker(
    context: Context,
    parameters: WorkerParameters,
) : CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result {
        val db = AppDatabase.getInstance(applicationContext)
        val start = LocalDateTime.now()
        val end = LocalDate.now().atTime(23, 59, 59)
        val zone = ZoneId.systemDefault()

        Log.d(TAG, "Scheduling lecture notifications")

        val wm = WorkManager.getInstance(applicationContext)
        wm.cancelAllWorkByTag(TAG)

        db.lectures().selectAllEnrolledWithinDates(start, end).forEach { lecture ->
            val courseInfo = db.course().getCourseInfo(lecture.courseId)

            val delay = lecture.start.atZone(zone).toEpochSecond() -
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