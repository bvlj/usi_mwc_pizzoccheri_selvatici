package ch.usi.inf.mwc.cusi.courses

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import ch.usi.inf.mwc.cusi.db.AppDatabase
import ch.usi.inf.mwc.cusi.model.CourseWithLecturers

class EnrolledCoursesViewModel(app: Application) : AndroidViewModel(app) {

    fun getEnrolledCourses(): LiveData<List<CourseWithLecturers>> {
        val db = AppDatabase.getInstance(getApplication())
        return db.course().getEnrolledLive()
    }
}
