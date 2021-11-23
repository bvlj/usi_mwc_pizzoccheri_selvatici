package ch.usi.inf.mwc.cusi.main

import android.app.Application
import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.AndroidViewModel
import ch.usi.inf.mwc.cusi.networking.sync.AppDataSync

class MainViewModel(app: Application) : AndroidViewModel(app) {

    suspend fun sync() {
        try {
            AppDataSync.fetchInfo(getApplication())
        } catch (e: SQLiteConstraintException) {
            e.printStackTrace()
        }
    }
}