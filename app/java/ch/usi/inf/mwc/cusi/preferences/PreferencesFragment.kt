package ch.usi.inf.mwc.cusi.preferences

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.preference.MultiSelectListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import ch.usi.inf.mwc.cusi.R
import kotlinx.coroutines.launch

class PreferencesFragment : PreferenceFragmentCompat() {

    private val viewModel by viewModels<PreferencesViewModel>()

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
        when (key) {
            KEY_FACULTIES -> onSelectedFacultiesChanged(prefs.getStringSet(key, null) ?: setOf())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PreferenceManager.getDefaultSharedPreferences(requireContext())
            .registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val facultiesPreference = findPreference<MultiSelectListPreference>(KEY_FACULTIES)

        viewModel.getFaculties().observe(this) { faculties ->
            facultiesPreference?.apply {
                entries = faculties.map { it.name }.toTypedArray()
                entryValues = faculties.map { it.facultyId.toString() }.toTypedArray()
            }
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

    private companion object {
        const val KEY_FACULTIES = "key_selected_faculties"
    }
}