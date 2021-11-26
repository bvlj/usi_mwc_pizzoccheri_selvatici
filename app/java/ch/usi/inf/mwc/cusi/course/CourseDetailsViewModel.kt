package ch.usi.inf.mwc.cusi.course

import android.app.Application
import android.text.SpannableStringBuilder
import android.text.style.BulletSpan
import android.text.style.QuoteSpan.STANDARD_COLOR
import androidx.core.text.HtmlCompat
import androidx.core.text.bold
import androidx.core.text.inSpans
import androidx.core.text.scale
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ch.usi.inf.mwc.cusi.db.AppDatabase
import ch.usi.inf.mwc.cusi.model.CourseInfo
import ch.usi.inf.mwc.cusi.model.CourseWithLecturers


class CourseDetailsViewModel(app: Application) : AndroidViewModel(app) {

    fun getCourseWithLecturers(courseId: Int): LiveData<CourseDetailsState> {
        val db = AppDatabase.getInstance(getApplication())
        return db.course().getCourse(courseId).map {
            CourseDetailsState(
                courseName = it.info.name,
                courseDescription = prepareDescription(it.info),
                lecturers = it.lecturers,
                hasEnrolled = it.info.hasEnrolled,
            )
        }
    }

    suspend fun invertEnroll(courseId: Int) {
        val db = AppDatabase.getInstance(getApplication())
        val info = db.course().getCourseInfo(courseId)
        if (info != null) {
            db.course().update(info.copy(hasEnrolled = !info.hasEnrolled))
        }
    }

    private fun prepareDescription(course: CourseInfo): CharSequence {
        return HtmlCompat.fromHtml(
            course.description,
            HtmlCompat.FROM_HTML_MODE_COMPACT
        )
    }
}