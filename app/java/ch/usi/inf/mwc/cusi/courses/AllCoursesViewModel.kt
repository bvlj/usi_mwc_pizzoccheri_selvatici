package ch.usi.inf.mwc.cusi.courses

import android.app.Application
import androidx.lifecycle.*
import ch.usi.inf.mwc.cusi.db.AppDatabase
import ch.usi.inf.mwc.cusi.model.Course
import ch.usi.inf.mwc.cusi.model.CourseWithLecturers
import ch.usi.inf.mwc.cusi.networking.sync.AppDataSync
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.withContext

class AllCoursesViewModel(app: Application) : AndroidViewModel(app) {

    private val filter = MutableLiveData("");

    suspend fun manualSync() {
        AppDataSync.fetchInfo(getApplication())
    }

    fun setFilter(value: String){
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

    suspend fun getFilteredCourses(name: String) = withContext(Default) {
        val db = AppDatabase.getInstance(getApplication())
        val list = db.course().getAll()
        list.filter { it.info.name.contains(name) }
    }
}