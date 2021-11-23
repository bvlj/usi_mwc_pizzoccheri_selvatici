package ch.usi.inf.mwc.cusi.networking.sync

import android.content.Context
import android.database.SQLException
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.io.IOException

class CoreDataSyncWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // Fetch new data
            AppDataSync.fetchInfo(applicationContext)
            // Update last sync date
            SyncInfoStorage(applicationContext).updateLastSync()
            Result.success()
        } catch (e: SQLException) {
            Log.e(TAG, "Failed to sync info data", e)
            // Db error, abort
            Result.failure()
        } catch (e: IOException) {
            Log.e(TAG, "Failed to sync info data", e)
            // Network error, retry
            return Result.retry()
        }
    }

    private companion object {
        const val TAG = "InfoSyncWorker"
    }
}