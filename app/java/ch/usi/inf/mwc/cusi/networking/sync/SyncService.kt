package ch.usi.inf.mwc.cusi.networking.sync

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.database.SQLException
import android.os.IBinder
import android.util.Log
import ch.usi.inf.mwc.cusi.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.coroutines.CoroutineContext

class SyncService : Service(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Default + job

    private val job = Job()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = buildNotification()
        startForeground(NOTIFICATION_ID, notification)

        val courseId = intent?.getIntExtra(SyncBroadcast.ARG_SYNC_LECTURES, -1) ?: -1

        launch(job) {
            if (courseId > 0) {
                // Sync lectures of the given course
                syncLectures(courseId)
            } else {
                // Sync all data
                syncAll()
            }
            stopSelf()
        }

        return START_STICKY
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    private fun buildNotification(): Notification {
        val nm = getSystemService(NotificationManager::class.java)
        if (nm != null && nm.getNotificationChannel(NOTIFICATION_CHANNEL) == null) {
            nm.createNotificationChannel(
                NotificationChannel(
                    NOTIFICATION_CHANNEL,
                    getString(R.string.notification_sync_channel),
                    NotificationManager.IMPORTANCE_MIN
                )
            )
        }

        return Notification.Builder(applicationContext, NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.ic_sync)
            .setContentTitle(getString(R.string.notification_sync_message))
            .setProgress(100, 20, true)
            .setOngoing(true)
            .build()
    }

    private suspend fun syncAll() {
        try {
            // Fetch new data
            AppDataSync.fetchInfo(applicationContext)
            // Update last sync date
            SyncInfoStorage(applicationContext).updateLastSync()
            // Notify the app
            sendBroadcast(SyncBroadcast.getIntent(true))
        } catch (e: SQLException) {
            Log.e(TAG, "Failed to insert data into the database", e)
            sendBroadcast(SyncBroadcast.getIntent(false))
        } catch (e: IOException) {
            Log.e(TAG, "Failed to sync data", e)
            sendBroadcast(SyncBroadcast.getIntent(false))
        }
    }

    private suspend fun syncLectures(courseId: Int) {
        try {
            AppDataSync.refreshCourseLectures(applicationContext, courseId)
        } catch (e: SQLException) {
            Log.e(TAG, "Failed to insert lectures into the database", e)
            sendBroadcast(SyncBroadcast.getIntent(false))
        } catch (e: IOException) {
            Log.e(TAG, "Failed to sync lectures", e)
            sendBroadcast(SyncBroadcast.getIntent(false))
        }
    }

    private companion object {
        const val TAG = "SyncService"
        const val NOTIFICATION_CHANNEL = "channel_sync"
        const val NOTIFICATION_ID = 80
    }
}