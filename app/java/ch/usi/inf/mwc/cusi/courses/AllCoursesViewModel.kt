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

    /**
     * Get all courses filtered by the current "filter" value.
     */
    fun getAllCourses(): LiveData<List<CourseWithLecturers>> {
        val db = AppDatabase.getInstance(getApplication())
        // Map the filter LiveData to a list of CourseWithLecturers so
        // that when the user changes the filter from the search bar,
        // the list LiveData gets automatically updated as well
        return filter.switchMap {
            if (it.isEmpty()) {
                db.course().getLiveAll()
            } else {
                // Surround the filter value with "% %" for the SQL "LIKE"
                db.course().getLiveFiltered("%$it%")
            }
        }
    }
}