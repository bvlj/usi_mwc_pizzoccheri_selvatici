package ch.usi.inf.mwc.cusi.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import ch.usi.inf.mwc.cusi.R
import ch.usi.inf.mwc.cusi.courses.AllCoursesActivity
import ch.usi.inf.mwc.cusi.networking.sync.CoreDataSyncWorker
import ch.usi.inf.mwc.cusi.networking.sync.SyncInfoStorage
import ch.usi.inf.mwc.cusi.schedule.ScheduleActivity

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        syncCoreDataIfNeed()

        val allCoursesBtn: Button = findViewById(R.id.all_courses_btn)
        val enrolledCoursesBtn: Button = findViewById(R.id.enrolled_courses_btn)

        allCoursesBtn.setOnClickListener {
            startActivity(Intent(this, AllCoursesActivity::class.java))
        }
        enrolledCoursesBtn.setOnClickListener {
            startActivity(Intent(this, ScheduleActivity::class.java))
        }
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