package ch.usi.inf.mwc.cusi.course

import android.app.Application
import androidx.core.text.HtmlCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ch.usi.inf.mwc.cusi.db.AppDatabase
import ch.usi.inf.mwc.cusi.model.CourseInfo
import ch.usi.inf.mwc.cusi.networking.sync.AppDataSync


class CourseDetailsViewModel(app: Application) : AndroidViewModel(app) {

    fun getCourseWithLecturers(courseId: Int): LiveData<CourseDetailsState> {
        val db = AppDatabase.getInstance(getApplication())
        return db.course().getCourseLive(courseId).map {
            CourseDetailsState(
                courseName = it.info.name,
                courseDescription = prepareDescription(it.info),
                lecturers = it.lecturers,
                hasEnrolled = it.info.hasEnrolled,
            )
        }
    }

    private suspend fun manageEnroll(courseId: Int, doEnroll: Boolean) {
        val db = AppDatabase.getInstance(getApplication())
        val info = db.course().getCourseInfo(courseId)
        if (info != null) {
            db.course().update(info.copy(hasEnrolled = doEnroll))
        }
    }

    suspend fun enroll(courseId: Int) {
        manageEnroll(courseId, true)
        AppDataSync.refreshCourseLectures(getApplication(), courseId)
    }

    suspend fun unenroll(courseId: Int) {
        manageEnroll(courseId, false)
        val db = AppDatabase.getInstance(getApplication())
        db.lectures().deleteAllOfCourse(courseId)
    }

    private fun prepareDescription(course: CourseInfo): CharSequence {
        return HtmlCompat.fromHtml(
            course.description,
            HtmlCompat.FROM_HTML_MODE_COMPACT
        )
    }
}