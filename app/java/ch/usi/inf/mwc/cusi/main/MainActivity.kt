package ch.usi.inf.mwc.cusi.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import ch.usi.inf.mwc.cusi.R
import ch.usi.inf.mwc.cusi.networking.sync.CoreDataSyncWorker
import ch.usi.inf.mwc.cusi.networking.sync.SyncInfoStorage
import kotlinx.coroutines.launch
import java.time.Duration

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        syncCoreDataIfNeed()
    }

    private fun syncCoreDataIfNeed() {
        if (SyncInfoStorage(this).shouldSync()) {
            val coreDataSyncRequest = OneTimeWorkRequestBuilder<CoreDataSyncWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                ).build()
            WorkManager.getInstance(this)
                .enqueue(coreDataSyncRequest)
        }
    }
}