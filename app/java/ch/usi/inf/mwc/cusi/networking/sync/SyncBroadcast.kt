package ch.usi.inf.mwc.cusi.networking.sync

import android.content.Context
import android.content.Intent
import android.content.IntentFilter

object SyncBroadcast {
    private const val ACTION = "ch.usi.inf.mwc.cusi.ACTION_SYNC"
    private const val SUCCESS = "success"

    const val ARG_SYNC_LECTURES = "lectures"

    val INTENT_FILTER = IntentFilter(ACTION)

    fun syncAll(context: Context) {
        context.startForegroundService(
            Intent(
                context,
                SyncService::class.java
            )
        )
    }

    fun syncLectures(context: Context, courseId: Int) {
        context.startForegroundService(
            Intent(
                context,
                SyncService::class.java
            ).putExtra(ARG_SYNC_LECTURES, courseId)
        )
    }


    fun getIntent(success: Boolean) = Intent()
        .setAction(ACTION)
        .putExtra(SUCCESS, success)

    fun isSuccessful(intent: Intent?) =
        intent?.getBooleanExtra(SUCCESS, false) ?: false
}