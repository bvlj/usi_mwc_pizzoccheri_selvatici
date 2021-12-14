package ch.usi.inf.mwc.cusi.courses

import android.app.Application
import androidx.lifecycle.*
import ch.usi.inf.mwc.cusi.db.AppDatabase
import ch.usi.inf.mwc.cusi.model.Course
import ch.usi.inf.mwc.cusi.model.CourseWithLecturers
import ch.usi.inf.mwc.cusi.networking.sync.AppDataSync
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.withContext

class EnrolledCoursesViewModel(app: Application) : AndroidViewModel(app) {


    suspend fun manualSync() {
        AppDataSync.fetchInfo(getApplication())
    }


    fun getAllCourses(): LiveData<List<CourseWithLecturers>> {
        val db = AppDatabase.getInstance(getApplication())
        return db.course().getEnrolledLive()
    }
}