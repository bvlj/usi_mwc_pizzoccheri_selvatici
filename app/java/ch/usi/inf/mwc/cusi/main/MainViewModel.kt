package ch.usi.inf.mwc.cusi.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ch.usi.inf.mwc.cusi.networking.sync.AppDataSync

class MainViewModel(app: Application) : AndroidViewModel(app) {

    suspend fun sync() {
        AppDataSync.fetchInfo(getApplication())
    }
}