package ch.usi.inf.mwc.cusi.course

import android.app.Application
import androidx.core.text.HtmlCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ch.usi.inf.mwc.cusi.db.AppDatabase
import ch.usi.inf.mwc.cusi.model.CourseInfo
import ch.usi.inf.mwc.cusi.networking.sync.SyncBroadcast


class CourseDetailsViewModel(app: Application) : AndroidViewModel(app) {

    /**
     * Get LiveData of the course details UI state.
     */
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

    /**
     * Enroll in the course.
     */
    suspend fun enroll(courseId: Int) {
        manageEnroll(courseId, true)
        SyncBroadcast.syncLectures(getApplication(), courseId)
    }

    /**
     * Un–enroll in the course.
     */
    suspend fun unenroll(courseId: Int) {
        manageEnroll(courseId, false)
        val db = AppDatabase.getInstance(getApplication())
        db.lectures().deleteAllOfCourse(courseId)
    }

    /**
     * Change enrollment status of a course.
     */
    private suspend fun manageEnroll(courseId: Int, doEnroll: Boolean) {
        val db = AppDatabase.getInstance(getApplication())
        val info = db.course().getCourseInfo(courseId)
        if (info != null) {
            db.course().update(info.copy(hasEnrolled = doEnroll))
        }
    }

    /**
     * Description HTML to Android Spanned string.
     */
    private fun prepareDescription(course: CourseInfo): CharSequence {
        return HtmlCompat.fromHtml(
            course.description,
            HtmlCompat.FROM_HTML_MODE_COMPACT
        )
    }
}
