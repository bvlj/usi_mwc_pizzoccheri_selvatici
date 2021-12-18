package ch.usi.inf.mwc.cusi.networking.sync

import android.content.Context
import androidx.preference.PreferenceManager
import ch.usi.inf.mwc.cusi.preferences.Preferences
import ch.usi.inf.mwc.cusi.utils.daysFrom
import ch.usi.inf.mwc.cusi.utils.getDate
import ch.usi.inf.mwc.cusi.utils.putDate
import java.time.LocalDate

class SyncInfoStorage(context: Context) {
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun shouldSync(): Boolean {
        val now = LocalDate.now()
        val lastSync = preferences.getDate(Preferences.KEY_SYNC_LAST, NEVER_SYNCED_DATE)
        val syncInterval = preferences.getString(
            Preferences.KEY_SYNC_FREQUENCY,
            Preferences.VALUE_SYNC_DAILY
        )
        return syncInterval != null &&
                syncInterval != Preferences.VALUE_SYNC_NEVER &&
                now.daysFrom(lastSync) > syncInterval.toInt()
    }

    fun updateLastSync() {
        preferences.edit()
            .putDate(Preferences.KEY_SYNC_LAST, LocalDate.now())
            .apply()
    }

    private companion object {
        val NEVER_SYNCED_DATE: LocalDate = LocalDate.of(1970, 1, 1)
    }
}