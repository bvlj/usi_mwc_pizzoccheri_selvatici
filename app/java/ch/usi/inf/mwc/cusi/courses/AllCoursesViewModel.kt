package ch.usi.inf.mwc.cusi.courses

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import ch.usi.inf.mwc.cusi.db.AppDatabase
import ch.usi.inf.mwc.cusi.model.CourseWithLecturers

class AllCoursesViewModel(app: Application) : AndroidViewModel(app) {

    private val filter = MutableLiveData("")

    fun setFilter(value: String) {
        filter.value = value
    }

    fun getAllCourses(): LiveData<List<CourseWithLecturers>> {
        val db = AppDatabase.getInstance(getApplication())
        return filter.switchMap {
            if (it.isEmpty()) {
                db.course().getLiveAll()
            } else {
                db.course().getLiveFiltered("%$it%")
            }
        }
    }
}