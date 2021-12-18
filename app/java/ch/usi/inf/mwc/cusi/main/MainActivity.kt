package ch.usi.inf.mwc.cusi.main

import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import ch.usi.inf.mwc.cusi.R
import ch.usi.inf.mwc.cusi.networking.sync.CoreDataSyncWorker
import ch.usi.inf.mwc.cusi.networking.sync.SyncInfoStorage
import ch.usi.inf.mwc.cusi.notification.LectureNotificationUtil
import ch.usi.inf.mwc.cusi.preferences.Preferences
import ch.usi.inf.mwc.cusi.utils.LightSensorListener
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var sensorManager: SensorManager
    private lateinit var prefsManager: SharedPreferences
    private lateinit var navController: NavController

    private val lightSensorListener = LightSensorListener(this::setDarkMode)
    private val prefsListener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
        when (key) {
            Preferences.KEY_STYLE -> onStyleChanged(prefs.getString(key, null))
        }
    }

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

        sensorManager = getSystemService(SensorManager::class.java)
        prefsManager = PreferenceManager.getDefaultSharedPreferences(this).apply {
            registerOnSharedPreferenceChangeListener(prefsListener)
            onStyleChanged(getString(Preferences.KEY_STYLE, Preferences.VALUE_STYLE_SYSTEM))
        }

        syncCoreDataIfNeed()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    override fun onDestroy() {
        disableLightSensor()
        prefsManager.unregisterOnSharedPreferenceChangeListener(prefsListener)
        super.onDestroy()
    }

    private fun setDarkMode(isDark: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun onStyleChanged(newValue: String?) {
        when (newValue) {
            Preferences.VALUE_STYLE_SYSTEM -> {
                disableLightSensor()
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            Preferences.VALUE_STYLE_LIGHT_SENSOR -> {
                enableLightSensor()
            }
            Preferences.VALUE_STYLE_DAY -> {
                disableLightSensor()
                setDarkMode(false)
            }
            Preferences.VALUE_STYLE_NIGHT -> {
                disableLightSensor()
                setDarkMode(true)
            }
        }
    }

    private fun enableLightSensor() {
        val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        if (lightSensor != null) {
            sensorManager.registerListener(
                lightSensorListener,
                lightSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    private fun disableLightSensor() {
        sensorManager.unregisterListener(lightSensorListener)
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