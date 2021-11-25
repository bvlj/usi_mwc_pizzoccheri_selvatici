package ch.usi.inf.mwc.cusi.schedule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import ch.usi.inf.mwc.cusi.db.AppDatabase
import ch.usi.inf.mwc.cusi.model.Course
import ch.usi.inf.mwc.cusi.model.CourseInfo
import ch.usi.inf.mwc.cusi.model.CourseWithLecturers
import ch.usi.inf.mwc.cusi.model.Lecture
import ch.usi.inf.mwc.cusi.networking.sync.AppDataSync
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class ScheduleViewModel(app: Application) : AndroidViewModel(app) {
    suspend fun getTodaySchedule() = withContext(Default){
        val db = AppDatabase.getInstance(getApplication())
        val today = LocalDate.now()
        val todayStart = LocalDateTime.of(today, LocalTime.of(0, 0, 0))
        val todayEnd = LocalDateTime.of(today, LocalTime.of(23, 59, 59))
        db.lectures().selectAllEnrolledWithinDates(todayStart, todayEnd).map {
            it to db.course().getCourseInfo(it.courseId)!!
        }
    }

}