package ch.usi.inf.mwc.cusi.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import ch.usi.inf.mwc.cusi.R
import ch.usi.inf.mwc.cusi.networking.sync.CoreDataSyncWorker
import ch.usi.inf.mwc.cusi.networking.sync.SyncInfoStorage
import ch.usi.inf.mwc.cusi.notification.LectureNotificationUtil
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val bottomBar: BottomNavigationView = findViewById(R.id.bottom_nav)

        setSupportActionBar(toolbar)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_container
        ) as NavHostFragment
        navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.coursesAllFragment,
                R.id.enrolledCoursesFragment,
                R.id.scheduleFragment,
                R.id.preferencesFragment,
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        bottomBar.setupWithNavController(navController)

        syncCoreDataIfNeed()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    private fun syncCoreDataIfNeed() {
        val wm = WorkManager.getInstance(this)
        if (SyncInfoStorage(this).shouldSync()) {
            val coreDataSyncRequest = OneTimeWorkRequestBuilder<CoreDataSyncWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                ).build()
            wm.enqueue(coreDataSyncRequest)
        }
        LectureNotificationUtil.schedule(wm)
    }
}