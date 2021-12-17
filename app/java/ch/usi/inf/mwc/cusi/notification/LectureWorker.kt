package ch.usi.inf.mwc.cusi.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import ch.usi.inf.mwc.cusi.R
import ch.usi.inf.mwc.cusi.utils.LocationUtils
import ch.usi.inf.mwc.cusi.utils.LocationUtils.minus

class LectureWorker(
    context: Context,
    parameters: WorkerParameters,
) : CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result {
        val name = inputData.getString(KEY_NAME) ?: "???"
        val room = inputData.getString(KEY_ROOM) ?: "???"
        val address = inputData.getString(KEY_ADDRESS) ?: return run {
            Log.e(TAG, "Missing address parameter")
            Result.failure()
        }

        val userLocation = LocationUtils.getLastGoodLocation(applicationContext)
            ?: return run {
                Log.e(TAG, "Failed to get user location")
                Result.retry()
            }
        val lectureAddress = LocationUtils.addressFromString(applicationContext, address)
            ?: return run {
                Log.e(TAG, "Could not decode address: \"$address\"")
                Result.failure()
            }

        val distance = userLocation - lectureAddress
        if (distance > DISTANCE_THRESHOLD) {
            sendNotification(applicationContext, name, room, address)
        } else {
            Log.d(
                TAG, "Within distance treshold: " +
                        "[${userLocation.longitude}, ${userLocation.latitude}] to" +
                        "[${lectureAddress.longitude}, ${lectureAddress.latitude}] =" +
                        "$distance < $DISTANCE_THRESHOLD"
            )
        }

        return Result.success()
    }

    private fun sendNotification(
        context: Context,
        courseName: String,
        room: String,
        address: String,
    ) {
        val nm = context.getSystemService(NotificationManager::class.java)
        if (nm.getNotificationChannel(CHANNEL_ID) == null) {
            nm.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.notification_channel_title),
                    NotificationManager.IMPORTANCE_HIGH,
                )
            )
        }

        val notification = Notification.Builder(context, CHANNEL_ID)
            .setContentTitle(courseName)
            .setContentText(
                context.getString(
                    R.string.notification_message, courseName,
                    room, address
                )
            ).build()
        nm.notify(courseName.hashCode(), notification)
    }

    companion object {
        private const val TAG = "LectureWorker"

        private const val CHANNEL_ID = "lecture_worker_channel"

        private const val DISTANCE_THRESHOLD = 800.0 // meters

        const val KEY_NAME = "name"
        const val KEY_ADDRESS = "address"
        const val KEY_ROOM = "room"
    }
}