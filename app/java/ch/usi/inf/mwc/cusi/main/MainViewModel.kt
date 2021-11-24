package ch.usi.inf.mwc.cusi.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import ch.usi.inf.mwc.cusi.db.AppDatabase
import ch.usi.inf.mwc.cusi.model.Course
import ch.usi.inf.mwc.cusi.networking.sync.AppDataSync

class MainViewModel(app: Application) : AndroidViewModel(app) {

    suspend fun manualSync() {
        AppDataSync.fetchInfo(getApplication())
    }
}