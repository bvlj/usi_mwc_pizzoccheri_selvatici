package ch.usi.inf.mwc.cusi.main

import android.util.Log
import androidx.lifecycle.ViewModel
import ch.usi.inf.mwc.cusi.networking.UsiServices

class MainViewModel : ViewModel() {
    suspend fun randomMethdo() {
        UsiServices.getFaculties().forEach {
            Log.e("HELLOTAG", UsiServices.getCoursesByFaculty(it).toString())
        }
    }
}