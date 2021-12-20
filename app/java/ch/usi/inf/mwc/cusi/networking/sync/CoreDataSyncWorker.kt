package ch.usi.inf.mwc.cusi.networking.sync

import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

/**
 * Worker that starts the [SyncService].
 */
class CoreDataSyncWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        applicationContext.startForegroundService(
            Intent(
                applicationContext,
                SyncService::class.java
            )
        )
        return Result.success()
    }
}