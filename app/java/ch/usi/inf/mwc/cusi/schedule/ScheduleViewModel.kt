package ch.usi.inf.mwc.cusi.schedule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ch.usi.inf.mwc.cusi.db.AppDatabase
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class ScheduleViewModel(app: Application) : AndroidViewModel(app) {

    suspend fun getSchedule(date: LocalDate = LocalDate.now()) = withContext(Default) {
        val db = AppDatabase.getInstance(getApplication())
        val todayStart = LocalDateTime.of(date, LocalTime.of(0, 0, 0))
        val todayEnd = LocalDateTime.of(date, LocalTime.of(23, 59, 59))
        db.lectures().selectAllEnrolledWithinDates(todayStart, todayEnd).map {
            it to db.course().getCourseInfo(it.courseId)!!
        }
    }
}