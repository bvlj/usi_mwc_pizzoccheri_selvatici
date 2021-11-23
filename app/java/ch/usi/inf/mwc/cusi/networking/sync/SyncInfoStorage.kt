package ch.usi.inf.mwc.cusi.networking.sync

import android.content.Context
import ch.usi.inf.mwc.cusi.utils.daysFrom
import ch.usi.inf.mwc.cusi.utils.getDate
import ch.usi.inf.mwc.cusi.utils.putDate
import java.time.LocalDate

class SyncInfoStorage(context: Context) {
    private val preferences = context.getSharedPreferences(
        PREFERENCES_DATA_SYNC,
        Context.MODE_PRIVATE,
    )

    fun shouldSync(): Boolean {
        val now = LocalDate.now()
        val lastSync = preferences.getDate(KEY_LAST_SYNC, NEVER_SYNCED_DATE)
        val syncInterval = preferences.getInt(KEY_SYNC_FREQ, VAL_SYNC_FREQ_DEFAULT)
        return syncInterval != VAL_SYNC_FREQ_NEVER && now.daysFrom(lastSync) > syncInterval
    }

    fun updateLastSync() {
        preferences.edit()
            .putDate(KEY_LAST_SYNC, LocalDate.now())
            .apply()
    }

    fun setSyncFrequency(days: Int) {
        preferences.edit()
            .putInt(KEY_SYNC_FREQ, days)
            .apply()
    }

    private companion object {
        const val PREFERENCES_DATA_SYNC = "data_sync"
        const val KEY_LAST_SYNC = "last_sync_date_time"
        const val KEY_SYNC_FREQ = "sync_freq"
        const val VAL_SYNC_FREQ_NEVER = -1
        const val VAL_SYNC_FREQ_DEFAULT = 3 // Days

        val NEVER_SYNCED_DATE: LocalDate = LocalDate.of(1970, 1, 1)
    }
}