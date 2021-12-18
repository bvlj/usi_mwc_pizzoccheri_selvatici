package ch.usi.inf.mwc.cusi.preferences

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.*
import ch.usi.inf.mwc.cusi.R
import kotlinx.coroutines.launch

class PreferencesFragment : PreferenceFragmentCompat() {

    private val viewModel by viewModels<PreferencesViewModel>()

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
        when (key) {
            Preferences.KEY_FACULTIES -> {
                onSelectedFacultiesChanged(prefs.getStringSet(key, null) ?: setOf())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PreferenceManager.getDefaultSharedPreferences(requireContext())
            .registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val facultiesPreference = findPreference<MultiSelectListPreference>(
            Preferences.KEY_FACULTIES
        )
        val darkModePreference = findPreference<ListPreference>(Preferences.KEY_STYLE)
        val notificationsPreference = findPreference<SwitchPreference>(
            Preferences.KEY_NOTIFICATIONS
        )

        darkModePreference?.apply {
            entryValues = arrayOf(
                Preferences.VALUE_STYLE_SYSTEM,
                Preferences.VALUE_STYLE_LIGHT_SENSOR,
                Preferences.VALUE_STYLE_DAY,
                Preferences.VALUE_STYLE_NIGHT,
            )
        }
        notificationsPreference?.apply {
            setNotificationPreferenceSummary(hasLocationPermissions())
            setOnPreferenceClickListener {
                // Intercept and request permissions if needed
                if (hasLocationPermissions()) {
                    false
                } else {
                    isChecked = false
                    requestBackgroundLocation()
                    true
                }
            }
        }

        viewModel.getFaculties().observe(this) { faculties ->
            facultiesPreference?.apply {
                entries = faculties.map { it.name }.toTypedArray()
                entryValues = faculties.map { it.facultyId.toString() }.toTypedArray()
            }
        }

        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            setNotificationPreferenceSummary(results.values.all { it })
        }
    }

    override fun onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(requireContext())
            .unregisterOnSharedPreferenceChangeListener(listener)

        super.onDestroy()
    }

    private fun onSelectedFacultiesChanged(newValue: Set<String>) {
        lifecycleScope.launch {
            viewModel.setSelectedFaculties(newValue.map { it.toInt() })
        }
    }

    private fun hasLocationPermissions(): Boolean {
        val ctx = requireContext()
        val coarse = ctx.checkSelfPermission(
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val fine = ctx.checkSelfPermission(
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val background = Build.VERSION.SDK_INT < 29 ||
                ctx.checkSelfPermission(
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
        return coarse && fine && background
    }

    private fun requestBackgroundLocation() {
        val activity = requireActivity()
        activity.requestPermissions(
            if (Build.VERSION.SDK_INT < 29) {
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                )
            } else {
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                )
            },
            PERMISSION_REQ
        )
    }

    private fun setNotificationPreferenceSummary(hasPermissions: Boolean) {
        findPreference<SwitchPreference>(Preferences.KEY_NOTIFICATIONS)?.apply {
            if (hasPermissions) {
                setSummaryOff(R.string.preference_notification_summary_off)
                setSummaryOn(R.string.preference_notification_summary_on)
            } else {
                setSummaryOff(R.string.preference_notification_summary_permissions)
                setSummaryOn(R.string.preference_notification_summary_permissions)
            }
        }
    }

    private companion object {
        const val PERMISSION_REQ = 1
    }
}